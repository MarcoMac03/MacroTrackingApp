package org.example.project.Database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.example.project.models.FoodRequest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import java.net.URI

// Initialize the database when the server starts
object DatabaseFactory {
    fun init() {
        val rawUrl = System.getenv("DATABASE_URL")

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"

            if (rawUrl != null) {
                try {
                    val cleanUrl = rawUrl.trim()

                    val protocolRemoved = cleanUrl.replace("postgresql://", "")

                    val parts = protocolRemoved.split("@")

                    val credentials = parts[0].split(":")
                    val username = credentials[0]
                    val password = credentials.drop(1).joinToString(":")

                    val hostAndRest = parts[1].split("/")
                    val hostAndPort = hostAndRest[0]
                    val dbName = hostAndRest[1]

                    val hostParts = hostAndPort.split(":")
                    val host = hostParts[0]
                    val port = if (hostParts.size > 1) ":${hostParts[1]}" else ""

                    jdbcUrl = "jdbc:postgresql://$host$port/$dbName"
                    this.username = username
                    this.password = password

                } catch (e: Exception) {
                    println("Errore durante il parsing dell'URL, uso i parametri di fallback: ${e.message}")
                    setupLocalFallback()
                }
            } else {
                setupLocalFallback()
            }

            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val ds = HikariDataSource(config)
        Database.connect(ds)

        transaction {
            SchemaUtils.create(Foods)

            if(Foods.selectAll().empty()) {
                println("Inserimento manuale")
                val initFoods = listOf(
                    FoodRequest(1, "pollo", null, 110, 0.0, 0.0, 23.0, 2.0, 0.6, 0.0),
                    FoodRequest(2, "pasta bianca", null, 350, 72.0, 3.5, 12.0, 1.5, 0.3, 3.0),
                    FoodRequest(3, "lenticchie", null, 350, 60.0, 2.0, 25.0, 1.0, 0.1, 11.0),
                    FoodRequest(4, "yogurt greco", null, 57, 5.2, 5.2, 9.0, 0.0, 0.0, 0.0),
                    FoodRequest(5, "pomodori", null, 18, 3.9, 2.6, 0.9, 0.2, 0.0, 1.2),
                )

                Foods.batchInsert(initFoods) { food->
                    this[Foods.id] = food.id
                    this[Foods.name] = food.name
                    this[Foods.calories] = food.calories
                    this[Foods.carbs] = food.carbs
                    this[Foods.sugars] = food.sugars
                    this[Foods.protein] = food.protein
                    this[Foods.fats] = food.fats
                    this[Foods.saturatedFats] = food.saturatedFats
                    this[Foods.fibers] = food.fibers
                }
            }
        }
    }

    private fun HikariConfig.setupLocalFallback() {
        jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
        username = "postgres"
        password = "admin"
    }
}