package com.andryoga.doodh.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.Date

@Dao
interface DoodhDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoodhRecord(record: DoodhEntity)

    @Query("SELECT * FROM doodh WHERE day = :day AND month = :month AND year = :year LIMIT 1")
    fun getDoodhRecordForDay(day: Int, month: Int, year: Int): Flow<DoodhEntity?>

    @Query("SELECT * FROM doodh ORDER BY year DESC, month DESC, day DESC")
    fun getAllDoodhRecords(): Flow<List<DoodhEntity>>

    @Query("SELECT * FROM doodh WHERE month = :month AND year = :year ORDER BY day ASC")
    fun getDoodhRecordsForMonth(month: Int, year: Int): Flow<List<DoodhEntity>>
}