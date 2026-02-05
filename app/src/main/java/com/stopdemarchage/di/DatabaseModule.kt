package com.stopdemarchage.di

import android.content.Context
import androidx.room.Room
import com.stopdemarchage.data.local.AppDatabase
import com.stopdemarchage.data.local.BlockedCallDao
import com.stopdemarchage.data.local.PrefixDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "stopdemarchage_db"
        ).build()
    }

    @Provides
    fun providePrefixDao(database: AppDatabase): PrefixDao {
        return database.prefixDao()
    }

    @Provides
    fun provideBlockedCallDao(database: AppDatabase): BlockedCallDao {
        return database.blockedCallDao()
    }
}
