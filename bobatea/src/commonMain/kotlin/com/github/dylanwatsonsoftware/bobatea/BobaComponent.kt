package com.github.dylanwatsonsoftware.bobatea

abstract class BobaComponent(
    open var padding: Int = 0,
    open var margin: Int = 0,
    open var borderStyle: BorderStyle = BorderStyle.NONE,
    open var color: String? = null
) {
    abstract fun render(): String

    protected fun wrapInBox(content: String): String {
        if (borderStyle == BorderStyle.NONE && padding == 0 && margin == 0 && color == null) {
            return content
        }
        return Box(content, padding, margin, borderStyle, color).render()
    }
}
