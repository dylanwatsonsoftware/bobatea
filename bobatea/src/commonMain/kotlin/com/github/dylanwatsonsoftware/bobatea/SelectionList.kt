package com.github.dylanwatsonsoftware.bobatea

import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.YELLOW
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.color
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.DOWN
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.UP

class SelectionList(
    val question: String,
    val options: List<String>,
    override var padding: Int = 0,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.NONE,
    override var color: String? = null
) : BobaComponent(padding, margin, borderStyle, color) {
    var currentIndex = 0

    override fun render(): String {
        val content = StringBuilder()
        content.append(color(question, GREEN)).append("\n")
        options.forEachIndexed { index, item ->
            if (index == currentIndex) {
                content.append(color("❯ $item", YELLOW)).append("\n")
            } else {
                content.append("  $item").append("\n")
            }
        }
        content.append("\n")
        content.append("Use ${color("UP/DOWN", GREEN)} arrow keys to choose.\n")
        content.append("${color("SPACE/ENTER", GREEN)} to confirm")

        return wrapInBox(content.toString().trimEnd('\n'))
    }

    suspend fun interact(terminal: Terminal): String {
        val startLine = margin + (if (borderStyle != BorderStyle.NONE) 1 else 0) + padding

        fun printList() {
            terminal.clear()
            terminal.write(render() + "\n")
        }

        printList()

        terminal.enableMouseTracking()
        try {
            while (true) {
                when (val event = terminal.readEvent()) {
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
                            SPACE.key, ENTER.key -> {
                                val selected = options[currentIndex]
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
                                if (clickedIndex == currentIndex) {
                                    val selected = options[currentIndex]
                                    currentIndex = -1
                                    printList()
                                    return selected
                                } else {
                                    currentIndex = clickedIndex
                                    printList()
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            terminal.disableMouseTracking()
        }
    }
}
