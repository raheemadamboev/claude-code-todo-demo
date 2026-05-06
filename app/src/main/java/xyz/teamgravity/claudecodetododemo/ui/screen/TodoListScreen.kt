package xyz.teamgravity.claudecodetododemo.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.teamgravity.claudecodetododemo.data.*
import xyz.teamgravity.claudecodetododemo.ui.component.AnimatedCheckbox
import java.util.UUID
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    todos: List<Todo>,
    onToggle: (String) -> Unit,
    onRemove: (String) -> Unit,
    onAdd: (Todo) -> Unit,
    onTodoClick: (String) -> Unit,
    onSupportClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAddSheet by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    val doneCount = todos.count { it.done }
    val totalCount = todos.size
    val todoCount = totalCount - doneCount

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Today")
                        Text(
                            text = "$todoCount of $totalCount to do",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = onSupportClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = "Help",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(28.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                scope.launch {
                    delay(800)
                    isRefreshing = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = todos,
                    key = { _, todo -> todo.id },
                ) { _, todo ->
                    SwipeableTodoRow(
                        todo = todo,
                        onToggle = { onToggle(todo.id) },
                        onClick = { onTodoClick(todo.id) },
                        onDelete = {
                            val removed = todo
                            onRemove(todo.id)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Task deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short,
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    onAdd(removed)
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    if (showAddSheet) {
        AddTodoBottomSheet(
            onDismiss = { showAddSheet = false },
            onAdd = { todo ->
                onAdd(todo)
                showAddSheet = false
            },
        )
    }
}

@Composable
private fun SwipeableTodoRow(
    todo: Todo,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
    ) {
        // Swipe background
        val swipeColor by animateColorAsState(
            targetValue = when {
                offsetX > 0 -> MaterialTheme.colorScheme.primaryContainer
                offsetX < 0 -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceContainer
            },
            animationSpec = tween(100),
            label = "swipe_bg",
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(swipeColor, RoundedCornerShape(20.dp))
                .padding(horizontal = 24.dp),
        ) {
            if (offsetX > 0) {
                Text(
                    text = "Complete",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            } else if (offsetX < 0) {
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }
        }

        // Foreground card
        val maxSwipe = with(density) { 300.dp.toPx() }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val threshold = maxSwipe * 0.6f
                            when {
                                offsetX > threshold -> {
                                    onToggle()
                                    offsetX = 0f
                                }
                                offsetX < -threshold -> {
                                    onDelete()
                                    offsetX = 0f
                                }
                                else -> offsetX = 0f
                            }
                        },
                        onDragCancel = { offsetX = 0f },
                    ) { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceIn(-maxSwipe, maxSwipe)
                    }
                }
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 16.dp),
            ) {
                AnimatedCheckbox(
                    checked = todo.done,
                    onCheckedChange = { onToggle() },
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        ),
                        color = if (todo.done) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (todo.done) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Tag chip
                        val tagColors = todo.tagColor.colors()
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(tagColors.bg)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = todo.tag,
                                style = MaterialTheme.typography.bodySmall,
                                color = tagColors.fg,
                            )
                        }

                        // Due date pill
                        todo.due?.let { due ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = due,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        // Priority flag
                        if (todo.priority != Priority.LOW) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Flag,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = when (todo.priority) {
                                        Priority.HIGH -> MaterialTheme.colorScheme.error
                                        Priority.MEDIUM -> androidx.compose.ui.graphics.Color(0xFFFF8F00)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = todo.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTodoBottomSheet(
    onDismiss: () -> Unit,
    onAdd: (Todo) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf(TagColor.WORK) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Add task",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tag selector
            Text(
                text = "TAG",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                TagColor.entries.forEach { tag ->
                    val tagColors = tag.colors()
                    FilterChip(
                        selected = selectedTag == tag,
                        onClick = { selectedTag = tag },
                        label = { Text(tag.displayName, style = MaterialTheme.typography.bodySmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tagColors.bg,
                            selectedLabelColor = tagColors.fg,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Priority selector
            Text(
                text = "PRIORITY",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = {
                            Text(
                                priority.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall,
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Due date") },
                placeholder = { Text("e.g. Today, Tomorrow") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            Todo(
                                id = UUID.randomUUID().toString(),
                                title = title.trim(),
                                tag = selectedTag.displayName,
                                tagColor = selectedTag,
                                priority = selectedPriority,
                                due = dueDate.ifBlank { null },
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = title.isNotBlank(),
            ) {
                Text("Add task")
            }
        }
    }
}
