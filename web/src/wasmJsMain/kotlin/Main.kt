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
import com.github.dylanwatsonsoftware.bobatea.MultiSelectionList
import com.github.dylanwatsonsoftware.bobatea.SelectionList
import com.github.dylanwatsonsoftware.bobatea.WasmTerminal
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    MainScope().launch {
        val terminal = WasmTerminal()

        // 1. Welcome
        terminal.write(Box("Welcome to Boba Tea Live Demo!", borderStyle = BorderStyle.ROUNDED, color = CYAN, padding = 1).render() + "\n")
        delay(2000)

        // 2. Loading
        terminal.write("${color("Step 1: Loading Indicators", GREEN)}\n")
        LoadingIndicator.runLoading("Processing your request...", LoaderStyle.SMALL_GREEN, terminal) {
            delay(3000)
        }
        terminal.write("\n")
        delay(1500)

        // 3. Expandable
        terminal.write("${color("Step 2: Interactive Components", BLUE)}\n")
        terminal.write("Try expanding the section below (Click or Space/Enter)\n")
        ExpandableComponent(
            title = "Click me to see a secret!",
            content = Box(
                "Surprise! You found the hidden box.\nBoba Tea makes it easy to create complex TUI layouts.",
                borderStyle = BorderStyle.DOUBLE,
                color = GREEN,
                padding = 1
            ).render()
        ).interact(terminal)
        terminal.write("\n")
        delay(1500)

        // 4. Coordinates
        terminal.write("${color("Step 3: Custom Navigation", YELLOW)}\n")
        terminal.write("Use WASD or Arrows to move the 'X' and press Enter to confirm.\n")
        delay(1000)
        coordinates(terminal)
        terminal.write("\n")
        delay(1500)

        // 5. Selection
        terminal.write("${color("Step 4: Selection Lists", CYAN)}\n")
        val selection = SelectionList(
            question = "What's your favourite Boba flavor?",
            options = listOf("Milk Tea", "Taro", "Matcha", "Fruit Tea", "Brown Sugar"),
        ).interact(terminal)
        terminal.write("Excellent choice: $selection!\n")
        delay(1500)

        // 6. Multi-Selection
        terminal.write("${color("Step 5: Multi-Selection", BLUE)}\n")
        val multiSelections = MultiSelectionList(
            question = "Select your favourite toppings (Space to toggle, Enter to confirm):",
            options = listOf("Pearls", "Grass Jelly", "Pudding", "Aloe Vera", "Red Bean"),
        ).interact(terminal)
        terminal.write("You selected: ${multiSelections.joinToString(", ")}\n")
        delay(2000)

        terminal.write("\n" + color("Demo Complete! Scroll down to see more examples.", GREEN) + "\n")
    }
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
