package com.example.janaushadhifinder

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.janaushadhifinder.model.Medicine
import com.example.janaushadhifinder.repository.FavoriteManager
import com.example.janaushadhifinder.repository.MedicineRepository
import java.util.*

class FavoritesActivity : AppCompatActivity() {

    private lateinit var repository: MedicineRepository
    private lateinit var favoriteManager: FavoriteManager
    private lateinit var favoritesContainer: LinearLayout
    private lateinit var emptyState: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        repository = MedicineRepository()
        favoriteManager = FavoriteManager(this)
        
        favoritesContainer = findViewById(R.id.favoritesContainer)
        emptyState = findViewById(R.id.emptyState)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnSettings.setOnClickListener {
            ThemeUtils.toggleTheme(this)
            recreate()
        }

        displayFavorites()
    }

    private fun displayFavorites() {
        favoritesContainer.removeAllViews()
        val favorites = favoriteManager.getFavorites()
        
        val favoriteMedicines = repository.getAllMedicines().filter { 
            favorites.contains(it.genericName) 
        }

        if (favoriteMedicines.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            return
        } else {
            emptyState.visibility = View.GONE
        }

        val inflater = LayoutInflater.from(this)
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)

        for (m in favoriteMedicines) {
            val itemView = inflater.inflate(R.layout.item_medicine, favoritesContainer, false)
            
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

            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on)

            btnFavorite.setOnClickListener {
                favoriteManager.toggleFavorite(m.genericName)
                Toast.makeText(this, "Removed from bookmarks", Toast.LENGTH_SHORT).show()
                displayFavorites()
            }

            btnLocate.setOnClickListener {
                startActivity(Intent(this, StoreLocatorActivity::class.java))
            }

            btnRemind.setOnClickListener {
                showTimePickerDialog(m.genericName)
            }

            btnShare.setOnClickListener {
                val shareText = "Found an affordable generic alternative for ${m.brandName}!\n\nGeneric: ${m.genericName}\nJan Aushadhi Price: ₹${m.genericPrice}\nYou save: ₹${m.brandPrice - m.genericPrice}"
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, "Share via"))
            }

            itemView.startAnimation(fadeIn)
            favoritesContainer.addView(itemView)
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
