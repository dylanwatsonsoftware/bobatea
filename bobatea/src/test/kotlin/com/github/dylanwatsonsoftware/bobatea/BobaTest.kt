package com.github.dylanwatsonsoftware.bobatea

import com.github.dylanwatsonsoftware.bobatea.Boba.Companion.nonBlockingTerminal
import com.github.dylanwatsonsoftware.bobatea.LoadingIndicator.Companion.runLoading
import kotlin.test.Test
import kotlin.test.assertEquals

class BobaTest {
    @Test
    fun test() {
        runLoading("Loading yo!", LoaderStyle.SMALL_GREEN) {
            Thread.sleep(10000)
        }

        assertEquals(Boba::class.simpleName, "Boba")

        nonBlockingTerminal {
            println("Can print non-blocking")
        }
    }
}
