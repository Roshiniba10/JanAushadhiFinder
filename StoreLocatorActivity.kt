package com.example.janaushadhifinder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.janaushadhifinder.model.Store
import com.example.janaushadhifinder.repository.StoreRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class StoreLocatorActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var storeRepository: StoreRepository
    private lateinit var storeContainer: LinearLayout
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_locator)

        storeRepository = StoreRepository()
        storeContainer = findViewById(R.id.storeContainer)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        val btnViewFavorites = findViewById<ImageButton>(R.id.btnViewFavorites)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

        displayStores()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        addStoreMarkers()
    }

    private fun addStoreMarkers() {
        val stores = storeRepository.getNearbyStores()
        if (stores.isEmpty()) return

        for (store in stores) {
            val location = LatLng(store.latitude, store.longitude)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(store.name)
                    .snippet(store.address)
            )
        }

        // Focus on the first store
        val firstStore = LatLng(stores[0].latitude, stores[0].longitude)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(firstStore, 12f))
    }

    private fun displayStores() {
        storeContainer.removeAllViews()
        val stores = storeRepository.getNearbyStores()
        val inflater = LayoutInflater.from(this)

        for (store in stores) {
            val itemView = inflater.inflate(R.layout.item_store, storeContainer, false)
            
            val nameText = itemView.findViewById<TextView>(R.id.storeNameText)
            val addressText = itemView.findViewById<TextView>(R.id.storeAddressText)
            val distanceText = itemView.findViewById<TextView>(R.id.storeDistanceText)
            val statusText = itemView.findViewById<TextView>(R.id.storeStatusText)
            val btnOpenMap = itemView.findViewById<Button>(R.id.btnOpenMap)

            nameText.text = store.name
            addressText.text = store.address
            distanceText.text = store.distance
            
            if (store.isOpen) {
                statusText.text = "OPEN NOW"
                statusText.setBackgroundResource(R.drawable.savings_badge_bg)
            } else {
                statusText.text = "CLOSED"
                statusText.setBackgroundResource(R.drawable.closed_badge_bg)
            }

            btnOpenMap.setOnClickListener {
                try {
                    val mapIntentUri = Uri.parse("geo:${store.latitude},${store.longitude}?q=${Uri.encode(store.name)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri)
                    startActivity(mapIntent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Could not open Maps", Toast.LENGTH_SHORT).show()
                }
            }
            
            itemView.setOnClickListener {
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(store.latitude, store.longitude), 
                        15f
                    )
                )
            }

            storeContainer.addView(itemView)
        }
    }
}
