package com.github.dylanwatsonsoftware.bobatea

import java.io.OutputStreamWriter
import java.io.PrintWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoadingIndicator(
    private val out: PrintWriter = PrintWriter(OutputStreamWriter(System.out)),
) {
    companion object {
        fun <R> runLoading(message: String = "", style: LoaderStyle = LoaderStyle.DEFAULT, callback: () -> R): R {
            return LoadingIndicator().runLoading(message, style, callback)
        }
    }

    fun <R> runLoading(message: String = "", style: LoaderStyle = LoaderStyle.DEFAULT, callback: () -> R): R {
        var result: R
        runBlocking {
            val job = launch(Dispatchers.Default) { show(message, style) }
            val messageEraser = message.map { " " }.joinToString("")
            result = callback()
            job.cancelAndJoin()
            out.print("\r   ${messageEraser}\r")
            out.flush()
        }
        return result
    }

    suspend fun show(message: String, style: LoaderStyle) {
        val charSequence = style.pattern.asInfiniteSequence()
        val pen = createPen(style.color)
        for (char in charSequence) {
            out.print("\r${pen(char.toString())} $message")
            out.flush()
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

enum class LoaderPatten(
    val chars: List<Char>,
) {
    SMALL(
        listOf(
            '⠟',
            '⠯',
            '⠷',
            '⠾',
            '⠽',
            '⠻',
        ),
    ),
    LARGE(
        listOf(
            '⡿',
            '⣟',
            '⣯',
            '⣷',
            '⣾',
            '⣽',
            '⣻',
            '⢿',
        ),
    ),
    ;

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
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
}

enum class Formatting(val value: String) {
    BOLD("\u001B[1m"),
    UNDERLINE("\u001B[4m"),
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

fun red(text: String): String = colour(text, with = TerminalColors.RED)

fun green(text: String): String = colour(text, with = TerminalColors.GREEN)

fun blue(text: String): String = colour(text, with = TerminalColors.BLUE)

fun black(text: String): String = colour(text, with = TerminalColors.BLACK)

fun yellow(text: String): String = colour(text, with = TerminalColors.YELLOW)

fun purple(text: String): String = colour(text, with = TerminalColors.PURPLE)

fun cyan(text: String): String = colour(text, with = TerminalColors.CYAN)

fun white(text: String): String = colour(text, with = TerminalColors.WHITE)

fun bold(text: String): String = "${Formatting.BOLD.value}${text}${TerminalColors.RESET.value}"

fun underline(text: String): String = "${Formatting.UNDERLINE.value}${text}${TerminalColors.RESET.value}"

fun createPen(penColor: TerminalColors?): (String) -> String = { if (penColor != null) colour(it, with = penColor) else it }

