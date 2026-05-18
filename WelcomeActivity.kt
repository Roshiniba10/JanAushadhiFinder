package com.example.janaushadhifinder

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.janaushadhifinder.repository.MedicineRepository
import com.google.android.material.card.MaterialCardView

class WelcomeActivity : AppCompatActivity() {

    private lateinit var repository: MedicineRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        repository = MedicineRepository()

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val healthTipText = findViewById<TextView>(R.id.healthTipText)
        val logoCard = findViewById<MaterialCardView>(R.id.logoCard)
        val insightCard = findViewById<MaterialCardView>(R.id.insightCard)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        val btnViewFavorites = findViewById<ImageButton>(R.id.btnViewFavorites)

        // Set a random health tip to keep the screen dynamic
        healthTipText.text = repository.getRandomHealthTip()

        // Vibrant Entrance Animations
        val fadeInScale = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeInScale.duration = 1000
        
        logoCard.startAnimation(fadeInScale)
        insightCard.startAnimation(fadeInScale)

        btnSettings.setOnClickListener {
            ThemeUtils.toggleTheme(this)
            recreate()
        }

        btnViewFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        loginBtn.setOnClickListener {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, UserDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}