package com.example.janaushadhifinder.model

data class Medicine(
    val brandName: String,
    val genericName: String,
    val brandPrice: Double,
    val genericPrice: Double,
    val disease: String,
    val symptoms: String = "",
    val isAvailable: Boolean = true
)