package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.PrintRequest
import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal
import com.github.ajalt.mordant.terminal.TerminalInfo
import com.github.ajalt.mordant.terminal.TerminalInterface

class ConsoleColors {
    private class AnsiTerminalInterface : TerminalInterface {
        override fun completePrintRequest(request: PrintRequest) {}
        override val info: TerminalInfo = TerminalInfo(
            width = 80, height = 24,
            ansiLevel = AnsiLevel.TRUECOLOR,
            ansiHyperLinks = false, outputInteractive = true, inputInteractive = true, crClearsLine = false
        )
        override fun readLineOrNull(hideInput: Boolean): String? = null
    }

    companion object {
        private val dummyTerminal = MordantTerminal(terminalInterface = AnsiTerminalInterface())

        fun color(text: String, color: String): String = "$color$text${ConsoleColors.RESET}"

        fun styleToString(style: com.github.ajalt.mordant.rendering.TextStyle): String {
            val rendered = dummyTerminal.render(style("X"))
            return rendered.substringBefore("X")
        }

        // Reset
        val RESET: String = "\u001b[0m"

        // Regular Colors
        val BLACK: String = styleToString(TextColors.black)
        val RED: String = styleToString(TextColors.red)
        val GREEN: String = styleToString(TextColors.green)
        val YELLOW: String = styleToString(TextColors.yellow)
        val BLUE: String = styleToString(TextColors.blue)
        val PURPLE: String = styleToString(TextColors.magenta)
        val CYAN: String = styleToString(TextColors.cyan)
        val WHITE: String = styleToString(TextColors.white)

        // Bold
        val BLACK_BOLD: String = styleToString(TextColors.black + TextStyles.bold)
        val RED_BOLD: String = styleToString(TextColors.red + TextStyles.bold)
        val GREEN_BOLD: String = styleToString(TextColors.green + TextStyles.bold)
        val YELLOW_BOLD: String = styleToString(TextColors.yellow + TextStyles.bold)
        val BLUE_BOLD: String = styleToString(TextColors.blue + TextStyles.bold)
        val PURPLE_BOLD: String = styleToString(TextColors.magenta + TextStyles.bold)
        val CYAN_BOLD: String = styleToString(TextColors.cyan + TextStyles.bold)
        val WHITE_BOLD: String = styleToString(TextColors.white + TextStyles.bold)

        // Underline
        val BLACK_UNDERLINED: String = styleToString(TextColors.black + TextStyles.underline)
        val RED_UNDERLINED: String = styleToString(TextColors.red + TextStyles.underline)
        val GREEN_UNDERLINED: String = styleToString(TextColors.green + TextStyles.underline)
        val YELLOW_UNDERLINED: String = styleToString(TextColors.yellow + TextStyles.underline)
        val BLUE_UNDERLINED: String = styleToString(TextColors.blue + TextStyles.underline)
        val PURPLE_UNDERLINED: String = styleToString(TextColors.magenta + TextStyles.underline)
        val CYAN_UNDERLINED: String = styleToString(TextColors.cyan + TextStyles.underline)
        val WHITE_UNDERLINED: String = styleToString(TextColors.white + TextStyles.underline)

        // Background
        val BLACK_BACKGROUND: String = styleToString(TextColors.black.bg)
        val RED_BACKGROUND: String = styleToString(TextColors.red.bg)
        val GREEN_BACKGROUND: String = styleToString(TextColors.green.bg)
        val YELLOW_BACKGROUND: String = styleToString(TextColors.yellow.bg)
        val BLUE_BACKGROUND: String = styleToString(TextColors.blue.bg)
        val PURPLE_BACKGROUND: String = styleToString(TextColors.magenta.bg)
        val CYAN_BACKGROUND: String = styleToString(TextColors.cyan.bg)
        val WHITE_BACKGROUND: String = styleToString(TextColors.white.bg)

        // High Intensity
        val BLACK_BRIGHT: String = styleToString(TextColors.gray)
        val RED_BRIGHT: String = styleToString(TextColors.brightRed)
        val GREEN_BRIGHT: String = styleToString(TextColors.brightGreen)
        val YELLOW_BRIGHT: String = styleToString(TextColors.brightYellow)
        val BLUE_BRIGHT: String = styleToString(TextColors.brightBlue)
        val PURPLE_BRIGHT: String = styleToString(TextColors.brightMagenta)
        val CYAN_BRIGHT: String = styleToString(TextColors.brightCyan)
        val WHITE_BRIGHT: String = styleToString(TextColors.brightWhite)

        // Bold High Intensity
        val BLACK_BOLD_BRIGHT: String = styleToString(TextColors.gray + TextStyles.bold)
        val RED_BOLD_BRIGHT: String = styleToString(TextColors.brightRed + TextStyles.bold)
        val GREEN_BOLD_BRIGHT: String = styleToString(TextColors.brightGreen + TextStyles.bold)
        val YELLOW_BOLD_BRIGHT: String = styleToString(TextColors.brightYellow + TextStyles.bold)
        val BLUE_BOLD_BRIGHT: String = styleToString(TextColors.brightBlue + TextStyles.bold)
        val PURPLE_BOLD_BRIGHT: String = styleToString(TextColors.brightMagenta + TextStyles.bold)
        val CYAN_BOLD_BRIGHT: String = styleToString(TextColors.brightCyan + TextStyles.bold)
        val WHITE_BOLD_BRIGHT: String = styleToString(TextColors.brightWhite + TextStyles.bold)

        // High Intensity backgrounds
        val BLACK_BACKGROUND_BRIGHT: String = styleToString(TextColors.gray.bg)
        val RED_BACKGROUND_BRIGHT: String = styleToString(TextColors.brightRed.bg)
        val GREEN_BACKGROUND_BRIGHT: String = styleToString(TextColors.brightGreen.bg)
        val YELLOW_BACKGROUND_BRIGHT: String = styleToString(TextColors.brightYellow.bg)
        val BLUE_BACKGROUND_BRIGHT: String = styleToString(TextColors.brightBlue.bg)
        val PURPLE_BACKGROUND_BRIGHT: String = styleToString(TextColors.brightMagenta.bg)
        val CYAN_BACKGROUND_BRIGHT: String = styleToString(TextColors.brightCyan.bg)
        val WHITE_BACKGROUND_BRIGHT: String = styleToString(TextColors.brightWhite.bg)
    }
}
