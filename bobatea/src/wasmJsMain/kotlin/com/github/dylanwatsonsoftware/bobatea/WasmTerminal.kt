package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.PrintRequest
import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal
import com.github.ajalt.mordant.terminal.TerminalInfo
import com.github.ajalt.mordant.terminal.TerminalInterface
import kotlinx.coroutines.channels.Channel

private val _inputChannel = Channel<String>(Channel.UNLIMITED)

@OptIn(ExperimentalJsExport::class)
@JsExport
fun pushTerminalInput(data: String) {
    _inputChannel.trySend(data)
}

@JsFun("(data) => { if(window._bobaterm) window._bobaterm.write(data); }")
private external fun jsWrite(data: String)

@JsFun("() => { return window.innerWidth.toString() + ';' + window.innerHeight.toString(); }")
private external fun jsGetViewportSize(): String

class WasmTerminalInterface : TerminalInterface {
    override fun completePrintRequest(request: PrintRequest) {
        if (request.text.isNotEmpty()) jsWrite(request.text)
    }

    override val info: TerminalInfo = run {
        val (w, h) = try {
            val sizeStr = jsGetViewportSize()
            val parts = sizeStr.split(";")
            parts[0].toInt() / 10 to parts[1].toInt() / 20
        } catch (e: Exception) {
            80 to 24
        }
        TerminalInfo(
            width = w, height = h,
            ansiLevel = AnsiLevel.TRUECOLOR,
            ansiHyperLinks = false, outputInteractive = true, inputInteractive = true, crClearsLine = false
        )
    }

    override fun readLineOrNull(hideInput: Boolean): String? = null
}

class WasmTerminal(
    override val mordant: MordantTerminal = MordantTerminal(terminalInterface = WasmTerminalInterface())
) : Terminal {
    override fun write(text: String) {
        mordant.rawPrint(text)
    }

    override fun clear() {
        mordant.cursor.move {
            clearScreen()
            setPosition(0, 0)
        }
    }

    override fun redraw(text: String) {
        mordant.cursor.move { setPosition(0, 0) }
        mordant.rawPrint(text + "\u001b[J")
    }

    override suspend fun readEvent(): BobaEvent {
        val data = _inputChannel.receive()
        return parseInput(data)
    }

    override fun enableMouseTracking(allMotion: Boolean) {
        if (allMotion) jsWrite("\u001b[?1003h") else jsWrite("\u001b[?1000h")
        jsWrite("\u001b[?1006h") // SGR extended mouse mode — xterm.js will send mouse events via onData
    }

    override fun disableMouseTracking() {
        jsWrite("\u001b[?1006l")
        jsWrite("\u001b[?1003l")
        jsWrite("\u001b[?1000l")
    }

    override fun size(): Pair<Int, Int> {
        return mordant.info.width to mordant.info.height
    }

    private fun parseInput(data: String): BobaEvent {
        if (data.isEmpty()) return BobaEvent.Key(0)

        // Handle escape sequences
        if (data.startsWith("\u001b[")) {
            val seq = data.substring(2)
            // SGR mouse protocol: \u001b[<...M or \u001b[<...m
            if (seq.startsWith("<")) {
                val isPress = seq.endsWith("M")
                val inner = seq.substring(1, seq.length - 1)
                val parts = inner.split(";")
                if (parts.size == 3) {
                    val buttonInfo = parts[0].toIntOrNull() ?: 0
                    val x = parts[1].toIntOrNull() ?: 0
                    val y = parts[2].toIntOrNull() ?: 0
                    val action = when {
                        (buttonInfo and 32) != 0 -> MouseAction.MOVE
                        isPress -> MouseAction.PRESS
                        else -> MouseAction.RELEASE
                    }
                    return BobaEvent.Mouse(x, y, buttonInfo, action)
                }
            }
            return when (seq) {
                "A" -> BobaEvent.Key(KeyCodes.UP.key)
                "B" -> BobaEvent.Key(KeyCodes.DOWN.key)
                "C" -> BobaEvent.Key(KeyCodes.RIGHT.key)
                "D" -> BobaEvent.Key(KeyCodes.LEFT.key)
                else -> BobaEvent.Key(data[0].code)
            }
        }

        return BobaEvent.Key(data[0].code)
    }
}
