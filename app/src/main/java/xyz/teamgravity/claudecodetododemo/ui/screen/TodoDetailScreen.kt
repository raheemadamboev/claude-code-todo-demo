package xyz.teamgravity.claudecodetododemo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import xyz.teamgravity.claudecodetododemo.data.*
import xyz.teamgravity.claudecodetododemo.ui.component.AnimatedCheckbox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todo: Todo,
    onBack: () -> Unit,
    onToggle: () -> Unit,
    onUpdate: (Todo.() -> Todo) -> Unit,
    onDelete: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    var editedTitle by remember(todo.id) { mutableStateOf(todo.title) }
    var editedNotes by remember(todo.id) { mutableStateOf(todo.notes) }

    // Tag picker state
    var showTagPicker by remember { mutableStateOf(false) }
    var showPriorityPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        // Save edits before navigating back
                        onUpdate { copy(title = editedTitle, notes = editedNotes) }
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = "More options",
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                    onBack()
                                },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Delete, contentDescription = null)
                                },
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Hero block
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                ) {
                    AnimatedCheckbox(
                        checked = todo.done,
                        onCheckedChange = { onToggle() },
                        size = 28.dp,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            textDecoration = if (todo.done) TextDecoration.LineThrough else TextDecoration.None,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                    )
                }

                // Meta chip row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                ) {
                    // Tag chip
                    val tagColors = todo.tagColor.colors()
                    Box {
                        AssistChip(
                            onClick = { showTagPicker = !showTagPicker },
                            label = { Text(todo.tag) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = tagColors.bg,
                                labelColor = tagColors.fg,
                            ),
                            border = null,
                        )
                        DropdownMenu(
                            expanded = showTagPicker,
                            onDismissRequest = { showTagPicker = false },
                        ) {
                            TagColor.entries.forEach { tag ->
                                DropdownMenuItem(
                                    text = { Text(tag.displayName) },
                                    onClick = {
                                        onUpdate { copy(tag = tag.displayName, tagColor = tag) }
                                        showTagPicker = false
                                    },
                                )
                            }
                        }
                    }

                    // Priority chip
                    Box {
                        AssistChip(
                            onClick = { showPriorityPicker = !showPriorityPicker },
                            label = {
                                Text(todo.priority.name.lowercase().replaceFirstChar { it.uppercase() })
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Flag,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = when (todo.priority) {
                                        Priority.HIGH -> MaterialTheme.colorScheme.error
                                        Priority.MEDIUM -> androidx.compose.ui.graphics.Color(0xFFFF8F00)
                                        Priority.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                )
                            },
                        )
                        DropdownMenu(
                            expanded = showPriorityPicker,
                            onDismissRequest = { showPriorityPicker = false },
                        ) {
                            Priority.entries.forEach { priority ->
                                DropdownMenuItem(
                                    text = {
                                        Text(priority.name.lowercase().replaceFirstChar { it.uppercase() })
                                    },
                                    onClick = {
                                        onUpdate { copy(priority = priority) }
                                        showPriorityPicker = false
                                    },
                                )
                            }
                        }
                    }

                    // Due date chip
                    todo.due?.let { due ->
                        AssistChip(
                            onClick = { },
                            label = { Text(due) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Notes section
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "NOTES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedNotes,
                        onValueChange = { editedNotes = it },
                        placeholder = { Text("Add notes…") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Activity section
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "ACTIVITY",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Created 2 days ago",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Last edited just now",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Bottom action row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilledTonalButton(
                    onClick = {
                        onToggle()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    shape = RoundedCornerShape(28.dp),
                ) {
                    Text(if (todo.done) "Mark incomplete" else "Mark complete")
                }

                TextButton(
                    onClick = {
                        onDelete()
                        onBack()
                    },
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
