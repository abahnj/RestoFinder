package com.wolt.restofinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wolt.restofinder.presentation.theme.RestoFinderTheme
import com.wolt.restofinder.presentation.venues.VenueListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestoFinderTheme {
                VenueListScreen()
            }
        }
    }
}