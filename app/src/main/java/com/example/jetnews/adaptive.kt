package com.example.jetnews

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    topPadding: Dp = 0.dp,
    itemSpacing: Dp = 4.dp,
    itemMaxWidth: Dp = 450.dp,
    multipleColumnsBreakPoint: Dp = 600.dp,
    content: @Composable () -> Unit,
) {
    Layout(content, modifier) { measurables, outerConstraints ->
        val multipleColumnsBreakPointPx: Int = multipleColumnsBreakPoint.roundToPx()
        val topPaddingPx: Int = topPadding.roundToPx()
        val itemSpacingPx: Int = itemSpacing.roundToPx()
        val itemMaxWidthPx: Int = itemMaxWidth.roundToPx()

        val columns: Int = if (outerConstraints.maxWidth < multipleColumnsBreakPointPx) 1 else 2
        // Max width for each item taking into account available space, spacing and `itemMaxWidth`
        val itemWidth: Int = if (columns == 1) {
            outerConstraints.maxWidth
        } else {
            val maxWidthWithSpaces: Int = outerConstraints.maxWidth - (columns - 1) * itemSpacingPx
            (maxWidthWithSpaces / columns).coerceIn(0, itemMaxWidthPx)
        }
        val itemConstraints: Constraints = outerConstraints.copy(maxWidth = itemWidth)

        val rowHeights: IntArray = IntArray(measurables.size / columns + 1)
        val placeables: List<Placeable> = measurables.mapIndexed { index, measureable ->
            val placeable: Placeable = measureable.measure(itemConstraints)
            val row: Int = index.floorDiv(columns)
            rowHeights[row] = max(rowHeights[row], placeable.height)
            placeable
        }

        val layoutHeight: Int = topPaddingPx + rowHeights.sum()
        val layoutWidth: Int = itemWidth * columns + (itemSpacingPx * (columns - 1))

        layout(width = outerConstraints.constrainWidth(layoutWidth), height = outerConstraints.constrainHeight(layoutHeight)) {
            var yPosition: Int = topPaddingPx
            placeables.chunked(columns).forEachIndexed { rowIndex, row ->
                var xPosition: Int = 0
                for (placeable in row) {
                    placeable.placeRelative(x = xPosition, y = yPosition)
                    xPosition += placeable.width + itemSpacingPx
                }
                yPosition += rowHeights[rowIndex]
            }
        }
    }
}
