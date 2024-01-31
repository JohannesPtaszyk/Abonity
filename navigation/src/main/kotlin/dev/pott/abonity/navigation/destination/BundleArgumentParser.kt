package dev.pott.abonity.navigation.destination

import android.os.Bundle

fun interface BundleArgumentParser<T> {
    fun parse(bundle: Bundle): T
}
