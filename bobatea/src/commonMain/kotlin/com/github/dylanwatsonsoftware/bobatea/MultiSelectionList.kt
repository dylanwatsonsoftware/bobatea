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
    override var maxHeight: Dimension = Dimension.Auto,
    val onComplete: (Set<String>) -> Unit = {}
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

        val result = wrapInBox(content.toString().trimEnd('\n'), availableWidth, availableHeight)
        val lines = result.lines()
        widthPx = lines.maxOfOrNull { visibleLength(it) } ?: 0
        heightPx = lines.size
        return result
    }

    override fun onEvent(event: BobaEvent): Boolean {
        when (event) {
            is BobaEvent.Key -> {
                when {
                    KeyCodes.isUp(event.code) -> {
                        currentIndex = (currentIndex - 1 + options.size) % options.size
                        return true
                    }
                    KeyCodes.isDown(event.code) -> {
                        currentIndex = (currentIndex + 1) % options.size
                        return true
                    }
                    event.code == SPACE.key -> {
                        toggle(currentIndex)
                        return true
                    }
                    event.code == ENTER.key || event.code == 'q'.code || event.code == 'Q'.code -> {
                        onComplete(selected)
                        return true
                    }
                }
            }
            is BobaEvent.Mouse -> {
                if (event.action == MouseAction.PRESS) {
                    val startLine = y + (if (borderStyle != BorderStyle.NONE) 1 else 0) + padding + 1
                    val clickedIndex = event.y - startLine
                    if (clickedIndex in options.indices) {
                        currentIndex = clickedIndex
                        toggle(currentIndex)
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun toggle(index: Int) {
        if (selected.contains(options[index])) {
            selected.remove(options[index])
        } else {
            selected.add(options[index])
        }
    }
}
