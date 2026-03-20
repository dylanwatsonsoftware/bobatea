package com.github.dylanwatsonsoftware.bobatea

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class ComponentTest {
    @Test
    fun `test SelectionList rendering with Box`() {
        val list = SelectionList(
            question = "Pick one:",
            options = listOf("A", "B"),
            padding = 0,
            borderStyle = BorderStyle.SINGLE
        )
        val rendered = list.render()
        assertThat(rendered).contains("┌")
        assertThat(rendered).contains("Pick one:")
        assertThat(rendered).contains("❯ A")
        assertThat(rendered).contains("  B")
    }

    @Test
    fun `test MultiSelectionList rendering with Box`() {
        val list = MultiSelectionList(
            question = "Pick many:",
            options = listOf("A", "B"),
            padding = 0,
            borderStyle = BorderStyle.DOUBLE
        )
        val rendered = list.render()
        assertThat(rendered).contains("╔")
        assertThat(rendered).contains("Pick many:")
        assertThat(rendered).contains("[ ]")
        assertThat(rendered).contains("A")
    }

    @Test
    fun `test ExpandableComponent rendering`() {
        val comp = ExpandableComponent(
            title = "Title",
            content = "Secret",
            borderStyle = BorderStyle.ROUNDED
        )
        var rendered = comp.render()
        assertThat(rendered).contains("▶")
        assertThat(rendered).contains("Title")
        assertThat(rendered).doesNotContain("Secret")

        comp.expanded = true
        rendered = comp.render()
        assertThat(rendered).contains("▼")
        assertThat(rendered).contains("Title")
        assertThat(rendered).contains("Secret")
    }
}
