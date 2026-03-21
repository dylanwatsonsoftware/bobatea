package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles

class BackButton(
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
        val text = " [ Back (q) ] "
        val styled = (TextColors.red.bg + TextColors.white + TextStyles.bold)(text)
        return wrapInBox(styled, availableWidth, availableHeight)
    }

    fun isClicked(event: BobaEvent.Mouse, yOffset: Int): Boolean {
        // Simple hit detection for the back button
        // Assuming it's rendered at the bottom or a specific spot
        return event.action == MouseAction.PRESS && event.y >= yOffset && event.y <= yOffset + 2
    }
}
