package com.andryoga.doodh.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "doodh", primaryKeys = ["day", "month", "year"])
data class DoodhEntity(
    @ColumnInfo(name = "day") val day: Int,
    @ColumnInfo(name = "month") val month: Int,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "qty") val qty: Double
)