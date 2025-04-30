package com.example.memories

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.view.navigation.AppNav


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoriesTheme {
                val navController = rememberNavController()
                AppNav(navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavPreview() {
    MemoriesTheme {
        AppNav(rememberNavController())
    }
}




