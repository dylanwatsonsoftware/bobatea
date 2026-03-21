package com.github.dylanwatsonsoftware.bobatea

import kotlinx.coroutines.delay

class Boba {
    companion object {
        suspend fun run(terminal: Terminal, root: BobaComponent) {
            terminal.clear()
            var lastTick = currentTimeMillis()

            while (true) {
                val (width, height) = terminal.size()
                val output = root.render(width, height)
                terminal.clear()
                terminal.write(output)

                val event = terminal.readEvent(50)
                if (event != null) {
                    if (root.onEvent(event)) {
                        // event handled, maybe we want to force a re-render or something
                    }
                }

                val now = currentTimeMillis()
                root.tick(now - lastTick)
                lastTick = now

                // Yield to other coroutines
                delay(10)
            }
        }
    }
}

internal expect fun currentTimeMillis(): Long
