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
                    // Initial Welcome & Loading
                    LoadingIndicator.runLoading("Initializing Boba Tea...", LoaderStyle.SMALL_GREEN, terminal) {
                        delay(1500)
                    }
                    terminal.clear()
                    terminal.write(Box("Welcome to Boba Tea!", borderStyle = BorderStyle.ROUNDED, color = ConsoleColors.CYAN, padding = 1).render() + "\n")
                    delay(1500)

                    mainMenu(terminal)
                }
            } finally {
                terminal.teardown()
            }
        }

        private suspend fun mainMenu(terminal: Terminal) {
            val options = listOf(
                "Layout Demo",
                "Nested Layout Demo",
                "Mordant Components (MD, Tables, Links)",
                "Expandable Section",
                "Coordinates Exploration",
                "Single Selection List",
                "Multi-Selection List",
                "Exit"
            )

            while (true) {
                terminal.clear()
                val (width, height) = terminal.size()

                val selection = SelectionList(
                    question = "Boba Tea Main Menu - Choose a screen:",
                    options = options,
                    borderStyle = BorderStyle.SINGLE,
                    padding = 1,
                    color = ConsoleColors.CYAN,
                    width = Dimension.Fixed(minOf(width, 50))
                ).interact(terminal)

                when (selection) {
                    "Layout Demo" -> layoutDemo(terminal)
                    "Nested Layout Demo" -> nestedLayoutDemo(terminal)
                    "Mordant Components (MD, Tables, Links)" -> mordantDemo(terminal)
                    "Expandable Section" -> expandableDemo(terminal)
                    "Coordinates Exploration" -> coordinates(terminal)
                    "Single Selection List" -> singleSelectDemo(terminal)
                    "Multi-Selection List" -> multiSelectDemo(terminal)
                    "Exit" -> return
                }
            }
        }

        private suspend fun renderWithBack(terminal: Terminal, component: BobaComponent) {
            val (width, height) = terminal.size()
            val back = BackButton(margin = 1)

            fun renderAll() {
                terminal.clear()
                terminal.write(component.render(width, height) + "\n")
                terminal.write(back.render(width, height) + "\n")
            }

            renderAll()
            terminal.enableMouseTracking(allMotion = true)
            try {
                while (true) {
                    val event = terminal.readEvent()
                    if (event is BobaEvent.Key && (event.code == 'q'.code || event.code == 'Q'.code)) return

                    if (event is BobaEvent.Mouse) {
                        val compLines = component.render(width, height).lines().size
                        val backY = compLines + 2
                        if (back.isClicked(event, 2, backY)) return
                    }
                }
            } finally {
                terminal.disableMouseTracking()
            }
        }

        private suspend fun nestedLayoutDemo(terminal: Terminal) {
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

            renderWithBack(terminal, root)
        }

        private suspend fun layoutDemo(terminal: Terminal) {
            val (width, height) = terminal.size()
            val root = buildDemoLayout(width)
            renderWithBack(terminal, root)
        }

        private suspend fun mordantDemo(terminal: Terminal) {
            val (width, height) = terminal.size()

            val md = Markdown("# Mordant Components\nThis screen showcases components powered by Mordant.")
            val link = Link("Visit Mordant GitHub", "https://github.com/ajalt/mordant")

            val stack = Stack(
                children = listOf(md, link),
                borderStyle = BorderStyle.DOUBLE,
                padding = 1,
                color = ConsoleColors.CYAN,
                width = Dimension.Fixed(minOf(width, 60))
            )

            renderWithBack(terminal, stack)
        }

        private suspend fun expandableDemo(terminal: Terminal) {
            ExpandableComponent(
                title = "Expandable Demo (Click or Space/Enter)",
                content = Box(
                    "You expanded the section!\nThis is a box inside an expandable component.",
                    borderStyle = BorderStyle.DOUBLE,
                    color = ConsoleColors.GREEN
                ).render(),
                padding = 1,
                borderStyle = BorderStyle.ROUNDED,
                color = ConsoleColors.BLUE
            ).interact(terminal)
        }

        private suspend fun singleSelectDemo(terminal: Terminal) {
            val selection = SelectionList(
                question = "Single Selection Demo:",
                options = listOf("Option A", "Option B", "Option C", "Go Back"),
                padding = 1,
                borderStyle = BorderStyle.SINGLE,
                color = ConsoleColors.YELLOW
            ).interact(terminal)
            if (selection != "Go Back") {
                terminal.write("You selected: $selection\n")
                delay(1000)
            }
        }

        private suspend fun multiSelectDemo(terminal: Terminal) {
            val multiSelections = MultiSelectionList(
                question = "Multi Selection Demo (Space to toggle, Enter to confirm):",
                options = listOf("Red", "Green", "Blue", "Cyan", "Yellow"),
                padding = 1,
                borderStyle = BorderStyle.DOUBLE,
                color = ConsoleColors.PURPLE
            ).interact(terminal)
            terminal.write("You selected: $multiSelections\n")
            delay(1500)
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
                    else if (event.code == 'q'.code || event.code == 'Q'.code) return
                    else if (event.code == KeyCodes.ENTER.key || event.code == KeyCodes.SPACE.key) return
                }
            }
        }
    }
}
