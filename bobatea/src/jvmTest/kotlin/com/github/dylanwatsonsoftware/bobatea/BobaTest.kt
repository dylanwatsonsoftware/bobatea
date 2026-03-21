package com.github.dylanwatsonsoftware.bobatea

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class BobaTest {
    @Test
    fun test() {
        val terminal = JvmTerminal()
        runBlocking {
            LoadingIndicator.runLoading("Loading yo!", LoaderStyle.SMALL_GREEN, terminal) {
                kotlinx.coroutines.delay(100)
            }
        }

        assertEquals(Boba::class.simpleName, "Boba")
    }
}
