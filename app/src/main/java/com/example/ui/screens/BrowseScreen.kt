package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.ApiCatalogSeed
import com.example.model.ApiCategory
import com.example.ui.components.ColoredApiCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun BrowseScreen(
    viewModel: MainViewModel,
    bottomPadding: Dp = 110.dp,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredApis = remember(uiState.selectedCategory, uiState.browseQuery) {
        val list = if (uiState.selectedCategory == ApiCategory.ALL) {
            ApiCatalogSeed.apis
        } else {
            ApiCatalogSeed.apis.filter { it.category == uiState.selectedCategory }
        }
        if (uiState.browseQuery.isBlank()) list
        else {
            val q = uiState.browseQuery.lowercase().trim()
            list.filter {
                it.name.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = bottomPadding)
    ) {
        // Hero title
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text(
                    text = "BROWSE 80+ FREE APIS",
                    color = AppWhite,
                    fontSize = 28.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Zero-cost endpoints verified with real network latency.",
                    color = AppWhite.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Search Bar (radius 28px, #312F30)
        item {
            OutlinedTextField(
                value = uiState.browseQuery,
                onValueChange = { viewModel.updateBrowseQuery(it) },
                placeholder = {
                    Text(
                        "Search by name or keyword...",
                        color = AppWhite.copy(alpha = 0.45f),
                        fontSize = 15.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = AppYellow)
                },
                trailingIcon = {
                    if (uiState.browseQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateBrowseQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AppWhite)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AppCardNav,
                    unfocusedContainerColor = AppCardNav,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = AppWhite,
                    unfocusedTextColor = AppWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("browse_search_field")
            )
        }

        // Filter chips: pills, active = sage fill (#C5D4CA) with dark text, inactive = #312F30 with white text
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ApiCategory.values().forEach { cat ->
                    val isSelected = uiState.selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AppSage else AppCardNav)
                            .clickable { viewModel.setCategory(cat) }
                            .padding(horizontal = 16.dp, vertical = 9.dp)
                            .testTag("category_pill_${cat.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat.displayName.uppercase(),
                            color = if (isSelected) AppBackground else AppWhite,
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Colored cards cycling blue, coral, yellow, sage
        itemsIndexed(filteredApis) { index, api ->
            val cardBg = CardFills[index % CardFills.size]
            ColoredApiCard(
                api = api,
                cardBgColor = cardBg,
                pingResult = uiState.pingResults[api.id],
                isPinging = uiState.isPingingMap[api.id] == true,
                onTestLive = { viewModel.testLiveApi(api) },
                onGetCode = { viewModel.openStarterCode(api) }
            )
        }
    }
}
