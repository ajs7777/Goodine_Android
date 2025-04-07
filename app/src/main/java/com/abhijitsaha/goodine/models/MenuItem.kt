package com.abhijitsaha.goodine.models

import java.util.UUID

data class MenuItem(
    val id: String = UUID.randomUUID().toString(),
    val foodname: String = "",
    val foodDescription: String = "",
    val foodPrice: Double = 0.0,
    val veg: Boolean = false,
    val foodImage: String = ""
)
