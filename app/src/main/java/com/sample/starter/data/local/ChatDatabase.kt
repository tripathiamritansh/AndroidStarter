package com.sample.starter.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sample.starter.domain.model.Message

@Database(
    entities = [Message::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MessageConverters::class)
abstract class ChatDatabase : RoomDatabase() {
    
    abstract fun messageDao(): MessageDao
    
    companion object {
        // Singleton prevents multiple database instances
        @Volatile
        private var INSTANCE: ChatDatabase? = null
        
        /**
         * Get database instance (creates if doesn't exist)
         * Thread-safe singleton pattern
         */
        fun getInstance(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
