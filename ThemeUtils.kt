package com.example.janaushadhifinder

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {
    fun toggleTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val isCurrentlyDark = sharedPreferences.getBoolean("is_dark_mode", false)
        
        val nextMode = if (isCurrentlyDark) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }

        sharedPreferences.edit().putBoolean("is_dark_mode", !isCurrentlyDark).apply()
        AppCompatDelegate.setDefaultNightMode(nextMode)
        
        val message = if (isCurrentlyDark) "Light Mode Activated" else "Dark Mode Activated"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}