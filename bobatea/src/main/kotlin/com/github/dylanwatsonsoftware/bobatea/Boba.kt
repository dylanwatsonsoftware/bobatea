package com.github.dylanwatsonsoftware.bobatea

import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.YELLOW
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.color
import com.github.dylanwatsonsoftware.bobatea.NonBlockingTerminal.Companion.KeyCodes.DOWN
import com.github.dylanwatsonsoftware.bobatea.NonBlockingTerminal.Companion.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.NonBlockingTerminal.Companion.KeyCodes.LEFT
import com.github.dylanwatsonsoftware.bobatea.NonBlockingTerminal.Companion.KeyCodes.RIGHT
import com.github.dylanwatsonsoftware.bobatea.NonBlockingTerminal.Companion.KeyCodes.SPACE
import com.github.dylanwatsonsoftware.bobatea.NonBlockingTerminal.Companion.KeyCodes.UP
import com.github.dylanwatsonsoftware.bobatea.NonBlockingTerminal.Companion.clear
import com.github.dylanwatsonsoftware.bobatea.NonBlockingTerminal.Companion.getChar
import com.github.dylanwatsonsoftware.bobatea.NonBlockingTerminal.Companion.nonBlockingTerminal
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.TreeSet

class Boba {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            nonBlockingTerminal {
                coordinates()

                val selection =
                    selectFromList(
                        question = "What's your favourite number?",
                        options =
                            listOf(
                                "one",
                                "two",
                                "three",
                                "four",
                                "five",
                                "six",
                                "seven",
                                "eight",
                                "nine",
                                "ten",
                            ),
                    )
                println("You selected: $selection")

                val multiSelections =
                    selectMultipleFromList(
                        question = "What are all your favourite numbers?",
                        options =
                            listOf(
                                "one",
                                "two",
                                "three",
                                "four",
                                "five",
                                "six",
                                "seven",
                                "eight",
                                "nine",
                                "ten",
                            ),
                    )
                println("You selected: $multiSelections")
            }
        }

        private fun coordinates() {
            fun printGridWithHighlight(rows: Int, cols: Int, highlightRow: Int, highlightCol: Int) {
                for (row in 0 until rows) {
                    for (col in 0 until cols) {
                        if (row == highlightRow && col == highlightCol) {
                            // Use a specific character or style to highlight the cell
                            print(" X ")
                        } else {
                            print("   ")
                        }
                    }
                    // New line after each row
                    println()
                }
            }

            fun render(x: Int, y: Int) {
                clear()
                println("$x, $y")
                printGridWithHighlight(10, 10, y, x)
                println("${color("SPACE/ENTER", GREEN)} to confirm")
            }

            var x = 0
            var y = 0

            render(x, y)

            while (true) {
                when (getChar()) {
                    DOWN.key -> render(x, ++y)
                    UP.key -> render(x, --y)
                    LEFT.key -> render(--x, y)
                    RIGHT.key -> render(++x, y)
                    ENTER.key, SPACE.key -> return
                }
            }
        }

        fun selectFromList(question: String, options: List<String>): String {
            var currentIndex = 0

            fun printList() {
                clear()
                println(color(question, GREEN))
                options.forEachIndexed { index, item ->
                    if (index == currentIndex) {
                        println(color("❯ $item", YELLOW))
                    } else {
                        println("  $item")
                    }
                }
                println()
                println("Use ${color("UP/DOWN", GREEN)} arrow keys to choose.")
                println("${color("SPACE/ENTER", GREEN)} to confirm")
            }

            fun moveUp() {
                currentIndex = (currentIndex - 1 + options.size) % options.size
            }

            fun moveDown() {
                currentIndex = (currentIndex + 1) % options.size
            }

            fun deselect() {
                currentIndex = -1
            }

            printList()

            while (true) {
                when (getChar()) {
                    UP.key -> {
                        moveUp()
                        printList()
                    }

                    DOWN.key -> {
                        moveDown()
                        printList()
                    }

                    SPACE.key, ENTER.key -> {
                        val selected = options[currentIndex]
                        deselect()
                        printList()
                        return selected
                    }
                }
            }
        }

        fun selectMultipleFromList(question: String, options: List<String>): MutableSet<String> {
            val selected = TreeSet<String>()

            var currentIndex = 0

            fun printList() {
                clear()
                println(color(question, GREEN))
                options.forEachIndexed { index, item ->
                    val isSelected = selected.contains(item)
                    val isCursorHighlighted = index == currentIndex

                    val prefix =
                        if (isSelected && isCursorHighlighted) {
                            "${color("[", YELLOW)}${color("✔", GREEN)}${color("]", YELLOW)}"
                        } else if (isSelected) {
                            color(
                                " ✔ ",
                                GREEN,
                            )
                        } else if (isCursorHighlighted) {
                            "[ ]"
                        } else {
                            "   "
                        }

                    if (isCursorHighlighted) {
                        println(color("$prefix ${color(item, YELLOW)}", YELLOW))
                    } else {
                        println("$prefix $item")
                    }
                }
                println()
                println("Use ${color("UP/DOWN", GREEN)} arrow keys to choose.")
                println("Press ${color("SPACE", GREEN)} to toggle selection")
                println("${color("ENTER", GREEN)} to confirm")
            }

            fun moveUp() {
                currentIndex = (currentIndex - 1 + options.size) % options.size
            }

            fun moveDown() {
                currentIndex = (currentIndex + 1) % options.size
            }

            fun toggle(index: Int) {
                if (selected.contains(options[index])) {
                    selected.remove(options[index])
                } else {
                    selected.add(options[index])
                }
            }

            fun deselectIndex() {
                currentIndex = -1
            }

            printList()

            while (true) {
                when (getChar()) {
                    UP.key -> {
                        moveUp()
                        printList()
                    }

                    DOWN.key -> {
                        moveDown()
                        printList()
                    }

                    SPACE.key -> {
                        toggle(currentIndex)
                        printList()
                    }

                    ENTER.key -> {
                        deselectIndex()
                        printList()
                        return selected
                    }
                }
            }
        }
    }
}

class ConsoleColors {
    companion object {
        fun color(text: String, color: String): String = "$color$text${ConsoleColors.RESET}"

        // Reset
        const val RESET: String = "\u001b[0m" // Text Reset

        // Regular Colors
        const val BLACK: String = "\u001b[0;30m" // BLACK
        const val RED: String = "\u001b[0;31m" // RED
        const val GREEN: String = "\u001b[0;32m" // GREEN
        const val YELLOW: String = "\u001b[0;33m" // YELLOW
        const val BLUE: String = "\u001b[0;34m" // BLUE
        const val PURPLE: String = "\u001b[0;35m" // PURPLE
        const val CYAN: String = "\u001b[0;36m" // CYAN
        const val WHITE: String = "\u001b[0;37m" // WHITE

        // Bold
        const val BLACK_BOLD: String = "\u001b[1;30m" // BLACK
        const val RED_BOLD: String = "\u001b[1;31m" // RED
        const val GREEN_BOLD: String = "\u001b[1;32m" // GREEN
        const val YELLOW_BOLD: String = "\u001b[1;33m" // YELLOW
        const val BLUE_BOLD: String = "\u001b[1;34m" // BLUE
        const val PURPLE_BOLD: String = "\u001b[1;35m" // PURPLE
        const val CYAN_BOLD: String = "\u001b[1;36m" // CYAN
        const val WHITE_BOLD: String = "\u001b[1;37m" // WHITE

        // Underline
        const val BLACK_UNDERLINED: String = "\u001b[4;30m" // BLACK
        const val RED_UNDERLINED: String = "\u001b[4;31m" // RED
        const val GREEN_UNDERLINED: String = "\u001b[4;32m" // GREEN
        const val YELLOW_UNDERLINED: String = "\u001b[4;33m" // YELLOW
        const val BLUE_UNDERLINED: String = "\u001b[4;34m" // BLUE
        const val PURPLE_UNDERLINED: String = "\u001b[4;35m" // PURPLE
        const val CYAN_UNDERLINED: String = "\u001b[4;36m" // CYAN
        const val WHITE_UNDERLINED: String = "\u001b[4;37m" // WHITE

        // Background
        const val BLACK_BACKGROUND: String = "\u001b[40m" // BLACK
        const val RED_BACKGROUND: String = "\u001b[41m" // RED
        const val GREEN_BACKGROUND: String = "\u001b[42m" // GREEN
        const val YELLOW_BACKGROUND: String = "\u001b[43m" // YELLOW
        const val BLUE_BACKGROUND: String = "\u001b[44m" // BLUE
        const val PURPLE_BACKGROUND: String = "\u001b[45m" // PURPLE
        const val CYAN_BACKGROUND: String = "\u001b[46m" // CYAN
        const val WHITE_BACKGROUND: String = "\u001b[47m" // WHITE

        // High Intensity
        const val BLACK_BRIGHT: String = "\u001b[0;90m" // BLACK
        const val RED_BRIGHT: String = "\u001b[0;91m" // RED
        const val GREEN_BRIGHT: String = "\u001b[0;92m" // GREEN
        const val YELLOW_BRIGHT: String = "\u001b[0;93m" // YELLOW
        const val BLUE_BRIGHT: String = "\u001b[0;94m" // BLUE
        const val PURPLE_BRIGHT: String = "\u001b[0;95m" // PURPLE
        const val CYAN_BRIGHT: String = "\u001b[0;96m" // CYAN
        const val WHITE_BRIGHT: String = "\u001b[0;97m" // WHITE

        // Bold High Intensity
        const val BLACK_BOLD_BRIGHT: String = "\u001b[1;90m" // BLACK
        const val RED_BOLD_BRIGHT: String = "\u001b[1;91m" // RED
        const val GREEN_BOLD_BRIGHT: String = "\u001b[1;92m" // GREEN
        const val YELLOW_BOLD_BRIGHT: String = "\u001b[1;93m" // YELLOW
        const val BLUE_BOLD_BRIGHT: String = "\u001b[1;94m" // BLUE
        const val PURPLE_BOLD_BRIGHT: String = "\u001b[1;95m" // PURPLE
        const val CYAN_BOLD_BRIGHT: String = "\u001b[1;96m" // CYAN
        const val WHITE_BOLD_BRIGHT: String = "\u001b[1;97m" // WHITE

        // High Intensity backgrounds
        const val BLACK_BACKGROUND_BRIGHT: String = "\u001b[0;100m" // BLACK
        const val RED_BACKGROUND_BRIGHT: String = "\u001b[0;101m" // RED
        const val GREEN_BACKGROUND_BRIGHT: String = "\u001b[0;102m" // GREEN
        const val YELLOW_BACKGROUND_BRIGHT: String = "\u001b[0;103m" // YELLOW
        const val BLUE_BACKGROUND_BRIGHT: String = "\u001b[0;104m" // BLUE
        const val PURPLE_BACKGROUND_BRIGHT: String = "\u001b[0;105m" // PURPLE
        const val CYAN_BACKGROUND_BRIGHT: String = "\u001b[0;106m" // CYAN
        const val WHITE_BACKGROUND_BRIGHT: String = "\u001b[0;107m" // WHITE
    }
}

/**
 * Allows us to have a non-blocking terminal -
 *
 * Mostly stolen from: https://darkcoding.net/software/non-blocking-console-io-is-not-possible/
 */
class NonBlockingTerminal {
    companion object {
        fun <T> nonBlockingTerminal(task: () -> T) {
            val ttyConfig = stty("-g")
            try {
                setTerminalToCBreak()

                task()
            } finally {
                try {
                    stty(ttyConfig.trim { it <= ' ' })
                } catch (e: Exception) {
                    System.err.println("Exception restoring tty config")
                }
            }
        }

        fun clear() {
            ProcessBuilder("clear").inheritIO().start().waitFor()
        }

        enum class KeyCodes(val key: Int) {
            UP(65),
            DOWN(66),
            LEFT(68),
            RIGHT(67),
            ENTER(10),
            SPACE(32),
        }

        fun getChar(): Int {
            while (true) {
                if (System.`in`.available() != 0) {
                    return System.`in`.read()
                }
            }
        }

        @Throws(IOException::class, InterruptedException::class)
        private fun setTerminalToCBreak() {
            // set the console to be character-buffered instead of line-buffered
            stty("-icanon min 1")

            // disable character echoing
            stty("-echo")
        }

        /**
         * Execute the stty command with the specified arguments
         * against the current active terminal.
         */
        @Throws(IOException::class, InterruptedException::class)
        private fun stty(args: String): String {
            val cmd = "stty $args < /dev/tty"

            return exec(
                arrayOf(
                    "sh",
                    "-c",
                    cmd,
                ),
            )
        }

        /**
         * Execute the specified command and return the output
         * (both stdout and stderr).
         */
        @Throws(IOException::class, InterruptedException::class)
        private fun exec(cmd: Array<String>): String {
            val bout = ByteArrayOutputStream()

            val p = Runtime.getRuntime().exec(cmd)
            var c: Int
            var `in` = p.inputStream

            while ((`in`.read().also { c = it }) != -1) {
                bout.write(c)
            }

            `in` = p.errorStream

            while ((`in`.read().also { c = it }) != -1) {
                bout.write(c)
            }

            p.waitFor()

            val result = String(bout.toByteArray())
            return result
        }
    }
}
