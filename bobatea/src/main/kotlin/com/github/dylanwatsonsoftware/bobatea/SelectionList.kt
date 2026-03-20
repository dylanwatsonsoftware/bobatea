package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.clear
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.disableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.enableMouseTracking
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.readEvent
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.terminal
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
    override var style: TextStyle = TextStyle()
) : BobaComponent(padding, margin, borderStyle, style) {
    var currentIndex = 0

    override fun render(): String {
        val lines = mutableListOf<String>()
        lines.add(terminal.render(TextColors.green(question)))
        options.forEachIndexed { index, item ->
            if (index == currentIndex) {
                lines.add(terminal.render(TextColors.yellow("❯ $item")))
            } else {
                lines.add("  $item")
            }
        }
        lines.add("")
        lines.add(terminal.render(TextColors.green("Use UP/DOWN arrow keys to choose.")))
        lines.add(terminal.render(TextColors.green("SPACE/ENTER to confirm")))

        return wrapInBox(lines.joinToString("\n"))
    }

    fun interact(): String {
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
            disableMouseTracking()
        }
    }
}
