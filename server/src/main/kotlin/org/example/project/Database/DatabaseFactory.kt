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
                    // Sostituiamo il protocollo per permettere a java.net.URI di parsare l'URL
                    val uriString = rawUrl.replace("postgresql://", "http://")
                    val uri = URI(uriString)

                    val userInfo = uri.userInfo // Sarà nel formato "user:password"
                    val parts = userInfo.split(":")
                    val user = parts[0]
                    val password = parts.drop(1).joinToString(":")

                    val host = uri.host
                    val port = if (uri.port != -1) ":${uri.port}" else ""
                    val path = uri.path // es. "/postgres"

                    jdbcUrl = "jdbc:postgresql://$host$port$path"
                    username = user
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