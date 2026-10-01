package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.ApiCatalogSeed
import com.example.model.SavedProject
import com.example.ui.components.PrimaryPillButton
import com.example.ui.components.SecondaryPillButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProjectsScreen(
    viewModel: MainViewModel,
    bottomPadding: Dp = 110.dp,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val projects by viewModel.savedProjects.collectAsState()
    val context = LocalContext.current
    val dailyChallenge = remember { ApiCatalogSeed.defaultDailyChallenge }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = bottomPadding)
    ) {
        // Title
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text(
                    text = "SAVED PROJECTS & CODE",
                    color = AppWhite,
                    fontSize = 28.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your architecture workspaces, saved matches, and starter code.",
                    color = AppWhite.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Daily Challenge Card in Sage
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = AppSage),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = AppBackground
                        ) {
                            Text(
                                text = "DAILY BUILD",
                                color = AppWhite,
                                fontSize = 11.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = dailyChallenge.estimatedTime.uppercase(),
                            color = AppBackground,
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = dailyChallenge.title.uppercase(),
                        color = AppBackground,
                        fontSize = 20.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dailyChallenge.description,
                        color = AppBackground.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    PrimaryPillButton(
                        text = "BUILD THIS CHALLENGE",
                        onClick = {
                            viewModel.onSelectExamplePrompt("Build a mood weather music generator combining Open-Meteo with iTunes audio previews")
                            viewModel.setTab(com.example.ui.viewmodel.AppNavTab.SEARCH)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Projects List Header
        item {
            Text(
                text = "SAVED WORKSPACES (${projects.size})",
                color = AppWhite,
                fontSize = 16.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Saved project cards
        if (projects.isEmpty()) {
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
                            text = "NO PROJECTS SAVED YET",
                            color = AppWhite,
                            fontSize = 18.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Run a search and click 'Save Project' to keep your architectural choices here.",
                            color = AppWhite.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryPillButton(
                            text = "START NEW SEARCH",
                            onClick = { viewModel.setTab(com.example.ui.viewmodel.AppNavTab.SEARCH) }
                        )
                    }
                }
            }
        } else {
            itemsIndexed(projects) { index, project ->
                val cardBg = CardFills[index % CardFills.size]
                val contentColor = getCardContentColor(cardBg)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("saved_project_card_${project.id}"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = project.title.uppercase(),
                                color = contentColor,
                                fontSize = 18.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.deleteProject(project.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = contentColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "\"${project.query}\"",
                            color = contentColor.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Matched tags
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            project.matchedApiIds.take(3).forEach { apiId ->
                                val api = ApiCatalogSeed.getById(apiId)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AppBackground
                                ) {
                                    Text(
                                        text = (api?.name ?: apiId).uppercase(),
                                        color = AppWhite,
                                        fontSize = 11.sp,
                                        fontFamily = ArchivoFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PrimaryPillButton(
                                text = "RE-RUN MATCH",
                                onClick = {
                                    viewModel.onSelectExamplePrompt(project.query)
                                    viewModel.setTab(com.example.ui.viewmodel.AppNavTab.SEARCH)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            SecondaryPillButton(
                                text = "SHARE",
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "APIMatch: ${project.title}")
                                        putExtra(Intent.EXTRA_TEXT, "Check out my project architecture '${project.title}'!\nPrompt: ${project.query}\nMatched APIs: ${project.matchedApiIds.joinToString(", ")}\n\nBuilt with APIMatch")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Project"))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
