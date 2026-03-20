package com.github.dylanwatsonsoftware.bobatea

import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.clear
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.disableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.enableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.readEvent
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
    override var color: String? = null
) : BobaComponent(padding, margin, borderStyle, color) {
    var expanded = false
    var isHovered = false

    override fun render(): String {
        val result = StringBuilder()
        val icon = if (expanded) "▼" else "▶"
        val text = " $icon $title "

        val background = if (isHovered) ConsoleColors.CYAN_BACKGROUND else ConsoleColors.BLUE_BACKGROUND
        val coloredTitle = color(text, background + ConsoleColors.WHITE_BOLD)

        result.append(coloredTitle).append("\n")
        if (expanded) {
            result.append(content).append("\n")
        }
        result.append("\n")
        result.append("Press ${color("SPACE/ENTER", GREEN)} or ${color("CLICK", GREEN)} to toggle\n")
        result.append("Press ${color("Q", GREEN)} to exit")

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
                        // Offset by 1 for mouse vs 0-indexed string logic if needed,
                        // but event.x is usually 1-indexed.
                        val currentlyHovered = event.y == titleLine + 1 && event.x <= title.length + 4
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
