package org.example.project

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.project.Database.DatabaseFactory
import org.example.project.Database.Foods
import org.example.project.models.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.Database.Meal
import org.example.project.Database.MealFood
import org.jetbrains.exposed.sql.lowerCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import org.example.project.Database.User
import org.example.project.mapper.toFoodRequest
import org.jetbrains.exposed.sql.and

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()      // initialization DB

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    routing {
        // post to insert a new food into the table if not already memorized
        post("/food") {
            val request = call.receive<FoodRequest>()
            val isDuplicate = transaction {
                Foods.selectAll().where { Foods.name eq request.name }.empty().not()
            }

            if(isDuplicate) {
                call.respond(HttpStatusCode.Conflict, "L'alimento '${request.name}' è già nel DB")
            } else {
                transaction {
                    Foods.insert {
                        it[name] = request.name
                        it[brand] = request.brand
                        it[calories] = request.calories
                        it[carbs] = request.carbs
                        it[sugars] = request.sugars
                        it[protein] = request.protein
                        it[fats] = request.fats
                        it[saturatedFats] = request.saturatedFats
                        it[fibers] = request.fibers
                    }
                }
                call.respond(HttpStatusCode.Created)
            }
        }

        route("/foods") {
            get {
                val query = call.request.queryParameters["name"] ?: ""
                val foodList = transaction {
                    Foods.selectAll().where { Foods.name.lowerCase().like("%${query.lowercase()}%") }
                        .map{ it.toFoodRequest() }
                }
                call.respond(HttpStatusCode.OK, foodList)
            }

        }

        // Get a food by its id
        get("/food/{id}") {
            val idParam = call.parameters["id"]
            val id = idParam?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Id non valido")
                return@get
            }

            val food = transaction {
                Foods.selectAll().where { Foods.id eq id }.map { row ->
                    FoodRequest(
                        id = row[Foods.id],
                        name = row[Foods.name],
                        brand = row[Foods.brand],
                        calories = row[Foods.calories],
                        carbs = row[Foods.carbs],
                        sugars = row[Foods.sugars],
                        protein = row[Foods.protein],
                        fats = row[Foods.fats],
                        saturatedFats = row[Foods.saturatedFats],
                        fibers = row[Foods.fibers],
                    )
                }.singleOrNull()
            }

            if(food != null) {
                call.respond(HttpStatusCode.OK, food)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/meal") {
            val request = call.receive<MealFoodInsert>()
            if(request.type.isBlank() || request.foodsId.isEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Missing information")
                return@post
            }
            if(request.foodsId.size != request.quantity.size) {
                call.respond(HttpStatusCode.BadRequest, "Quantity must be the same size as foods")
                return@post
            }
            transaction {
                val mealId = Meal.insert {
                    it[type] = request.type
                    it[userId] = 0
                    it[date] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                } get Meal.id

                request.foodsId.forEachIndexed { index, currentFood ->
                    MealFood.insert {
                        it[this.mealId] = mealId
                        it[this.foodId] = currentFood
                        it[this.quantity] = request.quantity[index]
                    }
                }
            }
            call.respond(HttpStatusCode.Created, "Pasto inserito correttamente")
        }

        get("/userMeals") {
            val userId = call.request.queryParameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing userId")
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

            val exist = transaction { !User.selectAll().where { User.id eq userId.toInt() }.empty()}
            if(!exist) {
                return@get call.respond(HttpStatusCode.NotFound, "User not found")
            }

            val dailyStats = transaction {
                val mealCount = Meal.select(Meal.id).where { (Meal.userId eq userId.toInt()) and (Meal.date eq today)}.count()

                val calories = (Meal innerJoin MealFood innerJoin Foods)
                    .select(Foods.calories, MealFood.quantity)
                    .where { (Meal.userId eq userId.toInt()) and (Meal.date eq today) }
                    .map { row -> (row[Foods.calories] * row[MealFood.quantity]) / 100.0 }
                    .sum()

                DailyStats(mealCount.toInt(), calories)
            }
            call.respond(HttpStatusCode.OK, dailyStats)
        }

        post("/meal/foods/{foods}") {
            //val request = call.receive<MealFoodInsert[]>()
        }
    }
}