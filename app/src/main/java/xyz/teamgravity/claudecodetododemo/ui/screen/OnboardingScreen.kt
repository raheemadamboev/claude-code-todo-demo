package xyz.teamgravity.claudecodetododemo.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.FilterCenterFocus
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val body: String,
    val icon: ImageVector,
    val containerColor: @Composable () -> androidx.compose.ui.graphics.Color,
    val onContainerColor: @Composable () -> androidx.compose.ui.graphics.Color,
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = "Capture every thought",
            body = "Quick-add tasks with tags, due dates and notes — without breaking your flow.",
            icon = Icons.AutoMirrored.Outlined.NoteAdd,
            containerColor = { MaterialTheme.colorScheme.primaryContainer },
            onContainerColor = { MaterialTheme.colorScheme.onPrimaryContainer },
        ),
        OnboardingPage(
            title = "Stay on top of your day",
            body = "Swipe to complete, drag to reorder, pull to refresh. The list adapts to you.",
            icon = Icons.Outlined.Checklist,
            containerColor = { MaterialTheme.colorScheme.tertiaryContainer },
            onContainerColor = { MaterialTheme.colorScheme.onTertiaryContainer },
        ),
        OnboardingPage(
            title = "Focus when it matters",
            body = "Priorities, categories and clean details so the next step is always obvious.",
            icon = Icons.Outlined.FilterCenterFocus,
            containerColor = { MaterialTheme.colorScheme.secondaryContainer },
            onContainerColor = { MaterialTheme.colorScheme.onSecondaryContainer },
        ),
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // Skip button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (!isLastPage) {
                    TextButton(onClick = onComplete) {
                        Text(
                            text = "Skip",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                val pageData = pages[page]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                ) {
                    // Illustration placeholder
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(280.dp)
                            .clip(CircleShape)
                            .background(pageData.containerColor()),
                    ) {
                        Icon(
                            imageVector = pageData.icon,
                            contentDescription = null,
                            tint = pageData.onContainerColor(),
                            modifier = Modifier.size(120.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = pageData.title,
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = pageData.body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                    )
                }
            }

            // Pager dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
            ) {
                repeat(pages.size) { index ->
                    val isActive = pagerState.currentPage == index
                    val width = animateDpAsState(
                        targetValue = if (isActive) 24.dp else 8.dp,
                        animationSpec = tween(200),
                        label = "dot_width",
                    )
                    val color = animateColorAsState(
                        targetValue = if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.32f),
                        animationSpec = tween(200),
                        label = "dot_color",
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width.value)
                            .clip(CircleShape)
                            .background(color.value),
                    )
                }
            }

            // Primary button
            Button(
                onClick = {
                    if (isLastPage) {
                        onComplete()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = if (isLastPage) "Get started" else "Next",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
