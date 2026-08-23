package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_table")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateStr: String,
    val phase: String,
    val morning: String,
    val noon: String,
    val night: String,
    val target: String
)
