package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.repository.JoblessEmpireRepository
import com.example.ui.screens.JoblessEmpireLandingScreen
import com.example.ui.theme.EmpireBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(EmpireBackground),
                    color = MaterialTheme.colorScheme.background
                ) {
                    JoblessEmpireApp()
                }
            }
        }
    }
}

@Composable
fun JoblessEmpireApp() {
    val context = LocalContext.current
    val repository = remember { JoblessEmpireRepository.getInstance(context) }
    JoblessEmpireLandingScreen(
        repository = repository,
        modifier = Modifier.fillMaxSize()
    )
}
