package xyz.teamgravity.claudecodetododemo.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TodoViewModel : ViewModel() {

    private val _todos = MutableStateFlow(SAMPLE_TODOS)
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    fun toggle(id: String) {
        _todos.update { list -> list.map { if (it.id == id) it.copy(done = !it.done) else it } }
    }

    fun remove(id: String) {
        _todos.update { list -> list.filter { it.id != id } }
    }

    fun update(id: String, patch: Todo.() -> Todo) {
        _todos.update { list -> list.map { if (it.id == id) it.patch() else it } }
    }

    fun reorder(fromIdx: Int, toIdx: Int) {
        _todos.update { list ->
            val mutable = list.toMutableList()
            val item = mutable.removeAt(fromIdx)
            mutable.add(toIdx, item)
            mutable
        }
    }

    fun add(todo: Todo) {
        _todos.update { list -> listOf(todo) + list }
    }
}
