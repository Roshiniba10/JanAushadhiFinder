package com.example.janaushadhifinder.repository

import com.example.janaushadhifinder.model.Medicine

class MedicineRepository {

    private val medicineList = listOf(
        // Fever
        Medicine("Dolo 650", "Paracetamol", 30.0, 10.0, "Fever", "High temperature, body ache, headache", true),
        Medicine("Calpol", "Paracetamol", 25.0, 10.0, "Fever", "Fever, pain, chills", true),
        
        // Cold & Cough
        Medicine("Vicks Action 500", "Paracetamol + Phenylephrine", 40.0, 15.0, "Common Cold", "Runny nose, sneezing, congestion", true),
        Medicine("Ascoril LS", "Ambroxol + Levosalbutamol + Guaiphenesin", 110.0, 35.0, "Cough", "Wet cough, chest congestion, mucus", true),
        Medicine("Benadryl", "Diphenhydramine", 120.0, 40.0, "Cough", "Dry cough, throat irritation, allergy", false),
        Medicine("Grilinctus", "Dextromethorphan + Chlorpheniramine", 105.0, 32.0, "Cough", "Dry cough, throat itching", true),

        // Allergy
        Medicine("Cetrizine", "Cetirizine Hydrochloride", 20.0, 5.0, "Allergy", "Itching, watery eyes, skin rash, sneezing", true),
        Medicine("Avil", "Pheniramine Maleate", 15.0, 4.0, "Allergy", "Allergic reaction, itching, hives", true),
        Medicine("Allegra", "Fexofenadine", 180.0, 60.0, "Allergy", "Seasonal allergy, runny nose, sneezing", false),

        // Pain Relief
        Medicine("Combiflam", "Ibuprofen + Paracetamol", 45.0, 12.0, "Pain Relief", "Muscle pain, toothache, joints pain", true),
        Medicine("Moov", "Diclofenac Gel", 150.0, 50.0, "Pain Relief", "Back pain, sprain, muscle strain", true),
        Medicine("Saridon", "Paracetamol + Propyphenazone + Caffeine", 40.0, 15.0, "Headache", "Severe headache, migraine", true),
        Medicine("Diclomol", "Diclofenac + Paracetamol", 45.0, 15.0, "Body Pain", "Chronic pain, inflammation", true),

        // Acidity & Digestion
        Medicine("Pan-D", "Pantoprazole + Domperidone", 150.0, 45.0, "Acidity", "Heartburn, bloating, gas, stomach discomfort", true),
        Medicine("Digene", "Magnesium Hydroxide + Aluminium Hydroxide", 130.0, 40.0, "Acidity", "Stomach acid, indigestion, sour stomach", true),
        Medicine("Eno", "Sodium Bicarbonate + Citric Acid", 10.0, 5.0, "Acidity", "Instant relief from acidity and gas", true),
        
        // Stomach issues
        Medicine("Meftal Spas", "Mefenamic Acid + Dicyclomine", 50.0, 15.0, "Stomach Pain", "Abdominal cramps, period pain", true),
        Medicine("Cyclopam", "Dicyclomine + Paracetamol", 60.0, 18.0, "Stomach Pain", "Stomach ache, bowel discomfort", false),
        Medicine("O2", "Ofloxacin + Ornidazole", 120.0, 40.0, "Loose Motion", "Diarrhea, stomach infection", true),

        // Diabetes
        Medicine("Glycomet", "Metformin", 60.0, 20.0, "Diabetes", "High blood sugar, insulin resistance", true),
        Medicine("Janumet", "Sitagliptin + Metformin", 300.0, 90.0, "Diabetes", "Type 2 diabetes management", true),

        // Blood Pressure
        Medicine("Telma 40", "Telmisartan", 90.0, 25.0, "Blood Pressure", "Hypertension, high BP", true),
        Medicine("Amlokind", "Amlodipine", 40.0, 12.0, "Blood Pressure", "Chest pain, hypertension", true),

        // General Health / Immunity
        Medicine("Limcee", "Vitamin C", 30.0, 10.0, "Immunity", "Vitamin deficiency, weak immune system", true),
        Medicine("Zincovit", "Multivitamins + Zinc", 105.0, 35.0, "Immunity", "Fatigue, recovery after illness", true),
        Medicine("ORS", "Oral Rehydration Salts", 20.0, 5.0, "Dehydration", "Fluid loss, vomiting, loose motion", true)
    )

    fun getAllMedicines(): List<Medicine> = medicineList

    fun getAllDiseases(): List<String> = medicineList.map { it.disease }.distinct().sorted()

    fun getRandomHealthTip(): String {
        val tips = listOf(
            "Generic medicines have the same active ingredients as branded ones.",
            "Complete the full course of antibiotics as prescribed.",
            "Jan Aushadhi medicines are quality-tested at NABL labs.",
            "Generic medicines can save you up to 90% on healthcare."
        )
        return tips.random()
    }

    fun smartSearch(query: String): List<Medicine> {
        if (query.isBlank()) return medicineList
        val q = query.lowercase().trim()
        return medicineList.filter {
            it.brandName.lowercase().contains(q) ||
            it.genericName.lowercase().contains(q) ||
            it.disease.lowercase().contains(q) ||
            it.symptoms.lowercase().contains(q)
        }
    }

    fun getMedicinesWithStatus(disease: String): Pair<List<Medicine>, Boolean> {
        val results = medicineList.filter { it.disease.contains(disease, true) }
        return if (results.isNotEmpty()) Pair(results, false)
        else Pair(medicineList.filter { it.disease == "Immunity" || it.disease == "Fever" }, true)
    }
}