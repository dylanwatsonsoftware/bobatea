package com.github.dylanwatsonsoftware.bobatea

import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.clear
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.disableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.enableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.readEvent
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.YELLOW
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.color
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.DOWN
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.UP
import java.util.TreeSet

class MultiSelectionList(
    val question: String,
    val options: List<String>,
    override var padding: Int = 0,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.NONE,
    override var color: String? = null
) : BobaComponent(padding, margin, borderStyle, color) {
    var currentIndex = 0
    val selected = TreeSet<String>()

    override fun render(): String {
        val content = StringBuilder()
        content.append(color(question, GREEN)).append("\n")
        options.forEachIndexed { index, item ->
            val isSelected = selected.contains(item)
            val isCursorHighlighted = index == currentIndex

            val prefix =
                if (isSelected && isCursorHighlighted) {
                    "${color("[", YELLOW)}${color("✔", GREEN)}${color("]", YELLOW)}"
                } else if (isSelected) {
                    color(
                        " ✔ ",
                        GREEN,
                    )
                } else if (isCursorHighlighted) {
                    "[ ]"
                } else {
                    "   "
                }

            if (isCursorHighlighted) {
                content.append(color("$prefix ${color(item, YELLOW)}", YELLOW)).append("\n")
            } else {
                content.append("$prefix $item").append("\n")
            }
        }
        content.append("\n")
        content.append("Use ${color("UP/DOWN", GREEN)} arrow keys to choose.\n")
        content.append("Press ${color("SPACE", GREEN)} to toggle selection\n")
        content.append("${color("ENTER", GREEN)} to confirm")

        return wrapInBox(content.toString().trimEnd('\n'))
    }

    fun interact(): MutableSet<String> {
        val startLine = margin + (if (borderStyle != BorderStyle.NONE) 1 else 0) + padding

        fun printList() {
            clear()
            println(render())
        }

        printList()

        enableMouseTracking()
        try {
            while (true) {
                when (val event = readEvent()) {
                    is BobaEvent.Key -> {
                        when (event.code) {
                            UP.key -> {
                                currentIndex = (currentIndex - 1 + options.size) % options.size
                                printList()
                            }
                            DOWN.key -> {
                                currentIndex = (currentIndex + 1) % options.size
                                printList()
                            }
                            SPACE.key -> {
                                toggle(currentIndex)
                                printList()
                            }
                            ENTER.key -> {
                                currentIndex = -1
                                printList()
                                return selected
                            }
                        }
                    }
                    is BobaEvent.Mouse -> {
                        if (event.action == MouseAction.PRESS) {
                            val clickedIndex = event.y - startLine - 2
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
            disableMouseTracking()
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
