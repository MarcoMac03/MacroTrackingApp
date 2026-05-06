package org.example.project.Database
import org.jetbrains.exposed.sql.Table

object User: Table("User") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 30)
    val email = varchar("email", 255)
    val age = integer("age")
    val phone = varchar("phone", 10).nullable()

    override val primaryKey = PrimaryKey(id)
}