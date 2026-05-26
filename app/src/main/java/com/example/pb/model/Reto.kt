package com.example.pb.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "retos")

data class Reto (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)