package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.terminal.Terminal
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.DOWN
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.UP
import java.io.ByteArrayOutputStream
import java.io.IOException

class Boba {
    companion object {
        var terminal = Terminal()

        fun selectFromList(
            question: String,
            options: List<String>,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            style: TextStyle = TextStyle()
        ): String {
            return SelectionList(question, options, padding, margin, borderStyle, style).interact()
        }

        fun expandable(
            title: String,
            content: String,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            style: TextStyle = TextStyle()
        ) {
            ExpandableComponent(title, content, padding, margin, borderStyle, style).interact()
        }

        fun selectMultipleFromList(
            question: String,
            options: List<String>,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            style: TextStyle = TextStyle()
        ): MutableSet<String> {
            return MultiSelectionList(question, options, padding, margin, borderStyle, style).interact()
        }

        /**
         * Allows us to have a non-blocking terminal
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
            terminal.cursor.move {
                clearScreen()
                // Move cursor to top-left to be safe
                val os = System.getProperty("os.name").lowercase()
                if (os.contains("win")) {
                    // cls is handled by ProcessBuilder if needed, but Mordant should handle it.
                    // Fallback for Windows if clearScreen is not enough
                } else {
                    print("\u001b[H\u001b[2J")
                }
            }
            System.`out`.flush()
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
                print("\u001b[?1003h")
            } else {
                print("\u001b[?1000h")
            }
            print("\u001b[?1006h")
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
            stty("-icanon min 1")
            stty("-echo")
        }

        @Throws(IOException::class, InterruptedException::class)
        private fun stty(args: String): String {
            val cmd = "stty $args < /dev/tty"
            return exec(arrayOf("sh", "-c", cmd))
        }

        @Throws(IOException::class, InterruptedException::class)
        private fun exec(cmd: Array<String>): String {
            val process = ProcessBuilder(*cmd)
                .redirectErrorStream(true)
                .start()

            val bout = ByteArrayOutputStream()
            process.inputStream.use { input ->
                input.copyTo(bout)
            }
            process.waitFor()
            return bout.toString()
        }
    }
}
