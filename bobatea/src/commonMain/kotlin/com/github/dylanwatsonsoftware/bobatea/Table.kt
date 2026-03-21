package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal
import com.github.ajalt.mordant.widgets.Text

class Table(
    val headers: List<String>,
    val rows: List<List<String>>,
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
        val innerAvailableWidth = (resolvedWidth ?: availableWidth ?: 80) - padding * 2

        val t = table {
            header {
                row(*headers.toTypedArray())
            }
            body {
                rows.forEach { r ->
                    row(*r.toTypedArray())
                }
            }
        }

        val rendered = getMordant().render(t, width = innerAvailableWidth)
        return wrapInBox(rendered.trimEnd('\n'), availableWidth, availableHeight)
    }
}
