package xyz.teamgravity.claudecodetododemo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.claudecodetododemo.ui.navigation.AppNavigation
import xyz.teamgravity.claudecodetododemo.ui.navigation.Routes
import xyz.teamgravity.claudecodetododemo.ui.theme.ClaudeCodeToDoDemoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("todo_app", Context.MODE_PRIVATE)
        val onboarded = prefs.getBoolean("onboarded", false)

        setContent {
            ClaudeCodeToDoDemoTheme {
                AppNavigation(
                    startDestination = if (onboarded) Routes.TODO_LIST else Routes.ONBOARDING,
                    onOnboardingComplete = {
                        prefs.edit().putBoolean("onboarded", true).apply()
                    },
                )
            }
        }
    }
}
