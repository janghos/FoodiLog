package com.foodilogg.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ReviewEntity::class], version = 1)
@TypeConverters(com.foodilogg.local.Converters::class)
abstract class FoodiDataBase : RoomDatabase() {
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: FoodiDataBase? = null

        fun getDatabase(context: Context): FoodiDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodiDataBase::class.java,
                    "foodi_database" // 데이터베이스 이름
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}