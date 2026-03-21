
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal

fun main() {
    val tTrue = Terminal(ansiLevel = AnsiLevel.TRUECOLOR, hyperlinks = true)
    val tFalse = Terminal(ansiLevel = AnsiLevel.TRUECOLOR, hyperlinks = false)

    val sTrue = tTrue.render(TextColors.green("X"))
    val sFalse = tFalse.render(TextColors.green("X"))

    println("Hyperlinks True:  ${sTrue.replace("\u001b", "ESC")}")
    println("Hyperlinks False: ${sFalse.replace("\u001b", "ESC")}")
}
