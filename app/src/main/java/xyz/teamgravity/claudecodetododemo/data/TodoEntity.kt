package xyz.teamgravity.claudecodetododemo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val notes: String = "",
    val tag: String,
    val tagColor: String,
    val priority: String,
    val due: String? = null,
    val done: Boolean = false,
    val sortOrder: Int = 0,
)

fun TodoEntity.toTodo(): Todo = Todo(
    id = id,
    title = title,
    notes = notes,
    tag = tag,
    tagColor = TagColor.valueOf(tagColor),
    priority = Priority.valueOf(priority),
    due = due,
    done = done,
)

fun Todo.toEntity(sortOrder: Int): TodoEntity = TodoEntity(
    id = id,
    title = title,
    notes = notes,
    tag = tag,
    tagColor = tagColor.name,
    priority = priority.name,
    due = due,
    done = done,
    sortOrder = sortOrder,
)
