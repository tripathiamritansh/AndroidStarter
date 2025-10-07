package com.sample.starter.di

import android.content.Context
import androidx.room.Room
import com.sample.starter.data.local.AppDataBase
import com.sample.starter.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDataBase = Room.databaseBuilder(
        context = context,
        klass = AppDataBase::class.java,
        name = "starter_database"
    )
        .fallbackToDestructiveMigration() // Clear DB on schema change
        .build()

    @Provides
    @Singleton
    fun provideDao(dataBase: AppDataBase): UserDao = dataBase.userDao()

}