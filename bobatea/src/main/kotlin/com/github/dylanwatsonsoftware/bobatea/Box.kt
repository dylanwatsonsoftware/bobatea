package com.github.dylanwatsonsoftware.bobatea

import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.Panel
import com.github.ajalt.mordant.widgets.Text
import com.github.ajalt.mordant.widgets.withPadding

enum class BorderStyle(val borderType: BorderType) {
    SINGLE(BorderType.SQUARE),
    DOUBLE(BorderType.DOUBLE),
    ROUNDED(BorderType.ROUNDED),
    NONE(BorderType.BLANK)
}

class Box(
    private val content: String,
    override var padding: Int = 1,
    override var margin: Int = 0,
    override var borderStyle: BorderStyle = BorderStyle.SINGLE,
    override var style: TextStyle = TextStyle()
) : BobaComponent(padding, margin, borderStyle, style) {

    override fun render(): String {
        val terminal = Boba.terminal
        var widget: com.github.ajalt.mordant.rendering.Widget = Text(content)
        if (padding > 0) {
            widget = widget.withPadding {
                top = padding
                bottom = padding
                left = padding
                right = padding
            }
        }

        val panel = Panel(
            content = widget,
            borderType = borderStyle.borderType,
            borderStyle = style
        )

        var finalWidget: com.github.ajalt.mordant.rendering.Widget = panel
        if (margin > 0) {
            finalWidget = finalWidget.withPadding {
                top = margin
                bottom = margin
                left = margin
                right = margin
            }
        }

        return terminal.render(finalWidget)
    }

    override fun toString(): String = render()
}
