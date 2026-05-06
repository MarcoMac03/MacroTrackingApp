package org.example.project.mapper

import org.example.project.Database.Foods
import org.example.project.models.FoodRequest
import kotlin.collections.get
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toFoodRequest(): FoodRequest {
    return FoodRequest(
        id = this[Foods.id],
        name = this[Foods.name],
        brand = this[Foods.brand],
        calories = this[Foods.calories],
        carbs = this[Foods.carbs],
        sugars = this[Foods.sugars],
        protein = this[Foods.protein],
        fats = this[Foods.fats],
        saturatedFats = this[Foods.saturatedFats],
        fibers = this[Foods.fibers]
    )
}