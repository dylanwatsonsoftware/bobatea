package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles

class ConsoleColors {
    companion object {
        fun color(text: String, color: String): String = "$color$text${RESET}"

        // Reset
        const val RESET: String = "\u001b[0m"

        val BLACK = Boba.terminal.render(TextColors.black("")).replace(RESET, "")
        val RED = Boba.terminal.render(TextColors.red("")).replace(RESET, "")
        val GREEN = Boba.terminal.render(TextColors.green("")).replace(RESET, "")
        val YELLOW = Boba.terminal.render(TextColors.yellow("")).replace(RESET, "")
        val BLUE = Boba.terminal.render(TextColors.blue("")).replace(RESET, "")
        val PURPLE = Boba.terminal.render(TextColors.magenta("")).replace(RESET, "")
        val CYAN = Boba.terminal.render(TextColors.cyan("")).replace(RESET, "")
        val WHITE = Boba.terminal.render(TextColors.white("")).replace(RESET, "")

        val BLACK_BOLD = Boba.terminal.render((TextColors.black + TextStyles.bold)("")).replace(RESET, "")
        val RED_BOLD = Boba.terminal.render((TextColors.red + TextStyles.bold)("")).replace(RESET, "")
        val GREEN_BOLD = Boba.terminal.render((TextColors.green + TextStyles.bold)("")).replace(RESET, "")
        val YELLOW_BOLD = Boba.terminal.render((TextColors.yellow + TextStyles.bold)("")).replace(RESET, "")
        val BLUE_BOLD = Boba.terminal.render((TextColors.blue + TextStyles.bold)("")).replace(RESET, "")
        val PURPLE_BOLD = Boba.terminal.render((TextColors.magenta + TextStyles.bold)("")).replace(RESET, "")
        val CYAN_BOLD = Boba.terminal.render((TextColors.cyan + TextStyles.bold)("")).replace(RESET, "")
        val WHITE_BOLD = Boba.terminal.render((TextColors.white + TextStyles.bold)("")).replace(RESET, "")

        val BLACK_BACKGROUND = Boba.terminal.render(TextColors.black.bg("")).replace(RESET, "")
        val RED_BACKGROUND = Boba.terminal.render(TextColors.red.bg("")).replace(RESET, "")
        val GREEN_BACKGROUND = Boba.terminal.render(TextColors.green.bg("")).replace(RESET, "")
        val YELLOW_BACKGROUND = Boba.terminal.render(TextColors.yellow.bg("")).replace(RESET, "")
        val BLUE_BACKGROUND = Boba.terminal.render(TextColors.blue.bg("")).replace(RESET, "")
        val PURPLE_BACKGROUND = Boba.terminal.render(TextColors.magenta.bg("")).replace(RESET, "")
        val CYAN_BACKGROUND = Boba.terminal.render(TextColors.cyan.bg("")).replace(RESET, "")
        val WHITE_BACKGROUND = Boba.terminal.render(TextColors.white.bg("")).replace(RESET, "")
    }
}
