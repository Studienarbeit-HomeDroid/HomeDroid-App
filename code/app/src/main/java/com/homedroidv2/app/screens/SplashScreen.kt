package com.homedroidv2.app.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.homedroidv2.app.ui.theme.HomeDroidTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import com.homedroidv2.carappservice.R




/**
 * Loading Screen activity welche Anzeigt, das die Daten derzeit geladen werden.
 */

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreen : ComponentActivity() {

    @SuppressLint("NotConstructor")
    @Composable
    fun SplashScreen(isParsing: Boolean, onSplashFinished: () -> Unit) {
        LaunchedEffect(Unit) {
            onSplashFinished()
        }

        HomeDroidTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.systemBars.asPaddingValues()),
                color = MaterialTheme.colorScheme.primary
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val isDark = isSystemInDarkTheme()
                        val imageRes = if (!isDark) com.homedroidv2.app.R.drawable.full_logo else com.homedroidv2.app.R.drawable.full_logo_white

                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )

                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 15.dp),
                            color = MaterialTheme.colorScheme.tertiary,
                            strokeWidth = 3.dp
                        )

                        Spacer(modifier = Modifier.height(90.dp))

                        if(isParsing)
                        {
                            Text(
                                text = "Website is Parsing...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Studienarbeit",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "TINF22B2",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Version: v.6",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }


    }
}