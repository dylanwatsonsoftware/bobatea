package com.github.dylanwatsonsoftware.bobatea

sealed class BobaEvent {
    data class Key(val code: Int) : BobaEvent()
    data class Mouse(val x: Int, val y: Int, val button: Int, val action: MouseAction) : BobaEvent()
}

enum class MouseAction {
    PRESS, RELEASE, MOVE
}
