package com.github.dylanwatsonsoftware.bobatea

import kotlinx.coroutines.delay

class Boba {
    companion object {
        suspend fun run(terminal: Terminal, root: BobaComponent) {
            terminal.clear()
            terminal.enableMouseTracking(true)
            var lastTick = currentTimeMillis()

            // Initialize root coordinates to match terminal 1-based system
            root.x = 1
            root.y = 1

            try {
                while (true) {
                    val (width, height) = terminal.size()
                    val output = root.render(width, height)
                    terminal.clear()
                    terminal.write(output)

                    val event = terminal.readEvent(50)
                    if (event != null) {
                        root.onEvent(event)
                    }

                    val now = currentTimeMillis()
                    root.tick(now - lastTick)
                    lastTick = now

                    // Yield to other coroutines
                    delay(10)
                }
            } finally {
                terminal.disableMouseTracking()
            }
        }
    }
}

internal expect fun currentTimeMillis(): Long
