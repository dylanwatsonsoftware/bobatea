package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.terminal.Terminal as MordantTerminal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

class JvmTerminal(
    override val mordant: MordantTerminal = MordantTerminal()
) : Terminal {
    private var savedTtyConfig: String? = null

    fun setup() {
        savedTtyConfig = stty("-g")
        setTerminalToCBreak()
    }

    fun teardown() {
        savedTtyConfig?.let {
            try {
                stty(it.trim())
            } catch (e: Exception) {
                System.err.println("Exception restoring tty config")
            }
        }
    }

    fun <T> use(block: () -> T): T {
        setup()
        try {
            return block()
        } finally {
            teardown()
        }
    }

    override fun write(text: String) {
        mordant.print(text)
    }

    override fun clear() {
        mordant.cursor.move {
            clearScreen()
            setPosition(0, 0)
        }
    }

    override suspend fun readEvent(): BobaEvent = withContext(Dispatchers.IO) {
        readEventBlocking()
    }

    override fun enableMouseTracking(allMotion: Boolean) {
        if (allMotion) {
            print("\u001b[?1003h")
        } else {
            print("\u001b[?1000h")
        }
        print("\u001b[?1006h")
        System.out.flush()
    }

    override fun disableMouseTracking() {
        print("\u001b[?1006l")
        print("\u001b[?1003l")
        print("\u001b[?1000l")
        System.out.flush()
    }

    override fun size(): Pair<Int, Int> {
        val size = mordant.size
        return size.width to size.height
    }

    fun getChar(): Int {
        while (true) {
            val read = System.`in`.read()
            if (read != -1) return read
        }
    }

    private fun readEventBlocking(): BobaEvent {
        val firstChar = getChar()
        if (firstChar == 27) { // ESC
            // Check for potential sequence after ESC
            var available = System.`in`.available()
            if (available == 0) {
                // Short sleep to allow potential subsequent characters of an escape sequence to arrive
                Thread.sleep(50)
                available = System.`in`.available()
            }

            if (available > 0) {
                val secondChar = System.`in`.read()
                if (secondChar == '['.code) {
                    val seq = StringBuilder()
                    var nextChar: Int
                    while (true) {
                        nextChar = System.`in`.read()
                        seq.append(nextChar.toChar())
                        if (nextChar in 64..126) break
                    }
                    val s = seq.toString()
                    if (s.startsWith("<")) { // SGR Mouse Protocol
                        val parts = s.substring(1, s.length - 1).split(";")
                        val buttonInfo = parts[0].toInt()
                        val x = parts[1].toInt()
                        val y = parts[2].toInt()
                        val action = when {
                            (buttonInfo and 32) != 0 -> MouseAction.MOVE
                            s.endsWith("M") -> MouseAction.PRESS
                            else -> MouseAction.RELEASE
                        }
                        return BobaEvent.Mouse(x, y, buttonInfo, action)
                    } else if (s == "A") return BobaEvent.Key(KeyCodes.UP.key)
                    else if (s == "B") return BobaEvent.Key(KeyCodes.DOWN.key)
                    else if (s == "C") return BobaEvent.Key(KeyCodes.RIGHT.key)
                    else if (s == "D") return BobaEvent.Key(KeyCodes.LEFT.key)
                }
            }
            return BobaEvent.Key(firstChar)
        }
        return BobaEvent.Key(firstChar)
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun setTerminalToCBreak() {
        stty("-icanon min 1")
        stty("-echo")
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun stty(args: String): String {
        val cmd = "stty $args < /dev/tty"
        return exec(arrayOf("sh", "-c", cmd))
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun exec(cmd: Array<String>): String {
        val bout = ByteArrayOutputStream()
        val p = Runtime.getRuntime().exec(cmd)
        var c: Int
        var `in` = p.inputStream
        while ((`in`.read().also { c = it }) != -1) {
            bout.write(c)
        }
        `in` = p.errorStream
        while ((`in`.read().also { c = it }) != -1) {
            bout.write(c)
        }
        p.waitFor()
        return String(bout.toByteArray())
    }
}
