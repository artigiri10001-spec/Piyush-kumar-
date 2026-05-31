package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CityInfo
import com.example.data.SelectedCity
import com.example.data.WorldMapData
import com.example.ui.theme.DawnCrimson
import com.example.ui.theme.DayAmber
import com.example.ui.theme.DeepMidnight
import com.example.ui.theme.LunarGrey
import com.example.ui.theme.NightIndigo
import com.example.ui.theme.SleepSlate
import com.example.ui.theme.SpaceSlate
import com.example.ui.theme.StarlightBlue
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WorkGreen
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockDashboard(
    viewModel: ClockViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCities by viewModel.selectedCities.collectAsStateWithLifecycle()
    val isLiveMode by viewModel.isLiveMode.collectAsStateWithLifecycle()
    val scrubberOffset by viewModel.scrubberOffsetMinutes.collectAsStateWithLifecycle()
    val simulatedTime by viewModel.simulatedTime.collectAsStateWithLifecycle()
    val highlightedTzId by viewModel.highlightedTzId.collectAsStateWithLifecycle()

    var showSearchSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSearchSheet = true },
                containerColor = StarlightBlue,
                contentColor = DeepMidnight,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.testTag("add_city_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Timezone Hub")
            }
        },
        containerColor = DeepMidnight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            HeaderWidget(
                isLive = isLiveMode,
                simulatedTime = simulatedTime,
                onResetLive = { viewModel.toggleLiveMode(true) }
            )

            // Dynamic Matrix World Map (The Visionary Canvas)
            Card(
                colors = CardDefaults.cardColors(containerColor = SpaceSlate),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LunarGrey),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.1f)
                    .testTag("world_map_card")
            ) {
                InteractiveWorldMap(
                    selectedCities = selectedCities,
                    highlightedTzId = highlightedTzId,
                    simulatedTime = simulatedTime,
                    onCitySelected = { tzId -> viewModel.setHighlightTzId(tzId) }
                )
            }

            // Scrubbing Timeline Controls
            TimeComparisonSlider(
                scrubberOffsetMinutes = scrubberOffset,
                isLiveMode = isLiveMode,
                simulatedTime = simulatedTime,
                onOffsetChanged = { viewModel.setScrubberOffsetHours(it) },
                onResetLive = { viewModel.toggleLiveMode(true) }
            )

            // Title & Active Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Timezone Hubs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    letterSpacing = 0.5.sp
                )
                if (highlightedTzId != null) {
                    TextButton(
                        onClick = { viewModel.setHighlightTzId(null) },
                        colors = ButtonDefaults.textButtonColors(contentColor = StarlightBlue)
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = "Clear Highlight", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset Map Pointer", fontSize = 12.sp)
                    }
                }
            }

            // Cards list of current selected cities
            if (selectedCities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(SpaceSlate, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Public,
                            contentDescription = null,
                            tint = TextDim.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No cities added yet",
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tap the '+' button below to add your first global city zone.",
                            color = TextDim,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("cities_list"),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(selectedCities, key = { it.timezoneId }) { city ->
                        CityTimeCard(
                            city = city,
                            simulatedTime = simulatedTime,
                            isFocusedOnMap = highlightedTzId == city.timezoneId,
                            onClick = {
                                viewModel.setHighlightTzId(city.timezoneId)
                            },
                            onDelete = {
                                viewModel.removeCityResult(city.timezoneId)
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Search Sheet
    if (showSearchSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showSearchSheet = false },
            sheetState = sheetState,
            containerColor = DeepMidnight
        ) {
            SearchCitySheetContent(
                viewModel = viewModel,
                simulatedTime = simulatedTime,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showSearchSheet = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun HeaderWidget(
    isLive: Boolean,
    simulatedTime: Instant,
    onResetLive: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "GLOBE TRACKER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = StarlightBlue,
                letterSpacing = 2.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Visionary Map Terminal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )
            }
        }

        Surface(
            color = if (isLive) Color(0x1510B981) else Color(0x15FBBF24),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isLive) WorkGreen.copy(alpha = 0.5f) else DayAmber.copy(alpha = 0.5f)
            ),
            modifier = Modifier.clickable { if (!isLive) onResetLive() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isLive) WorkGreen else DayAmber)
                )
                Text(
                    text = if (isLive) "LIVE SYSTEM TIME" else "EXPLORE COMPARING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLive) WorkGreen else DayAmber,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun InteractiveWorldMap(
    selectedCities: List<SelectedCity>,
    highlightedTzId: String?,
    simulatedTime: Instant,
    onCitySelected: (String) -> Unit
) {
    val radarTransition = rememberInfiniteTransition(label = "RadarPulse")
    val radarProgress by radarTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarScale"
    )

    val solarDeclination = remember(simulatedTime) {
        SolarCalculator.getDeclination(simulatedTime)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val mapWidth = constraints.maxWidth.toFloat()
        val mapHeight = constraints.maxHeight.toFloat()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(selectedCities) {
                    detectTapGestures { tapOffset ->
                        // Detect tap near any selected city to highlight it
                        var closestCity: SelectedCity? = null
                        var closestDist = 120f // Threshold within pixels (~40dp equivalent)

                        for (city in selectedCities) {
                            val (normX, normY) = WorldMapData.latLonToPos(city.latitude, city.longitude)
                            val cityPxX = normX * mapWidth
                            val cityPxY = normY * mapHeight
                            val dist = Math.hypot(
                                (tapOffset.x - cityPxX).toDouble(),
                                (tapOffset.y - cityPxY).toDouble()
                            ).toFloat()

                            if (dist < closestDist) {
                                closestDist = dist
                                closestCity = city
                            }
                        }

                        closestCity?.let {
                            onCitySelected(it.timezoneId)
                        }
                    }
                }
        ) {
            // 1. Draw grid of dots for world geography
            val cellWidth = mapWidth / WorldMapData.COLS
            val cellHeight = mapHeight / WorldMapData.ROWS
            val dotRadius = Math.min(cellWidth, cellHeight) * 0.38f

            for (row in 0 until WorldMapData.ROWS) {
                val rowStr = WorldMapData.grid.getOrNull(row) ?: continue
                for (col in 0 until WorldMapData.COLS) {
                    val char = rowStr.getOrNull(col) ?: ' '
                    if (char == '.' || char == '#') {
                        val lat = WorldMapData.indexToLat(row)
                        val lon = WorldMapData.indexToLon(col)

                        // Run precise solar calculation to determine if day or night or twilight
                        val elevation = SolarCalculator.calculateSunElevation(lat.toDouble(), lon.toDouble(), solarDeclination)

                        // Determine dot color according to daylight, twilight, or night curves
                        val dotColor = when {
                            elevation > 0.1 -> {
                                // Full daytime: beautiful warm sunny solar cream
                                DayAmber.copy(alpha = 0.5f)
                            }
                            elevation < -0.1 -> {
                                // Full nighttime: deep galactic slate blue
                                Color(0xFF1E293B)
                            }
                            else -> {
                                // Twilight terminator line (Dawn/Dusk): beautiful vibrant sunrise orange
                                DawnCrimson.copy(alpha = 0.8f)
                            }
                        }

                        drawCircle(
                            color = dotColor,
                            radius = dotRadius,
                            center = Offset(
                                col * cellWidth + cellWidth / 2,
                                row * cellHeight + cellHeight / 2
                            )
                        )
                    }
                }
            }

            // 2. Draw equator & prime meridian reference lines (Minimalist coordinate grids)
            val equatorY = WorldMapData.latLonToPos(0.0, 0.0).second * mapHeight
            val meridianX = WorldMapData.latLonToPos(0.0, 0.0).first * mapWidth
            drawLine(
                color = LunarGrey.copy(alpha = 0.25f),
                start = Offset(0f, equatorY),
                end = Offset(mapWidth, equatorY),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
            drawLine(
                color = LunarGrey.copy(alpha = 0.25f),
                start = Offset(meridianX, 0f),
                end = Offset(meridianX, mapHeight),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )

            // 3. Draw Selected Cities Markers on Map
            for (city in selectedCities) {
                val (normX, normY) = WorldMapData.latLonToPos(city.latitude, city.longitude)
                val pxX = normX * mapWidth
                val pxY = normY * mapHeight
                val isHighlighted = city.timezoneId == highlightedTzId

                // Check daylight status for the city
                val isDay = SolarCalculator.isDaylight(city.latitude, city.longitude, simulatedTime)
                val ringColor = if (isDay) DayAmber else StarlightBlue

                // Radar expansion ripple if highlighted
                if (isHighlighted) {
                    drawCircle(
                        color = ringColor.copy(alpha = 1f - radarProgress),
                        radius = (8.dp.toPx() + 20.dp.toPx() * radarProgress),
                        center = Offset(pxX, pxY),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                // Center solid beacon
                drawCircle(
                    color = if (isHighlighted) ringColor else ringColor.copy(alpha = 0.6f),
                    radius = if (isHighlighted) 5.dp.toPx() else 3.5.dp.toPx(),
                    center = Offset(pxX, pxY)
                )

                // Outer indicator boundary
                drawCircle(
                    color = TextWhite,
                    radius = if (isHighlighted) 7.dp.toPx() else 5.dp.toPx(),
                    center = Offset(pxX, pxY),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Overlay text tags for selected cities on map carefully to avoid overlapping clutter
        for (city in selectedCities) {
            val (normX, normY) = WorldMapData.latLonToPos(city.latitude, city.longitude)
            val isHighlighted = city.timezoneId == highlightedTzId

            // Display floating text indicator next to the highlighted city or prioritized selection
            if (isHighlighted) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = (normX * mapWidth / density - 44f).coerceIn(4f, mapWidth / density - 92f).dp,
                            top = (normY * mapHeight / density + 10f).coerceIn(4f, mapHeight / density - 45f).dp
                        )
                        .background(DeepMidnight.copy(alpha = 0.92f), RoundedCornerShape(6.dp))
                        .border(1.dp, StarlightBlue.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = city.cityName,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1
                    )
                }
            }
        }
    }
}



@Composable
fun TimeComparisonSlider(
    scrubberOffsetMinutes: Long,
    isLiveMode: Boolean,
    simulatedTime: Instant,
    onOffsetChanged: (Float) -> Unit,
    onResetLive: () -> Unit
) {
    // We map -18 hours to +18 hours of comparison offset
    val sliderValue = scrubberOffsetMinutes.toFloat() / 60f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SpaceSlate, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    tint = StarlightBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Timeline Global Adjuster",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            val offsetHoursLabel = if (scrubberOffsetMinutes == 0L) {
                "Actual Present Now"
            } else {
                val valHrs = scrubberOffsetMinutes / 60.0
                val sign = if (valHrs > 0) "+" else ""
                val formatted = if (valHrs % 1.0 == 0.0) valHrs.toInt().toString() else String.format("%.1f", valHrs)
                "$sign$formatted hrs offset"
            }

            Text(
                text = offsetHoursLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isLiveMode) StarlightBlue else DayAmber
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Slider(
            value = sliderValue.coerceIn(-18f, 18f),
            onValueChange = onOffsetChanged,
            valueRange = -18f..18f,
            colors = SliderDefaults.colors(
                thumbColor = if (isLiveMode) StarlightBlue else DayAmber,
                activeTrackColor = if (isLiveMode) StarlightBlue.copy(alpha = 0.6f) else DayAmber.copy(alpha = 0.6f),
                inactiveTrackColor = LunarGrey
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("time_offset_slider")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("-18 hrs", fontSize = 10.sp, color = TextDim)
            Text("-12h", fontSize = 10.sp, color = TextDim)
            Text("-6h", fontSize = 10.sp, color = TextDim)
            Text(
                "Present", 
                fontSize = 11.sp, 
                fontWeight = if (isLiveMode) FontWeight.Bold else FontWeight.Normal, 
                color = if (isLiveMode) StarlightBlue else TextDim,
                modifier = Modifier.clickable { onResetLive() }
            )
            Text("+6h", fontSize = 10.sp, color = TextDim)
            Text("+12h", fontSize = 10.sp, color = TextDim)
            Text("+18 hrs", fontSize = 10.sp, color = TextDim)
        }
    }
}

@Composable
fun CityTimeCard(
    city: SelectedCity,
    simulatedTime: Instant,
    isFocusedOnMap: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Calculate local values for this city
    val zdt = try {
        ZonedDateTime.ofInstant(simulatedTime, ZoneId.of(city.timezoneId))
    } catch (e: Exception) {
        ZonedDateTime.ofInstant(simulatedTime, ZoneId.of("UTC"))
    }

    val systemZdt = ZonedDateTime.ofInstant(simulatedTime, ZoneId.systemDefault())

    val cityHour = zdt.hour
    val isDaylight = SolarCalculator.isDaylight(city.latitude, city.longitude, simulatedTime)

    // Calculate relative offset and relative day label
    val relativeOffset = formatRelativeOffset(zdt, systemZdt)
    val relativeDay = getRelativeDayLabel(zdt, systemZdt)

    // Work Hours Definition:
    // Green (9 AM to 5 PM) -> Office shift open
    // Amber (6 AM - 9 AM OR 5 PM - 10 PM) -> Personal leisure clock
    // Indigo (10 PM - 6 AM) -> Rest/Sleep shift
    val availabilityStatus = when (cityHour) {
        in 9..16 -> Availability.Working
        in 6..8, in 17..21 -> Availability.Leisure
        else -> Availability.Sleeping
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isFocusedOnMap) SpaceSlate else SpaceSlate.copy(alpha = 0.75f)
        ),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isFocusedOnMap) StarlightBlue.copy(alpha = 0.8f) else LunarGrey
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("city_card_${city.timezoneId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Handdrawn dynamic Canvas clock face
            AnalogClockFace(
                hour = zdt.hour,
                minute = zdt.minute,
                second = zdt.second,
                isDay = isDaylight,
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info (City, Country, Offset)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = city.cityName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isFocusedOnMap) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(StarlightBlue)
                        )
                    }
                }
                Text(
                    text = city.countryName,
                    fontSize = 12.sp,
                    color = TextDim,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Availability chip based on local hour
                AvailabilityChip(availabilityStatus, localHour = zdt.hour)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Current Local Time Output
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = zdt.format(DateTimeFormatter.ofPattern("hh:mm")),
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDaylight) DayAmber else StarlightBlue
                )
                Text(
                    text = zdt.format(DateTimeFormatter.ofPattern("a")).uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDaylight) DayAmber.copy(alpha = 0.7f) else StarlightBlue.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$relativeDay ($relativeOffset)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDim,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Delete action button on the card
            IconButton(
                onClick = { onDelete() },
                modifier = Modifier.testTag("delete_city_${city.timezoneId}")
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove Zone",
                    tint = DawnCrimson.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AnalogClockFace(
    hour: Int,
    minute: Int,
    second: Int,
    isDay: Boolean,
    modifier: Modifier = Modifier
) {
    val dialColor = if (isDay) DayAmber else StarlightBlue
    val innerFaceBg = if (isDay) DayAmber.copy(alpha = 0.12f) else StarlightBlue.copy(alpha = 0.10f)

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2

        // Canvas face background shading
        drawCircle(
            color = innerFaceBg,
            radius = radius,
            center = center
        )
        // Dial stroke line
        drawCircle(
            color = dialColor.copy(alpha = 0.35f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.6.dp.toPx())
        )

        // Draw 12, 3, 6, 9 Cardinal Ticks
        for (angle in listOf(0f, 90f, 180f, 270f)) {
            val rad = Math.toRadians(angle.toDouble())
            val innerLength = radius - 3.5.dp.toPx()
            val outerLength = radius

            val start = Offset(
                (center.x + innerLength * Math.sin(rad)).toFloat(),
                (center.y - innerLength * Math.cos(rad)).toFloat()
            )
            val end = Offset(
                (center.x + outerLength * Math.sin(rad)).toFloat(),
                (center.y - outerLength * Math.cos(rad)).toFloat()
            )

            drawLine(
                color = dialColor.copy(alpha = 0.7f),
                start = start,
                end = end,
                strokeWidth = 1.5.dp.toPx()
            )
        }

        // Draw Dynamic Hour hand (handles fraction ticks beautifully)
        val hAngle = (hour % 12 + minute / 60f) * 30f
        val hRad = Math.toRadians(hAngle.toDouble())
        val hourHandLength = radius * 0.48f
        drawLine(
            color = TextWhite,
            start = center,
            end = Offset(
                (center.x + hourHandLength * Math.sin(hRad)).toFloat(),
                (center.y - hourHandLength * Math.cos(hRad)).toFloat()
            ),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Draw Dynamic Minute hand
        val mAngle = (minute + second / 60f) * 6f
        val mRad = Math.toRadians(mAngle.toDouble())
        val minuteHandLength = radius * 0.72f
        drawLine(
            color = TextWhite.copy(alpha = 0.85f),
            start = center,
            end = Offset(
                (center.x + minuteHandLength * Math.sin(mRad)).toFloat(),
                (center.y - minuteHandLength * Math.cos(mRad)).toFloat()
            ),
            strokeWidth = 1.8.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Draw Live Second tick hand
        val sAngle = second * 6f
        val sRad = Math.toRadians(sAngle.toDouble())
        val secondHandLength = radius * 0.84f
        drawLine(
            color = DawnCrimson,
            start = center,
            end = Offset(
                (center.x + secondHandLength * Math.sin(sRad)).toFloat(),
                (center.y - secondHandLength * Math.cos(sRad)).toFloat()
            ),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Center Pin anchor dot
        drawCircle(
            color = DawnCrimson,
            radius = 2.dp.toPx(),
            center = center
        )
    }
}

enum class Availability {
    Working, Leisure, Sleeping
}

@Composable
fun AvailabilityChip(
    availability: Availability,
    localHour: Int
) {
    val (label, bgColor, textColor) = when (availability) {
        Availability.Working -> Triple(
            "Working Shift (Business Ok)",
            WorkGreen.copy(alpha = 0.15f),
            WorkGreen
        )
        Availability.Leisure -> Triple(
            "Leisure Hours (Casual Chat)",
            DayAmber.copy(alpha = 0.12f),
            DayAmber
        )
        Availability.Sleeping -> Triple(
            "Sleeping Hours (Do Not Disturb)",
            SleepSlate.copy(alpha = 0.15f),
            TextDim
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(top = 2.dp)
    ) {
        Text(
            text = "$label • ${String.format("%02d", localHour)}:00",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCitySheetContent(
    viewModel: ClockViewModel,
    simulatedTime: Instant,
    onClose: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Discover Global Cities",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close panel", tint = TextWhite)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Large search bar input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search by name or country... (e.g. Paris)", color = TextDim) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StarlightBlue) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear text", tint = TextDim)
                    }
                }
            } else null,
            maxLines = 1,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SpaceSlate,
                unfocusedContainerColor = SpaceSlate,
                focusedBorderColor = StarlightBlue,
                unfocusedBorderColor = LunarGrey,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_cities_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "MATCHING DESTINATIONS (${searchResults.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextDim,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        // Scrollable Results List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("search_results_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchResults) { result ->
                val city = result.city
                // Draw current preview hours in search result item list dynamically
                val zdt = try {
                    ZonedDateTime.ofInstant(simulatedTime, ZoneId.of(city.timezoneId))
                } catch (e: Exception) {
                    ZonedDateTime.ofInstant(simulatedTime, ZoneId.of("UTC"))
                }
                val previewTime = zdt.format(DateTimeFormatter.ofPattern("hh:mm a"))
                val isDay = SolarCalculator.isDaylight(city.latitude, city.longitude, simulatedTime)

                Surface(
                    color = SpaceSlate.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LunarGrey),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = city.cityName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "${city.countryName} • (${city.timezoneId})",
                                fontSize = 11.sp,
                                color = TextDim,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Preview Time tag badge
                        Surface(
                            color = if (isDay) DayAmber.copy(alpha = 0.15f) else StarlightBlue.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = previewTime,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDay) DayAmber else StarlightBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Trigger Add or Added removal action
                        IconButton(
                            onClick = {
                                if (result.isAdded) {
                                    viewModel.removeCityResult(city.timezoneId)
                                } else {
                                    viewModel.addCityResult(city)
                                }
                            },
                            modifier = Modifier.testTag("toggle_add_${city.cityName}")
                        ) {
                            if (result.isAdded) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Added. Tap to delete.",
                                    tint = DawnCrimson,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Tap to Add Zone",
                                    tint = StarlightBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Global relative offsets calculation helpers
fun getRelativeDayLabel(cityTime: ZonedDateTime, systemTime: ZonedDateTime): String {
    val cityDay = cityTime.toLocalDate()
    val systemDay = systemTime.toLocalDate()
    return when {
        cityDay == systemDay -> "Today"
        cityDay == systemDay.plusDays(1) -> "Tomorrow"
        cityDay == systemDay.minusDays(1) -> "Yesterday"
        else -> cityTime.format(DateTimeFormatter.ofPattern("MMM dd"))
    }
}

fun formatRelativeOffset(cityTime: ZonedDateTime, systemTime: ZonedDateTime): String {
    val cityOffsetSec = cityTime.offset.totalSeconds
    val systemOffsetSec = systemTime.offset.totalSeconds
    val diffSec = cityOffsetSec - systemOffsetSec
    val diffHrs = diffSec / 3600.0
    val sign = if (diffHrs >= 0) "+" else ""
    val hrsLabel = if (diffHrs % 1.0 == 0.0) diffHrs.toInt().toString() else String.format("%.1f", diffHrs)
    return "$sign${hrsLabel}h"
}
