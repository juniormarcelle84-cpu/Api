package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.AppRepository
import com.example.model.TestStatus
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.AppBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = AppRepository(applicationContext)

        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel {
                    MainViewModel(repository)
                }

                ApimatchApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ApimatchApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val watchlistItems by viewModel.watchlistItems.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Observe snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Back handling: return to Search tab if on secondary tabs
    if (uiState.currentTab != AppNavTab.SEARCH) {
        BackHandler {
            viewModel.setTab(AppNavTab.SEARCH)
        }
    }

    val hasDownApi = remember(uiState.pingResults, watchlistItems) {
        uiState.pingResults.values.any { it.status == TestStatus.DOWN } ||
        watchlistItems.any { it.recentOutages > 0 }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = AppBackground,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                ApimatchBoldHeader(
                    isPro = uiState.isPro,
                    searchesLeft = uiState.searchesLeftToday,
                    onAvatarClick = { viewModel.openProfile(true) },
                    onQuotaClick = {
                        if (!uiState.isPro) {
                            viewModel.triggerPaywall("You've got ${uiState.searchesLeftToday}/5 searches left today. Upgrade to APIMatch Pro for unlimited AI searches.")
                        } else {
                            viewModel.openProfile(true)
                        }
                    }
                )
            },
            bottomBar = {
                BoldFloatingBottomNav(
                    currentTab = uiState.currentTab,
                    isPro = uiState.isPro,
                    hasDownApi = hasDownApi,
                    onTabSelected = { tab -> viewModel.setTab(tab) }
                )
            }
        ) { innerPadding ->
            val bottomPadding = innerPadding.calculateBottomPadding() + 24.dp
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                when (uiState.currentTab) {
                    AppNavTab.SEARCH -> SearchScreen(viewModel = viewModel, bottomPadding = bottomPadding)
                    AppNavTab.BROWSE -> BrowseScreen(viewModel = viewModel, bottomPadding = bottomPadding)
                    AppNavTab.WATCHLIST -> WatchlistScreen(viewModel = viewModel, bottomPadding = bottomPadding)
                    AppNavTab.PROJECTS -> ProjectsScreen(viewModel = viewModel, bottomPadding = bottomPadding)
                    AppNavTab.PROFILE -> {
                        // Profile is opened via header avatar, fallback to Search
                        SearchScreen(viewModel = viewModel, bottomPadding = bottomPadding)
                    }
                }
            }
        }

        // Sage Bottom Sheet Paywall
        val paywallReason = uiState.activePaywallReason
        if (paywallReason != null) {
            SagePaywallBottomSheet(
                reason = paywallReason,
                lifetimeSpotsRemaining = uiState.lifetimeSpotsRemaining,
                isTrialActive = uiState.isTrialActive,
                onUpgrade = { plan ->
                    if (!uiState.isTrialActive) {
                        viewModel.startFreeTrial(plan)
                    } else {
                        viewModel.upgradeToPro(plan)
                    }
                },
                onClaimLifetime = { viewModel.purchaseLifetime() },
                onOpenTeamWaitlist = { viewModel.openTeamWaitlist(true) },
                onOpenStudentDiscount = { viewModel.openStudentDiscount(true) },
                onDismiss = { viewModel.closePaywall() }
            )
        }

        // Student Discount Dialog
        if (uiState.showStudentDiscountDialog) {
            StudentDiscountDialog(
                onSubmit = { email -> viewModel.submitStudentDiscount(email) },
                onDismiss = { viewModel.openStudentDiscount(false) }
            )
        }

        // Team Waitlist Dialog
        if (uiState.showTeamWaitlistDialog) {
            TeamWaitlistDialog(
                onSubmit = { email -> viewModel.submitTeamWaitlist(email) },
                onDismiss = { viewModel.openTeamWaitlist(false) }
            )
        }

        // Profile Sheet (opened from yellow avatar)
        if (uiState.showProfileSheet) {
            ProfileModalSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.openProfile(false) }
            )
        }

        // Starter Code Modal
        val starterApi = uiState.starterCodeModalApi
        if (starterApi != null) {
            StarterCodeModal(
                api = starterApi,
                isPro = uiState.isPro,
                selectedLang = uiState.selectedCodeLang,
                selectedTab = uiState.selectedCodeTab,
                onLangSelected = { lang -> viewModel.setStarterCodeLang(lang) },
                onTabSelected = { tab -> viewModel.setStarterCodeTab(tab) },
                onExportZip = { viewModel.exportScaffoldZip(context, starterApi) },
                onCopySnippet = { viewModel.recordSnippetCopied(starterApi) },
                onDismiss = { viewModel.closeStarterCode() }
            )
        }

        // Email Capture Dialog after first search result
        if (uiState.showEmailCapture) {
            EmailCaptureDialog(
                onSaveEmail = { email -> viewModel.saveEmailCapture(email) },
                onDismiss = { viewModel.dismissEmailCapture() }
            )
        }

        // Weekly Digest Modal Preview
        if (uiState.showWeeklyDigest) {
            WeeklyDigestDialog(
                onDismiss = { viewModel.showWeeklyDigestPreview(false) }
            )
        }

        // Cancellation Flow Dialog
        if (uiState.showCancellationFlow) {
            CancellationFlowDialog(
                onPauseMonth = { reason ->
                    viewModel.cancelSubscription(reason, "paused_1_month")
                    Toast.makeText(context, "Subscription paused for 30 days. Projects kept in read-only mode.", Toast.LENGTH_LONG).show()
                },
                onClaimDiscount = { reason ->
                    viewModel.cancelSubscription(reason, "claimed_50_pct_discount")
                    viewModel.upgradeToPro("Discounted Pro ($4/mo)")
                    Toast.makeText(context, "50% discount applied to your next 3 billing cycles ($4/mo)!", Toast.LENGTH_LONG).show()
                },
                onConfirmCancel = { reason ->
                    viewModel.cancelSubscription(reason, "confirmed_cancellation")
                    Toast.makeText(context, "Subscription cancelled. Your data remains in read-only mode.", Toast.LENGTH_LONG).show()
                },
                onDismiss = { viewModel.showCancellationDialog(false) }
            )
        }

        // Unified Sign-up / Log-in Screen with Motion Logo
        if (uiState.showAuthScreen) {
            AuthScreen(
                onContinueGuest = { viewModel.openAuthScreen(false) },
                onAuthSuccess = { user -> viewModel.onAuthSuccess(user) }
            )
        }
    }
}
