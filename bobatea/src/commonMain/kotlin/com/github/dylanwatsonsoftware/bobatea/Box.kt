package com.github.dylanwatsonsoftware.bobatea

enum class BorderStyle(
    val topLeft: Char,
    val topRight: Char,
    val bottomLeft: Char,
    val bottomRight: Char,
    val horizontal: Char,
    val vertical: Char
) {
    SINGLE('┌', '┐', '└', '┘', '─', '│'),
    DOUBLE('╔', '╗', '╚', '╝', '═', '║'),
    ROUNDED('╭', '╮', '╰', '╯', '─', '│'),
    NONE(' ', ' ', ' ', ' ', ' ', ' ')
}

class Box(
    private val content: String,
    override var padding: Int = 1,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.SINGLE,
    override var color: String? = null
) : BobaComponent(padding, margin, borderStyle, color) {
    companion object {
        private val ANSI_REGEX = Regex("\u001b\\[[0-9;?]*[a-zA-Z]|\u001b\\][0-9;]*\u0007")
    }

    private fun visibleLength(s: String): Int {
        if (s.isEmpty()) return 0
        return s.replace(ANSI_REGEX, "").length
    }

    override fun render(): String {
        val lines = content.lines()
        val contentWidth = lines.maxOfOrNull { visibleLength(it) } ?: 0
        val innerWidth = contentWidth + padding * 2

        val result = StringBuilder()

        // Top margin
        repeat(margin) { result.append("\n") }

        if (borderStyle != BorderStyle.NONE) {
            // Top border
            result.append(" ".repeat(margin))
            result.append(borderStyle.topLeft)
            result.append(borderStyle.horizontal.toString().repeat(innerWidth))
            result.append(borderStyle.topRight)
            result.append("\n")
        }

        // Top padding
        repeat(padding) {
            result.append(" ".repeat(margin))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append(" ".repeat(innerWidth))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append("\n")
        }

        // Content
        lines.forEach { line ->
            result.append(" ".repeat(margin))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append(" ".repeat(padding))
            result.append(line)
            val paddingNeeded = contentWidth - visibleLength(line)
            if (paddingNeeded > 0) {
                result.append(" ".repeat(paddingNeeded))
            }
            result.append(" ".repeat(padding))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append("\n")
        }

        // Bottom padding
        repeat(padding) {
            result.append(" ".repeat(margin))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append(" ".repeat(innerWidth))
            if (borderStyle != BorderStyle.NONE) result.append(borderStyle.vertical)
            result.append("\n")
        }

        if (borderStyle != BorderStyle.NONE) {
            // Bottom border
            result.append(" ".repeat(margin))
            result.append(borderStyle.bottomLeft)
            result.append(borderStyle.horizontal.toString().repeat(innerWidth))
            result.append(borderStyle.bottomRight)
            result.append("\n")
        }

        // Bottom margin
        repeat(margin) { result.append("\n") }

        val rendered = result.toString().trimEnd('\n')
        return if (color != null) {
            val lineColor = color!!
            rendered.lines().joinToString("\n") { line ->
                if (line.isEmpty()) "" else {
                    val restored = line.replace(
                        ConsoleColors.RESET,
                        ConsoleColors.RESET + lineColor
                    )
                    ConsoleColors.color(restored, lineColor)
                }
            }
        } else {
        } else {
            rendered
        }
    }

    override fun toString(): String = render()
}
