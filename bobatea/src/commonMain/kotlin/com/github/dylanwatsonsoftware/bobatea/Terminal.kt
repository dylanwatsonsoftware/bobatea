package com.github.dylanwatsonsoftware.bobatea

interface Terminal {
    fun write(text: String)
    fun clear()
    suspend fun readEvent(): BobaEvent
    fun enableMouseTracking(allMotion: Boolean = false)
    fun disableMouseTracking()
    fun size(): Pair<Int, Int> = 80 to 24
}
