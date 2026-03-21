package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal
import com.github.ajalt.mordant.widgets.Panel
import com.github.ajalt.mordant.widgets.Text
import com.github.ajalt.mordant.widgets.withPadding
import kotlin.math.max
import kotlin.math.min

enum class BorderStyle(val mordant: BorderType) {
    SINGLE(BorderType.SQUARE),
    DOUBLE(BorderType.DOUBLE),
    ROUNDED(BorderType.ROUNDED),
    NONE(BorderType.BLANK)
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
        val resolvedWidth = BobaComponent.resolveDimension(width, availableWidth)
        val resolvedMaxWidth = BobaComponent.resolveDimension(maxWidth, availableWidth)
        val resolvedHeight = BobaComponent.resolveDimension(height, availableHeight)
        val resolvedMaxHeight = BobaComponent.resolveDimension(maxHeight, availableHeight)

        var widget = Text(content).withPadding(padding)

        val border = if (borderStyle == BorderStyle.NONE && padding == 0) null else borderStyle.mordant
        val panel = Panel(
            content = widget,
            borderType = border,
            expand = resolvedWidth != null || width is Dimension.Percent
        )

        val renderWidth = resolvedWidth ?: availableWidth ?: 80

        val result = StringBuilder()
        repeat(margin) { result.append("\n") }

        // Handle dimensions via Mordant's render if possible, or manual wrapping
        // For now, let's use dummyTerminal to render it to string
        val rendered = getMordant().render(panel, width = renderWidth)

        // Manual cropping/sizing if needed
        val lines = rendered.lines()
        var finalLines = lines

        resolvedHeight?.let { h ->
            finalLines = finalLines.take(h)
        }
        resolvedMaxHeight?.let { h ->
            finalLines = finalLines.take(min(finalLines.size, h))
        }

        finalLines.forEach { line ->
            var finalLine = line
            if (finalLine.isNotBlank() || borderStyle != BorderStyle.NONE) {
                if (margin > 0) result.append(" ".repeat(margin))
                resolvedWidth?.let { w ->
                    val vLen = BobaComponent.visibleLength(finalLine)
                    if (vLen > w) {
                        // Truncate visible part correctly
                        var currentVisible = 0
                        val truncated = StringBuilder()
                        var j = 0
                        while (j < finalLine.length && currentVisible < w) {
                            if (finalLine[j] == '\u001b') {
                                val match = BobaComponent.ANSI_REGEX.find(finalLine, j)
                                if (match != null && match.range.first == j) {
                                    truncated.append(match.value)
                                    j = match.range.last + 1
                                    continue
                                }
                            }
                            truncated.append(finalLine[j])
                            currentVisible++
                            j++
                        }
                        finalLine = truncated.toString()
                    }
                }
                result.append(finalLine).append("\n")
            }
        }

        repeat(margin) { result.append("\n") }

        val output = result.toString().trimEnd('\n')
        return if (color != null) "$color$output${ConsoleColors.RESET}" else output
    }

    override fun toString(): String = render()
}
