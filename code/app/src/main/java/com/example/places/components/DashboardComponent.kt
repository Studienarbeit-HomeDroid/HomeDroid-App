package com.example.places.components

import android.credentials.CredentialDescription
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.places.ui.theme.HomeDroidTheme

class DashboardComponent {

    @Composable
    fun Dashboard() {
        HomeDroidTheme {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(top = 13.dp)
                    .padding(bottom = 8.dp)
            )
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {

                Column {
                    Text(
                        text = "1.10.24 | 16:12:22",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp)
                            .padding(top = 5.dp),
                        textAlign = TextAlign.End
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 10.dp)
                    ) {
                        Box(
                            Modifier.size(width = 110.dp, height = 110.dp)

                        )
                        {
                            Column {
                                BoxHeaderText("Haustür")
                                BoxDescriptionText("offen")
                            }
                        }

                        Box(
                            Modifier.size(width = 110.dp, height = 110.dp),
                        )
                        {
                            Column {
                                BoxHeaderText("Fenster")
                                BoxDescriptionText("unverriegelt")
                                BoxValueText("2")
                            }
                        }

                        Box(
                            Modifier.size(width = 110.dp, height = 110.dp),
                        )
                        {
                            Column {
                                BoxHeaderText("Türen")
                                BoxDescriptionText("unverriegelt")
                                BoxValueText("2")
                                BoxDescriptionText("offen")
                                BoxValueText("3")

                            }
                        }

                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 10.dp)
                    ) {
                        Box(
                            Modifier.size(width = 110.dp, height = 110.dp)
                        )
                        {
                            Column {
                                BoxHeaderText("Strom")
                                BoxDescriptionText("bezug")
                                BoxValueText("32")
                                BoxDescriptionText("lieferung")
                                BoxValueText("322")
                            }
                        }

                        Box(
                            Modifier.size(width = 110.dp, height = 110.dp),
                        )
                        {
                            Column {
                                BoxHeaderText("Zähler")
                                BoxDescriptionText("bezug")
                                BoxValueText("32")
                                BoxDescriptionText("lieferung")
                                BoxValueText("322")
                            }
                        }

                        Box(
                            Modifier.size(width = 110.dp, height = 110.dp),
                        )
                        {
                            Column {
                                BoxHeaderText("Solar")
                                BoxDescriptionText("tages")
                                BoxValueText("12")
                                BoxDescriptionText("gesamt")
                                BoxValueText("3222")
                            }
                        }

                    }
                }
            }
        }

    }

    @Composable
    fun BoxHeaderText(text: String) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center

            )


    }

    @Composable
    fun BoxDescriptionText(description: String) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center

        )
    }

    @Composable
    fun BoxValueText(value: String) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold

            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center

        )
    }
}