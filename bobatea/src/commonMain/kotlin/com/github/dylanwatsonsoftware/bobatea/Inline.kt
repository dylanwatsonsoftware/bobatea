package com.github.dylanwatsonsoftware.bobatea

import kotlin.math.max
import kotlin.math.min

class Inline(
    val children: List<BobaComponent>,
    override var padding: Int = 0,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.NONE,
    override var color: String? = null,
    override var width: Dimension = Dimension.Auto,
    override var maxWidth: Dimension = Dimension.Auto,
    override var height: Dimension = Dimension.Auto,
    override var maxHeight: Dimension = Dimension.Auto
) : BobaComponent(padding, margin, borderStyle, color, width, maxWidth, height, maxHeight) {

    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val resolvedWidth = BobaComponent.resolveDimension(width, availableWidth)
        val resolvedMaxWidth = BobaComponent.resolveDimension(maxWidth, availableWidth)
        val resolvedHeight = BobaComponent.resolveDimension(height, availableHeight)
        val resolvedMaxHeight = BobaComponent.resolveDimension(maxHeight, availableHeight)

        val borderSize = if (borderStyle != BorderStyle.NONE) 2 else 0
        val horizontalTotal = padding * 2 + borderSize
        val verticalTotal = padding * 2 + borderSize

        val innerAvailableWidth = if (resolvedWidth != null) {
            max(0, resolvedWidth - horizontalTotal)
        } else if (resolvedMaxWidth != null) {
            max(0, resolvedMaxWidth - horizontalTotal)
        } else {
            availableWidth?.let { max(0, it - horizontalTotal) }
        }

        // Divide available width among children (simplified, could be more complex with weighted widths)
        val childAvailableWidth = innerAvailableWidth?.let { it / children.size }

        val renderedChildren = children.map { it.render(childAvailableWidth, null) }
        val childrenAsLines = renderedChildren.map { it.lines() }

        val maxChildHeight = childrenAsLines.maxOfOrNull { it.size } ?: 0

        val combinedLines = mutableListOf<String>()
        for (i in 0 until maxChildHeight) {
            val combinedLine = StringBuilder()
            childrenAsLines.forEachIndexed { index, childLines ->
                val line = if (i < childLines.size) childLines[i] else ""
                val childWidth = childrenAsLines[index].maxOfOrNull { BobaComponent.visibleLength(it) } ?: 0
                combinedLine.append(line)
                combinedLine.append(" ".repeat(max(0, childWidth - BobaComponent.visibleLength(line))))
            }
            combinedLines.add(combinedLine.toString())
        }

        val contentWidth = combinedLines.maxOfOrNull { BobaComponent.visibleLength(it) } ?: 0
        val contentHeight = combinedLines.size

        var finalWidth = resolvedWidth ?: (contentWidth + horizontalTotal)
        resolvedMaxWidth?.let { finalWidth = min(finalWidth, it) }

        var finalHeight = resolvedHeight ?: (contentHeight + verticalTotal)
        resolvedMaxHeight?.let { finalHeight = min(finalHeight, it) }

        val innerWidth = max(0, finalWidth - horizontalTotal)
        val innerHeight = max(0, finalHeight - verticalTotal)

        val content = combinedLines.take(innerHeight).joinToString("\n")

        return Box(
            content,
            padding = padding,
            margin = margin,
            borderStyle = borderStyle,
            color = color,
            width = Dimension.Fixed(finalWidth),
            height = Dimension.Fixed(finalHeight)
        ).render()
    }
}
