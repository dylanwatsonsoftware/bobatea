package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.TextColors
import java.io.OutputStreamWriter
import java.io.PrintWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoadingIndicator(
    private val out: PrintWriter = PrintWriter(OutputStreamWriter(System.`out`)),
    override var padding: Int = 0,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.NONE,
    override var style: TextStyle = TextStyle()
) : BobaComponent(padding, margin, borderStyle, style) {
    companion object {
        fun <R> runLoading(
            message: String = "",
            loaderStyle: LoaderStyle = LoaderStyle.DEFAULT,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            style: TextStyle = TextStyle(),
            callback: () -> R
        ): R {
            return LoadingIndicator(padding = padding, margin = margin, borderStyle = borderStyle, style = style)
                .runLoading(message, loaderStyle, callback)
        }
    }

    /**
     * Documentation: rendering is handled via show() during the loading process.
     * This component manages its own animation output stream.
     */
    override fun render(): String = ""

    fun <R> runLoading(message: String = "", loaderStyle: LoaderStyle = LoaderStyle.DEFAULT, callback: () -> R): R {
        var result: R
        runBlocking {
            val job = launch(Dispatchers.Default) { show(message, loaderStyle) }
            val messageEraser = " ".repeat(message.length + 10)
            result = callback()
            job.cancelAndJoin()
            out.print("\r$messageEraser\r")
            out.flush()
        }
        return result
    }

    suspend fun show(message: String, loaderStyle: LoaderStyle) {
        val charSequence = loaderStyle.pattern.asInfiniteSequence()
        val pen = createPen(loaderStyle.color)

        val needsBox = borderStyle != BorderStyle.NONE || padding > 0 || margin > 0

        for (char in charSequence) {
            val loadingLine = "${pen(char.toString())} $message"
            if (needsBox) {
                // Optimally we would reuse the box if the content length is constant,
                // but since the spinner character changes, we render it each frame.
                val box = Box(loadingLine, padding, margin, borderStyle, style)
                Boba.clear()
                out.print(box.render())
            } else {
                out.print("\r$loadingLine")
            }
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

fun colour(txt: String, with: TerminalColors): String {
    val mordantColor = when(with) {
        TerminalColors.BLACK -> TextColors.black
        TerminalColors.RED -> TextColors.red
        TerminalColors.GREEN -> TextColors.green
        TerminalColors.YELLOW -> TextColors.yellow
        TerminalColors.BLUE -> TextColors.blue
        TerminalColors.PURPLE -> TextColors.magenta
        TerminalColors.CYAN -> TextColors.cyan
        TerminalColors.WHITE -> TextColors.white
        TerminalColors.RESET -> TextColors.gray // fallback
    }
    return Boba.terminal.render(mordantColor(txt))
}

fun createPen(penColor: TerminalColors?): (String) -> String = { if (penColor != null) colour(it, with = penColor) else it }
