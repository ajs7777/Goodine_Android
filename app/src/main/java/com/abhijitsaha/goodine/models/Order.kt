package com.abhijitsaha.goodine.models

import com.google.firebase.Timestamp
import java.util.Date


data class Order(
    val id: String = "",
    val userId: String = "",
    val items: Map<String, OrderItem> = emptyMap(),
    val timestamp: Timestamp = Timestamp(Date()),
    val status: String = "pending"
)


data class OrderItem(
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
)
