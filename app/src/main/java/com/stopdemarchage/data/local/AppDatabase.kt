package com.stopdemarchage.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stopdemarchage.data.model.BlockedCall
import com.stopdemarchage.data.model.Prefix

@Database(
    entities = [Prefix::class, BlockedCall::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun prefixDao(): PrefixDao
    abstract fun blockedCallDao(): BlockedCallDao

    companion object {
        private const val DATABASE_NAME = "stopdemarchage_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
