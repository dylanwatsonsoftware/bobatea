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
    private val terminal: Terminal,
    override var padding: Int = 0,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.NONE,
    override var color: String? = null,
    override var width: Dimension = Dimension.Auto,
    override var maxWidth: Dimension = Dimension.Auto,
    override var height: Dimension = Dimension.Auto,
    override var maxHeight: Dimension = Dimension.Auto
) : BobaComponent(padding, margin, borderStyle, color, width, maxWidth, height, maxHeight) {
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
            return LoadingIndicator(terminal = terminal, padding = padding, margin = margin, borderStyle = borderStyle, color = color)
                .runLoading(message, style, callback)
        }
    }

    override fun render(availableWidth: Int?, availableHeight: Int?): String =
        Box("", padding, margin, borderStyle, color, width, maxWidth, height, maxHeight).render(availableWidth, availableHeight)

    suspend fun <R> runLoading(message: String = "", style: LoaderStyle = LoaderStyle.DEFAULT, callback: suspend () -> R): R {
        return coroutineScope {
            val job = launch { show(message, style) }
            val result = callback()
            job.cancelAndJoin()
            val messageEraser = message.map { " " }.joinToString("")
            terminal.write("\r   $messageEraser\r")
            result
        }
    }

    suspend fun show(message: String, style: LoaderStyle) {
        val charSequence = style.pattern.asInfiniteSequence()
        val pen = createPen(style.color)

        val (availableWidth, availableHeight) = terminal.size()
        for (char in charSequence) {
            val loadingLine = "${pen(char.toString())} $message"
            val output = if (borderStyle != BorderStyle.NONE || padding > 0 || margin > 0 ||
                width != Dimension.Auto || maxWidth != Dimension.Auto || height != Dimension.Auto || maxHeight != Dimension.Auto) {
                Box(loadingLine, padding, margin, borderStyle, color, width, maxWidth, height, maxHeight).render(availableWidth, availableHeight)
            } else {
                "\r$loadingLine"
            }

            if (borderStyle != BorderStyle.NONE || padding > 0 || margin > 0) {
                terminal.clear()
                terminal.write(output)
            } else {
                terminal.write(output)
            }
            delay(200)
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
