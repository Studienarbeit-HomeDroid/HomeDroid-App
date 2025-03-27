package com.homedroidv2.app.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homedroidv2.app.ui.theme.HomeDroidTheme
import com.homedroidv2.app.viewmodel.ServerConfigViewModel

class LoginScreen: ComponentActivity() {

    @Composable
    fun LoginComponent(viewModel: ServerConfigViewModel = viewModel() ): Boolean {

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoggedIn by remember { mutableStateOf(false) }


        HomeDroidTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.systemBars.asPaddingValues()),
                color = MaterialTheme.colorScheme.primary
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Willkommen bei",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 80.dp)
                    )

                    Image(
                        painter = painterResource(id = com.homedroidv2.app.R.drawable.full_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                    Spacer(modifier = Modifier.height(90.dp))


                    Column(
                        modifier = Modifier
                            .width(300.dp)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {

                        OutlinedTextField(
                            value = password,
                            maxLines = 1,
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = "password")
                            },
                            label = { Text("Enter Username") },
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onPrimary
                            ),
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                cursorColor = MaterialTheme.colorScheme.onPrimary,
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

                        var isLoading by remember { mutableStateOf(false) }

                        Button(
                            onClick = {
                                isLoading = true
                                viewModel.password = password
                                isLoggedIn = true
//                                viewModel.logIn(username, password) {
//                                    Log.i("Login", "Login result: $it")
//                                    isLoggedIn = it
//                                    isLoading = false
//                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(0.dp, 25.dp, 0.dp, 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(17.dp)
                                            .padding(horizontal = 2.dp), // Abstand zum Text
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                }
                                Text(
                                    text = if (isLoading) "Logging in..." else "Login",
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }

//                    Spacer(modifier = Modifier.height(50.dp))
//
//                    Image(
//                        painter = painterResource(id = com.homedroid.app.R.drawable.google_login_light),
//                        contentDescription = "Google Login Button",
//                        modifier = Modifier
//                            .width(200.dp)
//                            .height(45.dp)
//                            .clip(RoundedCornerShape(20.dp))
//                            .clickable {  }
//                    )
                    }
                }
            }
        }
        return isLoggedIn
    }
}