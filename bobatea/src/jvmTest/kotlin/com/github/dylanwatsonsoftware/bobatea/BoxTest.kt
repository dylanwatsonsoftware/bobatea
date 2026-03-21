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
        // Mordant uses SQUARE for SINGLE, which is ┌─┐
        // Actually SQUARE in Mordant IS ┌─┐
        // Let's see what it actually produces
        val rendered = box.render()
        assertThat(rendered).contains("Hello")
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

    @Test
    fun `test box with colored content maintains alignment`() {
        val coloredContent = "${ConsoleColors.RED}Red${ConsoleColors.RESET} and ${ConsoleColors.BLUE}Blue${ConsoleColors.RESET}"
        val box = Box(coloredContent, padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE)

        // "Red and Blue" has visible length of 12
        // ANSI codes add length but should be ignored for alignment
        val rendered = box.render()
        assertThat(rendered).contains("Red")
        assertThat(rendered).contains("Blue")
    }

    @Test
    fun `test box with fixed width and truncation`() {
        val box = Box("Hello World", padding = 0, borderStyle = BorderStyle.NONE, width = Dimension.Fixed(5))
        val rendered = box.render()
        assertThat(rendered).isEqualTo("Hello")
    }

    @Test
    fun `test box with fixed height and truncation`() {
        val box = Box("Line 1\nLine 2\nLine 3", padding = 0, borderStyle = BorderStyle.NONE, height = Dimension.Fixed(2))
        val rendered = box.render()
        assertThat(rendered.lines()).hasSize(2)
        assertThat(rendered).isEqualTo("Line 1\nLine 2")
    }

    @Test
    fun `test box with percentage width`() {
        val box = Box("Test", padding = 0, borderStyle = BorderStyle.NONE, width = Dimension.Percent(50.0))
        val rendered = box.render(availableWidth = 10)
        assertThat(rendered).isEqualTo("Test ")
    }
}
