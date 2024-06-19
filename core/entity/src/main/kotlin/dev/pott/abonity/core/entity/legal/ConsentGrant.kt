package dev.pott.abonity.core.entity.legal

enum class ConsentGrant {
    GRANTED,
    DENIED,
    UNKNOWN,
    ;

    val isGranted: Boolean
        get() = this == GRANTED

    fun toggle(): ConsentGrant =
        when (this) {
            GRANTED -> DENIED
            DENIED -> GRANTED
            UNKNOWN -> GRANTED
        }
}
