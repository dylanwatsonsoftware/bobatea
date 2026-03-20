package com.github.dylanwatsonsoftware.bobatea

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class BoxTest {
    @Test
    fun `test simple box with single border`() {
        val box = Box("Hello", padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE)
        val expected = """
            ┌─────┐
            │Hello│
            └─────┘
        """.trimIndent()
        assertThat(box.render()).isEqualTo(expected)
    }

    @Test
    fun `test box with padding`() {
        val box = Box("Hi", padding = 1, margin = 0, borderStyle = BorderStyle.SINGLE)
        val expected = """
            ┌────┐
            │    │
            │ Hi │
            │    │
            └────┘
        """.trimIndent()
        assertThat(box.render()).isEqualTo(expected)
    }

    @Test
    fun `test box with rounded corners`() {
        val box = Box("OK", padding = 0, margin = 0, borderStyle = BorderStyle.ROUNDED)
        val expected = """
            ╭──╮
            │OK│
            ╰──╯
        """.trimIndent()
        assertThat(box.render()).isEqualTo(expected)
    }

    @Test
    fun `test double border style`() {
        val box = Box("Test", padding = 0, margin = 0, borderStyle = BorderStyle.DOUBLE)
        val expected = """
            ╔════╗
            ║Test║
            ╚════╝
        """.trimIndent()
        assertThat(box.render()).isEqualTo(expected)
    }

    @Test
    fun `test box with multi-line content`() {
        val content = "Line 1\nLine 22"
        val box = Box(content, padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE)
        val expected = """
            ┌───────┐
            │Line 1 │
            │Line 22│
            └───────┘
        """.trimIndent()
        assertThat(box.render()).isEqualTo(expected)
    }
}
