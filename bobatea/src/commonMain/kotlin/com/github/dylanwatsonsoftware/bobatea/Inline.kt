package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal
import com.github.ajalt.mordant.widgets.Text
import com.github.ajalt.mordant.table.table
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
        val resolvedHeight = BobaComponent.resolveDimension(height, availableHeight)

        val borderSize = if (borderStyle != BorderStyle.NONE) 2 else 0
        val horizontalTotal = padding * 2 + borderSize

        val innerAvailableWidth = (resolvedWidth ?: availableWidth)?.let { max(0, it - horizontalTotal) }
        val childAvailableWidth = if (children.isNotEmpty()) {
            innerAvailableWidth?.let { it / children.size }
        } else {
            null
        }

        val t = table {
            borderType = BorderType.BLANK
            padding(0)
            body {
                row {
                    children.forEach { child ->
                        cell(Text(child.render(childAvailableWidth, resolvedHeight)))
                    }
                }
            }
        }

        val content = getMordant().render(t)
        return wrapInBox(content, availableWidth, availableHeight)
    }
}
