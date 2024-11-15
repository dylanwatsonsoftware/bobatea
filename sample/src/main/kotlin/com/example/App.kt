package com.example

import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.selectFromList
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.selectMultipleFromList
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.color
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.DOWN
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.LEFT
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.RIGHT
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.UP
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.clear
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.getChar
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.nonBlockingTerminal


class App {
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

                val multiSelections = selectMultipleFromList(
                        question = "What are all your favourite numbers?",
                        options =
                        listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"),
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
    }
}
