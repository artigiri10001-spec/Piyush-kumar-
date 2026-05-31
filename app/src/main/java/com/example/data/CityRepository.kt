package com.example.data

import kotlinx.coroutines.flow.Flow

class CityRepository(private val cityDao: CityDao) {
    val allSelectedCities: Flow<List<SelectedCity>> = cityDao.getAllSelectedCities()

    suspend fun addCity(city: SelectedCity) {
        cityDao.insertCity(city)
    }

    suspend fun removeCity(timezoneId: String) {
        cityDao.deleteCityByTimezone(timezoneId)
    }

    suspend fun checkAndInitialize() {
        if (cityDao.countSelectedCities() == 0) {
            cityDao.insertCities(DefaultCities.initialSelection)
        }
    }
}
