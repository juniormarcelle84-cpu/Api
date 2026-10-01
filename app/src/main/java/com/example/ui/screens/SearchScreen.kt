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
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.model.ApiMatchResult
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    bottomPadding: Dp = 110.dp,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Personalized example prompts based on "What are you building?" onboarding choice
    val examplePrompts = remember(uiState.userBuildingType) {
        when (uiState.userBuildingType) {
            "Web app" -> listOf(
                "SaaS dashboard with Stripe & GitHub metrics",
                "E-commerce store with currency converter",
                "Real-time crypto & stock portfolio",
                "Recipe finder with nutrition analyzer"
            )
            "Mobile app" -> listOf(
                "Fitness habit tracker with daily quote",
                "Local weather & offline maps companion",
                "Podcast player with audio transcription",
                "Smart travel itinerary planner"
            )
            "Game" -> listOf(
                "Multiplayer retro trivia with leaderboard",
                "Pokemon card battle simulator with stats",
                "DnD character sheet with spellbook lookup",
                "Chess engine with daily puzzle challenges"
            )
            "Data/AI" -> listOf(
                "Sentiment analysis pipeline for news feeds",
                "Computer vision model benchmark dataset",
                "Stock market prediction with sentiment metrics",
                "Knowledge graph generator from Wikipedia"
            )
            "School project" -> listOf(
                "Interactive space exploration quiz with NASA imagery",
                "Historical census data visualizer",
                "Periodic table & chemical reaction calculator",
                "Country geography facts & flags quiz"
            )
            else -> listOf(
                "Habit tracker with weather quotes",
                "Multiplayer retro game with leaderboards",
                "Crypto portfolio with real-time news",
                "Cookbook that pairs wine with recipes"
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = bottomPadding)
    ) {
        // Hero: FIND THE RIGHT API IN 30 SECONDS
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text(
                    text = "FIND THE RIGHT API IN 30 SECONDS",
                    color = AppWhite,
                    fontSize = 32.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black,
                    lineHeight = 34.sp,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "AI-recommended free public APIs tested live with instant starter code.",
                    color = AppWhite.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Trial Ending Recap Banner (2 days before trial ends)
        if (uiState.isTrialActive && uiState.trialDaysRemaining <= 2) {
            item {
                TrialEndingRecapCard(
                    searchesCount = 12,
                    savedProjectsCount = 3,
                    alertsCount = 2,
                    onKeepPro = {
                        viewModel.upgradeToPro("Yearly ($96/year - $8/mo)")
                    },
                    onDismiss = {
                        viewModel.dismissLiveTestBanner()
                    }
                )
            }
        }

        // Live Test 3rd Ping Pro Banner ("Want to know when this API fails? Follow it with Pro.")
        if (uiState.showLiveTestProBanner) {
            item {
                LiveTestProAlertBanner(
                    onFollowPro = {
                        viewModel.triggerPaywall("Want to know when this API fails? Follow it with 24/7 downtime alerts on Pro.")
                    },
                    onDismiss = {
                        viewModel.dismissLiveTestBanner()
                    }
                )
            }
        }

        // Dark Input Card (#312F30, radius 28px) with Coral "Find APIs" button
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = AppCardNav),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = {
                            Text(
                                "Describe your project in one sentence...",
                                color = AppWhite.copy(alpha = 0.45f),
                                fontSize = 15.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_description_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = AppWhite,
                            unfocusedTextColor = AppWhite
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NO SIGNUP REQUIRED",
                            color = AppWhite.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )

                        PrimaryPillButton(
                            text = if (uiState.isSearching) "MATCHING..." else "FIND APIS",
                            onClick = { viewModel.performSearch() },
                            modifier = Modifier.testTag("match_apis_button"),
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AppBackground
                                )
                            }
                        )
                    }
                }
            }
        }

        // Example prompts as horizontal chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                examplePrompts.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppCardNav)
                            .clickable { viewModel.onSelectExamplePrompt(prompt) }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                            .testTag("example_prompt_${prompt.take(8).replace(" ", "_")}")
                    ) {
                        Text(
                            text = prompt,
                            color = AppWhite,
                            fontSize = 13.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Onboarding Checklist Card (Progress Bar: Run a search, Test live, Copy snippet, Save project)
        item {
            ChecklistCard(
                firstSearch = uiState.checklistFirstSearch,
                firstTest = uiState.checklistFirstTest,
                firstCopy = uiState.checklistFirstCopy,
                firstSave = uiState.checklistFirstSave,
                onUnlockSave = {
                    val firstResult = uiState.searchResults?.firstOrNull()?.api?.name ?: "Current Search"
                    viewModel.triggerPaywall("Save '$firstResult' to your workspace. Free tier is limited to 2 projects. Pro offers unlimited workspaces.")
                }
            )
        }

        // Searching State
        if (uiState.isSearching) {
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
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AppCoral, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "MATCHING BEST FREE APIS...",
                            color = AppWhite,
                            fontSize = 16.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // One-question onboarding after first result: "What are you building?"
        if (uiState.searchResults != null) {
            item {
                OnboardingWhatAreYouBuildingCard(
                    selectedType = uiState.userBuildingType,
                    onSelectType = { type -> viewModel.selectBuildingType(type) }
                )
            }
        }

        // Top 3 results as colored cards cycling blue, coral, yellow, sage
        val results = uiState.searchResults
        if (results != null) {
            item {
                Text(
                    text = "TOP 3 RECOMMENDED APIS",
                    color = AppWhite,
                    fontSize = 18.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            itemsIndexed(results) { index, match ->
                val cardBg = CardFills[index % CardFills.size]
                ColoredApiCard(
                    api = match.api,
                    cardBgColor = cardBg,
                    matchScore = match.matchScore,
                    why = match.why,
                    miniPlan = match.miniPlan,
                    pingResult = uiState.pingResults[match.api.id],
                    isPinging = uiState.isPingingMap[match.api.id] == true,
                    onTestLive = { viewModel.testLiveApi(match.api) },
                    onGetCode = { viewModel.openStarterCode(match.api) }
                )
            }
        }
    }
}
