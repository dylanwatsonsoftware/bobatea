package com.example

import com.github.dylanwatsonsoftware.bobatea.BorderStyle
import com.github.dylanwatsonsoftware.bobatea.Box
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.CYAN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.color
import com.github.dylanwatsonsoftware.bobatea.ExpandableComponent
import com.github.dylanwatsonsoftware.bobatea.JvmTerminal
import com.github.dylanwatsonsoftware.bobatea.KeyCodes
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.ENTER
import com.github.dylanwatsonsoftware.bobatea.KeyCodes.SPACE
import com.github.dylanwatsonsoftware.bobatea.LoaderStyle.SMALL_GREEN
import com.github.dylanwatsonsoftware.bobatea.LoadingIndicator
import com.github.dylanwatsonsoftware.bobatea.MultiSelectionList
import com.github.dylanwatsonsoftware.bobatea.*
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.BLUE
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.CYAN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.YELLOW
import com.github.dylanwatsonsoftware.bobatea.SelectionList
import com.github.dylanwatsonsoftware.bobatea.BobaEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val terminal = JvmTerminal()
            terminal.setup()
            try {
                runBlocking {
                    layoutDemo(terminal)
                    nestedLayoutDemo(terminal)

                    LoadingIndicator.runLoading("Loading yo!", SMALL_GREEN, terminal) {
                        delay(2000)
                    }

                    terminal.write(Box("Welcome to Boba Tea!", borderStyle = BorderStyle.ROUNDED, color = CYAN).render() + "\n")
                    delay(2000)

                    ExpandableComponent(
                        title = "Click me to see something cool!",
                        content = Box(
                            "You expanded the section!\nThis is a box inside an expandable component.",
                            borderStyle = BorderStyle.DOUBLE,
                            color = GREEN
                        ).render()
                    ).interact(terminal)

                    coordinates(terminal)

                    val selection = SelectionList(
                        question = "What's your favourite number?",
                        options = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"),
                    ).interact(terminal)
                    terminal.write("You selected: $selection\n")

                    val multiSelections = MultiSelectionList(
                        question = "What are all your favourite numbers?",
                        options = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"),
                    ).interact(terminal)
                    terminal.write("You selected: $multiSelections\n")
                }
            } finally {
                terminal.teardown()
            }
        }

        private suspend fun nestedLayoutDemo(terminal: JvmTerminal) {
            terminal.clear()

            val box1 = Box("Box 1.1\nCol 1", borderStyle = BorderStyle.SINGLE, width = Dimension.Fixed(15))
            val box2 = Box("Box 1.2\nCol 2", borderStyle = BorderStyle.DOUBLE, width = Dimension.Fixed(15))
            val box3 = Box("Box 1.3\nCol 3", borderStyle = BorderStyle.ROUNDED, width = Dimension.Fixed(15))

            val inline1 = Inline(
                children = listOf(box1, box2, box3),
                padding = 1,
                borderStyle = BorderStyle.SINGLE,
                color = CYAN
            )

            val box4 = Box("Box 2.1\nCol A", borderStyle = BorderStyle.SINGLE, width = Dimension.Fixed(15))
            val box5 = Box("Box 2.2\nCol B", borderStyle = BorderStyle.DOUBLE, width = Dimension.Fixed(15))
            val box6 = Box("Box 2.3\nCol C", borderStyle = BorderStyle.ROUNDED, width = Dimension.Fixed(15))

            val inline2 = Inline(
                children = listOf(box4, box5, box6),
                padding = 1,
                borderStyle = BorderStyle.SINGLE,
                color = YELLOW
            )

            val root = Stack(
                children = listOf(
                    Box("Complex Nested Layout Demo\n(Stack of Inlines of Boxes)", padding = 1, color = GREEN),
                    inline1,
                    inline2
                ),
                width = Dimension.Fixed(55),
                borderStyle = BorderStyle.DOUBLE
            )

            terminal.write(root.render(60, 24) + "\n")
            terminal.write("\nPress any key to continue...")
            terminal.readEvent()
        }

        private suspend fun layoutDemo(terminal: JvmTerminal) {
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

            terminal.write("\nPress any key to continue to the original demo...")
            terminal.readEvent()
        }

        private suspend fun coordinates(terminal: JvmTerminal) {
            fun printGridWithHighlight(rows: Int, cols: Int, highlightRow: Int, highlightCol: Int): String {
                val sb = StringBuilder()
                for (row in 0 until rows) {
                    for (col in 0 until cols) {
                        if (row == highlightRow && col == highlightCol) {
                            sb.append(" X ")
                        } else {
                            sb.append("   ")
                        }
                    }
                    sb.append("\n")
                }
                return sb.toString()
            }

            fun render(x: Int, y: Int) {
                terminal.clear()
                terminal.write("$x, $y\n")
                terminal.write(printGridWithHighlight(10, 10, y, x))
                terminal.write("Use ${color("UP/DOWN/LEFT/RIGHT", GREEN)} or ${color("WASD", GREEN)} to move\n")
                terminal.write("${color("SPACE/ENTER", GREEN)} to confirm\n")
            }

            var x = 0
            var y = 0

            render(x, y)

            while (true) {
                val event = terminal.readEvent()
                if (event is BobaEvent.Key) {
                    if (KeyCodes.isDown(event.code)) render(x, ++y)
                    else if (KeyCodes.isUp(event.code)) render(x, --y)
                    else if (KeyCodes.isLeft(event.code)) render(--x, y)
                    else if (KeyCodes.isRight(event.code)) render(++x, y)
                    else if (event.code == ENTER.key || event.code == SPACE.key) return
                }
            }
        }
    }
}
