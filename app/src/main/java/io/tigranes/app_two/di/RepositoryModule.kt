package io.tigranes.app_two.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.tigranes.app_two.data.repository.FilterRepository
import io.tigranes.app_two.data.repository.FilterRepositoryImpl
import io.tigranes.app_two.data.repository.ImageRepository
import io.tigranes.app_two.data.repository.ImageRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository
    
    @Binds
    @Singleton
    abstract fun bindFilterRepository(
        filterRepositoryImpl: FilterRepositoryImpl
    ): FilterRepository
}