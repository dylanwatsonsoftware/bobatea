package com.github.dylanwatsonsoftware.bobatea

import kotlinx.coroutines.runBlocking

class Boba {
    companion object {
        private val terminal = JvmTerminal()

        fun <T> nonBlockingTerminal(task: () -> T): T = terminal.use { task() }

        fun clear() = terminal.clear()

        fun getChar(): Int = terminal.getChar()

        fun readEvent(): BobaEvent = runBlocking { terminal.readEvent() }

        fun enableMouseTracking(allMotion: Boolean = false) = terminal.enableMouseTracking(allMotion)

        fun disableMouseTracking() = terminal.disableMouseTracking()

        fun selectFromList(
            question: String,
            options: List<String>,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            color: String? = null
        ): String = runBlocking {
            SelectionList(question, options, padding, margin, borderStyle, color).interact(terminal)
        }

        fun expandable(
            title: String,
            content: String,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            color: String? = null
        ) = runBlocking {
            ExpandableComponent(title, content, padding, margin, borderStyle, color).interact(terminal)
        }

        fun selectMultipleFromList(
            question: String,
            options: List<String>,
            padding: Int = 0,
            margin: Int = 0,
            borderStyle: BorderStyle = BorderStyle.NONE,
            color: String? = null
        ): MutableSet<String> = runBlocking {
            MultiSelectionList(question, options, padding, margin, borderStyle, color).interact(terminal)
        }
    }
}
