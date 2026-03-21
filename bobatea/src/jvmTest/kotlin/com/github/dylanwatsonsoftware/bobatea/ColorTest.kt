package com.github.dylanwatsonsoftware.bobatea

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class ColorTest {
    @Test
    fun `test console colors produce ansi`() {
        val red = ConsoleColors.RED
        println("Red is: $red")
        assertThat(red).contains("\u001b[")
        assertThat(red).doesNotContain("TxtStyle")
    }
}
