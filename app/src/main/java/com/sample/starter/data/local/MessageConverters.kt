package com.sample.starter.data.local

import androidx.room.TypeConverter
import com.sample.starter.domain.model.Message

/**
 * TypeConverters for Room database
 * Teaches Room how to convert complex types (enums) to/from primitives
 */
class MessageConverters {
    
    @TypeConverter
    fun fromSender(sender: Message.Sender): String = sender.name
    
    @TypeConverter
    fun toSender(value: String): Message.Sender = 
        try { 
            Message.Sender.valueOf(value) 
        } catch (e: IllegalArgumentException) { 
            Message.Sender.SYSTEM 
        }
    
    @TypeConverter
    fun fromStatus(status: Message.Status): String = status.name
    
    @TypeConverter
    fun toStatus(value: String): Message.Status = 
        try { 
            Message.Status.valueOf(value) 
        } catch (e: IllegalArgumentException) { 
            Message.Status.SENT 
        }
}
