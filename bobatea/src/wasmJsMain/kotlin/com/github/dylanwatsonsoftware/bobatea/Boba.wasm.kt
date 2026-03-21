package com.github.dylanwatsonsoftware.bobatea

@JsFun("() => { return Date.now(); }")
private external fun jsDateNow(): Double

internal actual fun currentTimeMillis(): Long = jsDateNow().toLong()
