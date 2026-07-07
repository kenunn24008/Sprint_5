package com.example.opennotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val auth = FirebaseAuth.getInstance()
            // Track state if a user is currently logged in
            var user by remember { mutableStateOf(auth.currentUser) }

            if (user == null) {
                AuthScreen(onAuthSuccess = {
                    user = auth.currentUser
                })
            } else {
                NotesScreen(onLogOut = {
                    user = null
                })
            }
        }
    }
}