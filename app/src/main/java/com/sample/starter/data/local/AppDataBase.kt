package com.sample.starter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sample.starter.data.local.model.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
}