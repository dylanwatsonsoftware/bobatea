package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal

class Link(
    val text: String,
    val url: String,
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
        val styledText = TextStyles.hyperlink(url)(text)
        return wrapInBox(styledText, availableWidth, availableHeight)
    }
}
