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
            val startLine = 0 // question is on line 0 (after clear)

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

            enableMouseTracking()
            try {
                while (true) {
                    when (val event = readEvent()) {
                        is BobaEvent.Key -> {
                            when (event.code) {
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
                        is BobaEvent.Mouse -> {
                            if (event.action == MouseAction.PRESS) {
                                val clickedIndex = event.y - startLine - 1
                                if (clickedIndex in options.indices) {
                                    if (clickedIndex == currentIndex) {
                                        val selected = options[currentIndex]
                                        deselect()
                                        printList()
                                        return selected
                                    } else {
                                        currentIndex = clickedIndex
                                        printList()
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                disableMouseTracking()
            }
        }

        fun expandable(title: String, content: String) {
            var expanded = false
            val titleLine = 0

            fun printExpandable() {
                clear()
                val prefix = if (expanded) "[-] " else "[+] "
                println(color("$prefix$title", YELLOW))
                if (expanded) {
                    println(content)
                }
                println()
                println("Press ${color("SPACE/ENTER", GREEN)} or ${color("CLICK", GREEN)} to toggle")
                println("Press ${color("Q", GREEN)} to exit")
            }

            printExpandable()

            enableMouseTracking()
            try {
                while (true) {
                    when (val event = readEvent()) {
                        is BobaEvent.Key -> {
                            when (event.code) {
                                SPACE.key, ENTER.key -> {
                                    expanded = !expanded
                                    printExpandable()
                                }
                                'q'.toInt(), 'Q'.toInt() -> return
                            }
                        }
                        is BobaEvent.Mouse -> {
                            if (event.action == MouseAction.PRESS) {
                                if (event.y == titleLine + 1) {
                                    expanded = !expanded
                                    printExpandable()
                                }
                            }
                        }
                    }
                }
            } finally {
                disableMouseTracking()
            }
        }

        fun selectMultipleFromList(question: String, options: List<String>): MutableSet<String> {
            val selected = TreeSet<String>()
            var currentIndex = 0
            val startLine = 0

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

            enableMouseTracking()
            try {
                while (true) {
                    when (val event = readEvent()) {
                        is BobaEvent.Key -> {
                            when (event.code) {
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
                        is BobaEvent.Mouse -> {
                            if (event.action == MouseAction.PRESS) {
                                val clickedIndex = event.y - startLine - 1
                                if (clickedIndex in options.indices) {
                                    currentIndex = clickedIndex
                                    toggle(currentIndex)
                                    printList()
                                }
                            }
                        }
                    }
                }
            } finally {
                disableMouseTracking()
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
                            val button = parts[0].toInt()
                            val x = parts[1].toInt()
                            val y = parts[2].toInt()
                            val action = if (s.endsWith("M")) MouseAction.PRESS else MouseAction.RELEASE
                            return BobaEvent.Mouse(x, y, button, action)
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

        fun enableMouseTracking() {
            print("\u001b[?1000h") // Enable basic mouse tracking
            print("\u001b[?1006h") // Enable SGR extended mode
            System.out.flush()
        }

        fun disableMouseTracking() {
            print("\u001b[?1006l")
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
