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
    private val padding: Int = 1,
    private val margin: Int = 0,
    private val borderStyle: BorderStyle = BorderStyle.SINGLE,
    private val color: String? = null
) {
    fun render(): String {
        val lines = content.lines()
        val contentWidth = lines.maxOfOrNull { it.length } ?: 0
        val innerWidth = contentWidth + padding * 2

        val result = StringBuilder()

        // Top margin
        repeat(margin) { result.append("\n") }

        // Top border
        result.append(" ".repeat(margin))
        result.append(borderStyle.topLeft)
        result.append(borderStyle.horizontal.toString().repeat(innerWidth))
        result.append(borderStyle.topRight)
        result.append("\n")

        // Top padding
        repeat(padding) {
            result.append(" ".repeat(margin))
            result.append(borderStyle.vertical)
            result.append(" ".repeat(innerWidth))
            result.append(borderStyle.vertical)
            result.append("\n")
        }

        // Content
        lines.forEach { line ->
            result.append(" ".repeat(margin))
            result.append(borderStyle.vertical)
            result.append(" ".repeat(padding))
            result.append(line)
            result.append(" ".repeat(contentWidth - line.length))
            result.append(" ".repeat(padding))
            result.append(borderStyle.vertical)
            result.append("\n")
        }

        // Bottom padding
        repeat(padding) {
            result.append(" ".repeat(margin))
            result.append(borderStyle.vertical)
            result.append(" ".repeat(innerWidth))
            result.append(borderStyle.vertical)
            result.append("\n")
        }

        // Bottom border
        result.append(" ".repeat(margin))
        result.append(borderStyle.bottomLeft)
        result.append(borderStyle.horizontal.toString().repeat(innerWidth))
        result.append(borderStyle.bottomRight)
        result.append("\n")

        // Bottom margin
        repeat(margin) { result.append("\n") }

        val rendered = result.toString().trimEnd('\n')
        return if (color != null) ConsoleColors.color(rendered, color) else rendered
    }

    override fun toString(): String = render()
}
