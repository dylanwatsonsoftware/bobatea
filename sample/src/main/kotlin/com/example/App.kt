package com.example

import com.github.dylanwatsonsoftware.bobatea.*
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

                    LoadingIndicator.runLoading("Loading yo!", LoaderStyle.SMALL_GREEN, terminal) {
                        delay(2000)
                    }

                    terminal.write(Box("Welcome to Boba Tea!", borderStyle = BorderStyle.ROUNDED, color = ConsoleColors.CYAN).render() + "\n")
                    delay(2000)

                    ExpandableComponent(
                        title = "Click me to see something cool!",
                        content = Box(
                            "You expanded the section!\nThis is a box inside an expandable component.",
                            borderStyle = BorderStyle.DOUBLE,
                            color = ConsoleColors.GREEN
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

        private suspend fun nestedLayoutDemo(terminal: Terminal) {
            terminal.clear()
            val (width, height) = terminal.size()

            val box1 = Box("Box 1.1\nCol 1", borderStyle = BorderStyle.SINGLE, width = Dimension.Fixed(15))
            val box2 = Box("Box 1.2\nCol 2", borderStyle = BorderStyle.DOUBLE, width = Dimension.Fixed(15))
            val box3 = Box("Box 1.3\nCol 3", borderStyle = BorderStyle.ROUNDED, width = Dimension.Fixed(15))

            val inline1 = Inline(
                children = listOf(box1, box2, box3),
                padding = 1,
                borderStyle = BorderStyle.SINGLE,
                color = ConsoleColors.CYAN
            )

            val box4 = Box("Box 2.1\nCol A", borderStyle = BorderStyle.SINGLE, width = Dimension.Fixed(15))
            val box5 = Box("Box 2.2\nCol B", borderStyle = BorderStyle.DOUBLE, width = Dimension.Fixed(15))
            val box6 = Box("Box 2.3\nCol C", borderStyle = BorderStyle.ROUNDED, width = Dimension.Fixed(15))

            val inline2 = Inline(
                children = listOf(box4, box5, box6),
                padding = 1,
                borderStyle = BorderStyle.SINGLE,
                color = ConsoleColors.YELLOW
            )

            val root = Stack(
                children = listOf(
                    Box("Complex Nested Layout Demo\n(Stack of Inlines of Boxes)", padding = 1, color = ConsoleColors.GREEN),
                    inline1,
                    inline2
                ),
                width = Dimension.Fixed(minOf(width, 55)),
                borderStyle = BorderStyle.DOUBLE
            )

            terminal.write(root.render(width, height) + "\n")
            terminal.write("\nPress any key to continue...")
            terminal.readEvent()
        }

        private suspend fun layoutDemo(terminal: Terminal) {
            terminal.clear()
            val (width, height) = terminal.size()

            val root = buildDemoLayout(width)

            terminal.write(root.render(width, height) + "\n")

            terminal.write("\nPress any key to continue to the original demo...")
            terminal.readEvent()
        }

        private suspend fun coordinates(terminal: Terminal) {
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

            fun renderPos(x: Int, y: Int) {
                terminal.clear()
                val (width, height) = terminal.size()
                terminal.write("$x, $y\n")
                terminal.write(printGridWithHighlight(10, 10, y, x))
                terminal.write("Use ${ConsoleColors.color("UP/DOWN/LEFT/RIGHT", ConsoleColors.GREEN)} or ${ConsoleColors.color("WASD", ConsoleColors.GREEN)} to move\n")
                terminal.write("${ConsoleColors.color("SPACE/ENTER", ConsoleColors.GREEN)} to confirm\n")
            }

            var x = 0
            var y = 0

            renderPos(x, y)

            while (true) {
                val event = terminal.readEvent()
                if (event is BobaEvent.Key) {
                    if (KeyCodes.isDown(event.code)) renderPos(x, ++y)
                    else if (KeyCodes.isUp(event.code)) renderPos(x, --y)
                    else if (KeyCodes.isLeft(event.code)) renderPos(--x, y)
                    else if (KeyCodes.isRight(event.code)) renderPos(++x, y)
                    else if (event.code == KeyCodes.ENTER.key || event.code == KeyCodes.SPACE.key) return
                }
            }
        }
    }
}
