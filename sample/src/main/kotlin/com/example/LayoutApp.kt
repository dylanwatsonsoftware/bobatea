package com.example

import com.github.dylanwatsonsoftware.bobatea.*
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.BLUE
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.CYAN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.YELLOW
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

                    val header = Box(
                        "Boba Tea Layout Demo",
                        borderStyle = BorderStyle.ROUNDED,
                        color = CYAN,
                        width = Dimension.Percent(100.0),
                        padding = 1
                    )

                    val leftPanel = Box(
                        "Sidebar\n- Item 1\n- Item 2",
                        borderStyle = BorderStyle.SINGLE,
                        color = YELLOW,
                        width = Dimension.Fixed(15),
                        height = Dimension.Fixed(10)
                    )

                    val mainContent = Box(
                        "Main Content Area\nThis is a demonstration of nested Stack and Inline layouts.\nThe sidebar and this content are inside an Inline component.",
                        borderStyle = BorderStyle.DOUBLE,
                        color = GREEN,
                        width = Dimension.Fixed(40),
                        height = Dimension.Fixed(10)
                    )

                    val middleRow = Inline(
                        children = listOf(leftPanel, mainContent)
                    )

                    val footer = Box(
                        "Footer Information",
                        borderStyle = BorderStyle.SINGLE,
                        color = BLUE,
                        width = Dimension.Percent(100.0)
                    )

                    val root = Stack(
                        children = listOf(header, middleRow, footer),
                        width = Dimension.Fixed(60)
                    )

                    terminal.write(root.render(60, 24) + "\n")

                    terminal.write("\nPress any key to exit...")
                    terminal.readEvent()
                }
            } finally {
                terminal.teardown()
            }
        }
    }
}
