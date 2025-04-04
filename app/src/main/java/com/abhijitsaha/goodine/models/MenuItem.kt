package com.abhijitsaha.goodine.models

import java.util.UUID

data class MenuItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val isVeg: Boolean = false,
    val imageUrl: String = ""
)
