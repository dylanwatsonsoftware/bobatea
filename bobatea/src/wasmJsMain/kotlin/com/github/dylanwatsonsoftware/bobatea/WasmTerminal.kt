package com.github.dylanwatsonsoftware.bobatea

import kotlinx.coroutines.channels.Channel

private val _inputChannel = Channel<String>(Channel.UNLIMITED)

@OptIn(ExperimentalJsExport::class)
@JsExport
fun pushTerminalInput(data: String) {
    _inputChannel.trySend(data)
}

@JsFun("(data) => { if(window._bobaterm) window._bobaterm.write(data); }")
private external fun jsWrite(data: String)

class WasmTerminal : Terminal {
    override fun write(text: String) = jsWrite(text)

    override fun clear() {
        jsWrite("\u001b[2J\u001b[H")
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

    override fun size(): Pair<Int, Int> = 80 to 24

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
