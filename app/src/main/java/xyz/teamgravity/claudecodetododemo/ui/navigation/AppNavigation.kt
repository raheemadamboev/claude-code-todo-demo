package xyz.teamgravity.claudecodetododemo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xyz.teamgravity.claudecodetododemo.data.TodoViewModel
import xyz.teamgravity.claudecodetododemo.ui.screen.*

object Routes {
    const val ONBOARDING = "onboarding"
    const val TODO_LIST = "todo_list"
    const val TODO_DETAIL = "todo_detail/{todoId}"
    const val SUPPORT = "support"

    fun todoDetail(id: String) = "todo_detail/$id"
}

@Composable
fun AppNavigation(
    startDestination: String,
    onOnboardingComplete: () -> Unit,
    viewModel: TodoViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val todos by viewModel.todos.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onComplete = {
                    onOnboardingComplete()
                    navController.navigate(Routes.TODO_LIST) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.TODO_LIST) {
            TodoListScreen(
                todos = todos,
                onToggle = viewModel::toggle,
                onRemove = viewModel::remove,
                onAdd = viewModel::add,
                onTodoClick = { id ->
                    navController.navigate(Routes.todoDetail(id))
                },
                onSupportClick = {
                    navController.navigate(Routes.SUPPORT)
                },
            )
        }

        composable(Routes.TODO_DETAIL) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId") ?: return@composable
            val todo = todos.find { it.id == todoId } ?: run {
                navController.popBackStack()
                return@composable
            }

            TodoDetailScreen(
                todo = todo,
                onBack = { navController.popBackStack() },
                onToggle = { viewModel.toggle(todoId) },
                onUpdate = { patch -> viewModel.update(todoId, patch) },
                onDelete = { viewModel.remove(todoId) },
            )
        }

        composable(Routes.SUPPORT) {
            SupportScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
