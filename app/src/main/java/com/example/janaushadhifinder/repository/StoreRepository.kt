package com.example.janaushadhifinder.repository

import com.example.janaushadhifinder.model.Store

class StoreRepository {
    private val stores = listOf(
        // Bengaluru Stores
        Store("Jan Aushadhi Kendra - Majestics", "1.2 km", true, "Near Railway Station, Bengaluru, Karnataka", 12.9767, 77.5713),
        Store("PMBJP Store - Jayanagar", "3.5 km", true, "4th Block, Jayanagar, Bengaluru, Karnataka", 12.9250, 77.5897),
        
        // Mysore Stores
        Store("Jan Aushadhi Kendra - Mysore Central", "0.5 km", true, "Near Palace, Mysore, Karnataka", 12.3051, 76.6551),
        Store("PMBJP Store - Kuvempunagar", "2.8 km", true, "Kuvempunagar, Mysore, Karnataka", 12.2900, 76.6200),
        
        // Kodagu Stores
        Store("Jan Aushadhi Kendra - Madikeri", "1.1 km", true, "Town Hall Road, Madikeri, Kodagu, Karnataka", 12.4244, 75.7382),
        Store("PMBJP Store - Kushalnagar", "15 km", false, "Main Road, Kushalnagar, Kodagu, Karnataka", 12.4439, 75.9600),
        
        // More Bengaluru Stores
        Store("Jan Aushadhi - Malleshwaram", "4.1 km", false, "Sampige Road, Malleshwaram, Bengaluru, Karnataka", 12.9988, 77.5717),
        Store("Generic Medical Store - Indiranagar", "6.2 km", true, "100 Feet Rd, Indiranagar, Bengaluru, Karnataka", 12.9719, 77.6412),
        Store("Aushadhi Kendra - Mysore Road", "5.8 km", true, "Mysore Road, Nayandahalli, Bengaluru, Karnataka", 12.9430, 77.5220)
    )

    fun getNearbyStores(): List<Store> {
        return stores
    }
}