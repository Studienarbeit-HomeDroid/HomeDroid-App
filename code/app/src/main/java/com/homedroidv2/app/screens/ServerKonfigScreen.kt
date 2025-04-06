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
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

class ServerKonfigScreen : ComponentActivity() {

    /**
     * Composable-Funktion zur Konfiguration des Servers durch den Nutzer.
     *
     * Diese Funktion stellt eine Oberfläche bereit, über die die Server-URL eingegeben und ein
     * Client-Zertifikat aus dem Android Certificate Store ausgewählt werden kann.
     */
    @Composable
    fun ServerKonfig(context: Context, viewModel: ServerConfigViewModel = viewModel(), onFinished: () -> Unit) {

        var serverUrl by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var certUri by remember { mutableStateOf<Uri?>(null) }

        val aliasSelected = remember { mutableStateOf(false) }

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

                    Text(
                        text = "Willkommen bei",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 80.dp)
                    )

                    val isDark = isSystemInDarkTheme()
                    val imageRes =
                        if (!isDark) com.homedroidv2.app.R.drawable.full_logo else com.homedroidv2.app.R.drawable.full_logo_white

                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                }

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
                            style = MaterialTheme.typography.bodyLarge
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
                        text = "Version: v.6",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}