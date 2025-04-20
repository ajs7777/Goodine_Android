package com.abhijitsaha.goodine.models

import java.util.Date

data class Reservation(
    val id: String = "",
    val tables: List<Int> = emptyList(),
    val seats: Map<Int, List<Boolean>> = emptyMap(),
    val peopleCount: Map<Int, Int> = emptyMap(),
    val timestamp: Date = Date(),
    val billingTime: Date? = null,
    val isPaid: Boolean = false
)