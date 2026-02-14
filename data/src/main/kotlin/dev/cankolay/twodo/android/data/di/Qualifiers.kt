package dev.cankolay.twodo.android.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(value = AnnotationRetention.BINARY)
annotation class SettingsDataStore

@Qualifier
@Retention(value = AnnotationRetention.BINARY)
annotation class AuthDataStore
