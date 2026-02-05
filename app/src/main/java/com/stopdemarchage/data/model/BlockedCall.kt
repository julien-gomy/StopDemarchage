package com.stopdemarchage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_calls")
data class BlockedCall(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val matchedPrefix: String,
    val timestamp: Long = System.currentTimeMillis()
)
