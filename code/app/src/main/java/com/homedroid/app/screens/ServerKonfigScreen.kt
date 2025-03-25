package com.homedroid.app.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroid.app.ui.theme.HomeDroidTheme
import com.homedroid.app.viewmodel.ServerConfigViewModel

class ServerKonfigScreen : ComponentActivity() {

    @Composable
    fun ServerKonfig(viewModel: ServerConfigViewModel = viewModel(), onFinished: () -> Unit) {
        var serverUrl by remember { mutableStateOf("") }
        var certUri by remember { mutableStateOf<Uri?>(null) }
        val context = LocalContext.current


        val certPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            certUri = uri
            Log.d("ServerKonfig", "Zertifikat ausgewählt: $uri")
        }

        HomeDroidTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Server Konfiguration",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Eingabefeld für die Server-URL
                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        label = { Text("Server URL eingeben") },
                        leadingIcon = { Icon(Icons.Default.Cloud, contentDescription = "URL") },
                        textStyle = TextStyle(color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Button zum Zertifikat hochladen
                    Button(
                        onClick = { certPicker.launch("*/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = "Zertifikat hochladen")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Client-Zertifikat hochladen")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    certUri?.let {
                        Text(text = "Zertifikat: ${it.lastPathSegment}", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Speichern-Button
                    Button(
                        onClick = {
                            viewModel.saveServerConfig(context, serverUrl, certUri)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Speichern")
                    }
                }
            }
        }
    }
}