package org.example.project

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json

object NetworkClient {

    const val BASE_URL = "https://macrotrackingapp.onrender.com"
    val client = HttpClient {
        install(ContentNegotiation) { json() }
    }
}