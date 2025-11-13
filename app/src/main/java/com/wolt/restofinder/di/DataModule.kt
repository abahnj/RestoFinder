package com.wolt.restofinder.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.wolt.restofinder.data.local.FavouritesDataStore
import com.wolt.restofinder.data.location.LocationRepositoryImpl
import com.wolt.restofinder.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "resto_finder_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideFavouritesDataStore(dataStore: DataStore<Preferences>): FavouritesDataStore =
        FavouritesDataStore(dataStore)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindsModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository
}
