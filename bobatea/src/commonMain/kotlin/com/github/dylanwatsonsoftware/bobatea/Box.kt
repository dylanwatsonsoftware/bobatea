package com.github.dylanwatsonsoftware.bobatea

import kotlin.math.max
import kotlin.math.min

enum class BorderStyle(
    val topLeft: Char,
    val topRight: Char,
    val bottomLeft: Char,
    val bottomRight: Char,
    val horizontal: Char,
    val vertical: Char
) {
    SINGLE('┌', '┐', '└', '┘', '─', '│'),
    DOUBLE('╔', '╗', '╚', '╝', '═', '║'),
    ROUNDED('╭', '╮', '╰', '╯', '─', '│'),
    NONE(' ', ' ', ' ', ' ', ' ', ' ')
}

class Box(
    private val content: String,
    override var padding: Int = 1,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.SINGLE,
    override var color: String? = null,
    override var width: Dimension = Dimension.Auto,
    override var maxWidth: Dimension = Dimension.Auto,
    override var height: Dimension = Dimension.Auto,
    override var maxHeight: Dimension = Dimension.Auto
) : BobaComponent(padding, margin, borderStyle, color, width, maxWidth, height, maxHeight) {

    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val lines = content.lines()
        val contentWidth = lines.maxOfOrNull { BobaComponent.visibleLength(it) } ?: 0
        val contentHeight = lines.size

        val resolvedWidth = BobaComponent.resolveDimension(width, availableWidth)
        val resolvedMaxWidth = BobaComponent.resolveDimension(maxWidth, availableWidth)
        val resolvedHeight = BobaComponent.resolveDimension(height, availableHeight)
        val resolvedMaxHeight = BobaComponent.resolveDimension(maxHeight, availableHeight)

        val borderSize = if (borderStyle != BorderStyle.NONE) 2 else 0
        val horizontalTotal = padding * 2 + borderSize
        val verticalTotal = padding * 2 + borderSize

        var finalWidth = resolvedWidth ?: (contentWidth + horizontalTotal)
        resolvedMaxWidth?.let { finalWidth = min(finalWidth, it) }

        var finalHeight = resolvedHeight ?: (contentHeight + verticalTotal)
        resolvedMaxHeight?.let { finalHeight = min(finalHeight, it) }

        val innerWidth = max(0, finalWidth - horizontalTotal)
        val innerHeight = max(0, finalHeight - verticalTotal)

        val result = StringBuilder()

        // Top margin
        repeat(margin) { result.append("\n") }

        if (borderStyle != BorderStyle.NONE) {
            // Top border
            result.append(" ".repeat(margin))
            result.append(borderStyle.topLeft)
            result.append(borderStyle.horizontal.toString().repeat(innerWidth + padding * 2))
            result.append(borderStyle.topRight)
            result.append("\n")
        }

        // Top padding
        repeat(padding) {
            result.append(" ".repeat(margin))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append(" ".repeat(innerWidth + padding * 2))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append("\n")
        }

        // Content
        for (i in 0 until innerHeight) {
            result.append(" ".repeat(margin))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append(" ".repeat(padding))

            if (i < lines.size) {
                val line = lines[i]
                val vLen = BobaComponent.visibleLength(line)
                if (vLen <= innerWidth) {
                    result.append(line)
                    result.append(" ".repeat(innerWidth - vLen))
                } else {
                    // Truncate line if it's too long
                    // This is tricky with ANSI codes, but for now simple truncation
                    // A better way would be to truncate the visible part
                    var currentVisible = 0
                    val truncated = StringBuilder()
                    var j = 0
                    while (j < line.length && currentVisible < innerWidth) {
                        if (line[j] == '\u001b') {
                            val match = BobaComponent.ANSI_REGEX.find(line, j)
                            if (match != null && match.range.first == j) {
                                truncated.append(match.value)
                                j = match.range.last + 1
                                continue
                            }
                        }
                        truncated.append(line[j])
                        currentVisible++
                        j++
                    }
                    result.append(truncated.toString())
                }
            } else {
                result.append(" ".repeat(innerWidth))
            }

            result.append(" ".repeat(padding))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append("\n")
        }

        // Bottom padding
        repeat(padding) {
            result.append(" ".repeat(margin))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append(" ".repeat(innerWidth + padding * 2))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append("\n")
        }

        if (borderStyle != BorderStyle.NONE) {
            // Bottom border
            result.append(" ".repeat(margin))
            result.append(borderStyle.bottomLeft)
            result.append(borderStyle.horizontal.toString().repeat(innerWidth + padding * 2))
            result.append(borderStyle.bottomRight)
            result.append("\n")
        }

        // Bottom margin
        repeat(margin) { result.append("\n") }

        val rendered = result.toString().trimEnd('\n')
        return if (color != null) ConsoleColors.color(rendered, color!!) else rendered
    }

    override fun toString(): String = render()
}
