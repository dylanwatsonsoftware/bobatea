package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal
import kotlin.math.floor

sealed class Dimension {
    data object Auto : Dimension()
    data class Fixed(val value: Int) : Dimension()
    data class Percent(val percentage: Double) : Dimension()
}

abstract class BobaComponent(
    open var padding: Int = 0,
    open var margin: Int = 0,
    open var borderStyle: BorderStyle = BorderStyle.NONE,
    open var color: String? = null,
    open var width: Dimension = Dimension.Auto,
    open var maxWidth: Dimension = Dimension.Auto,
    open var height: Dimension = Dimension.Auto,
    open var maxHeight: Dimension = Dimension.Auto
) {
    companion object {
        val ANSI_REGEX = Regex("\u001b\\[[0-9;?]*[a-zA-Z]")

        fun visibleLength(s: String): Int {
            return s.replace(ANSI_REGEX, "").length
        }

        private class AnsiTerminalInterface : com.github.ajalt.mordant.terminal.TerminalInterface {
            override fun completePrintRequest(request: com.github.ajalt.mordant.terminal.PrintRequest) {}
            override fun info(ansiLevel: com.github.ajalt.mordant.rendering.AnsiLevel?, hyperlinks: Boolean?, outputInteractive: Boolean?, inputInteractive: Boolean?): com.github.ajalt.mordant.terminal.TerminalInfo {
                return com.github.ajalt.mordant.terminal.TerminalInfo(ansiLevel = com.github.ajalt.mordant.rendering.AnsiLevel.TRUECOLOR, ansiHyperLinks = false, outputInteractive = true, inputInteractive = true, supportsAnsiCursor = true)
            }
            override fun getTerminalSize(): com.github.ajalt.mordant.rendering.Size? = com.github.ajalt.mordant.rendering.Size(80, 24)
            override fun readLineOrNull(hideInput: Boolean): String? = null
        }

        private val dummyTerminal = MordantTerminal(terminalInterface = AnsiTerminalInterface())

        fun resolveDimension(dimension: Dimension, available: Int?): Int? {
            return when (dimension) {
                is Dimension.Auto -> null
                is Dimension.Fixed -> dimension.value
                is Dimension.Percent -> {
                    if (available != null) {
                        floor(available * (dimension.percentage / 100.0)).toInt()
                    } else {
                        null
                    }
                }
            }
        }
    }

    abstract fun render(availableWidth: Int? = null, availableHeight: Int? = null): String

    protected fun getMordant(): MordantTerminal {
        return dummyTerminal
    }

    protected fun wrapInBox(content: String, availableWidth: Int? = null, availableHeight: Int? = null): String {
        if (borderStyle == BorderStyle.NONE && padding == 0 && margin == 0 && color == null &&
            width == Dimension.Auto && maxWidth == Dimension.Auto &&
            height == Dimension.Auto && maxHeight == Dimension.Auto) {
            return content
        }
        return Box(content, padding, margin, borderStyle, color, width, maxWidth, height, maxHeight).render(availableWidth, availableHeight)
    }
}
