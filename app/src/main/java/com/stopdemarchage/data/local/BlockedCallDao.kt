package com.stopdemarchage.data.local

import androidx.room.*
import com.stopdemarchage.data.model.BlockedCall
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedCallDao {

    @Query("SELECT * FROM blocked_calls ORDER BY timestamp DESC")
    fun getAllBlockedCalls(): Flow<List<BlockedCall>>

    @Query("SELECT * FROM blocked_calls ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentBlockedCalls(limit: Int): Flow<List<BlockedCall>>

    @Query("SELECT COUNT(*) FROM blocked_calls WHERE timestamp >= :startTime")
    fun getBlockedCallCountSince(startTime: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM blocked_calls WHERE timestamp >= :startTime")
    suspend fun getBlockedCallCountSinceSync(startTime: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blockedCall: BlockedCall): Long

    @Delete
    suspend fun delete(blockedCall: BlockedCall)

    @Query("DELETE FROM blocked_calls")
    suspend fun deleteAll()

    @Query("DELETE FROM blocked_calls WHERE timestamp < :beforeTime")
    suspend fun deleteOlderThan(beforeTime: Long): Int
}
