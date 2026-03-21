package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextColors
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE

class SelectionList(
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
    val onSelect: (String) -> Unit = {}
) : BobaComponent(padding, margin, borderStyle, color, width, maxWidth, height, maxHeight) {
    var currentIndex = 0
    private val pink = TextColors.rgb("#F179B4")
    private val bobaCyan = TextColors.rgb("#00D7FF")
    private val gray = TextColors.rgb("#808080")

    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val content = StringBuilder()
        content.append(pink(question)).append("\n\n")
        options.forEachIndexed { index, item ->
            if (index == currentIndex) {
                content.append(bobaCyan("❯ $item")).append("\n")
            } else {
                content.append("  $item").append("\n")
            }
        }
        content.append("\n")
        content.append(gray("Use UP/DOWN or W/S keys to choose.\n"))
        content.append(gray("SPACE/ENTER or Q to confirm"))

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
                    event.code == ENTER.key || event.code == SPACE.key || event.code == 'q'.code || event.code == 'Q'.code -> {
                        val selected = options[currentIndex]
                        onSelect(selected)
                        return true
                    }
                }
            }
            is BobaEvent.Mouse -> {
                if (event.action == MouseAction.PRESS) {
                    val startLine = this.y + (if (this.borderStyle != BorderStyle.NONE) 1 else 0) + this.padding + 2
                    val clickedIndex = event.y - startLine
                    if (clickedIndex in options.indices) {
                        if (clickedIndex == currentIndex) {
                            val selected = options[currentIndex]
                            onSelect(selected)
                        } else {
                            currentIndex = clickedIndex
                        }
                        return true
                    }
                }
            }
        }
        return false
    }
}
