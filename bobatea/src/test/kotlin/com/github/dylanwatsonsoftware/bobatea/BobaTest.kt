package com.johndoe.library

import com.github.dylanwatsonsoftware.bobatea.Boba
import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.nonBlockingTerminal
import kotlin.test.Test
import kotlin.test.assertEquals

class ViewTest {
    @Test
    fun test() {
        assertEquals(Boba::class.simpleName, "Boba")

        nonBlockingTerminal {
            println("Can print non-blocking")
        }
    }
}
