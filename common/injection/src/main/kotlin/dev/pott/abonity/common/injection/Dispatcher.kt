package dev.pott.abonity.common.injection

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val value: Type) {
    enum class Type {
        IO,
        DEFAULT,
    }
}
