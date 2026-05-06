package org.example.project.Database
import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date
import org.jetbrains.exposed.sql.Table

// Table to store meals during the days
object Meal: Table("Meals") {
    val id = integer("id").autoIncrement()
    val type = varchar("meal_type", 20)
    val userId = integer("user_id") references User.id
    val date = varchar("date", 10)

    override val primaryKey = PrimaryKey(id)
}