package com.github.dylanwatsonsoftware.bobatea

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class KeyCodesTest {
    @Test
    fun `test isUp`() {
        assertThat(KeyCodes.isUp(KeyCodes.UP.key)).isTrue()
        assertThat(KeyCodes.isUp(KeyCodes.W.key)).isTrue()
        assertThat(KeyCodes.isUp(KeyCodes.W_UPPER.key)).isTrue()
        assertThat(KeyCodes.isUp(KeyCodes.DOWN.key)).isFalse()
    }

    @Test
    fun `test isDown`() {
        assertThat(KeyCodes.isDown(KeyCodes.DOWN.key)).isTrue()
        assertThat(KeyCodes.isDown(KeyCodes.S.key)).isTrue()
        assertThat(KeyCodes.isDown(KeyCodes.S_UPPER.key)).isTrue()
        assertThat(KeyCodes.isDown(KeyCodes.UP.key)).isFalse()
    }

    @Test
    fun `test isLeft`() {
        assertThat(KeyCodes.isLeft(KeyCodes.LEFT.key)).isTrue()
        assertThat(KeyCodes.isLeft(KeyCodes.A.key)).isTrue()
        assertThat(KeyCodes.isLeft(KeyCodes.A_UPPER.key)).isTrue()
        assertThat(KeyCodes.isLeft(KeyCodes.RIGHT.key)).isFalse()
    }

    @Test
    fun `test isRight`() {
        assertThat(KeyCodes.isRight(KeyCodes.RIGHT.key)).isTrue()
        assertThat(KeyCodes.isRight(KeyCodes.D.key)).isTrue()
        assertThat(KeyCodes.isRight(KeyCodes.D_UPPER.key)).isTrue()
        assertThat(KeyCodes.isRight(KeyCodes.LEFT.key)).isFalse()
    }
}
