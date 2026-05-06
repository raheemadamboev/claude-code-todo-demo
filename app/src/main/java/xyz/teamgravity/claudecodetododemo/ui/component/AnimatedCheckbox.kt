package xyz.teamgravity.claudecodetododemo.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedColor: Color = MaterialTheme.colorScheme.outline,
) {
    val fillColor by animateColorAsState(
        targetValue = if (checked) checkedColor else Color.Transparent,
        animationSpec = tween(220),
        label = "fill",
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) checkedColor else uncheckedColor,
        animationSpec = tween(220),
        label = "border",
    )
    val checkScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "check_scale",
    )
    val checkAlpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(180),
        label = "check_alpha",
    )

    Canvas(
        modifier = modifier
            .size(size + 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onCheckedChange(!checked) },
    ) {
        val center = this.center
        val radius = (size.toPx()) / 2f

        // Fill
        drawCircle(
            color = fillColor,
            radius = radius,
            center = center,
        )

        // Border
        drawCircle(
            color = borderColor,
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx()),
        )

        // Check mark
        if (checkAlpha > 0f) {
            val strokeWidth = 2.5.dp.toPx()
            val scaledRadius = radius * checkScale
            val checkSize = scaledRadius * 0.6f

            val startX = center.x - checkSize * 0.6f
            val startY = center.y + checkSize * 0.05f
            val midX = center.x - checkSize * 0.1f
            val midY = center.y + checkSize * 0.5f
            val endX = center.x + checkSize * 0.7f
            val endY = center.y - checkSize * 0.4f

            drawLine(
                color = Color.White.copy(alpha = checkAlpha),
                start = Offset(startX, startY),
                end = Offset(midX, midY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = Color.White.copy(alpha = checkAlpha),
                start = Offset(midX, midY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}
