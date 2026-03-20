package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextColors
import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class BoxTest {
    @Test
    fun `test simple box with single border`() {
        val box = Box("Hello", padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE)
        val rendered = box.render()
        assertThat(rendered).contains("Hello")
        // Mordant's borders for SQUARE are like single borders.
        // We'll just check if it contains common box characters.
        assertThat(rendered).contains("┌")
        assertThat(rendered).contains("┐")
    }

    @Test
    fun `test box with padding`() {
        val box = Box("Hi", padding = 1, margin = 0, borderStyle = BorderStyle.SINGLE)
        val rendered = box.render()
        assertThat(rendered).contains("Hi")
        // Check for empty line due to padding
        val lines = rendered.lines()
        assertThat(lines.size).isAtLeast(5) // 1 top, 1 bottom, 1 content, 2 padding
    }

    @Test
    fun `test box with rounded corners`() {
        val box = Box("OK", padding = 0, margin = 0, borderStyle = BorderStyle.ROUNDED)
        val rendered = box.render()
        assertThat(rendered).contains("╭")
        assertThat(rendered).contains("╮")
    }

    @Test
    fun `test box with styling`() {
        val box = Box("Test", style = com.github.ajalt.mordant.rendering.TextStyle(color = TextColors.red))
        val rendered = box.render()
        assertThat(rendered).contains("Test")
        // Check for ANSI codes if possible or just verify it renders
        assertThat(rendered).isNotEmpty()
    }
}
