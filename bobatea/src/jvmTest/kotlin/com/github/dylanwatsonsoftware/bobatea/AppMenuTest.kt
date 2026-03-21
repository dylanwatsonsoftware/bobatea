package com.github.dylanwatsonsoftware.bobatea

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class AppMenuTest {
    @Test
    fun `test back button rendering`() {
        val back = BackButton()
        val rendered = back.render()
        assertThat(rendered).contains("Back")
    }
}
