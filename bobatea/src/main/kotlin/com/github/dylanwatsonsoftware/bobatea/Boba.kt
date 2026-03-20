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

        fun selectFromList(
            question: String,
            options: List<String>,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            color: String? = null
        ): String {
            return SelectionList(question, options, padding, margin, borderStyle, color).interact()
        }

        fun expandable(
            title: String,
            content: String,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            color: String? = null
        ) {
            ExpandableComponent(title, content, padding, margin, borderStyle, color).interact()
        }

        fun selectMultipleFromList(
            question: String,
            options: List<String>,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            color: String? = null
        ): MutableSet<String> {
            return MultiSelectionList(question, options, padding, margin, borderStyle, color).interact()
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
                val read = System.`in`.read()
                if (read != -1) return read
            }
        }

        fun readEvent(): BobaEvent {
            val firstChar = getChar()
            if (firstChar == 27) { // ESC
                if (System.`in`.available() > 0) {
                    val secondChar = System.`in`.read()
                    if (secondChar == '['.toInt()) {
                        val seq = StringBuilder()
                        var nextChar: Int
                        while (true) {
                            nextChar = System.`in`.read()
                            seq.append(nextChar.toChar())
                            if (nextChar in 64..126) break
                        }
                        val s = seq.toString()
                        if (s.startsWith("<")) { // SGR Mouse Protocol
                            val parts = s.substring(1, s.length - 1).split(";")
                            val buttonInfo = parts[0].toInt()
                            val x = parts[1].toInt()
                            val y = parts[2].toInt()
                            val action = when {
                                (buttonInfo and 32) != 0 -> MouseAction.MOVE
                                s.endsWith("M") -> MouseAction.PRESS
                                else -> MouseAction.RELEASE
                            }
                            return BobaEvent.Mouse(x, y, buttonInfo, action)
                        } else if (s == "A") return BobaEvent.Key(UP.key)
                        else if (s == "B") return BobaEvent.Key(DOWN.key)
                        else if (s == "C") return BobaEvent.Key(KeyCodes.RIGHT.key)
                        else if (s == "D") return BobaEvent.Key(KeyCodes.LEFT.key)
                    }
                }
                return BobaEvent.Key(firstChar)
            }
            return BobaEvent.Key(firstChar)
        }

        fun enableMouseTracking(allMotion: Boolean = false) {
            if (allMotion) {
                print("\u001b[?1003h") // Enable all motion tracking
            } else {
                print("\u001b[?1000h") // Enable basic mouse tracking
            }
            print("\u001b[?1006h") // Enable SGR extended mode
            System.out.flush()
        }

        fun disableMouseTracking() {
            print("\u001b[?1006l")
            print("\u001b[?1003l")
            print("\u001b[?1000l")
            System.out.flush()
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
