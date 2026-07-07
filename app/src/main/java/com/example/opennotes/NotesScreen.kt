package com.example.opennotes


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(onLogOut: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""
    val context = LocalContext.current

    var notes by remember { mutableStateOf(listOf<Note>()) }
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }

    // Real-time listener fetching notes specific to the logged-in user
    DisposableEffect(currentUserId) {
        val query = db.collection("notes")
            .whereEqualTo("userId", currentUserId)

        val listener = query.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                notes = snapshot.documents.map { doc ->
                    Note(doc.data ?: emptyMap(), doc.id)
                }
            }
        }
        onDispose { listener.remove() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Notes") },
                actions = {
                    TextButton(onClick = {
                        auth.signOut()
                        onLogOut()
                    }) {
                        Text("Log Out", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Inputs to add a new note
            OutlinedTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                label = { Text("Note Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                label = { Text("Write something...") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                        val newNote = Note(userId = currentUserId, title = noteTitle, content = noteContent)
                        db.collection("notes").add(newNote.toMap())
                            .addOnSuccessListener {
                                noteTitle = ""
                                noteContent = ""
                            }
                    } else {
                        Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Note")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable List of Notes
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(notes) { note ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(onClick = {
                                db.collection("notes").document(note.id).delete()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}