package com.homedroidv2.app.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.security.KeyChain
import android.security.KeyChainAliasCallback
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroidv2.app.ui.theme.HomeDroidTheme
import com.homedroidv2.app.viewmodel.ServerConfigViewModel
import android.util.Log

class ServerKonfigScreen : ComponentActivity() {

    @Composable
    fun ServerKonfig(context: Context, viewModel: ServerConfigViewModel = viewModel(), onFinished: () -> Unit) {

        var serverUrl by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var certUri by remember { mutableStateOf<Uri?>(null) }

        val aliasSelected = remember { mutableStateOf(false) }

//        if (!aliasSelected.value) {
//            LaunchedEffect(Unit) {
//                KeyChain.choosePrivateKeyAlias(
//                    context as Activity,
//                    { alias ->
//                        Log.d("ServerKonfig", "Zertifikatsalias: $alias")
//                        aliasSelected.value = true
//                        viewModel.setCertAlias(alias)
//
//                    },
//                    null, null, null, -1, null
//                )
//            }
//        }

        val certPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    certUri = it
                    Log.d("ServerKonfig", "Zertifikat ausgewählt: $it")
                } catch (e: SecurityException) {
                    Log.e("ServerKonfig", "Zugriff auf Zertifikat fehlgeschlagen: ${e.message}")
                }
            }
        }

        HomeDroidTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
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
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(16.dp),
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

                        Spacer(modifier = Modifier.height(10.dp))

//                    OutlinedTextField(
//                        value = password,
//                        maxLines = 1,
//                        leadingIcon = {
//                            Icon(Icons.Default.Key, contentDescription = "password")
//                        },
//                        label = { Text("Enter Passwort") },
//                        textStyle = TextStyle(
//                            fontWeight = FontWeight.Normal,
//                            color = MaterialTheme.colorScheme.onPrimary
//                        ),
//                        onValueChange = { password = it },
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(16.dp),
//                        colors = TextFieldDefaults.colors(
//                            cursorColor = MaterialTheme.colorScheme.onPrimary,
//                            focusedContainerColor = Color.Transparent,
//                            unfocusedContainerColor = Color.Transparent,
//                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
//                            focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
//                            focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
//                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
//                            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
//                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
//                        )
//                    )

                        Spacer(modifier = Modifier.height(10.dp))



                        Button(
                            onClick = {
                                KeyChain.choosePrivateKeyAlias(
                                    context as Activity,
                                    { alias ->
                                        Log.d("ServerKonfig", "Zertifikatsalias: $alias")
                                        aliasSelected.value = true
                                        viewModel.setCertAlias(alias)

                                    },
                                    null, null, null, -1, null)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = "Zertifikat hochladen")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Client-Zertifikat hochladen")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.saveServerConfig(context, serverUrl, certUri, password){
                                        result ->
                                    if(result)
                                    {
                                        onFinished()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Speichern")
                        }
                    }

                    Text(
                        text = "Version: v.3",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}