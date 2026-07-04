package com.ddeeaaddllyy.zenith

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ddeeaaddllyy.zenith.ui.ZenithApp
import com.ddeeaaddllyy.zenith.ui.theme.ZenithTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as ZenithApplication).container
        setContent {
            ZenithTheme {
                ZenithApp(container = container)
            }
        }
    }
}
