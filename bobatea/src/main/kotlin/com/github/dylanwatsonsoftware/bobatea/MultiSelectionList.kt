package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.TextColors
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.clear
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.disableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.enableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.readEvent
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.terminal
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
    override var style: TextStyle = TextStyle()
) : BobaComponent(padding, margin, borderStyle, style) {
    var currentIndex = 0
    val selected = TreeSet<String>()

    override fun render(): String {
        val lines = mutableListOf<String>()
        lines.add(terminal.render(TextColors.green(question)))
        options.forEachIndexed { index, item ->
            val isSelected = selected.contains(item)
            val isCursorHighlighted = index == currentIndex

            val prefix =
                if (isSelected && isCursorHighlighted) {
                    terminal.render(TextColors.yellow("[") + TextColors.green("✔") + TextColors.yellow("]"))
                } else if (isSelected) {
                    terminal.render(TextColors.green(" ✔ "))
                } else if (isCursorHighlighted) {
                    "[ ]"
                } else {
                    "   "
                }

            if (isCursorHighlighted) {
                lines.add(terminal.render(TextColors.yellow("$prefix $item")))
            } else {
                lines.add("$prefix $item")
            }
        }
        lines.add("")
        lines.add(terminal.render(TextColors.green("Use UP/DOWN arrow keys to choose.")))
        lines.add(terminal.render(TextColors.green("Press SPACE to toggle selection")))
        lines.add(terminal.render(TextColors.green("ENTER to confirm")))

        return wrapInBox(lines.joinToString("\n"))
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
