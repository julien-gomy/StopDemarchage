package com.stopdemarchage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "prefixes")
data class Prefix(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prefix: String,
    val description: String = "",
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
