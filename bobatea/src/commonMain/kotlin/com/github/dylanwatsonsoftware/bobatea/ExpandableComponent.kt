package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.color
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE

class ExpandableComponent(
    val title: String,
    val content: String,
    override var padding: Int = 0,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.NONE,
    override var color: String? = null,
    override var width: Dimension = Dimension.Auto,
    override var maxWidth: Dimension = Dimension.Auto,
    override var height: Dimension = Dimension.Auto,
    override var maxHeight: Dimension = Dimension.Auto
) : BobaComponent(padding, margin, borderStyle, color, width, maxWidth, height, maxHeight) {
    var expanded = false
    var isHovered = false

    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val result = StringBuilder()
        val icon = if (expanded) "▼" else "▶"
        val text = " $icon  $title "

        val coloredTitle = if (isHovered) {
            (TextColors.white.bg + TextColors.blue + TextStyles.bold)(text)
        } else {
            (TextColors.brightBlue.bg + TextColors.white + TextStyles.bold)(text)
        }

        result.append(coloredTitle).append("\n")
        if (expanded) {
            result.append(content).append("\n")
        }
        result.append("\n")
        result.append("Press ${color("SPACE/ENTER", GREEN)} or ${color("CLICK", GREEN)} to toggle\n")
        result.append("Press ${color("Q", GREEN)} to exit")

        val output = wrapInBox(result.toString().trimEnd('\n'), availableWidth, availableHeight)
        val lines = output.lines()
        widthPx = lines.maxOfOrNull { visibleLength(it) } ?: 0
        heightPx = lines.size
        return output
    }

    override fun onEvent(event: BobaEvent): Boolean {
        val titleLine = y + (if (borderStyle != BorderStyle.NONE) 1 else 0) + padding
        when (event) {
            is BobaEvent.Key -> {
                when (event.code) {
                    SPACE.key, ENTER.key -> {
                        expanded = !expanded
                        return true
                    }
                }
            }
            is BobaEvent.Mouse -> {
                val currentlyHovered = event.y == titleLine + 1 && event.x >= x && event.x <= x + title.length + 4
                if (currentlyHovered != isHovered) {
                    isHovered = currentlyHovered
                    // return true to re-render?
                }

                if (event.action == MouseAction.PRESS && event.button < 64 && currentlyHovered) {
                    expanded = !expanded
                    return true
                }
            }
        }
        return false
    }
}
