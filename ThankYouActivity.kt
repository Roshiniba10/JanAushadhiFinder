package com.example.janaushadhifinder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ThankYouActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        val btnViewFavorites = findViewById<ImageButton>(R.id.btnViewFavorites)
        val backToHomeBtn = findViewById<Button>(R.id.backToHomeBtn)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnSettings.setOnClickListener {
            ThemeUtils.toggleTheme(this)
            recreate()
        }

        btnViewFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        backToHomeBtn.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}