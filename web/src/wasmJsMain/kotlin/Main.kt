import com.github.dylanwatsonsoftware.bobatea.BorderStyle
import com.github.dylanwatsonsoftware.bobatea.BobaEvent
import com.github.dylanwatsonsoftware.bobatea.Box
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.CYAN
import com.github.dylanwatsonsoftware.bobatea.ConsoleColors.Companion.GREEN
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

        LoadingIndicator.runLoading("Loading yo!", LoaderStyle.SMALL_GREEN, terminal) {
            delay(3000)
        }

        terminal.write(Box("Welcome to Boba Tea!", borderStyle = BorderStyle.ROUNDED, color = CYAN).render() + "\n")
        delay(2000)

        val expandable = ExpandableComponent(
            title = "Click me to see something cool!",
            content = Box(
                "You expanded the section!\nThis is a box inside an expandable component.",
                borderStyle = BorderStyle.DOUBLE,
                color = GREEN
            ).render()
        )
        expandable.expanded = true
        expandable.interact(terminal)

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
}

private suspend fun coordinates(terminal: WasmTerminal) {
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
        terminal.write("${color("SPACE/ENTER", GREEN)} to confirm\n")
    }

    var x = 0
    var y = 0

    render(x, y)

    while (true) {
        when ((terminal.readEvent() as? BobaEvent.Key)?.code) {
            KeyCodes.DOWN.key -> render(x, ++y)
            KeyCodes.UP.key -> render(x, --y)
            KeyCodes.LEFT.key -> render(--x, y)
            KeyCodes.RIGHT.key -> render(++x, y)
            KeyCodes.ENTER.key, KeyCodes.SPACE.key -> return
        }
    }
}
