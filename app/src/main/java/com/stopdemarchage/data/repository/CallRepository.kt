package com.stopdemarchage.data.repository

import com.stopdemarchage.data.local.BlockedCallDao
import com.stopdemarchage.data.local.PrefixDao
import com.stopdemarchage.data.model.BlockedCall
import com.stopdemarchage.data.model.Prefix
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepository @Inject constructor(
    private val prefixDao: PrefixDao,
    private val blockedCallDao: BlockedCallDao
) {
    // Prefix operations
    fun getAllPrefixes(): Flow<List<Prefix>> = prefixDao.getAllPrefixes()

    fun getEnabledPrefixes(): Flow<List<Prefix>> = prefixDao.getEnabledPrefixes()

    suspend fun getEnabledPrefixesList(): List<Prefix> = prefixDao.getEnabledPrefixesList()

    suspend fun getPrefixById(id: Long): Prefix? = prefixDao.getPrefixById(id)

    fun getEnabledPrefixCount(): Flow<Int> = prefixDao.getEnabledPrefixCount()

    suspend fun insertPrefix(prefix: Prefix): Long = prefixDao.insert(prefix)

    suspend fun insertAllPrefixes(prefixes: List<Prefix>) = prefixDao.insertAll(prefixes)

    suspend fun updatePrefix(prefix: Prefix) = prefixDao.update(prefix)

    suspend fun deletePrefix(prefix: Prefix) = prefixDao.delete(prefix)

    suspend fun deleteAllPrefixes() = prefixDao.deleteAll()

    suspend fun prefixExists(prefix: String): Boolean = prefixDao.prefixExists(prefix)

    // Blocked call operations
    fun getAllBlockedCalls(): Flow<List<BlockedCall>> = blockedCallDao.getAllBlockedCalls()

    fun getRecentBlockedCalls(limit: Int): Flow<List<BlockedCall>> =
        blockedCallDao.getRecentBlockedCalls(limit)

    fun getBlockedCallCountSince(startTime: Long): Flow<Int> =
        blockedCallDao.getBlockedCallCountSince(startTime)

    suspend fun getBlockedCallCountSinceSync(startTime: Long): Int =
        blockedCallDao.getBlockedCallCountSinceSync(startTime)

    suspend fun insertBlockedCall(blockedCall: BlockedCall): Long =
        blockedCallDao.insert(blockedCall)

    suspend fun deleteBlockedCall(blockedCall: BlockedCall) = blockedCallDao.delete(blockedCall)

    suspend fun deleteAllBlockedCalls() = blockedCallDao.deleteAll()

    suspend fun deleteBlockedCallsOlderThan(beforeTime: Long): Int =
        blockedCallDao.deleteOlderThan(beforeTime)

    // Default prefixes for French telemarketing
    companion object {
        val DEFAULT_PREFIXES = listOf(
            Prefix(prefix = "+33162", description = "Démarchage téléphonique", isEnabled = true),
            Prefix(prefix = "+33163", description = "Démarchage téléphonique", isEnabled = true),
            Prefix(prefix = "+33270", description = "Démarchage téléphonique", isEnabled = true),
            Prefix(prefix = "+33271", description = "Démarchage téléphonique", isEnabled = true),
            Prefix(prefix = "+33377", description = "Démarchage téléphonique", isEnabled = true),
            Prefix(prefix = "+33378", description = "Démarchage téléphonique", isEnabled = true),
            Prefix(prefix = "+33424", description = "Démarchage téléphonique", isEnabled = true),
            Prefix(prefix = "+33425", description = "Démarchage téléphonique", isEnabled = true),
            Prefix(prefix = "0162", description = "Démarchage téléphonique (format local)", isEnabled = true),
            Prefix(prefix = "0163", description = "Démarchage téléphonique (format local)", isEnabled = true),
            Prefix(prefix = "0270", description = "Démarchage téléphonique (format local)", isEnabled = true),
            Prefix(prefix = "0271", description = "Démarchage téléphonique (format local)", isEnabled = true),
            Prefix(prefix = "0377", description = "Démarchage téléphonique (format local)", isEnabled = true),
            Prefix(prefix = "0378", description = "Démarchage téléphonique (format local)", isEnabled = true),
            Prefix(prefix = "0424", description = "Démarchage téléphonique (format local)", isEnabled = true),
            Prefix(prefix = "0425", description = "Démarchage téléphonique (format local)", isEnabled = true),
        )
    }
}
