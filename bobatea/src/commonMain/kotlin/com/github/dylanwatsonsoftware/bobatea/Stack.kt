package com.github.dylanwatsonsoftware.bobatea

import kotlin.math.max
import kotlin.math.min

class Stack(
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

        // If width is auto, we need to know children's widths to determine our own width
        // but children might also be auto.
        // For Stack, children typically take the full width available.

        val innerAvailableWidth = if (resolvedWidth != null) {
            max(0, resolvedWidth - horizontalTotal)
        } else if (resolvedMaxWidth != null) {
            max(0, resolvedMaxWidth - horizontalTotal)
        } else {
            availableWidth?.let { max(0, it - horizontalTotal) }
        }

        val renderedChildren = children.map { it.render(innerAvailableWidth, null) }
        val childrenLines = renderedChildren.flatMap { it.lines() }

        val contentWidth = childrenLines.maxOfOrNull { BobaComponent.visibleLength(it) } ?: 0
        val contentHeight = childrenLines.size

        var finalWidth = resolvedWidth ?: (contentWidth + horizontalTotal)
        resolvedMaxWidth?.let { finalWidth = min(finalWidth, it) }

        var finalHeight = resolvedHeight ?: (contentHeight + verticalTotal)
        resolvedMaxHeight?.let { finalHeight = min(finalHeight, it) }

        val innerWidth = max(0, finalWidth - horizontalTotal)
        val innerHeight = max(0, finalHeight - verticalTotal)

        val content = childrenLines.take(innerHeight).joinToString("\n")

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
