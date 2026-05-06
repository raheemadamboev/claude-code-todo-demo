package xyz.teamgravity.claudecodetododemo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos ORDER BY sortOrder ASC")
    suspend fun getAll(): List<TodoEntity>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getById(id: String): TodoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todos: List<TodoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM todos")
    suspend fun count(): Int

    @Query("UPDATE todos SET done = NOT done WHERE id = :id")
    suspend fun toggleDone(id: String)

    @Query("UPDATE todos SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateSortOrder(id: String, sortOrder: Int)

    @Transaction
    suspend fun reorderAll(updates: List<Pair<String, Int>>) {
        updates.forEach { (id, order) -> updateSortOrder(id, order) }
    }

    @Transaction
    suspend fun insertAtTop(todo: TodoEntity) {
        val all = getAll()
        all.forEach { updateSortOrder(it.id, it.sortOrder + 1) }
        insert(todo.copy(sortOrder = 0))
    }
}
