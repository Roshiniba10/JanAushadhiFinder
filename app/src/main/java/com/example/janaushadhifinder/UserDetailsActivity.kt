package com.example.janaushadhifinder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class UserDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        val btnViewFavorites = findViewById<ImageButton>(R.id.btnViewFavorites)
        
        val nameInputLayout = findViewById<TextInputLayout>(R.id.nameInputLayout)
        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val genderToggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.genderToggleGroup)
        val ageInputLayout = findViewById<TextInputLayout>(R.id.ageInputLayout)
        val ageInput = findViewById<TextInputEditText>(R.id.ageInput)
        val weightInputLayout = findViewById<TextInputLayout>(R.id.weightInputLayout)
        val weightInput = findViewById<TextInputEditText>(R.id.weightInput)
        val heightInputLayout = findViewById<TextInputLayout>(R.id.heightInputLayout)
        val heightInput = findViewById<TextInputEditText>(R.id.heightInput)
        val nextBtn = findViewById<Button>(R.id.nextBtn)

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

        nextBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val age = ageInput.text.toString().trim()
            val weight = weightInput.text.toString().trim()
            val height = heightInput.text.toString().trim()
            
            val selectedGenderId = genderToggleGroup.checkedButtonId
            val gender = when (selectedGenderId) {
                R.id.btnMale -> "Male"
                R.id.btnFemale -> "Female"
                R.id.btnOther -> "Other"
                else -> ""
            }

            var isValid = true

            if (name.isEmpty()) {
                nameInputLayout.error = "Name is required"
                isValid = false
            } else if (!name.all { it.isLetter() || it.isWhitespace() }) {
                nameInputLayout.error = "Alphabets only"
                isValid = false
            } else {
                nameInputLayout.error = null
            }

            if (gender.isEmpty()) {
                Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            if (age.isEmpty()) {
                ageInputLayout.error = "Required"
                isValid = false
            } else {
                ageInputLayout.error = null
            }

            if (weight.isEmpty()) {
                weightInputLayout.error = "Required"
                isValid = false
            } else {
                weightInputLayout.error = null
            }

            if (height.isEmpty()) {
                heightInputLayout.error = "Required"
                isValid = false
            } else {
                heightInputLayout.error = null
            }

            if (isValid) {
                try {
                    val w = weight.toFloat()
                    val h = height.toFloat() / 100
                    val bmi = w / (h * h)
                    val bmiStatus = getBMIStatus(bmi)

                    val intent = Intent(this, DiseaseSelectionActivity::class.java)
                    intent.putExtra("USER_NAME", name)
                    intent.putExtra("USER_GENDER", gender)
                    intent.putExtra("USER_AGE", age)
                    intent.putExtra("USER_WEIGHT", weight)
                    intent.putExtra("USER_HEIGHT", height)
                    intent.putExtra("BMI_VALUE", String.format(Locale.US, "%.1f", bmi))
                    intent.putExtra("BMI_STATUS", bmiStatus)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getBMIStatus(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Normal weight"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }
    }
}
