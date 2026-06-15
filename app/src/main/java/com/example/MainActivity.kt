package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.BarangayDatabase
import com.example.data.BarangayRepository
import com.example.ui.BarangayViewModel
import com.example.ui.BarangayViewModelFactory
import com.example.ui.screens.BarangayAppUI
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Initialize DB and Repository layers linked to lifeScope
        val database = BarangayDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = BarangayRepository(database.barangayDao())
        
        // 2. Instantiate and register MVVM VM
        val viewModel: BarangayViewModel by viewModels {
            BarangayViewModelFactory(repository)
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    BarangayAppUI(viewModel = viewModel)
                }
            }
        }
    }
}
