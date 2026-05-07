package org.example.project.Database
import org.jetbrains.exposed.sql.Table

object MealFood: Table("MealFood") {
    val id = integer("id").autoIncrement()
    val mealId = integer("meal_id") references Meal.id
    val foodId = integer("food_id") references Foods.id
    val quantity = double("quantity")

    override val primaryKey = PrimaryKey(id)
}