package com.example.janaushadhifinder.model

data class Store(
    val name: String,
    val distance: String,
    val isOpen: Boolean,
    val address: String,
    val latitude: Double,
    val longitude: Double
)