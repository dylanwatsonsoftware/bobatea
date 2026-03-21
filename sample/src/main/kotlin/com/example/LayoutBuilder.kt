package com.example

import com.github.dylanwatsonsoftware.bobatea.*

fun buildDemoLayout(terminalWidth: Int): BobaComponent {
    val header = Box(
        "Boba Tea Layout Demo",
        borderStyle = BorderStyle.ROUNDED,
        color = ConsoleColors.CYAN,
        width = Dimension.Percent(100.0),
        padding = 1
    )

    val leftPanel = Box(
        "Sidebar\n- Item 1\n- Item 2",
        borderStyle = BorderStyle.SINGLE,
        color = ConsoleColors.YELLOW,
        width = Dimension.Fixed(15),
        height = Dimension.Fixed(10)
    )

    val mainContent = Box(
        "Main Content Area\nThis is a demonstration of nested Stack and Inline layouts.\nThe sidebar and this content are inside an Inline component.",
        borderStyle = BorderStyle.DOUBLE,
        color = ConsoleColors.GREEN,
        width = Dimension.Fixed(40),
        height = Dimension.Fixed(10)
    )

    val middleRow = Inline(
        children = listOf(leftPanel, mainContent)
    )

    val footer = Box(
        "Footer Information",
        borderStyle = BorderStyle.SINGLE,
        color = ConsoleColors.BLUE,
        width = Dimension.Percent(100.0)
    )

    val markdown = Markdown(
        "# Mordant Integration\n" +
        "Boba Tea now uses **Mordant** for rendering.\n" +
        "- Supports Markdown\n" +
        "- Supports Tables\n" +
        "- Supports Hyperlinks",
        padding = 1
    )

    val table = Table(
        headers = listOf("Feature", "Status"),
        rows = listOf(
            listOf("Markdown", "✅ Working"),
            listOf("Tables", "✅ Working"),
            listOf("Links", "✅ Working")
        ),
        padding = 1
    )

    val link = Link("Visit Mordant Website", "https://ajalt.github.io/mordant/", padding = 1)

    return Stack(
        children = listOf(header, middleRow, markdown, table, link, footer),
        width = Dimension.Fixed(terminalWidth)
    )
}
