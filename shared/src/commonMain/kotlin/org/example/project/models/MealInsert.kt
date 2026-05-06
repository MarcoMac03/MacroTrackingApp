package org.example.project.models

import kotlinx.serialization.Serializable

// Json data to insert in the Meals table
@Serializable
data class MealInsert(
    val type: String,
    val userId: Int,
    val date: String,
)