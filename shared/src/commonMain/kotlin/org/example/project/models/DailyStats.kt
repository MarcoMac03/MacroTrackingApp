package org.example.project.models
import kotlinx.serialization.Serializable

@Serializable
data class DailyStats (
    var mealCount: Int = 0,
    var calories: Double = 0.0
)