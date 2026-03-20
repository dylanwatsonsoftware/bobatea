package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.terminal.Terminal
import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class BoxTest {
    @Test
    fun `test simple box with single border`() {
        val box = Box("Hello", padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE)
        val rendered = box.render()
        assertThat(rendered).contains("Hello")
        assertThat(rendered).contains("┌")
        assertThat(rendered).contains("┐")
    }

    @Test
    fun `test box with double border`() {
        val box = Box("Double", padding = 0, margin = 0, borderStyle = BorderStyle.DOUBLE)
        val rendered = box.render()
        assertThat(rendered).contains("Double")
        assertThat(rendered).contains("╔")
        assertThat(rendered).contains("╗")
        assertThat(rendered).contains("╚")
        assertThat(rendered).contains("╝")
        assertThat(rendered).contains("═")
        assertThat(rendered).contains("║")
    }

    @Test
    fun `test box with multi-line content`() {
        val content = "Line 1\nLine 2"
        val box = Box(content, padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE)
        val rendered = box.render()
        assertThat(rendered).contains("Line 1")
        assertThat(rendered).contains("Line 2")
        val lines = rendered.lines().filter { it.isNotBlank() }
        assertThat(lines.size).isEqualTo(4) // 2 borders + 2 content lines
    }

    @Test
    fun `test box with padding`() {
        val box = Box("Hi", padding = 1, margin = 0, borderStyle = BorderStyle.SINGLE)
        val rendered = box.render()
        assertThat(rendered).contains("Hi")
        val lines = rendered.lines()
        assertThat(lines.size).isAtLeast(5)
    }

    @Test
    fun `test box with styling`() {
        // Force ANSI support for testing
        Boba.terminal = Terminal(ansiLevel = AnsiLevel.TRUECOLOR)
        val box = Box("Test", style = TextStyle(color = TextColors.red))
        val rendered = box.render()
        assertThat(rendered).contains("Test")
        assertThat(rendered).contains("\u001b[31m")
    }
}
