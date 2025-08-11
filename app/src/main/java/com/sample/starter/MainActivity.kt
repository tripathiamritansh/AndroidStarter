package com.sample.starter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sample.feed.ui.theme.StarterTheme
import com.sample.starter.ui.chat.ChatScreen
import com.sample.starter.ui.chat.ChatViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarterTheme {
                val viewModel: ChatViewModel = viewModel()
                ChatScreen(viewModel)
            }
        }
    }

}