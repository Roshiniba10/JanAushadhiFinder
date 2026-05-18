package com.example.janaushadhifinder

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.janaushadhifinder.repository.MedicineRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class DiseaseSelectionActivity : AppCompatActivity() {

    private lateinit var repository: MedicineRepository
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_selection)

        repository = MedicineRepository()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        val btnViewFavorites = findViewById<ImageButton>(R.id.btnViewFavorites)
        val welcomeText = findViewById<TextView>(R.id.welcomeUserText)
        val bmiValueText = findViewById<TextView>(R.id.bmiValueText)
        val bmiStatusText = findViewById<TextView>(R.id.bmiStatusText)
        val genderTextDisplay = findViewById<TextView>(R.id.genderTextDisplay)
        
        val diseaseAutoComplete = findViewById<AutoCompleteTextView>(R.id.diseaseAutoComplete)
        val customDiseaseInput = findViewById<EditText>(R.id.customDiseaseInput)
        val findMedicineBtn = findViewById<Button>(R.id.findMedicineBtn)

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

        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        val gender = intent.getStringExtra("USER_GENDER") ?: ""
        val age = intent.getStringExtra("USER_AGE") ?: ""
        val weight = intent.getStringExtra("USER_WEIGHT") ?: ""
        val height = intent.getStringExtra("USER_HEIGHT") ?: ""
        val bmiValue = intent.getStringExtra("BMI_VALUE") ?: "--"
        val bmiStatus = intent.getStringExtra("BMI_STATUS") ?: "--"

        welcomeText.text = "Hello, $userName!"
        bmiValueText.text = "Your BMI: $bmiValue"
        bmiStatusText.text = "Status: $bmiStatus"
        genderTextDisplay.text = "Gender: $gender"

        val diseases = repository.getAllDiseases()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, diseases)
        diseaseAutoComplete.setAdapter(adapter)

        findMedicineBtn.setOnClickListener {
            val selectedFromDropdown = diseaseAutoComplete.text.toString().trim()
            val customInput = customDiseaseInput.text.toString().trim()
            val finalDisease = if (customInput.isNotEmpty()) customInput else selectedFromDropdown

            if (!finalDisease.isNullOrBlank()) {
                // Show a loading toast
                Toast.makeText(this, "Saving data...", Toast.LENGTH_SHORT).show()

                val userMap = hashMapOf(
                    "name" to userName,
                    "gender" to gender,
                    "age" to age,
                    "weight" to weight,
                    "height" to height,
                    "bmi" to bmiValue,
                    "bmiStatus" to bmiStatus,
                    "disease" to finalDisease,
                    "timestamp" to Timestamp.now()
                )

                db.collection("users")
                    .add(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully saved to Cloud!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("SELECTED_DISEASE", finalDisease)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // If it fails, show the exact reason (e.g. Permission Denied)
                        Toast.makeText(this, "Cloud Save Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        
                        // Proceed to results anyway so the app doesn't feel broken
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("SELECTED_DISEASE", finalDisease)
                        startActivity(intent)
                    }
            } else {
                Toast.makeText(this, "Please select or enter a disease", Toast.LENGTH_SHORT).show()
            }
        }
    }
}