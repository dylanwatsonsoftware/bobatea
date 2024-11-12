package com.github.dylanwatsonsoftware.bobatea

import com.github.dylanwatsonsoftware.bobatea.KeyCodes.DOWN
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.UP
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.YELLOW
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.color
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.TreeSet

class Boba {
    companion object {

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

        /**
         * Allows us to have a non-blocking terminal -
         *
         * Mostly stolen from: https://darkcoding.net/software/non-blocking-console-io-is-not-possible/
         */
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
