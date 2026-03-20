package com.github.dylanwatsonsoftware.bobatea

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class LayoutTest {
    @Test
    fun `test simple stack`() {
        val stack = Stack(
            children = listOf(
                Box("One", padding = 0, borderStyle = BorderStyle.SINGLE),
                Box("Two", padding = 0, borderStyle = BorderStyle.SINGLE)
            )
        )
        val rendered = stack.render()
        val lines = rendered.lines()

        assertThat(lines).contains("┌───┐")
        assertThat(lines).contains("│One│")
        assertThat(lines).contains("└───┘")
        assertThat(lines).contains("┌───┐")
        assertThat(lines).contains("│Two│")
        assertThat(lines).contains("└───┘")
    }

    @Test
    fun `test simple inline`() {
        val inline = Inline(
            children = listOf(
                Box("A", padding = 0, borderStyle = BorderStyle.SINGLE),
                Box("B", padding = 0, borderStyle = BorderStyle.SINGLE)
            )
        )
        val rendered = inline.render()
        val lines = rendered.lines()

        // Expected:
        // ┌─┐┌─┐
        // │A││B│
        // └─┘└─┘
        assertThat(lines[0]).isEqualTo("┌─┐┌─┐")
        assertThat(lines[1]).isEqualTo("│A││B│")
        assertThat(lines[2]).isEqualTo("└─┘└─┘")
    }

    @Test
    fun `test stack with fixed width`() {
        val stack = Stack(
            children = listOf(
                Box("Hi", padding = 0, borderStyle = BorderStyle.SINGLE)
            ),
            width = Dimension.Fixed(10)
        )
        val rendered = stack.render()
        val lines = rendered.lines()

        // The Box inside will have its own natural width (4) unless we force it.
        // Stack passes available width to children.
        // Box with Auto width will use content width.

        // Wait, if Stack has width 10, it should probably be a box of width 10.
        assertThat(BobaComponent.visibleLength(lines[0])).isEqualTo(10)
    }

    @Test
    fun `test nested layouts`() {
        val layout = Stack(
            children = listOf(
                Inline(
                    children = listOf(
                        Box("1", padding = 0, borderStyle = BorderStyle.SINGLE),
                        Box("2", padding = 0, borderStyle = BorderStyle.SINGLE)
                    )
                ),
                Box("Bottom", padding = 0, borderStyle = BorderStyle.SINGLE)
            )
        )
        val rendered = layout.render()
        assertThat(rendered).contains("1")
        assertThat(rendered).contains("2")
        assertThat(rendered).contains("Bottom")
    }
}
