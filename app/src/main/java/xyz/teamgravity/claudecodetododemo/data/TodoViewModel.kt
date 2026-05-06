package xyz.teamgravity.claudecodetododemo.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val dao: TodoDao,
) : ViewModel() {

    val todos: StateFlow<List<Todo>> = dao.observeAll()
        .map { entities -> entities.map { it.toTodo() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (dao.count() == 0) {
                val entities = SAMPLE_TODOS.mapIndexed { index, todo ->
                    todo.toEntity(sortOrder = index)
                }
                dao.insertAll(entities)
            }
        }
    }

    fun toggle(id: String) {
        viewModelScope.launch { dao.toggleDone(id) }
    }

    fun remove(id: String) {
        viewModelScope.launch { dao.deleteById(id) }
    }

    fun update(id: String, patch: Todo.() -> Todo) {
        viewModelScope.launch {
            val entity = dao.getById(id) ?: return@launch
            val updatedTodo = entity.toTodo().patch()
            dao.update(updatedTodo.toEntity(sortOrder = entity.sortOrder))
        }
    }

    fun reorder(fromIdx: Int, toIdx: Int) {
        viewModelScope.launch {
            val list = dao.getAll()
            if (fromIdx !in list.indices || toIdx !in list.indices) return@launch
            val mutable = list.toMutableList()
            val item = mutable.removeAt(fromIdx)
            mutable.add(toIdx, item)
            val updates = mutable.mapIndexedNotNull { index, entity ->
                if (entity.sortOrder != index) entity.id to index else null
            }
            if (updates.isNotEmpty()) {
                dao.reorderAll(updates)
            }
        }
    }

    fun add(todo: Todo) {
        viewModelScope.launch {
            dao.insertAtTop(todo.toEntity(sortOrder = 0))
        }
    }
}
