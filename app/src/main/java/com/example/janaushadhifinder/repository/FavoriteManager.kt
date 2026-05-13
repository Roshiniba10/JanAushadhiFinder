package com.example.janaushadhifinder.repository

import android.content.Context
import android.content.SharedPreferences

class FavoriteManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    fun isFavorite(medicineName: String): Boolean {
        return getFavorites().contains(medicineName)
    }

    fun toggleFavorite(medicineName: String) {
        val favorites = getFavorites().toMutableSet()
        if (favorites.contains(medicineName)) {
            favorites.remove(medicineName)
        } else {
            favorites.add(medicineName)
        }
        prefs.edit().putStringSet("favorite_list", favorites).apply()
    }

    fun getFavorites(): Set<String> {
        return prefs.getStringSet("favorite_list", emptySet()) ?: emptySet()
    }
}