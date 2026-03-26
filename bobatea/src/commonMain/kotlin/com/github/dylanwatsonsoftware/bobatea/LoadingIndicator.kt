package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingIndicator(
    val message: String = "",
    val style: LoaderStyle = LoaderStyle.DEFAULT,
    override var padding: Int = 0,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.NONE,
    override var color: String? = null,
    override var width: Dimension = Dimension.Auto,
    override var maxWidth: Dimension = Dimension.Auto,
    override var height: Dimension = Dimension.Auto,
    override var maxHeight: Dimension = Dimension.Auto
) : BobaComponent(padding, margin, borderStyle, color, width, maxWidth, height, maxHeight) {
    private var frameIndex = 0
    private var elapsedMs = 0L

    companion object {
        suspend fun <R> runLoading(
            message: String = "",
            style: LoaderStyle = LoaderStyle.DEFAULT,
            terminal: Terminal,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            color: String? = null,
            callback: suspend () -> R
        ): R {
            // Deprecated legacy method for backwards compatibility if needed,
            // but for now we follow the new declarative style
            return callback()
        }
    }

    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val pattern = style.pattern.chars
        val char = pattern[frameIndex % pattern.size]
        val pen = createPen(style.color)
        val loadingLine = "${pen(char.toString())} $message"

        val output = wrapInBox(loadingLine, availableWidth, availableHeight)
        val lines = output.lines()
        widthPx = lines.maxOfOrNull { visibleLength(it) } ?: 0
        heightPx = lines.size
        return output
    }

    override fun tick(deltaMs: Long) {
        elapsedMs += deltaMs
        if (elapsedMs >= 200) {
            frameIndex++
            elapsedMs = 0
        }
    }
}

enum class LoaderStyle(
    val pattern: LoaderPatten,
    val color: TerminalColors? = null,
) {
    DEFAULT(LoaderPatten.SMALL),
    SMALL(LoaderPatten.SMALL),
    SMALL_GREEN(LoaderPatten.SMALL, TerminalColors.GREEN),
    SMALL_BLUE(LoaderPatten.SMALL, TerminalColors.BLUE),
    LARGE(LoaderPatten.LARGE),
    LARGE_GREEN(LoaderPatten.LARGE, TerminalColors.GREEN),
    LARGE_BLUE(LoaderPatten.LARGE, TerminalColors.BLUE),
}

enum class LoaderPatten(val chars: List<Char>) {
    SMALL(listOf('⠟', '⠯', '⠷', '⠾', '⠽', '⠻')),
    LARGE(listOf('⡿', '⣟', '⣯', '⣷', '⣾', '⣽', '⣻', '⢿'));

    fun asInfiniteSequence() = infiniteRange(0..chars.lastIndex).map { chars[it] }
}

fun infiniteRange(range: IntRange): Sequence<Int> = sequence {
    while (true) {
        for (i in range) {
            yield(i)
        }
    }
}

enum class TerminalColors(val value: String) {
    RESET(ConsoleColors.RESET),
    BLACK(ConsoleColors.BLACK),
    RED(ConsoleColors.RED),
    GREEN(ConsoleColors.GREEN),
    YELLOW(ConsoleColors.YELLOW),
    BLUE(ConsoleColors.BLUE),
    PURPLE(ConsoleColors.PURPLE),
    CYAN(ConsoleColors.CYAN),
    WHITE(ConsoleColors.WHITE),
}

enum class Formatting(val value: String) {
    BOLD(ConsoleColors.WHITE_BOLD),
    UNDERLINE(ConsoleColors.WHITE_UNDERLINED),
}

enum class CursorMovement(val value: String) {
    MOVE_UP("\u001B[1A"),
    MOVE_DOWN("\u001B[1B"),
    CLEAR_LINE("\u001B[2K"),
}

fun moveUp() = CursorMovement.MOVE_UP.value
fun moveDown() = CursorMovement.MOVE_DOWN.value
fun clearLine() = CursorMovement.CLEAR_LINE.value

fun colour(txt: String, with: TerminalColors): String = "${with.value}${txt}${TerminalColors.RESET.value}"

fun red(text: String): String = ConsoleColors.color(text, ConsoleColors.RED)
fun green(text: String): String = ConsoleColors.color(text, ConsoleColors.GREEN)
fun blue(text: String): String = ConsoleColors.color(text, ConsoleColors.BLUE)
fun black(text: String): String = ConsoleColors.color(text, ConsoleColors.BLACK)
fun yellow(text: String): String = ConsoleColors.color(text, ConsoleColors.YELLOW)
fun purple(text: String): String = ConsoleColors.color(text, ConsoleColors.PURPLE)
fun cyan(text: String): String = ConsoleColors.color(text, ConsoleColors.CYAN)
fun white(text: String): String = ConsoleColors.color(text, ConsoleColors.WHITE)

fun bold(text: String): String = ConsoleColors.color(text, ConsoleColors.WHITE_BOLD)
fun underline(text: String): String = ConsoleColors.color(text, ConsoleColors.WHITE_UNDERLINED)
fun createPen(penColor: TerminalColors?): (String) -> String = { if (penColor != null) colour(it, with = penColor) else it }
