package xyz.teamgravity.claudecodetododemo.data

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import xyz.teamgravity.claudecodetododemo.ui.theme.*

enum class Priority { HIGH, MEDIUM, LOW }

enum class TagColor {
    WORK, ERRAND, PERSONAL, LEARN, HEALTH, HOME;

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

data class TagColorPair(val bg: Color, val fg: Color)

fun TagColor.colors(isDark: Boolean): TagColorPair = when (this) {
    TagColor.WORK -> if (isDark) TagColorPair(TagWorkBgDark, TagWorkFgDark) else TagColorPair(TagWorkBgLight, TagWorkFgLight)
    TagColor.ERRAND -> if (isDark) TagColorPair(TagErrandBgDark, TagErrandFgDark) else TagColorPair(TagErrandBgLight, TagErrandFgLight)
    TagColor.PERSONAL -> if (isDark) TagColorPair(TagPersonalBgDark, TagPersonalFgDark) else TagColorPair(TagPersonalBgLight, TagPersonalFgLight)
    TagColor.LEARN -> if (isDark) TagColorPair(TagLearnBgDark, TagLearnFgDark) else TagColorPair(TagLearnBgLight, TagLearnFgLight)
    TagColor.HEALTH -> if (isDark) TagColorPair(TagHealthBgDark, TagHealthFgDark) else TagColorPair(TagHealthBgLight, TagHealthFgLight)
    TagColor.HOME -> if (isDark) TagColorPair(TagHomeBgDark, TagHomeFgDark) else TagColorPair(TagHomeBgLight, TagHomeFgLight)
}

@Composable
fun TagColor.colors(): TagColorPair = colors(isSystemInDarkTheme())

data class Todo(
    val id: String,
    val title: String,
    val notes: String = "",
    val tag: String,
    val tagColor: TagColor,
    val priority: Priority,
    val due: String? = null,
    val done: Boolean = false,
)

val SAMPLE_TODOS = listOf(
    Todo("t1", "Design review with Kiera", "Bring the v3 mocks and the spec doc. Focus on the empty states.", "Work", TagColor.WORK, Priority.HIGH, "Today, 4:00 PM"),
    Todo("t2", "Pick up dry cleaning", "Two shirts, the navy suit. Closes at 7.", "Errand", TagColor.ERRAND, Priority.MEDIUM, "Today"),
    Todo("t3", "Reply to Marco about the lease", "", "Personal", TagColor.PERSONAL, Priority.HIGH, "Today, 6:00 PM"),
    Todo("t4", "Read chapter 4 of Designing Data-Intensive Apps", "Page 96 onwards.", "Learn", TagColor.LEARN, Priority.LOW, "Tomorrow"),
    Todo("t5", "Book the dentist", "Try Dr. Patel — friend recommended.", "Health", TagColor.HEALTH, Priority.MEDIUM, "Fri, May 9"),
    Todo("t6", "Water the monstera", "", "Home", TagColor.HOME, Priority.LOW, "Sat", done = true),
    Todo("t7", "Submit Q2 expenses", "Concur — receipts in Drive folder.", "Work", TagColor.WORK, Priority.MEDIUM, "Mon, May 12", done = true),
)
