package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.yellow
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE

class MultiSelectionList(
    val question: String,
    val options: List<String>,
    override var padding: Int = 0,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.NONE,
    override var color: String? = null,
    override var width: Dimension = Dimension.Auto,
    override var maxWidth: Dimension = Dimension.Auto,
    override var height: Dimension = Dimension.Auto,
    override var maxHeight: Dimension = Dimension.Auto
) : BobaComponent(padding, margin, borderStyle, color, width, maxWidth, height, maxHeight) {
    var currentIndex = 0
    val selected = mutableSetOf<String>()

    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val content = StringBuilder()
        content.append(green(question)).append("\n")
        options.forEachIndexed { index, item ->
            val isSelected = selected.contains(item)
            val isCursorHighlighted = index == currentIndex

            val prefix =
                if (isSelected && isCursorHighlighted) {
                    "${yellow("[")}${green("✔")}${yellow("]")}"
                } else if (isSelected) {
                    green(" ✔ ")
                } else if (isCursorHighlighted) {
                    "[ ]"
                } else {
                    "   "
                }

            if (isCursorHighlighted) {
                content.append(yellow("$prefix ${yellow(item)}")).append("\n")
            } else {
                content.append("$prefix $item").append("\n")
            }
        }
        content.append("\n")
        content.append("Use ${green("UP/DOWN")} or ${green("W/S")} keys to choose.\n")
        content.append("Press ${green("SPACE")} to toggle selection\n")
        content.append("${green("ENTER")} or ${green("Q")} to confirm")

        return wrapInBox(content.toString().trimEnd('\n'), availableWidth, availableHeight)
    }

    suspend fun interact(terminal: Terminal): MutableSet<String> {
        val (availableWidth, availableHeight) = terminal.size()
        val startLine = margin + (if (borderStyle != BorderStyle.NONE) 1 else 0) + padding + 1 // +1 for the question line

        fun printList() {
            terminal.redraw(render(availableWidth, availableHeight) + "\n")
        }

        printList()

        terminal.enableMouseTracking()
        try {
            while (true) {
                when (val event = terminal.readEvent()) {
                    is BobaEvent.Key -> {
                        when {
                            KeyCodes.isUp(event.code) -> {
                                currentIndex = (currentIndex - 1 + options.size) % options.size
                                printList()
                            }
                            KeyCodes.isDown(event.code) -> {
                                currentIndex = (currentIndex + 1) % options.size
                                printList()
                            }
                            event.code == SPACE.key -> {
                                toggle(currentIndex)
                                printList()
                            }
                            event.code == ENTER.key || event.code == 'q'.code || event.code == 'Q'.code -> {
                                currentIndex = -1
                                printList()
                                return selected
                            }
                        }
                    }
                    is BobaEvent.Mouse -> {
                        if (event.action == MouseAction.PRESS) {
                            val clickedIndex = event.y - startLine
                            if (clickedIndex in options.indices) {
                                currentIndex = clickedIndex
                                toggle(currentIndex)
                                printList()
                            }
                        }
                    }
                }
            }
        } finally {
            terminal.disableMouseTracking()
        }
    }

    private fun toggle(index: Int) {
        if (selected.contains(options[index])) {
            selected.remove(options[index])
        } else {
            selected.add(options[index])
        }
    }
}
