package com.abhijitsaha.goodine

import java.util.Date

data class Restaurant(
    val id: String = "",
    val ownerName: String = "",
    val name: String = "",
    val type: String = "",
    val city: String = "",
    val state: String = "",
    val address: String = "",
    val zipcode: String = "",
    val averageCost: String? = null,
    val openingTime: Date = Date(),
    val closingTime: Date = Date(),
    val imageUrls: List<String> = emptyList(),
    val currency: String = "INR",
    val currencySymbol: String = "₹"
)
