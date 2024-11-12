package com.github.dylanwatsonsoftware.bobatea

import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.nonBlockingTerminal
import kotlin.test.Test
import kotlin.test.assertEquals

class BobaTest {
    @Test
    fun test() {
        assertEquals(Boba::class.simpleName, "Boba")

        nonBlockingTerminal {
            println("Can print non-blocking")
        }
    }
}
