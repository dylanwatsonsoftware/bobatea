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
    override var maxHeight: Dimension = Dimension.Auto,
    val onClicked: () -> Unit = {}
) : BobaComponent(padding, margin, borderStyle, color, width, maxWidth, height, maxHeight) {

    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val text = " [ ← Back (q) ] "
        val styled = (TextColors.red.bg + TextColors.white + TextStyles.bold)(text)
        val result = wrapInBox(styled, availableWidth, availableHeight)
        val lines = result.lines()
        widthPx = lines.maxOfOrNull { visibleLength(it) } ?: 0
        heightPx = lines.size
        return result
    }

    override fun onEvent(event: BobaEvent): Boolean {
        if (event is BobaEvent.Key && (event.code == 'q'.code || event.code == 'Q'.code)) {
            onClicked()
            return true
        }
        if (event is BobaEvent.Mouse && event.action == MouseAction.PRESS) {
            val textLength = 16 // " [ ← Back (q) ] ".length
            val startX = getContentStartX()
            val startY = getContentStartY()
            if (event.y == startY && event.x >= startX && event.x < startX + textLength) {
                onClicked()
                return true
            }
        }
        return false
    }
}
