package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.clear
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.disableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.enableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.readEvent
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.terminal
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE

class ExpandableComponent(
    val title: String,
    val content: String,
    override var padding: Int = 0,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.NONE,
    override var style: TextStyle = TextStyle()
) : BobaComponent(padding, margin, borderStyle, style) {
    var expanded = false
    var isHovered = false

    override fun render(): String {
        val result = StringBuilder()
        val icon = if (expanded) "▼" else "▶"
        val text = " $icon $title "

        val background = if (isHovered) TextColors.cyan.bg else TextColors.blue.bg
        val coloredTitleStyle = background + TextColors.white + TextStyles.bold

        result.append(terminal.render(coloredTitleStyle(text))).append("\n")
        if (expanded) {
            result.append(content).append("\n")
        }
        result.append("\n")
        result.append(terminal.render(TextColors.green("Press SPACE/ENTER or CLICK to toggle"))).append("\n")
        result.append(terminal.render(TextColors.green("Press Q to exit")))

        return wrapInBox(result.toString().trimEnd('\n'))
    }

    fun interact() {
        val titleLine = margin + (if (borderStyle != BorderStyle.NONE) 1 else 0) + padding

        fun printExpandable() {
            clear()
            println(render())
        }

        printExpandable()

        enableMouseTracking(allMotion = true)
        try {
            while (true) {
                when (val event = readEvent()) {
                    is BobaEvent.Key -> {
                        when (event.code) {
                            SPACE.key, ENTER.key -> {
                                expanded = !expanded
                                printExpandable()
                            }
                            'q'.toInt(), 'Q'.toInt() -> return
                        }
                    }
                    is BobaEvent.Mouse -> {
                        val iconWidth = 3 // " ▶ " or " ▼ "
                        val titleTextWidth = title.length

                        val currentlyHovered = event.y == titleLine + 1 && event.x <= titleTextWidth + iconWidth + 1
                        if (currentlyHovered != isHovered) {
                            isHovered = currentlyHovered
                            printExpandable()
                        }

                        if (event.action == MouseAction.PRESS && isHovered) {
                            expanded = !expanded
                            printExpandable()
                        }
                    }
                }
            }
        } finally {
            disableMouseTracking()
        }
    }
}
