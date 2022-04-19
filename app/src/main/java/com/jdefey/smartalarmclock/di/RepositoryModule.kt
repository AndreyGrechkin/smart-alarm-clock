package com.jdefey.smartalarmclock.di


import com.jdefey.smartalarmclock.repository.Repository
import com.jdefey.smartalarmclock.repository.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun providesPhotoRepository(impl: RepositoryImpl): Repository
}