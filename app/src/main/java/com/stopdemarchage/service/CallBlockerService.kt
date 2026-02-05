package com.stopdemarchage.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.stopdemarchage.data.local.AppDatabase
import com.stopdemarchage.data.model.BlockedCall
import com.stopdemarchage.data.repository.CallRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CallBlockerService : CallScreeningService() {

    companion object {
        private const val TAG = "CallBlockerService"
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var repository: CallRepository? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service de filtrage demarre")

        try {
            val database = AppDatabase.getInstance(applicationContext)
            repository = CallRepository(database.prefixDao(), database.blockedCallDao())
        } catch (e: Exception) {
            Log.e(TAG, "Erreur initialisation: ${e.message}", e)
        }
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: ""
        Log.d(TAG, "Appel entrant: $phoneNumber")

        if (repository == null) {
            Log.e(TAG, "Repository null, appel autorise")
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        if (phoneNumber.isEmpty()) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        runBlocking {
            try {
                val enabledPrefixes = repository!!.getEnabledPrefixesList()

                if (enabledPrefixes.isEmpty()) {
                    respondToCall(callDetails, CallResponse.Builder().build())
                    return@runBlocking
                }

                val normalizedNumber = normalizePhoneNumber(phoneNumber)
                var matchedPrefix: com.stopdemarchage.data.model.Prefix? = null

                for (prefix in enabledPrefixes) {
                    val normalizedPrefix = normalizePhoneNumber(prefix.prefix)
                    val directMatch = phoneNumber.startsWith(prefix.prefix)
                    val normalizedMatch = normalizedNumber.startsWith(normalizedPrefix)

                    if (directMatch || normalizedMatch) {
                        matchedPrefix = prefix
                        break
                    }
                }

                if (matchedPrefix != null) {
                    Log.i(TAG, "Blocage: $phoneNumber (prefixe: ${matchedPrefix.prefix})")

                    val response = CallResponse.Builder()
                        .setDisallowCall(true)
                        .setRejectCall(true)
                        .setSkipCallLog(false)
                        .setSkipNotification(false)
                        .build()

                    serviceScope.launch {
                        try {
                            repository?.insertBlockedCall(
                                BlockedCall(
                                    phoneNumber = phoneNumber,
                                    matchedPrefix = matchedPrefix.prefix
                                )
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Erreur enregistrement: ${e.message}")
                        }
                    }

                    respondToCall(callDetails, response)
                } else {
                    respondToCall(callDetails, CallResponse.Builder().build())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erreur: ${e.message}", e)
                respondToCall(callDetails, CallResponse.Builder().build())
            }
        }
    }

    private fun normalizePhoneNumber(number: String): String {
        val cleaned = number.replace(Regex("[^+\\d]"), "")

        return when {
            cleaned.startsWith("0") && cleaned.length >= 2 -> "+33${cleaned.substring(1)}"
            cleaned.startsWith("+33") -> cleaned
            cleaned.startsWith("33") && cleaned.length >= 4 -> "+$cleaned"
            else -> cleaned
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Service arrete")
    }
}
