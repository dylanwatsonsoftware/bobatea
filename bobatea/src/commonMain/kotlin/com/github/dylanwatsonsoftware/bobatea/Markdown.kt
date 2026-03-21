package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.markdown.Markdown as MordantMarkdown
import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal

class Markdown(
    val markdown: String,
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
        val widget = MordantMarkdown(markdown)
        val rendered = getMordant().render(widget, width = (resolvedWidth ?: availableWidth ?: 80) - padding * 2)
        return wrapInBox(rendered.trimEnd('\n'), availableWidth, availableHeight)
    }
}
