package org.example.project.models

import kotlinx.serialization.Serializable

@Serializable
data class MealFoodInsert(
    val type: String,
    val foodsId: List<Int>,
    val quantity: List<Double>
)