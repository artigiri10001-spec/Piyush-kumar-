package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM selected_cities ORDER BY displayOrder ASC, cityName ASC")
    fun getAllSelectedCities(): Flow<List<SelectedCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SelectedCity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<SelectedCity>)

    @Query("DELETE FROM selected_cities WHERE timezoneId = :timezoneId")
    suspend fun deleteCityByTimezone(timezoneId: String)

    @Query("SELECT COUNT(*) FROM selected_cities")
    suspend fun countSelectedCities(): Int
}
