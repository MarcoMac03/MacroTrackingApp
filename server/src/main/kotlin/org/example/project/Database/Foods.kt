package org.example.project.Database
import org.jetbrains.exposed.sql.Table

// Table to store the different foods with their nutritional values
object Foods: Table("Foods") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    val brand = varchar("brand", 50).nullable()
    val calories = integer("calories")
    val carbs = double("carbs")
    val sugars = double("sugars")
    val protein = double("protein")
    val fats = double("fats")
    val saturatedFats = double("saturatedFats")
    val fibers = double("fibers")

    override val primaryKey = PrimaryKey(id)
}