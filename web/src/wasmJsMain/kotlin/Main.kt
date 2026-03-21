import com.github.dylanwatsonsoftware.bobatea.*
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.CYAN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.YELLOW
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.color
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class WasmScreen {
    WELCOME, MENU, LAYOUT, MORDANT, EXPANDABLE, COORDINATES, SELECTION, MULTI, RESET
}

var currentWasmScreen = WasmScreen.WELCOME

fun main() {
    MainScope().launch {
        val terminal = WasmTerminal()
        val root = WasmAppRoot()
        Boba.run(terminal, root)
    }
}

class WasmAppRoot : BobaComponent() {
    private var welcomeElapsed = 0L
    private val loadingIndicator = LoadingIndicator("Booting TUI Engine...", LoaderStyle.SMALL_GREEN)
    private val mainMenu = SelectionList(
        question = "WASM Interactive Menu - Choose an example:",
        options = listOf(
            "Layout Showcase",
            "Mordant (Markdown & Links)",
            "Interactive Expandable",
            "Coordinate Explorer",
            "Selection Lists",
            "Multi-Selection",
            "Reset Demo"
        ),
        borderStyle = BorderStyle.SINGLE,
        padding = 1,
        color = CYAN,
        onSelect = { selection ->
            currentWasmScreen = when (selection) {
                "Layout Showcase" -> WasmScreen.LAYOUT
                "Mordant (Markdown & Links)" -> WasmScreen.MORDANT
                "Interactive Expandable" -> WasmScreen.EXPANDABLE
                "Coordinate Explorer" -> WasmScreen.COORDINATES
                "Selection Lists" -> WasmScreen.SELECTION
                "Multi-Selection" -> WasmScreen.MULTI
                "Reset Demo" -> WasmScreen.RESET
                else -> WasmScreen.MENU
            }
        }
    )
    private val backButton = BackButton(margin = 1, onClicked = { currentWasmScreen = WasmScreen.MENU })
    private val coords = WasmCoords()

    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val content = when (currentWasmScreen) {
            WasmScreen.WELCOME -> Stack(listOf(
                Box("Welcome to Boba Tea WASM Demo!", borderStyle = BorderStyle.ROUNDED, color = CYAN, padding = 1),
                loadingIndicator
            ))
            WasmScreen.MENU -> mainMenu
            WasmScreen.LAYOUT -> Stack(listOf(buildWasmLayout(), backButton))
            WasmScreen.MORDANT -> Stack(listOf(buildWasmMordant(), backButton))
            WasmScreen.EXPANDABLE -> Stack(listOf(buildWasmExpandable(), backButton))
            WasmScreen.COORDINATES -> Stack(listOf(coords, backButton))
            WasmScreen.SELECTION -> Stack(listOf(
                SelectionList("What's your favourite Boba?", listOf("Milk Tea", "Matcha", "Taro", "Brown Sugar"), onSelect = { currentWasmScreen = WasmScreen.MENU }),
                backButton
            ))
            WasmScreen.MULTI -> Stack(listOf(
                MultiSelectionList("Toppings:", listOf("Pearls", "Grass Jelly", "Pudding", "Aloe Vera", "Red Bean"), onComplete = { currentWasmScreen = WasmScreen.MENU }),
                backButton
            ))
            WasmScreen.RESET -> { currentWasmScreen = WasmScreen.WELCOME; welcomeElapsed = 0; mainMenu }
        }
        val output = content.render(availableWidth, availableHeight)
        widthPx = content.widthPx
        heightPx = content.heightPx
        return output
    }

    override fun tick(deltaMs: Long) {
        if (currentWasmScreen == WasmScreen.WELCOME) {
            welcomeElapsed += deltaMs
            loadingIndicator.tick(deltaMs)
            if (welcomeElapsed > 3500) {
                currentWasmScreen = WasmScreen.MENU
            }
        }
    }

    override fun onEvent(event: BobaEvent): Boolean {
        return when (currentWasmScreen) {
            WasmScreen.MENU -> mainMenu.onEvent(event)
            WasmScreen.LAYOUT -> backButton.onEvent(event)
            WasmScreen.MORDANT -> backButton.onEvent(event)
            WasmScreen.EXPANDABLE -> buildWasmExpandable().onEvent(event) || backButton.onEvent(event)
            WasmScreen.COORDINATES -> coords.onEvent(event) || backButton.onEvent(event)
            WasmScreen.SELECTION -> backButton.onEvent(event)
            WasmScreen.MULTI -> backButton.onEvent(event)
            else -> false
        }
    }
}

fun buildWasmLayout(): BobaComponent {
    val box1 = Box("Box 1.1\nCol 1", borderStyle = BorderStyle.SINGLE, width = Dimension.Fixed(15))
    val box2 = Box("Box 1.2\nCol 2", borderStyle = BorderStyle.DOUBLE, width = Dimension.Fixed(15))
    val inline1 = Inline(listOf(box1, box2), padding = 1, borderStyle = BorderStyle.SINGLE, color = CYAN)
    return Stack(listOf(Box("Complex Nested Layout Demo", padding = 1, color = GREEN), inline1), width = Dimension.Fixed(55), borderStyle = BorderStyle.DOUBLE)
}

fun buildWasmMordant(): BobaComponent {
    val md = Markdown("# Mordant Integration\nBoba Tea now uses **Mordant** for rendering.\n- Rich Markdown support\n- OSC 8 Hyperlinks\n- Flexible Tables")
    val link = Link("Visit Mordant GitHub", "https://github.com/ajalt/mordant")
    return Stack(listOf(md, link), borderStyle = BorderStyle.ROUNDED, padding = 1, color = CYAN, width = Dimension.Fixed(55))
}

fun buildWasmExpandable(): BobaComponent {
    return ExpandableComponent(
        title = "Click to reveal secret",
        content = Box("You found it!\nMordant makes this look great.", borderStyle = BorderStyle.DOUBLE, color = GREEN).render(),
        padding = 1,
        borderStyle = BorderStyle.SINGLE,
        color = com.github.dylanwatsonsoftware.bobatea.ConsoleColors.BLUE
    )
}

class WasmCoords : BobaComponent() {
    var xPos = 0
    var yPos = 0
    override fun render(availableWidth: Int?, availableHeight: Int?): String {
        val sb = StringBuilder()
        sb.append("${color("Step 3: Custom Navigation", YELLOW)}\n")
        sb.append("Position: $xPos, $yPos\n")
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                if (row == yPos && col == xPos) sb.append(" X ") else sb.append(" . ")
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
                KeyCodes.isDown(event.code) -> if (yPos < 7) yPos++
                KeyCodes.isUp(event.code) -> if (yPos > 0) yPos--
                KeyCodes.isLeft(event.code) -> if (xPos > 0) xPos--
                KeyCodes.isRight(event.code) -> if (xPos < 7) xPos++
            }
            return true
        }
        return false
    }
}
