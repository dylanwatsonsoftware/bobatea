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

        val borderSizeVal = if (this.borderStyle != BorderStyle.NONE) 2 else 0
        val horizontalTotal = this.padding * 2 + borderSizeVal

        val innerAvailableWidth = (resolvedWidth ?: availableWidth)?.let { max(0, it - horizontalTotal) }
        val childAvailableWidth = if (children.isNotEmpty()) {
            innerAvailableWidth?.let { it / children.size }
        } else {
            null
        }

        var currentX = this.x + this.padding + (if (this.borderStyle != BorderStyle.NONE) 1 else 0)
        val t = table {
            borderType = BorderType.BLANK
            padding(0)
            body {
                row {
                    children.forEach { child ->
                        val renderedChild = child.render(childAvailableWidth, resolvedHeight)
                        val lines = renderedChild.lines()

                        child.x = currentX
                        child.y = this@Inline.y + this@Inline.padding + (if (this@Inline.borderStyle != BorderStyle.NONE) 1 else 0)
                        child.widthPx = lines.maxOfOrNull { visibleLength(it) } ?: 0
                        child.heightPx = lines.size

                        currentX += child.widthPx
                        cell(Text(renderedChild))
                    }
                }
            }
        }

        val content = getMordant().render(t)
        val wrapped = wrapInBox(content, availableWidth, availableHeight)
        val wrappedLines = wrapped.lines()
        widthPx = wrappedLines.maxOfOrNull { visibleLength(it) } ?: 0
        heightPx = wrappedLines.size
        return wrapped
    }

    override fun onEvent(event: BobaEvent): Boolean {
        for (child in children) {
            if (child.onEvent(event)) return true
        }
        return super.onEvent(event)
    }

    override fun tick(deltaMs: Long) {
        children.forEach { it.tick(deltaMs) }
    }
}
