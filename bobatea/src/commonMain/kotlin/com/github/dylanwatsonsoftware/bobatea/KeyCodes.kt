package com.github.dylanwatsonsoftware.bobatea

enum class KeyCodes(val key: Int) {
    UP(1001),
    DOWN(1002),
    RIGHT(1003),
    LEFT(1004),
    ENTER(10),
    SPACE(32),
    W(119),
    W_UPPER(87),
    A(97),
    A_UPPER(65),
    S(115),
    S_UPPER(83),
    D(100),
    D_UPPER(68);

    companion object {
        fun isUp(code: Int) = code == UP.key || code == W.key || code == W_UPPER.key
        fun isDown(code: Int) = code == DOWN.key || code == S.key || code == S_UPPER.key
        fun isLeft(code: Int) = code == LEFT.key || code == A.key || code == A_UPPER.key
        fun isRight(code: Int) = code == RIGHT.key || code == D.key || code == D_UPPER.key
    }
}
