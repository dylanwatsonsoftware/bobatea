package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal

class ConsoleColors {
    companion object {
        private val t = Terminal()

        fun color(text: String, color: String): String = "$color$text${RESET}"

        // Reset
        const val RESET: String = "\u001b[0m"

        val BLACK = t.render(TextColors.black("")).replace(RESET, "")
        val RED = t.render(TextColors.red("")).replace(RESET, "")
        val GREEN = t.render(TextColors.green("")).replace(RESET, "")
        val YELLOW = t.render(TextColors.yellow("")).replace(RESET, "")
        val BLUE = t.render(TextColors.blue("")).replace(RESET, "")
        val PURPLE = t.render(TextColors.magenta("")).replace(RESET, "")
        val CYAN = t.render(TextColors.cyan("")).replace(RESET, "")
        val WHITE = t.render(TextColors.white("")).replace(RESET, "")

        val BLACK_BOLD = t.render((TextColors.black + TextStyles.bold)("")).replace(RESET, "")
        val RED_BOLD = t.render((TextColors.red + TextStyles.bold)("")).replace(RESET, "")
        val GREEN_BOLD = t.render((TextColors.green + TextStyles.bold)("")).replace(RESET, "")
        val YELLOW_BOLD = t.render((TextColors.yellow + TextStyles.bold)("")).replace(RESET, "")
        val BLUE_BOLD = t.render((TextColors.blue + TextStyles.bold)("")).replace(RESET, "")
        val PURPLE_BOLD = t.render((TextColors.magenta + TextStyles.bold)("")).replace(RESET, "")
        val CYAN_BOLD = t.render((TextColors.cyan + TextStyles.bold)("")).replace(RESET, "")
        val WHITE_BOLD = t.render((TextColors.white + TextStyles.bold)("")).replace(RESET, "")

        val BLACK_BACKGROUND = t.render(TextColors.black.bg("")).replace(RESET, "")
        val RED_BACKGROUND = t.render(TextColors.red.bg("")).replace(RESET, "")
        val GREEN_BACKGROUND = t.render(TextColors.green.bg("")).replace(RESET, "")
        val YELLOW_BACKGROUND = t.render(TextColors.yellow.bg("")).replace(RESET, "")
        val BLUE_BACKGROUND = t.render(TextColors.blue.bg("")).replace(RESET, "")
        val PURPLE_BACKGROUND = t.render(TextColors.magenta.bg("")).replace(RESET, "")
        val CYAN_BACKGROUND = t.render(TextColors.cyan.bg("")).replace(RESET, "")
        val WHITE_BACKGROUND = t.render(TextColors.white.bg("")).replace(RESET, "")
    }
}
