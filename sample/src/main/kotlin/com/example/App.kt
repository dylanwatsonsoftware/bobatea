package com.example

import com.github.dylanwatsonsoftware.bobatea.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

enum class Screen {
    MAIN, LAYOUT, NESTED, MORDANT, EXPANDABLE, COORDINATES, SINGLE_SELECT, MULTI_SELECT, EXIT
}

class App {
    companion object {
        var currentScreen = Screen.MAIN
        var lastSelection = ""
        var lastMultiSelection = setOf<String>()

        @JvmStatic
        fun main(args: Array<String>) {
            val terminal = JvmTerminal()
            terminal.setup()
            try {
                runBlocking {
                    val root = AppRoot()
                    Boba.run(terminal, root)
                }
            } finally {
                terminal.teardown()
            }
        }
    }

    class AppRoot : BobaComponent() {
        private val mainMenu = SelectionList(
            question = "Boba Tea Main Menu - Choose a screen:",
            options = listOf(
                "Layout Demo",
                "Nested Layout Demo",
                "Mordant Components (MD, Tables, Links)",
                "Expandable Section",
                "Coordinates Exploration",
                "Single Selection List",
                "Multi-Selection List",
                "Exit"
            ),
            borderStyle = BorderStyle.ROUNDED,
            padding = 1,
            color = ConsoleColors.BOBA_PINK,
            onSelect = { selection ->
                App.currentScreen = when (selection) {
                    "Layout Demo" -> Screen.LAYOUT
                    "Nested Layout Demo" -> Screen.NESTED
                    "Mordant Components (MD, Tables, Links)" -> Screen.MORDANT
                    "Expandable Section" -> Screen.EXPANDABLE
                    "Coordinates Exploration" -> Screen.COORDINATES
                    "Single Selection List" -> Screen.SINGLE_SELECT
                    "Multi-Selection List" -> Screen.MULTI_SELECT
                    "Exit" -> Screen.EXIT
                    else -> Screen.MAIN
                }
            }
        )

        private val backButton = BackButton(margin = 1, onClicked = { App.currentScreen = Screen.MAIN })
        private val coordinatesDemo = CoordinatesDemo()
        private val expandableLayout = buildExpandableLayout()

        override fun render(availableWidth: Int?, availableHeight: Int?): String {
            if (App.currentScreen == Screen.EXIT) {
                kotlin.system.exitProcess(0)
            }

            val content = when (App.currentScreen) {
                Screen.MAIN -> mainMenu
                Screen.LAYOUT -> Stack(listOf(buildDemoLayout(availableWidth ?: 80), backButton))
                Screen.NESTED -> Stack(listOf(buildNestedLayout(availableWidth ?: 80), backButton))
                Screen.MORDANT -> Stack(listOf(buildMordantLayout(availableWidth ?: 80), backButton))
                Screen.EXPANDABLE -> Stack(listOf(expandableLayout, backButton))
                Screen.COORDINATES -> Stack(listOf(coordinatesDemo, backButton))
                Screen.SINGLE_SELECT -> Stack(listOf(
                    SelectionList("Single Selection Demo:", listOf("Option A", "Option B", "Option C"),
                        onSelect = { App.lastSelection = it; App.currentScreen = Screen.MAIN }),
                    backButton
                ))
                Screen.MULTI_SELECT -> Stack(listOf(
                    MultiSelectionList("Multi Selection Demo:", listOf("Red", "Green", "Blue", "Cyan", "Yellow"),
                        onComplete = { App.lastMultiSelection = it; App.currentScreen = Screen.MAIN }),
                    backButton
                ))
                else -> mainMenu
            }

            val result = content.render(availableWidth, availableHeight)
            widthPx = content.widthPx
            heightPx = content.heightPx
            return result
        }

        override fun onEvent(event: BobaEvent): Boolean {
            return when (App.currentScreen) {
                Screen.MAIN -> mainMenu.onEvent(event)
                Screen.LAYOUT -> backButton.onEvent(event)
                Screen.NESTED -> backButton.onEvent(event)
                Screen.MORDANT -> backButton.onEvent(event)
                Screen.EXPANDABLE -> expandableLayout.onEvent(event) || backButton.onEvent(event)
                Screen.COORDINATES -> coordinatesDemo.onEvent(event) || backButton.onEvent(event)
                Screen.SINGLE_SELECT -> backButton.onEvent(event)
                Screen.MULTI_SELECT -> backButton.onEvent(event)
                else -> false
            }
        }
    }
}

fun buildNestedLayout(width: Int): BobaComponent {
    val box1 = Box("Box 1.1\nCol 1", borderStyle = BorderStyle.SINGLE, width = Dimension.Fixed(15))
    val box2 = Box("Box 1.2\nCol 2", borderStyle = BorderStyle.DOUBLE, width = Dimension.Fixed(15))
    val box3 = Box("Box 1.3\nCol 3", borderStyle = BorderStyle.ROUNDED, width = Dimension.Fixed(15))

    val inline1 = Inline(
        children = listOf(box1, box2, box3),
        padding = 1,
        borderStyle = BorderStyle.SINGLE,
        color = ConsoleColors.BOBA_CYAN
    )

    return Stack(
        children = listOf(
            Box("Complex Nested Layout Demo", padding = 1, color = ConsoleColors.BOBA_GREEN),
            inline1
        ),
        width = Dimension.Fixed(minOf(width, 55)),
        borderStyle = BorderStyle.DOUBLE
    )
}

fun buildMordantLayout(width: Int): BobaComponent {
    val md = Markdown("# Mordant Components\nThis screen showcases components powered by Mordant.")
    val link = Link("Visit Mordant GitHub", "https://github.com/ajalt/mordant")

    return Stack(
        children = listOf(md, link),
        borderStyle = BorderStyle.DOUBLE,
        padding = 1,
        color = ConsoleColors.BOBA_CYAN,
        width = Dimension.Fixed(minOf(width, 60))
    )
}

fun buildExpandableLayout(): BobaComponent {
    return ExpandableComponent(
        title = "Expandable Demo (Click or Space/Enter)",
        content = Box(
            "You expanded the section!\nThis is a box inside an expandable component.",
            borderStyle = BorderStyle.DOUBLE,
            color = ConsoleColors.BOBA_GREEN
        ).render(),
        padding = 1,
        borderStyle = BorderStyle.ROUNDED,
        color = ConsoleColors.BOBA_PURPLE
    )
}

class CoordinatesDemo : BobaComponent() {
    var xPos = 0
    var yPos = 0

    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val sb = StringBuilder()
        sb.append(com.github.ajalt.mordant.rendering.TextColors.rgb("#A7FF00")("$xPos, $yPos\n"))
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                if (row == yPos && col == xPos) sb.append(com.github.ajalt.mordant.rendering.TextColors.rgb("#F179B4")(" X ")) else sb.append(" . ")
            }
            sb.append("\n")
        }
        val output = sb.toString()
        val lines = output.lines()
        widthPx = lines.maxOfOrNull { visibleLength(it) } ?: 0
        heightPx = lines.size
        return output
    }

    override fun onEvent(event: BobaEvent): Boolean {
        if (event is BobaEvent.Key) {
            when {
                KeyCodes.isDown(event.code) -> yPos++
                KeyCodes.isUp(event.code) -> yPos--
                KeyCodes.isLeft(event.code) -> xPos--
                KeyCodes.isRight(event.code) -> xPos++
            }
            return true
        }
        return false
    }
}
