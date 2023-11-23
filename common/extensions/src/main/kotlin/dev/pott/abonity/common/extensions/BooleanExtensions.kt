package dev.pott.abonity.common.extensions

private const val TRUE = 1
private const val FALSE = 0

fun Boolean.toInt(): Int = if (this) TRUE else FALSE
