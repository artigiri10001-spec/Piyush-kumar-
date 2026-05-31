package com.example.data

object WorldMapData {
    // 80 columns representing 360 degrees of longitude (-180 to +180)
    // 34 rows representing ~160 degrees of latitude (North to South, excluding extremes)
    val grid = listOf(
        "                                . . . .. . .                                    ", // Row 0: Greenland, Northern Islands
        "                            . ............. . .                                 ", // Row 1: Greenland, Northern Canada, Arctic Russia
        "                      . .........................                               ", // Row 2
        "       ........................................... .                            ", // Row 3: Canada, Alaska, Siberia
        "      .............................................                             ", // Row 4
        "    ..................................................                          ", // Row 5: N. America, Russia, Northern Europe
        "    .....................................................        ..             ", // Row 6
        "    ........................................................    ....            ", // Row 7
        "   ........................................................... ......           ", // Row 8: US, Europe, Central Asia, China
        "   ..................................................................           ", // Row 9
        "   ......................       ....................................            ", // Row 10: US, Mediterranean, Middle East, Asia, Japan
        "     ..................           ..................................            ", // Row 11
        "      ...............             .................................             ", // Row 12: Mexico, North Africa, India, Southeast Asia
        "       ............               .................................             ", // Row 13
        "        ..........               ..................................             ", // Row 14: Central America, Sahara, South Asia, Philippines
        "         .......                 ............................. .                ", // Row 15
        "         ......                  ............................                   ", // Row 16: Central America, Central Africa, Indonesia
        "          ....                    ..........................                    ", // Row 17
        "          ....                     ........................                     ", // Row 18: South America (North), Central-South Africa, Indonesia, PNG
        "          .....                    ......................                       ", // Row 19
        "          ......                    ..................           .. .           ", // Row 20: S. America, Congo, S. East Africa, Australia (North)
        "          ......                    ...............            .......          ", // Row 21
        "          .....                      ...........              .........         ", // Row 22: S. America, South Africa, Australia (Central)
        "          ....                       ..........              .........          ", // Row 23
        "          ....                        ........               .........          ", // Row 24: S. America, Tasmania, S. Australia, New Zealand
        "           ...                        .......                 .......           ", // Row 25
        "           ...                         .....                     ...            ", // Row 26
        "            ..                         .....                      .             ", // Row 27
        "            .                           ...                                     ", // Row 28
        "                                         .                                      ", // Row 29
        "                                                                                "  // Row 30
    )

    const val COLS = 80
    const val ROWS = 31

    // Convert pixel coordinates or map indices back to Latitude/Longitude
    fun indexToLon(col: Int): Float {
        return -180f + (col.toFloat() / COLS) * 360f
    }

    fun indexToLat(row: Int): Float {
        // Linear mapping from top (row 0) to bottom (row ROWS - 1)
        // North is ~75N, South is ~55S to center the populated continents
        return 75f - (row.toFloat() / ROWS) * 130f
    }

    // Convert Latitude/Longitude to matrix coordinate [0..1] range
    fun latLonToPos(lat: Double, lon: Double): Pair<Float, Float> {
        val x = ((lon + 180f) / 360f).toFloat()
        // Center-map latitude
        val latClamped = lat.coerceIn(-55.0, 75.0)
        val y = ((75.0 - latClamped) / 130.0).toFloat()
        return Pair(x, y)
    }
}
