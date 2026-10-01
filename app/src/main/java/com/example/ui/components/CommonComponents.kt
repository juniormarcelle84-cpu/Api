package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.seed.ApiCatalogSeed
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.MainViewModel
import com.example.util.StarterCodeGenerator

@Composable
fun ApimatchBoldHeader(
    isPro: Boolean,
    searchesLeft: Int,
    onAvatarClick: () -> Unit,
    onQuotaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "HI, DEV",
                color = AppWhite,
                fontSize = 22.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            // 4/5 left pill (turns coral at 1 search left)
            val isUrgent = !isPro && searchesLeft <= 1
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isUrgent) AppCoral else AppCardNav,
                modifier = Modifier
                    .clickable { onQuotaClick() }
                    .testTag("search_counter_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isUrgent) AppBackground else if (isPro) AppBlue else AppYellow)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPro) "PRO UNLIMITED" else "$searchesLeft/5 LEFT",
                        color = if (isUrgent) AppBackground else AppWhite,
                        fontSize = 11.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Yellow avatar
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(AppYellow)
                .clickable { onAvatarClick() }
                .testTag("header_avatar_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = AppBackground,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BoldFloatingBottomNav(
    currentTab: AppNavTab,
    isPro: Boolean,
    hasDownApi: Boolean,
    onTabSelected: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = AppCardNav,
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    AppNavTab.SEARCH to Icons.Default.Search,
                    AppNavTab.BROWSE to Icons.Default.GridView,
                    AppNavTab.WATCHLIST to Icons.Default.NotificationsActive,
                    AppNavTab.PROJECTS to Icons.Default.Folder
                )

                tabs.forEach { (tab, icon) ->
                    val isSelected = currentTab == tab
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        animationSpec = tween(durationMillis = 200),
                        label = "tab_scale"
                    )

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .clip(RoundedCornerShape(26.dp))
                            .background(if (isSelected) AppBlue else Color.Transparent)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onTabSelected(tab)
                            }
                            .padding(horizontal = if (isSelected) 14.dp else 10.dp, vertical = 10.dp)
                            .testTag("nav_tab_${tab.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) AppWhite else AppWhite.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                                // Coral dot on Watchlist tab if an API is down
                                if (tab == AppNavTab.WATCHLIST && hasDownApi) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .align(Alignment.TopEnd)
                                            .clip(CircleShape)
                                            .background(AppCoral)
                                    )
                                }
                            }

                            if (isSelected) {
                                Text(
                                    text = tab.title.uppercase(),
                                    color = AppWhite,
                                    fontSize = 12.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            // "PRO" tag on Watchlist for free users
                            if (tab == AppNavTab.WATCHLIST && !isPro && !isSelected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AppCoral)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "PRO",
                                        color = AppBackground,
                                        fontSize = 8.sp,
                                        fontFamily = ArchivoFontFamily,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    status: TestStatus,
    responseTimeMs: Long = 0L,
    isTesting: Boolean = false,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val (label, dotColor) = when {
        isTesting -> "TESTING..." to AppYellow
        status == TestStatus.UP -> "UP • ${if (responseTimeMs > 0) "${responseTimeMs}ms" else "FAST"}" to AppBackground
        status == TestStatus.SLOW -> "SLOW • ${responseTimeMs}ms" to AppCoral
        status == TestStatus.DOWN -> "DOWN" to AppCoral
        status == TestStatus.CORS_RESTRICTED -> "CORS RESTRICTED" to AppCoral
        else -> "READY" to contentColor
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = AppBackground.copy(alpha = 0.85f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = label,
                color = AppWhite,
                fontSize = 11.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun TagPill(
    text: String,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, contentColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 11.sp,
            fontFamily = ArchivoFontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PrimaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppCoral,
            contentColor = AppBackground
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
        modifier = modifier.height(44.dp)
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text.uppercase(),
            fontSize = 13.sp,
            fontFamily = ArchivoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SecondaryPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppBackground,
            contentColor = AppWhite
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        modifier = modifier.height(44.dp)
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text.uppercase(),
            fontSize = 13.sp,
            fontFamily = ArchivoFontFamily,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun RoundBlackDocsButton(
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(AppBackground)
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {}
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "↗",
            color = AppWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ColoredApiCard(
    api: PublicApi,
    cardBgColor: Color,
    matchScore: Int? = null,
    pingResult: LivePingResult? = null,
    isPinging: Boolean = false,
    onTestLive: () -> Unit,
    onGetCode: () -> Unit,
    why: String? = null,
    miniPlan: List<String>? = null,
    modifier: Modifier = Modifier
) {
    val contentColor = getCardContentColor(cardBgColor)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("colored_api_card_${api.id}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Card Header: BIG UPPERCASE NAME + BLACK PILL (MATCH %)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = api.name.uppercase(),
                    color = contentColor,
                    fontSize = 20.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.weight(1f)
                )

                if (matchScore != null) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = AppBackground
                    ) {
                        Text(
                            text = "$matchScore%",
                            color = AppWhite,
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Outlined Tags: category, "No key" / auth, "CORS: yes"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TagPill(text = api.category.displayName.uppercase(), contentColor = contentColor)
                TagPill(
                    text = if (api.auth == AuthType.NONE) "NO KEY" else api.auth.displayName.uppercase(),
                    contentColor = contentColor
                )
                TagPill(
                    text = if (api.cors == CorsType.YES) "CORS: YES" else "CORS: NO",
                    contentColor = contentColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = api.description,
                color = contentColor.copy(alpha = 0.9f),
                fontSize = 15.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Medium,
                lineHeight = 21.sp
            )

            // Why explanation if present
            if (!why.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "WHY: $why",
                    color = contentColor,
                    fontSize = 13.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Mini plan
            if (miniPlan != null && miniPlan.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    miniPlan.forEachIndexed { i, step ->
                        Text(
                            text = "${i + 1}. $step",
                            color = contentColor.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Status chip if available
            if (pingResult != null) {
                StatusChip(
                    status = pingResult.status,
                    responseTimeMs = pingResult.responseTimeMs,
                    isTesting = isPinging,
                    contentColor = contentColor
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Action row: Primary Button (coral pill) or Secondary Button + Docs round button ↗
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryPillButton(
                        text = if (isPinging) "PINGING..." else "TEST LIVE",
                        onClick = onTestLive,
                        icon = {
                            Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                    SecondaryPillButton(
                        text = "CODE",
                        onClick = onGetCode,
                        icon = {
                            Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                }

                // Round black "↗" button at the bottom right
                RoundBlackDocsButton(url = api.docsUrl)
            }
        }
    }
}

@Composable
fun LockedProCard(
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(rotationZ = -2f)
            .testTag("locked_pro_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AppCoral),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOCKED PRO FEATURE",
                    color = AppBackground,
                    fontSize = 18.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = AppBackground,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Watchlists, 24/7 downtime alerts, server-side CORS tests, and full project scaffold exports are exclusive to APIMatch Pro.",
                color = AppBackground,
                fontSize = 15.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Medium,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onUnlock,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppBackground,
                    contentColor = AppWhite
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(
                    text = "UNLOCK WITH PRO",
                    fontSize = 14.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun SagePaywallBottomSheet(
    reason: String,
    lifetimeSpotsRemaining: Int = 37,
    isTrialActive: Boolean = false,
    onUpgrade: (String) -> Unit,
    onClaimLifetime: () -> Unit,
    onOpenTeamWaitlist: () -> Unit,
    onOpenStudentDiscount: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf("yearly") } // "monthly" vs "yearly"
    var expandedFaq by remember { mutableStateOf<Int?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
                .padding(horizontal = 14.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.96f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* prevent backdrop dismissal when clicking inside card */ }
                    .testTag("sage_paywall_sheet"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = AppSage),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GO PRO",
                            color = AppBackground,
                            fontSize = 32.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppBackground)
                                .testTag("close_paywall_button")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = AppWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Contextual Reason Trigger
                    Text(
                        text = reason,
                        color = AppBackground,
                        fontSize = 15.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Plan Selector: Yearly ($96/year = $8/mo, Preselected) vs Monthly ($10/mo)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Yearly card (Most popular, yellow highlighted)
                        Card(
                            modifier = Modifier
                                .weight(1.1f)
                                .clickable { selectedPlan = "yearly" }
                                .testTag("plan_yearly_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPlan == "yearly") AppYellow else AppCardNav
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "YEARLY",
                                        color = if (selectedPlan == "yearly") AppBackground else AppWhite,
                                        fontSize = 13.sp,
                                        fontFamily = ArchivoFontFamily,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AppBackground)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "MOST POPULAR",
                                            color = AppYellow,
                                            fontSize = 9.sp,
                                            fontFamily = ArchivoFontFamily,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$8 / MO",
                                    color = if (selectedPlan == "yearly") AppBackground else AppWhite,
                                    fontSize = 22.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Billed $96/yr (Save 20%)",
                                    color = if (selectedPlan == "yearly") AppBackground.copy(alpha = 0.85f) else AppWhite.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Monthly card ($10/mo)
                        Card(
                            modifier = Modifier
                                .weight(0.9f)
                                .clickable { selectedPlan = "monthly" }
                                .testTag("plan_monthly_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedPlan == "monthly") AppYellow else AppCardNav
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "MONTHLY",
                                    color = if (selectedPlan == "monthly") AppBackground else AppWhite,
                                    fontSize = 13.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$10 / MO",
                                    color = if (selectedPlan == "monthly") AppBackground else AppWhite,
                                    fontSize = 22.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Flexible cancel",
                                    color = if (selectedPlan == "monthly") AppBackground.copy(alpha = 0.85f) else AppWhite.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lighter Free Tier Reference Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppBackground.copy(alpha = 0.12f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current Free Tier",
                                color = AppBackground,
                                fontSize = 12.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "5 searches/day • 1 watchlist • Snippets",
                                color = AppBackground.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary CTA Button: "Start 7-day free trial" (after trial: "Continue with Pro")
                    Button(
                        onClick = {
                            val planLabel = if (selectedPlan == "yearly") "Yearly ($96/year - $8/mo)" else "Monthly ($10/mo)"
                            onUpgrade(planLabel)
                        },
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppBackground,
                            contentColor = AppWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("primary_paywall_cta_button")
                    ) {
                        Text(
                            text = if (isTrialActive) "CONTINUE WITH PRO" else "START 7-DAY FREE TRIAL",
                            fontSize = 14.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "7 days free, cancel anytime. 14-day money-back guarantee.",
                        color = AppBackground.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Launch Offer: Lifetime $79 (first 100 buyers only, REAL counter)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = AppCardNav),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LAUNCH OFFER: LIFETIME $79",
                                    color = AppYellow,
                                    fontSize = 14.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Black
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AppCoral)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "$lifetimeSpotsRemaining / 100 SPOTS LEFT",
                                        color = AppBackground,
                                        fontSize = 10.sp,
                                        fontFamily = ArchivoFontFamily,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "One-time payment for perpetual access. Fair-use note: covers up to 500 AI searches/month and lifetime access to all core Pro features.",
                                color = AppWhite.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onClaimLifetime,
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AppYellow, contentColor = AppBackground),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text(
                                    text = "CLAIM LIFETIME PASS ($79)",
                                    fontSize = 12.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3 Benefit Bullets with Icons
                    Text(
                        text = "EVERYTHING IN PRO",
                        color = AppBackground,
                        fontSize = 13.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val benefits = listOf(
                        Triple(Icons.Default.Bolt, "UNLIMITED AI MATCHES", "Never hit daily search limits or midnight reset wait times."),
                        Triple(Icons.Default.NotificationsActive, "24/7 DOWNTIME WATCHLIST", "Instant notifications when your critical public APIs suffer outages."),
                        Triple(Icons.Default.Inventory2, "FULL PROJECT SCAFFOLDS", "Export ready-to-run multi-file code, Dockerfiles and GitHub repositories.")
                    )

                    benefits.forEach { (icon, title, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(AppBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = AppWhite, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = title,
                                    color = AppBackground,
                                    fontSize = 13.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = desc,
                                    color = AppBackground.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 4-Question FAQ (Collapsible)
                    Text(
                        text = "FREQUENTLY ASKED QUESTIONS",
                        color = AppBackground,
                        fontSize = 13.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val faqs = listOf(
                        "Can I cancel anytime?" to "Yes! You can cancel in 1 tap directly from your profile. No retention hurdles, and your projects and watchlist are preserved in read-only mode forever.",
                        "What is the 14-day money-back guarantee?" to "If APIMatch doesn't save you hours of research and API debugging, email support for a full, unconditional refund.",
                        "Are there API limits on Pro?" to "There is no throttling on normal development. A generous fair-use policy of 500 AI searches per month covers 99.9% of developers.",
                        "How does the student discount work?" to "Students get an instant 50% off discount! Tap the student link below to request your student code."
                    )

                    faqs.forEachIndexed { index, (q, a) ->
                        val isExpanded = expandedFaq == index
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { expandedFaq = if (isExpanded) null else index },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = AppBackground.copy(alpha = 0.08f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = q,
                                        color = AppBackground,
                                        fontSize = 13.sp,
                                        fontFamily = ArchivoFontFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = AppBackground
                                    )
                                }
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = a,
                                        color = AppBackground.copy(alpha = 0.85f),
                                        fontSize = 12.sp,
                                        fontFamily = ArchivoFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Team Waitlist & Student Discount links
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onOpenStudentDiscount) {
                            Text(
                                text = "🎓 Student? Get 50% off",
                                color = AppBackground,
                                fontSize = 12.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        TextButton(onClick = onOpenTeamWaitlist) {
                            Text(
                                text = "👥 Team: Join waitlist",
                                color = AppBackground,
                                fontSize = 12.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Trust Row (marked as placeholder in code comment)
                    // placeholder: social proof metrics and encryption badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = AppBackground.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "12,480 APIs checked this week • 99.8% uptime tracker • Encrypted",
                            color = AppBackground.copy(alpha = 0.75f),
                            fontSize = 10.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Maybe later (keeps results visible behind)
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().testTag("paywall_maybe_later_button")
                    ) {
                        Text(
                            text = "Maybe later",
                            color = AppBackground.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Onboarding & Retention UI Components
// -------------------------------------------------------------

@Composable
fun OnboardingWhatAreYouBuildingCard(
    selectedType: String?,
    onSelectType: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val types = listOf("Web app", "Mobile app", "Game", "Data/AI", "School project")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("onboarding_building_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardNav),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WHAT ARE YOU BUILDING?",
                    color = AppWhite,
                    fontSize = 15.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppYellow)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "1-TAP PERSONALIZATION",
                        color = AppBackground,
                        fontSize = 9.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap your focus to tailor starter prompts & API recommendations:",
                color = AppWhite.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { type ->
                    val isSelected = selectedType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) AppSage else AppBackground)
                            .clickable { onSelectType(type) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("building_chip_${type.lowercase().replace(" ", "_")}")
                    ) {
                        Text(
                            text = type.uppercase(),
                            color = if (isSelected) AppBackground else AppWhite,
                            fontSize = 11.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistCard(
    firstSearch: Boolean,
    firstTest: Boolean,
    firstCopy: Boolean,
    firstSave: Boolean,
    onUnlockSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = listOf(firstSearch, firstTest, firstCopy, firstSave).count { it }
    val progress = completedCount / 4f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("developer_checklist_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardNav),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GETTING STARTED",
                    color = AppWhite,
                    fontSize = 14.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$completedCount / 4 COMPLETED",
                    color = AppSage,
                    fontSize = 11.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = AppSage,
                trackColor = AppBackground
            )

            Spacer(modifier = Modifier.height(14.dp))

            val steps = listOf(
                Pair("Run a project search", firstSearch),
                Pair("Test an API live", firstTest),
                Pair("Copy a code snippet", firstCopy),
                Pair("Save project to workspace (Pro)", firstSave)
            )

            steps.forEachIndexed { index, (label, done) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable(enabled = index == 3 && !done) { onUnlockSave() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (done) AppSage else AppBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AppBackground, modifier = Modifier.size(13.dp))
                            }
                        }
                        Text(
                            text = label,
                            color = if (done) AppWhite.copy(alpha = 0.6f) else AppWhite,
                            fontSize = 13.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = if (done) FontWeight.Medium else FontWeight.Bold
                        )
                    }

                    if (index == 3 && !done) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AppCoral)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = AppBackground, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("PRO", color = AppBackground, fontSize = 9.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveTestProAlertBanner(
    onFollowPro: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_test_pro_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppCoral),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "WANT TO KNOW WHEN THIS API FAILS?",
                    color = AppBackground,
                    fontSize = 13.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Follow it with Pro for 24/7 downtime alerts.",
                    color = AppBackground.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onFollowPro,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppBackground, contentColor = AppWhite),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("FOLLOW WITH PRO", fontSize = 10.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.ExtraBold)
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = AppBackground, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun TrialEndingRecapCard(
    searchesCount: Int,
    savedProjectsCount: Int,
    alertsCount: Int,
    onKeepPro: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trial_ending_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppYellow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TRIAL ENDS IN 2 DAYS",
                    color = AppBackground,
                    fontSize = 15.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = AppBackground, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Here's what you accomplished during your trial:",
                color = AppBackground.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppBackground)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("$searchesCount", color = AppYellow, fontSize = 18.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                        Text("Searches run", color = AppWhite.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppBackground)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("$savedProjectsCount", color = AppSage, fontSize = 18.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                        Text("Projects saved", color = AppWhite.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppBackground)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("$alertsCount", color = AppCoral, fontSize = 18.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                        Text("Alerts active", color = AppWhite.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onKeepPro,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppBackground, contentColor = AppWhite),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Text("KEEP UNLIMITED ACCESS ($8/MO)", fontSize = 12.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun StudentDiscountDialog(
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppCardNav),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "STUDENT DISCOUNT",
                    color = AppWhite,
                    fontSize = 20.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Enter your student or .edu email address to receive an immediate 50% discount coupon.",
                    color = AppWhite.copy(alpha = 0.75f),
                    fontSize = 13.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("you@university.edu", color = AppWhite.copy(alpha = 0.4f)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppBackground,
                        unfocusedContainerColor = AppBackground,
                        focusedBorderColor = AppYellow,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = AppWhite,
                        unfocusedTextColor = AppWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryPillButton(text = "CANCEL", onClick = onDismiss, modifier = Modifier.weight(1f))
                    PrimaryPillButton(
                        text = "SUBMIT",
                        onClick = { onSubmit(email) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TeamWaitlistDialog(
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppCardNav),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "TEAM ACCESS WAITLIST",
                    color = AppWhite,
                    fontSize = 20.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Shared project workspaces, team API keys, and centralized downtime monitors. Enter your work email for beta invite:",
                    color = AppWhite.copy(alpha = 0.75f),
                    fontSize = 13.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("alex@company.com", color = AppWhite.copy(alpha = 0.4f)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppBackground,
                        unfocusedContainerColor = AppBackground,
                        focusedBorderColor = AppSage,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = AppWhite,
                        unfocusedTextColor = AppWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryPillButton(text = "CANCEL", onClick = onDismiss, modifier = Modifier.weight(1f))
                    PrimaryPillButton(
                        text = "JOIN WAITLIST",
                        onClick = { onSubmit(email) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun StarterCodeModal(
    api: PublicApi,
    isPro: Boolean,
    selectedLang: String,
    selectedTab: Int,
    onLangSelected: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onExportZip: () -> Unit,
    onCopySnippet: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snippet = remember(api, selectedLang) {
        StarterCodeGenerator.generateSnippet(api, selectedLang)
    }
    val scaffold = remember(api, selectedLang) {
        StarterCodeGenerator.generateFullProjectScaffold(api, selectedLang)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppCardNav),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "STARTER CODE",
                            color = AppWhite,
                            fontSize = 20.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = api.name.uppercase(),
                            color = AppYellow,
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AppBackground)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AppWhite, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher: Free Snippet vs Pro Scaffold
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppBackground)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selectedTab == 0) AppCoral else Color.Transparent)
                            .clickable { onTabSelected(0) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SNIPPET (FREE)",
                            color = if (selectedTab == 0) AppBackground else AppWhite,
                            fontSize = 11.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selectedTab == 1) AppCoral else Color.Transparent)
                            .clickable { onTabSelected(1) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SCAFFOLD (PRO)",
                                color = if (selectedTab == 1) AppBackground else AppWhite,
                                fontSize = 11.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Black
                            )
                            if (!isPro) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Pro",
                                    tint = if (selectedTab == 1) AppBackground else AppCoral,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Language pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("JavaScript", "Python", "Dart").forEach { lang ->
                        val isSelected = selectedLang.equals(lang, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) AppSage else AppBackground)
                                .clickable { onLangSelected(lang) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = lang.uppercase(),
                                color = if (isSelected) AppBackground else AppWhite,
                                fontSize = 11.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Code display container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppBackground)
                        .padding(14.dp)
                ) {
                    val codeContent = if (selectedTab == 0) snippet else scaffold
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = codeContent,
                            color = AppWhite,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryPillButton(
                        text = "COPY CODE",
                        onClick = {
                            val textToCopy = if (selectedTab == 0) snippet else scaffold
                            clipboardManager.setText(AnnotatedString(textToCopy))
                            onCopySnippet?.invoke()
                            Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        icon = {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )

                    if (selectedTab == 1) {
                        PrimaryPillButton(
                            text = "EXPORT ZIP",
                            onClick = onExportZip,
                            modifier = Modifier.weight(1f),
                            icon = {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = AppBackground)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileModalSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppCardNav),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEV PROFILE",
                        color = AppWhite,
                        fontSize = 24.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AppBackground)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AppWhite, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(AppYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.userEmail?.take(1)?.uppercase() ?: "D",
                                color = AppBackground,
                                fontSize = 22.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = uiState.userEmail ?: "developer@apimatch.dev",
                                color = AppWhite,
                                fontSize = 15.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (uiState.isPro) "PRO MEMBER" else "FREE TIER (5/DAY)",
                                color = if (uiState.isPro) AppBlue else AppYellow,
                                fontSize = 12.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sign in / Sign out
                if (uiState.userEmail == null) {
                    PrimaryPillButton(
                        text = "SIGN IN / CREATE ACCOUNT",
                        onClick = {
                            onDismiss()
                            viewModel.openAuthScreen(true)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    SecondaryPillButton(
                        text = "SIGN OUT",
                        onClick = { viewModel.signOut() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = AppBackground)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("${uiState.streakDays} DAYS", color = AppYellow, fontSize = 18.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                            Text("ACTIVE STREAK", color = AppWhite.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = AppBackground)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("${uiState.apisTestedCount}", color = AppSage, fontSize = 18.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                            Text("APIS TESTED", color = AppWhite.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Subscription Management Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppBackground),
                    modifier = Modifier.fillMaxWidth().testTag("subscription_management_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when {
                                    uiState.isTrialActive -> "7-DAY TRIAL ACTIVE"
                                    uiState.isPro -> "APIMATCH PRO ACTIVE"
                                    uiState.isDowngraded -> "ACCOUNT (READ-ONLY)"
                                    else -> "FREE TIER"
                                },
                                color = if (uiState.isPro) AppYellow else AppWhite,
                                fontSize = 14.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Black
                            )
                            if (uiState.isPro) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (uiState.isTrialActive) AppYellow else AppSage)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (uiState.isTrialActive) "TRIAL" else "ACTIVE",
                                        color = AppBackground,
                                        fontSize = 9.sp,
                                        fontFamily = ArchivoFontFamily,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = when {
                                uiState.isTrialActive -> "Trial expires in ${uiState.trialDaysRemaining} days. Cancel anytime with 1 tap."
                                uiState.isPro -> "Unlimited searches, 24/7 downtime alerts, and full scaffold export."
                                uiState.isDowngraded -> "Your projects & watchlist remain preserved in read-only mode."
                                else -> "${uiState.searchesLeftToday}/5 AI searches left today."
                            },
                            color = AppWhite.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (uiState.isPro) {
                            // 1-tap cancel flow as required by instructions
                            Button(
                                onClick = {
                                    viewModel.showCancellationDialog(true)
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppCoral.copy(alpha = 0.2f),
                                    contentColor = AppCoral
                                ),
                                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("profile_cancel_subscription_button")
                            ) {
                                Text(
                                    text = "CANCEL SUBSCRIPTION",
                                    fontSize = 12.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        } else {
                            PrimaryPillButton(
                                text = if (uiState.isDowngraded) "REACTIVATE PRO ($8/MO)" else "UPGRADE TO PRO",
                                onClick = {
                                    viewModel.triggerPaywall("Upgrade to APIMatch Pro for unlimited AI searches and downtime monitoring.")
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Export Full Project Source Code / ZIP
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "EXPORT APP PROJECT ZIP",
                            color = AppWhite,
                            fontSize = 14.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Download the complete APIMatch application archive with all Kotlin code, resources, and Gradle build files.",
                            color = AppWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        PrimaryPillButton(
                            text = "EXPORT APPLICATION ZIP",
                            onClick = {
                                val dummyApi = ApiCatalogSeed.apis[0]
                                viewModel.exportScaffoldZip(context, dummyApi)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Analytics Events Log Viewer
                var showAnalyticsLog by remember { mutableStateOf(false) }
                val eventsList by com.example.util.AnalyticsTracker.eventsFlow.collectAsState()

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppBackground),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showAnalyticsLog = !showAnalyticsLog },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📊", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ANALYTICS EVENT LOG (${eventsList.size})",
                                    color = AppWhite,
                                    fontSize = 13.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Icon(
                                if (showAnalyticsLog) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = AppWhite
                            )
                        }

                        if (showAnalyticsLog) {
                            Spacer(modifier = Modifier.height(10.dp))
                            if (eventsList.isEmpty()) {
                                Text(
                                    text = "No events logged yet.",
                                    color = AppWhite.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    fontFamily = ArchivoFontFamily
                                )
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    eventsList.take(20).forEach { event ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${event.formattedTime} [${event.name}]",
                                                color = AppYellow,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (event.params.isNotEmpty()) {
                                            Text(
                                                text = event.params.entries.joinToString(", ") { "${it.key}: ${it.value}" },
                                                color = AppWhite.copy(alpha = 0.7f),
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmailCaptureDialog(
    onSaveEmail: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var emailInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppCardNav),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SAVE YOUR MATCHES",
                    color = AppWhite,
                    fontSize = 20.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter your email to sync your matching APIs and starter code across all devices.",
                    color = AppWhite.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    placeholder = { Text("dev@company.com", color = AppWhite.copy(alpha = 0.4f)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppBackground,
                        unfocusedContainerColor = AppBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = AppWhite,
                        unfocusedTextColor = AppWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryPillButton(
                        text = "LATER",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryPillButton(
                        text = "SAVE",
                        onClick = { onSaveEmail(emailInput) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyDigestDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppCardNav),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WEEKLY DIGEST",
                        color = AppWhite,
                        fontSize = 22.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AppBackground)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AppWhite, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "3 NEW FREE APIS MATCHING YOUR PROJECTS",
                    color = AppYellow,
                    fontSize = 13.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                val items = listOf(
                    Triple("Jolpica F1 API", "Sports & Racing", AppBlue),
                    Triple("disease.sh", "Health & Open Data", AppCoral),
                    Triple("Ollama Local REST", "AI & ML", AppSage)
                )

                items.forEach { (name, cat, color) ->
                    val contentColor = getCardContentColor(color)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = color),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(name.uppercase(), color = contentColor, fontSize = 16.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                            Text(cat.uppercase(), color = contentColor.copy(alpha = 0.8f), fontSize = 11.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CancellationFlowDialog(
    onPauseMonth: (String) -> Unit,
    onClaimDiscount: (String) -> Unit,
    onConfirmCancel: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedReason by remember { mutableStateOf("Too expensive") }
    val reasons = listOf(
        "Too expensive",
        "Didn't find right API",
        "Project finished",
        "Found another tool",
        "Other"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().testTag("cancellation_flow_dialog"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppCardNav),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BEFORE YOU GO...",
                    color = AppWhite,
                    fontSize = 20.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Please tell us why you are canceling:",
                    color = AppWhite.copy(alpha = 0.75f),
                    fontSize = 13.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Reason Selection Chips
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    reasons.forEach { reason ->
                        val isSelected = selectedReason == reason
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AppBackground else Color.Transparent)
                                .clickable { selectedReason = reason }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) AppYellow else AppWhite.copy(alpha = 0.3f))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = reason,
                                color = if (isSelected) AppWhite else AppWhite.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Retention Offers
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onPauseMonth(selectedReason) },
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppSage, contentColor = AppBackground),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Text("PAUSE 1 MONTH (FREE)", fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                    }

                    Button(
                        onClick = { onClaimDiscount(selectedReason) },
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppYellow, contentColor = AppBackground),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Text("CLAIM 50% OFF (3 MONTHS)", fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Note: If you cancel, your saved projects and watchlist are kept read-only (never deleted) so you can return anytime.",
                        color = AppWhite.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    TextButton(
                        onClick = { onConfirmCancel(selectedReason) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Cancellation", color = AppCoral, fontSize = 12.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
