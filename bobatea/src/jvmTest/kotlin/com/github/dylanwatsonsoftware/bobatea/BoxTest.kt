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

    @Test
    fun `test box with colored content maintains alignment`() {
        val coloredContent = "${ConsoleColors.RED}Red${ConsoleColors.RESET} and ${ConsoleColors.BLUE}Blue${ConsoleColors.RESET}"
        val box = Box(coloredContent, padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE)

        // "Red and Blue" has visible length of 12
        // ANSI codes add length but should be ignored for alignment
        val rendered = box.render()
        val lines = rendered.lines()

        // Top border: ┌ + 12 * ─ + ┐ = 14 chars
        assertThat(lines[0]).isEqualTo("┌────────────┐")
        // Content line: │ + coloredContent + │
        assertThat(lines[1]).isEqualTo("│$coloredContent│")
        // Bottom border: └ + 12 * ─ + ┘ = 14 chars
        assertThat(lines[2]).isEqualTo("└────────────┘")
    }

    @Test
    fun `test color is applied to each line individually`() {
        val color = ConsoleColors.BLUE
        val box = Box("Test", padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE, color = color)
        val rendered = box.render()
        val lines = rendered.lines()

        assertThat(lines).hasSize(3)
        lines.forEach { line ->
            assertThat(line).startsWith(color)
            assertThat(line).endsWith(ConsoleColors.RESET)
        }
    }

    @Test
    fun `test nested boxes maintain alignment`() {
        val innerBox = Box("Inner", padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE, color = ConsoleColors.GREEN)
        val outerBox = Box(innerBox.render(), padding = 0, margin = 0, borderStyle = BorderStyle.SINGLE, color = ConsoleColors.BLUE)

        val rendered = outerBox.render()
        val lines = rendered.lines()

        // "Inner" is 5 chars, box adds 2 (borders), total 7
        // Outer box adds 2 more (borders), total 9
        assertThat(lines).hasSize(5)
        lines.forEach { line ->
            // Each line should be colored BLUE
            assertThat(line).startsWith(ConsoleColors.BLUE)
            assertThat(line).endsWith(ConsoleColors.RESET)

            // Stripping all ANSI codes should give a line of length 9
            val stripped = line.replace(Regex("\u001b\\[[0-9;?]*[a-zA-Z]|\u001b\\][0-9;]*\u0007"), "")
            assertThat(stripped.length).isEqualTo(9)
        }
    }
}
