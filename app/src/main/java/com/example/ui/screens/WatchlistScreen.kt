package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.ApiCatalogSeed
import com.example.model.TestStatus
import com.example.ui.components.ColoredApiCard
import com.example.ui.components.LockedProCard
import com.example.ui.components.PrimaryPillButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun WatchlistScreen(
    viewModel: MainViewModel,
    bottomPadding: Dp = 110.dp,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val watchlistItems by viewModel.watchlistItems.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = bottomPadding)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text(
                    text = "API HEALTH WATCHLIST",
                    color = AppWhite,
                    fontSize = 28.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Real-time downtime alerts and latency monitoring.",
                    color = AppWhite.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // If not Pro, show Locked Pro Card tilted -2 degrees
        if (!uiState.isPro) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                LockedProCard(
                    onUnlock = {
                        viewModel.triggerPaywall("Get alerted instantly when your production APIs go down. Unlock 24/7 Watchlist monitoring.")
                    }
                )
            }
        }

        // Monitored items or sample monitored APIs
        if (watchlistItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = AppCardNav),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NO APIS MONITORED YET",
                            color = AppWhite,
                            fontSize = 18.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add APIs from Search or Browse to track response times and receive outage notices.",
                            color = AppWhite.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryPillButton(
                            text = "EXPLORE APIS",
                            onClick = { viewModel.setTab(com.example.ui.viewmodel.AppNavTab.BROWSE) }
                        )
                    }
                }
            }
        } else {
            itemsIndexed(watchlistItems) { index, item ->
                val api = ApiCatalogSeed.getById(item.apiId)
                if (api != null) {
                    val cardBg = CardFills[index % CardFills.size]
                    ColoredApiCard(
                        api = api,
                        cardBgColor = cardBg,
                        pingResult = uiState.pingResults[api.id] ?: com.example.model.LivePingResult(
                            apiId = api.id,
                            status = item.currentStatus,
                            responseTimeMs = 120L
                        ),
                        isPinging = uiState.isPingingMap[api.id] == true,
                        onTestLive = { viewModel.testLiveApi(api) },
                        onGetCode = { viewModel.openStarterCode(api) }
                    )
                }
            }
        }
    }
}
