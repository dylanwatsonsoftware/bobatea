import com.github.dylanwatsonsoftware.bobatea.BorderStyle
import com.github.dylanwatsonsoftware.bobatea.BobaEvent
import com.github.dylanwatsonsoftware.bobatea.Box
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.BLUE
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.CYAN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.YELLOW
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.color
import com.github.dylanwatsonsoftware.bobatea.ExpandableComponent
import com.github.dylanwatsonsoftware.bobatea.KeyCodes
import com.github.dylanwatsonsoftware.bobatea.LoaderStyle
import com.github.dylanwatsonsoftware.bobatea.LoadingIndicator
import com.github.dylanwatsonsoftware.bobatea.*
import com.github.dylanwatsonsoftware.bobatea.MultiSelectionList
import com.github.dylanwatsonsoftware.bobatea.SelectionList
import com.github.dylanwatsonsoftware.bobatea.WasmTerminal
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    MainScope().launch {
        val terminal = WasmTerminal()

        // 1. Welcome & Initial Loading
        terminal.write(Box("Welcome to Boba Tea WASM Demo!", borderStyle = BorderStyle.ROUNDED, color = CYAN, padding = 1).render() + "\n")
        delay(1500)

        LoadingIndicator.runLoading("Booting TUI Engine...", LoaderStyle.SMALL_GREEN, terminal) {
            delay(2000)
        }
        terminal.write("\n")

        mainMenu(terminal)
    }
}

private suspend fun mainMenu(terminal: Terminal) {
    val options = listOf(
        "Layout Showcase",
        "Mordant (Markdown & Links)",
        "Interactive Expandable",
        "Coordinate Explorer",
        "Selection Lists",
        "Multi-Selection",
        "Reset Demo"
    )

    while (true) {
        terminal.clear()
        val (width, height) = terminal.size()

        val selection = SelectionList(
            question = "WASM Interactive Menu - Choose an example:",
            options = options,
            borderStyle = BorderStyle.SINGLE,
            padding = 1,
            color = CYAN,
            width = Dimension.Fixed(minOf(width, 50))
        ).interact(terminal)

        when (selection) {
            "Layout Showcase" -> layoutShowcase(terminal)
            "Mordant (Markdown & Links)" -> mordantShowcase(terminal)
            "Interactive Expandable" -> expandableShowcase(terminal)
            "Coordinate Explorer" -> coordinatesShowcase(terminal)
            "Selection Lists" -> selectionShowcase(terminal)
            "Multi-Selection" -> multiSelectionShowcase(terminal)
            "Reset Demo" -> return
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
            // For WASM, we use a fixed estimate for back button position if size detection is tricky
            if (event is BobaEvent.Mouse && back.isClicked(event, height - 3)) return
        }
    } finally {
        terminal.disableMouseTracking()
    }
}

private suspend fun layoutShowcase(terminal: Terminal) {
    val box1 = Box("Box 1.1\nCol 1", borderStyle = BorderStyle.SINGLE, width = Dimension.Fixed(15))
    val box2 = Box("Box 1.2\nCol 2", borderStyle = BorderStyle.DOUBLE, width = Dimension.Fixed(15))
    val box3 = Box("Box 1.3\nCol 3", borderStyle = BorderStyle.ROUNDED, width = Dimension.Fixed(15))

    val inline1 = Inline(children = listOf(box1, box2, box3), padding = 1, borderStyle = BorderStyle.SINGLE, color = CYAN)

    val root = Stack(
        children = listOf(
            Box("Complex Nested Layout Demo", padding = 1, color = GREEN),
            inline1
        ),
        width = Dimension.Fixed(55),
        borderStyle = BorderStyle.DOUBLE
    )

    renderWithBack(terminal, root)
}

private suspend fun mordantShowcase(terminal: Terminal) {
    val md = Markdown("# Mordant Integration\nBoba Tea now uses **Mordant** for rendering.\n- Rich Markdown support\n- OSC 8 Hyperlinks\n- Flexible Tables")
    val link = Link("Visit Mordant GitHub", "https://github.com/ajalt/mordant")
    val tbl = Table(listOf("Component", "Status"), listOf(listOf("Markdown", "✅"), listOf("Table", "✅"), listOf("Link", "✅")))

    val stack = Stack(
        children = listOf(md, link, tbl),
        borderStyle = BorderStyle.ROUNDED,
        padding = 1,
        color = CYAN,
        width = Dimension.Fixed(55)
    )

    renderWithBack(terminal, stack)
}

private suspend fun expandableShowcase(terminal: Terminal) {
    ExpandableComponent(
        title = "Click to reveal secret",
        content = Box("You found it!\nMordant makes this look great.", borderStyle = BorderStyle.DOUBLE, color = GREEN).render(),
        padding = 1,
        borderStyle = BorderStyle.SINGLE,
        color = BLUE
    ).interact(terminal)
}

private suspend fun selectionShowcase(terminal: Terminal) {
    val selection = SelectionList(
        question = "What's your favourite Boba?",
        options = listOf("Milk Tea", "Matcha", "Taro", "Brown Sugar", "Go Back"),
    ).interact(terminal)
    if (selection != "Go Back") {
        terminal.write("Excellent: $selection!\n")
        delay(1000)
    }
}

private suspend fun multiSelectionShowcase(terminal: Terminal) {
    val multiSelections = MultiSelectionList(
        question = "Toppings (Space to toggle, Enter to confirm):",
        options = listOf("Pearls", "Grass Jelly", "Pudding", "Aloe Vera", "Red Bean"),
    ).interact(terminal)
    terminal.write("You selected: ${multiSelections.joinToString(", ")}\n")
    delay(1500)
}

private suspend fun coordinatesShowcase(terminal: Terminal) {
    coordinates(terminal as WasmTerminal)
}

private suspend fun coordinates(terminal: WasmTerminal) {
    fun printGridWithHighlight(rows: Int, cols: Int, highlightRow: Int, highlightCol: Int): String {
        val sb = StringBuilder()
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (row == highlightRow && col == highlightCol) {
                    sb.append(" X ")
                } else {
                    sb.append(" . ")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    fun render(x: Int, y: Int) {
        terminal.clear()
        terminal.write("${color("Step 3: Custom Navigation", YELLOW)}\n")
        terminal.write("Position: $x, $y\n")
        terminal.write(printGridWithHighlight(8, 8, y, x))
        terminal.write("Use ${color("UP/DOWN/LEFT/RIGHT", GREEN)} or ${color("WASD", GREEN)} to move\n")
        terminal.write("${color("SPACE/ENTER", GREEN)} to confirm\n")
    }

    var x = 0
    var y = 0

    render(x, y)

    while (true) {
        val event = terminal.readEvent()
        if (event is BobaEvent.Key) {
            when {
                KeyCodes.isDown(event.code) -> if (y < 7) render(x, ++y)
                KeyCodes.isUp(event.code) -> if (y > 0) render(x, --y)
                KeyCodes.isLeft(event.code) -> if (x > 0) render(--x, y)
                KeyCodes.isRight(event.code) -> if (x < 7) render(++x, y)
                event.code == KeyCodes.ENTER.key || event.code == KeyCodes.SPACE.key -> {
                    terminal.clear()
                    terminal.write("${color("Step 3: Custom Navigation", YELLOW)}\n")
                    terminal.write("Confirmed position: $x, $y\n")
                    return
                }
            }
        }
    }
}
