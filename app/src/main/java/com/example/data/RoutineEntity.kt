package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey val dateKey: String, // format "yyyy-MM-dd"
    val displayDate: String,
    val phase: String,
    val morning: String,
    val noon: String,
    val night: String,
    val target: String,
    val ratio: String = "3:1"
)
