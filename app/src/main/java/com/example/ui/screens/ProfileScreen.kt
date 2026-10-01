package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.ApiCatalogSeed
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.SecondaryPillButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 110.dp)
    ) {
        // User Account Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = AppCardNav),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(AppYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (uiState.userEmail?.take(1)?.uppercase() ?: "D"),
                                color = AppBackground,
                                fontSize = 22.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (uiState.userEmail != null) uiState.userEmail!!.substringBefore("@").uppercase() else "DEVELOPER",
                                    color = AppWhite,
                                    fontSize = 17.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (uiState.isPro) AppBlue else AppBackground
                                ) {
                                    Text(
                                        text = if (uiState.isPro) "PRO" else "FREE",
                                        color = AppWhite,
                                        fontSize = 10.sp,
                                        fontFamily = ArchivoFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = uiState.userEmail ?: "Signed in locally (Sync ready)",
                                color = AppWhite.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (uiState.userEmail == null) {
                        PrimaryPillButton(
                            text = "LOGIN",
                            onClick = { viewModel.signInWithGoogle(context) }
                        )
                    } else {
                        IconButton(onClick = { viewModel.signOut() }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out", tint = AppWhite)
                        }
                    }
                }
            }
        }

        // Stats in yellow & sage
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = AppCardNav)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${uiState.streakDays} DAYS", color = AppYellow, fontSize = 20.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                        Text("ACTIVE STREAK", color = AppWhite.copy(alpha = 0.7f), fontSize = 11.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = AppCardNav)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${uiState.apisTestedCount} APIS", color = AppSage, fontSize = 20.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                        Text("TESTED LIVE", color = AppWhite.copy(alpha = 0.7f), fontSize = 11.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
