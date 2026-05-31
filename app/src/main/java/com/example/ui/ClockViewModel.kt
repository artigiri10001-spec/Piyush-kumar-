package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CityInfo
import com.example.data.CityRepository
import com.example.data.DefaultCities
import com.example.data.SelectedCity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ClockViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CityRepository

    // Selected cities from database
    val selectedCities: StateFlow<List<SelectedCity>>

    // Live Mode vs Scrubbing/Simulation Mode
    private val _isLiveMode = MutableStateFlow(true)
    val isLiveMode: StateFlow<Boolean> = _isLiveMode.asStateFlow()

    // Scrubber offset in minutes (supports half-hour zones properly)
    private val _scrubberOffsetMinutes = MutableStateFlow(0L)
    val scrubberOffsetMinutes: StateFlow<Long> = _scrubberOffsetMinutes.asStateFlow()

    // Live clock ticker
    private val _liveTime = MutableStateFlow(Instant.now())
    val liveTime: StateFlow<Instant> = _liveTime.asStateFlow()

    // Search query for adding cities
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Coordinates of last tapped/highlighted city on the map to focus UI info
    private val _highlightedTzId = MutableStateFlow<String?>(null)
    val highlightedTzId: StateFlow<String?> = _highlightedTzId.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CityRepository(database.cityDao())

        // Fetch selected cities and initialize defaults if database is empty
        selectedCities = repository.allSelectedCities
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        viewModelScope.launch {
            repository.checkAndInitialize()
        }

        // Clock ticker: ticks every 1 second to update the visual clock hands and times (if in Live Mode)
        viewModelScope.launch {
            while (true) {
                if (_isLiveMode.value) {
                    _liveTime.value = Instant.now()
                }
                delay(1000)
            }
        }
    }

    // Combine default list with added database status to present searchable suggestions
    val searchResults: StateFlow<List<CitySearchResult>> = combine(
        _searchQuery,
        selectedCities
    ) { query, selected ->
        val selectedIds = selected.map { it.timezoneId }.toSet()
        val allCities = DefaultCities.list

        val filtered = if (query.isBlank()) {
            allCities
        } else {
            allCities.filter {
                it.cityName.contains(query, ignoreCase = true) ||
                        it.countryName.contains(query, ignoreCase = true) ||
                        it.timezoneId.contains(query, ignoreCase = true)
            }
        }

        filtered.map { city ->
            CitySearchResult(
                city = city,
                isAdded = selectedIds.contains(city.timezoneId)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active time we should display on the screen
    val simulatedTime: StateFlow<Instant> = combine(
        _liveTime,
        _isLiveMode,
        _scrubberOffsetMinutes
    ) { live, isLive, offset ->
        if (isLive) {
            live
        } else {
            // Anchor to the moment live time was paused, plus user offset
            Instant.now().plus(offset, ChronoUnit.MINUTES)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Instant.now())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setHighlightTzId(tzId: String?) {
        _highlightedTzId.value = tzId
    }

    fun setScrubberOffsetHours(hours: Float) {
        _isLiveMode.value = false
        _scrubberOffsetMinutes.value = (hours * 60f).toLong()
    }

    fun toggleLiveMode(isLive: Boolean) {
        _isLiveMode.value = isLive
        if (isLive) {
            _scrubberOffsetMinutes.value = 0L
            _liveTime.value = Instant.now()
        }
    }

    fun addCityResult(city: CityInfo) {
        viewModelScope.launch {
            repository.addCity(
                SelectedCity(
                    timezoneId = city.timezoneId,
                    cityName = city.cityName,
                    countryName = city.countryName,
                    latitude = city.latitude,
                    longitude = city.longitude,
                    displayOrder = (selectedCities.value.maxOfOrNull { it.displayOrder } ?: 0) + 1
                )
            )
        }
    }

    fun removeCityResult(timezoneId: String) {
        viewModelScope.launch {
            repository.removeCity(timezoneId)
            if (_highlightedTzId.value == timezoneId) {
                _highlightedTzId.value = null
            }
        }
    }
}

data class CitySearchResult(
    val city: CityInfo,
    val isAdded: Boolean
)

data class SolarDeclination(
    val declinationRad: Double,
    val subsolarLonRad: Double
)

// Auxiliary utility to construct precise solar declination daylight computations for maps
object SolarCalculator {
    fun getDeclination(instant: Instant): SolarDeclination {
        // Convert instant to UTC hour of day
        val zdt = ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"))
        val utcHour = zdt.hour + zdt.minute / 60.0 + zdt.second / 3600.0

        // Day of Year approximate declination
        val dayOfYear = zdt.dayOfYear
        val declinationRad = Math.toRadians(23.44 * Math.sin(2.0 * Math.PI * (dayOfYear - 80.0) / 365.0))

        // longitude of the subsolar point (the noon line)
        val subsolarLonRad = Math.toRadians(-((utcHour / 24.0) * 360.0 - 180.0))

        return SolarDeclination(declinationRad, subsolarLonRad)
    }

    fun calculateSunElevation(lat: Double, lon: Double, dec: SolarDeclination): Double {
        // Map latitude and longitude to Radians
        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)

        // Solar altitude angle formula
        return Math.sin(latRad) * Math.sin(dec.declinationRad) +
                Math.cos(latRad) * Math.cos(dec.declinationRad) * Math.cos(lonRad - dec.subsolarLonRad)
    }

    fun calculateSunElevation(lat: Double, lon: Double, instant: Instant): Double {
        return calculateSunElevation(lat, lon, getDeclination(instant))
    }

    fun isDaylight(lat: Double, lon: Double, instant: Instant): Boolean {
        return calculateSunElevation(lat, lon, instant) > 0.0
    }
}
