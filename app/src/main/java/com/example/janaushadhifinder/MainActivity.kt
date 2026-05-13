package com.example.janaushadhifinder

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.example.janaushadhifinder.model.Medicine
import com.example.janaushadhifinder.repository.FavoriteManager
import com.example.janaushadhifinder.repository.MedicineRepository
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var repository: MedicineRepository
    private lateinit var favoriteManager: FavoriteManager
    private lateinit var resultContainer: LinearLayout
    private lateinit var diseaseSubtitle: TextView
    private var initialDisease: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = MedicineRepository()
        favoriteManager = FavoriteManager(this)
        
        resultContainer = findViewById(R.id.resultContainer)
        diseaseSubtitle = findViewById(R.id.diseaseNameSubtitle)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnViewFavorites = findViewById<ImageButton>(R.id.btnViewFavorites)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        val finishBtn = findViewById<Button>(R.id.finishBtn)
        val searchView = findViewById<SearchView>(R.id.medicineSearchView)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Theme Toggle
        btnSettings.setOnClickListener {
            ThemeUtils.toggleTheme(this)
            recreate()
        }

        // Navigate to dedicated Bookmarks screen
        btnViewFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        // Get selected disease from Intent
        initialDisease = intent.getStringExtra("SELECTED_DISEASE") ?: ""
        
        if (initialDisease.isNotEmpty()) {
            displayInitialResults(initialDisease)
        } else {
            diseaseSubtitle.text = getString(R.string.app_name) + ": Search medicines"
        }

        // --- Smart Search Implementation ---
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    if (initialDisease.isNotEmpty()) {
                        displayInitialResults(initialDisease)
                    } else {
                        resultContainer.removeAllViews()
                        diseaseSubtitle.text = "Search for medicines, diseases or symptoms"
                    }
                } else {
                    val filteredList = repository.smartSearch(newText)
                    updateUI(filteredList, "Search results for: $newText", false)
                }
                return true
            }
        })

        finishBtn.setOnClickListener {
            val intent = Intent(this, ThankYouActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayInitialResults(disease: String) {
        val (medicines, isFallback) = repository.getMedicinesWithStatus(disease)
        val statusText = if (isFallback) {
            "No exact match for \"$disease\". Showing suggestions:"
        } else {
            "Recommended for: $disease"
        }
        updateUI(medicines, statusText, isFallback)
    }

    private fun updateUI(medicines: List<Medicine>, subtitle: String, isAccent: Boolean) {
        resultContainer.removeAllViews()
        diseaseSubtitle.text = subtitle
        diseaseSubtitle.setTextColor(
            if (isAccent) ContextCompat.getColor(this, R.color.secondary)
            else ContextCompat.getColor(this, R.color.text_body)
        )

        if (medicines.isEmpty()) {
            val noDataText = TextView(this)
            noDataText.text = "No matching medicines found."
            noDataText.setPadding(40, 80, 40, 40)
            noDataText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            noDataText.gravity = android.view.Gravity.CENTER
            noDataText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            resultContainer.addView(noDataText)
            return
        }

        val inflater = LayoutInflater.from(this)
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        
        for (m in medicines) {
            val itemView = inflater.inflate(R.layout.item_medicine, resultContainer, false)
            
            val genericName = itemView.findViewById<TextView>(R.id.genericNameText)
            val brandName = itemView.findViewById<TextView>(R.id.brandNameText)
            val diseaseContext = itemView.findViewById<TextView>(R.id.diseaseContextText)
            val genericPrice = itemView.findViewById<TextView>(R.id.genericPriceText)
            val savings = itemView.findViewById<TextView>(R.id.savingsText)
            val availabilityText = itemView.findViewById<TextView>(R.id.availabilityText)
            val btnLocate = itemView.findViewById<Button>(R.id.btnLocate)
            val btnShare = itemView.findViewById<Button>(R.id.btnShare)
            val btnFavorite = itemView.findViewById<ImageButton>(R.id.btnFavorite)
            val btnRemind = itemView.findViewById<Button>(R.id.btnRemind)

            genericName.text = m.genericName
            brandName.text = getString(R.string.brand_format, m.brandName, m.brandPrice)
            diseaseContext.text = getString(R.string.used_for_format, m.disease)
            genericPrice.text = getString(R.string.price_format, m.genericPrice)
            savings.text = getString(R.string.savings_format, m.brandPrice - m.genericPrice)

            if (m.isAvailable) {
                availabilityText.text = "Available"
                availabilityText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            } else {
                availabilityText.text = "Out of Stock"
                availabilityText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }

            // Handle Bookmarking logic
            updateFavoriteIcon(btnFavorite, m.genericName)
            btnFavorite.setOnClickListener {
                favoriteManager.toggleFavorite(m.genericName)
                updateFavoriteIcon(btnFavorite, m.genericName)
                if (favoriteManager.isFavorite(m.genericName)) {
                    Toast.makeText(this, "Bookmarked!", Toast.LENGTH_SHORT).show()
                }
            }

            btnLocate.setOnClickListener {
                val intent = Intent(this, StoreLocatorActivity::class.java)
                startActivity(intent)
            }

            btnRemind.setOnClickListener {
                showTimePickerDialog(m.genericName)
            }

            btnShare.setOnClickListener {
                val shareText = "Affordable alternative for ${m.brandName}: Use ${m.genericName} at only ₹${m.genericPrice}!"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, "Share via"))
            }

            itemView.startAnimation(fadeIn)
            resultContainer.addView(itemView)
        }
    }

    private fun updateFavoriteIcon(button: ImageButton, medicineName: String) {
        if (favoriteManager.isFavorite(medicineName)) {
            button.setImageResource(android.R.drawable.btn_star_big_on)
        } else {
            button.setImageResource(android.R.drawable.btn_star_big_off)
        }
    }

    private fun showTimePickerDialog(medicineName: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            scheduleReminder(medicineName, selectedHour, selectedMinute)
        }, hour, minute, true).show()
    }

    private fun scheduleReminder(medicineName: String, hour: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Check for exact alarm permission on Android 12+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("MEDICINE_NAME", medicineName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            medicineName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Toast.makeText(this, "Reminder set for ${String.format("%02d:%02d", hour, minute)}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to set reminder", Toast.LENGTH_SHORT).show()
        }
    }
}
