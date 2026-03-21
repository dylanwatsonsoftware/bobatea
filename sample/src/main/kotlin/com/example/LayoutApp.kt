package com.example

import com.github.dylanwatsonsoftware.bobatea.*
import kotlinx.coroutines.runBlocking

class LayoutApp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val terminal = JvmTerminal()
            terminal.setup()
            try {
                runBlocking {
                    terminal.clear()
                    val (width, height) = terminal.size()

                    val root = buildDemoLayout(width)

                    terminal.write(root.render(width, height) + "\n")

                    terminal.write("\nPress any key to exit...")
                    terminal.readEvent()
                }
            } finally {
                terminal.teardown()
            }
        }
    }
}
