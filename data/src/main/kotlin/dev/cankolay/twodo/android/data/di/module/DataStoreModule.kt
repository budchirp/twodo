package dev.cankolay.twodo.android.data.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.cankolay.twodo.android.data.di.AuthDataStore
import dev.cankolay.twodo.android.data.di.SettingsDataStore
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "settings")
private val Context.authDataStore by preferencesDataStore(name = "auth")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    @SettingsDataStore
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.settingsDataStore

    @Provides
    @Singleton
    @AuthDataStore
    fun provideAuthDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.authDataStore
}