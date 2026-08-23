package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey val dateKey: String, // Format: "yyyy-MM-dd", e.g. "2026-08-23"
    val displayDate: String,         // UI Format: e.g. "২৩ আগস্ট (রবি)"
    val phase: String,
    val morning: String,
    val noon: String,
    val night: String,
    val target: String,
    val ratio: String = "3:1"
)
