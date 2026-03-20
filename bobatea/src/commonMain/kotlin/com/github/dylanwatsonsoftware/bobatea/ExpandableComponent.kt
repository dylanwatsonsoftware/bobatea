package com.github.dylanwatsonsoftware.bobatea

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
        val text = " $icon  $title "

        val coloredTitle = if (isHovered) {
            color(text, ConsoleColors.WHITE_BACKGROUND + ConsoleColors.BLUE_BOLD)
        } else {
            color(text, ConsoleColors.BLUE_BACKGROUND_BRIGHT + ConsoleColors.WHITE_BOLD)
        }

        result.append(coloredTitle).append("\n")
        if (expanded) {
            result.append(content).append("\n")
        }
        result.append("\n")
        result.append("Press ${color("SPACE/ENTER", GREEN)} or ${color("CLICK", GREEN)} to toggle\n")
        result.append("Press ${color("Q", GREEN)} to exit")

        return wrapInBox(result.toString().trimEnd('\n'))
    }

    suspend fun interact(terminal: Terminal) {
        val titleLine = margin + (if (borderStyle != BorderStyle.NONE) 1 else 0) + padding

        fun printExpandable() {
            terminal.clear()
            terminal.write(render() + "\n")
        }

        printExpandable()

        terminal.enableMouseTracking(allMotion = true)
        try {
            while (true) {
                when (val event = terminal.readEvent()) {
                    is BobaEvent.Key -> {
                        when (event.code) {
                            SPACE.key, ENTER.key -> {
                                expanded = !expanded
                                printExpandable()
                            }
                            'q'.code, 'Q'.code -> return
                        }
                    }
                    is BobaEvent.Mouse -> {
                        val currentlyHovered = event.y == titleLine + 1 && event.x <= title.length + 4
                        if (currentlyHovered != isHovered) {
                            isHovered = currentlyHovered
                            printExpandable()
                        }

                        if (event.action == MouseAction.PRESS && event.button < 64 && currentlyHovered) {
                            expanded = !expanded
                            printExpandable()
                        }
                    }
                }
            }
        } finally {
            terminal.disableMouseTracking()
        }
    }
}
