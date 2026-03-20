package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.TextStyle

abstract class BobaComponent(
    open var padding: Int = 0,
    open var margin: Int = 0,
    open var borderStyle: BorderStyle = BorderStyle.NONE,
    open var style: TextStyle = TextStyle()
) {
    abstract fun render(): String

    protected fun wrapInBox(content: String): String {
        if (borderStyle == BorderStyle.NONE && padding == 0 && margin == 0 && style == TextStyle()) {
            return content
        }
        return Box(content, padding, margin, borderStyle, style).render()
    }
}
