package com.abhijitsaha.goodine.models

import java.util.Date

data class HistoryRecord(
    val reservationID: String = "",
    val isPaid: Boolean = false,
    val billingTime: Date = Date(),
    val timestamp: Date = Date(),
    val peopleCount: Map<Int, Int> = emptyMap(),
    val tables: List<Int> = emptyList(),
    val seats: Map<Int, List<Boolean>> = emptyMap()
)

