package com.stopdemarchage.data.local

import androidx.room.*
import com.stopdemarchage.data.model.Prefix
import kotlinx.coroutines.flow.Flow

@Dao
interface PrefixDao {

    @Query("SELECT * FROM prefixes ORDER BY createdAt DESC")
    fun getAllPrefixes(): Flow<List<Prefix>>

    @Query("SELECT * FROM prefixes WHERE isEnabled = 1")
    fun getEnabledPrefixes(): Flow<List<Prefix>>

    @Query("SELECT * FROM prefixes WHERE isEnabled = 1")
    suspend fun getEnabledPrefixesList(): List<Prefix>

    @Query("SELECT * FROM prefixes WHERE id = :id")
    suspend fun getPrefixById(id: Long): Prefix?

    @Query("SELECT COUNT(*) FROM prefixes WHERE isEnabled = 1")
    fun getEnabledPrefixCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prefix: Prefix): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prefixes: List<Prefix>)

    @Update
    suspend fun update(prefix: Prefix)

    @Delete
    suspend fun delete(prefix: Prefix)

    @Query("DELETE FROM prefixes")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM prefixes WHERE prefix = :prefix)")
    suspend fun prefixExists(prefix: String): Boolean
}
