package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_cities")
data class SelectedCity(
    @PrimaryKey val timezoneId: String, // Each timezone is unique on the dashboard
    val cityName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
    val displayOrder: Int = 0
)
