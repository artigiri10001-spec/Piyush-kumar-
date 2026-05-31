package com.example.data

data class CityInfo(
    val cityName: String,
    val countryName: String,
    val timezoneId: String,
    val latitude: Double,
    val longitude: Double
)

object DefaultCities {
    val list = listOf(
        CityInfo("London", "United Kingdom", "Europe/London", 51.5074, -0.1278),
        CityInfo("New York", "United States", "America/New_York", 40.7128, -74.0060),
        CityInfo("Los Angeles", "United States", "America/Los_Angeles", 34.0522, -118.2437),
        CityInfo("Tokyo", "Japan", "Asia/Tokyo", 35.6762, 139.6503),
        CityInfo("Sydney", "Australia", "Australia/Sydney", -33.8688, 151.2093),
        CityInfo("Cairo", "Egypt", "Africa/Cairo", 30.0444, 31.2357),
        CityInfo("Paris", "France", "Europe/Paris", 48.8566, 2.3522),
        CityInfo("New Delhi", "India", "Asia/Kolkata", 28.6139, 77.2090),
        CityInfo("Singapore", "Singapore", "Asia/Singapore", 1.3521, 103.8198),
        CityInfo("Dubai", "United Arab Emirates", "Asia/Dubai", 25.2048, 55.2708),
        CityInfo("Moscow", "Russia", "Europe/Moscow", 55.7558, 37.6173),
        CityInfo("São Paulo", "Brazil", "America/Sao_Paulo", -23.5505, -46.6333),
        CityInfo("Berlin", "Germany", "Europe/Berlin", 52.5200, 13.4050),
        CityInfo("Lagos", "Nigeria", "Africa/Lagos", 6.5244, 3.3792),
        CityInfo("Johannesburg", "South Africa", "Africa/Johannesburg", -26.2041, 28.0473),
        CityInfo("Nairobi", "Kenya", "Africa/Nairobi", -1.2921, 36.8219),
        CityInfo("Buenos Aires", "Argentina", "America/Argentina/Buenos_Aires", -34.6037, -58.3816),
        CityInfo("Auckland", "New Zealand", "Pacific/Auckland", -36.8485, 174.7633),
        CityInfo("Honolulu", "United States", "Pacific/Honolulu", 21.3069, -157.8583),
        CityInfo("Reykjavik", "Iceland", "Atlantic/Reykjavik", 64.1466, -21.9426),
        CityInfo("Denver", "United States", "America/Denver", 39.7392, -104.9903),
        CityInfo("Anchorage", "United States", "America/Anchorage", 61.2181, -149.9003),
        CityInfo("Shanghai", "China", "Asia/Shanghai", 31.2304, 121.4737),
        CityInfo("Seoul", "South Korea", "Asia/Seoul", 37.5665, 126.9780),
        CityInfo("Bangkok", "Thailand", "Asia/Bangkok", 13.7563, 100.5018),
        CityInfo("Vancouver", "Canada", "America/Vancouver", 49.2827, -123.1207),
        CityInfo("Mexico City", "Mexico", "America/Mexico_City", 19.4326, -99.1332),
        CityInfo("Karachi", "Pakistan", "Asia/Karachi", 24.8607, 67.0011),
        CityInfo("Istanbul", "Turkey", "Europe/Istanbul", 41.0082, 28.9784),
        CityInfo("Cape Town", "South Africa", "Africa/Cape_Town", -33.9249, 18.4241),
        CityInfo("Phoenix", "United States", "America/Phoenix", 33.4484, -112.0740),
        CityInfo("Chicago", "United States", "America/Chicago", 41.8781, -87.6298),
        CityInfo("Jakarta", "Indonesia", "Asia/Jakarta", -6.2088, 106.8456),
        CityInfo("Toronto", "Canada", "America/Toronto", 43.6532, -79.3832),
        CityInfo("Riyadh", "Saudi Arabia", "Asia/Riyadh", 24.7136, 46.6753),
        CityInfo("Rome", "Italy", "Europe/Rome", 41.9028, 12.4964),
        CityInfo("Madrid", "Spain", "Europe/Madrid", 40.4168, -3.7038),
        CityInfo("Riyadh", "Saudi Arabia", "Asia/Riyadh", 24.7136, 46.6753),
        CityInfo("Hong Kong", "Hong Kong", "Asia/Hong_Kong", 22.3193, 114.1694),
        CityInfo("Melbourne", "Australia", "Australia/Melbourne", -37.8136, 144.9631),
        CityInfo("Warsaw", "Poland", "Europe/Warsaw", 52.2297, 21.0122),
        CityInfo("Athens", "Greece", "Europe/Athens", 37.9838, 23.7275)
    ).distinctBy { it.timezoneId }

    // Initial selected set when the database is empty
    val initialSelection = listOf(
        SelectedCity("Europe/London", "London", "United Kingdom", 51.5074, -0.1278, 0),
        SelectedCity("America/New_York", "New York", "United States", 40.7128, -74.0060, 1),
        SelectedCity("Asia/Tokyo", "Tokyo", "Japan", 35.6762, 139.6503, 2),
        SelectedCity("Australia/Sydney", "Sydney", "Australia", -33.8688, 151.2093, 3),
        SelectedCity("Africa/Cairo", "Cairo", "Egypt", 30.0444, 31.2357, 4)
    )
}
