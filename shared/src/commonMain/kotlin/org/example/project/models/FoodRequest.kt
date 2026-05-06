package org.example.project.models

import kotlinx.serialization.Serializable

// Json data to insert in the Foods table
@Serializable
data class FoodRequest(
    val id: Int,
    val name: String,
    val brand: String? = null,
    val calories: Int,
    val carbs: Double,
    val sugars: Double,
    val protein: Double,
    val fats: Double,
    val saturatedFats: Double,
    val fibers: Double,
)