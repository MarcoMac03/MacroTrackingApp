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
import org.example.project.models.FoodRequest
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.example.project.Database.Meal
import org.example.project.Database.MealFood
import org.example.project.models.MealFoodInsert
import org.example.project.models.MealInsert
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.lowerCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import org.example.project.mapper.toFoodRequest

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
            transaction {
                val mealId = Meal.insert {
                    it[type] = request.type
                    it[userId] = 0
                    it[date] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
                } get Meal.id

                request.foodsId.forEach{ foodId ->
                    MealFood.insert {
                        it[this.mealId] = mealId
                        it[this.foodId] = foodId

                    }
                }
            }
            call.respond(HttpStatusCode.Created, "Pasto inserito correttamente")
        }

        post("7meal/foods/{foods}") {
            //val request = call.receive<MealFoodInsert[]>()
        }
    }
}