package org.example.project.models

import kotlinx.serialization.Serializable

// Json data to insert in the Foods table
@Serializable
data class FoodRequest(
    val id: Int = 0,
    val name: String = "",
    val brand: String? = null,
    val calories: Int = 0,
    val carbs: Double = 0.0,
    val sugars: Double = 0.0,
    val protein: Double = 0.0,
    val fats: Double = 0.0,
    val saturatedFats: Double = 0.0,
    val fibers: Double = 0.0,
    var grams: Double = 0.0,
)