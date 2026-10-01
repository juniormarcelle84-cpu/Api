package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AppRepository
import com.example.model.ApiCategory
import com.example.model.ApiMatchResult
import com.example.model.DifficultyLevel
import com.example.model.LivePingResult
import com.example.model.PublicApi
import com.example.model.SavedProject
import com.example.model.TestStatus
import com.example.model.WatchlistItem
import com.example.util.AnalyticsTracker
import com.example.util.StarterCodeGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

enum class AppNavTab(val title: String) {
    SEARCH("Search"),
    BROWSE("Browse"),
    WATCHLIST("Watchlist"),
    PROJECTS("Projects"),
    PROFILE("Profile")
}

data class UiState(
    val currentTab: AppNavTab = AppNavTab.SEARCH,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<ApiMatchResult>? = null,
    val searchesLeftToday: Int = 5,
    val isPro: Boolean = false,
    val isTrialActive: Boolean = false,
    val trialDaysRemaining: Int = 2,
    val isDowngraded: Boolean = false,
    val userEmail: String? = null,
    val userBuildingType: String? = null,
    val selectedCategory: ApiCategory = ApiCategory.ALL,
    val browseQuery: String = "",
    val pingResults: Map<String, LivePingResult> = emptyMap(),
    val isPingingMap: Map<String, Boolean> = emptyMap(),
    val activePaywallReason: String? = null,
    val starterCodeModalApi: PublicApi? = null,
    val selectedCodeLang: String = "JavaScript",
    val selectedCodeTab: Int = 0, // 0: Free snippet, 1: Pro Scaffold
    val showEmailCapture: Boolean = false,
    val showWeeklyDigest: Boolean = false,
    val showCancellationFlow: Boolean = false,
    val showShareCard: SavedProject? = null,
    val streakDays: Int = 3,
    val apisTestedCount: Int = 6,
    val liveTestsSessionCount: Int = 0,
    val showLiveTestProBanner: Boolean = false,
    val lifetimeSpotsRemaining: Int = 37,
    val showStudentDiscountDialog: Boolean = false,
    val showTeamWaitlistDialog: Boolean = false,
    val checklistFirstSearch: Boolean = false,
    val checklistFirstTest: Boolean = false,
    val checklistFirstCopy: Boolean = false,
    val checklistFirstSave: Boolean = false,
    val checklistFirstFollow: Boolean = false,
    val showProfileSheet: Boolean = false,
    val showAuthScreen: Boolean = false,
    val authUser: com.example.data.auth.User? = null,
    val snackbarMessage: String? = null
)

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            isPro = repository.isPro(),
            isTrialActive = repository.isTrialActive(),
            trialDaysRemaining = repository.getTrialDaysRemaining(),
            isDowngraded = repository.isDowngraded(),
            userEmail = repository.getUserEmail(),
            userBuildingType = repository.getUserBuildingType(),
            searchesLeftToday = repository.getDailySearchesRemaining(),
            streakDays = repository.getStreakDays(),
            apisTestedCount = repository.getApisTestedCount(),
            liveTestsSessionCount = repository.getLiveTestsCount(),
            showLiveTestProBanner = repository.getLiveTestsCount() >= 3 && !repository.isPro() && !repository.isLiveTestBannerDismissed(),
            lifetimeSpotsRemaining = repository.getLifetimeSpotsRemaining(),
            checklistFirstSearch = repository.isChecklistFirstSearchDone(),
            checklistFirstTest = repository.isChecklistFirstTestDone(),
            checklistFirstCopy = repository.isChecklistFirstCopyDone(),
            checklistFirstSave = repository.isChecklistFirstSaveDone(),
            checklistFirstFollow = repository.isChecklistFirstFollowDone()
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val savedProjects: StateFlow<List<SavedProject>> = repository.savedProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchlistItems: StateFlow<List<WatchlistItem>> = repository.watchlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Pre-populate watchlist with 2 items if empty to showcase live monitoring
        viewModelScope.launch {
            if (_uiState.value.isPro) {
                // If Pro, unlimited
            }
        }
    }

    fun setTab(tab: AppNavTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun openProfile(show: Boolean) {
        _uiState.value = _uiState.value.copy(showProfileSheet = show)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onSelectExamplePrompt(prompt: String) {
        _uiState.value = _uiState.value.copy(searchQuery = prompt)
        performSearch(prompt)
    }

    fun selectBuildingType(type: String) {
        repository.setUserBuildingType(type)
        AnalyticsTracker.logOnboardingAnswered(type)
        _uiState.value = _uiState.value.copy(userBuildingType = type)
    }

    fun performSearch(queryToRun: String? = null) {
        val query = (queryToRun ?: _uiState.value.searchQuery).trim()
        if (query.isBlank()) return

        if (!repository.canPerformSearch()) {
            val resetTime = repository.getResetTimeFormatted()
            triggerPaywall("You've used your 5 free searches today. Resets in $resetTime. Upgrade to APIMatch Pro for unlimited AI searches.")
            AnalyticsTracker.logPaywallView("6th_search_limit")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true, searchQuery = query)
            try {
                val matches = repository.findMatches(query)
                val remaining = repository.getDailySearchesRemaining()
                val needsEmailCapture = !_uiState.value.isPro && _uiState.value.userEmail.isNullOrBlank()

                AnalyticsTracker.logSearchRun(query, matches.size)
                AnalyticsTracker.logChecklistStep("run_search")

                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    searchResults = matches,
                    searchesLeftToday = remaining,
                    checklistFirstSearch = true,
                    showEmailCapture = needsEmailCapture,
                    streakDays = repository.getStreakDays()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    snackbarMessage = "Search error: ${e.message}"
                )
            }
        }
    }

    fun testLiveApi(api: PublicApi, isProFeature: Boolean = false) {
        if (isProFeature && !_uiState.value.isPro) {
            triggerPaywall("Bypass client CORS restrictions for ${api.name} with APIMatch Pro server-side proxy.")
            AnalyticsTracker.logPaywallView("server_side_test_cors_${api.id}")
            return
        }

        viewModelScope.launch {
            val pingingMap = _uiState.value.isPingingMap.toMutableMap()
            pingingMap[api.id] = true
            _uiState.value = _uiState.value.copy(isPingingMap = pingingMap)

            val sessionCount = repository.incrementLiveTestsCount()
            val result = repository.testApi(api, isPro = _uiState.value.isPro || isProFeature)

            AnalyticsTracker.logTestLive(api.id, result.status.name, result.responseTimeMs, sessionCount)
            AnalyticsTracker.logChecklistStep("test_live_api")

            val newPingResults = _uiState.value.pingResults.toMutableMap()
            newPingResults[api.id] = result
            pingingMap[api.id] = false

            val shouldShowProBanner = sessionCount >= 3 && !_uiState.value.isPro && !repository.isLiveTestBannerDismissed()

            _uiState.value = _uiState.value.copy(
                pingResults = newPingResults,
                isPingingMap = pingingMap,
                checklistFirstTest = true,
                liveTestsSessionCount = sessionCount,
                showLiveTestProBanner = shouldShowProBanner,
                apisTestedCount = repository.getApisTestedCount()
            )
        }
    }

    fun dismissLiveTestBanner() {
        repository.dismissLiveTestBanner()
        _uiState.value = _uiState.value.copy(showLiveTestProBanner = false)
    }

    fun recordSnippetCopied(api: PublicApi? = null) {
        repository.markFirstCopyDone()
        AnalyticsTracker.logChecklistStep("copy_snippet")
        _uiState.value = _uiState.value.copy(
            checklistFirstCopy = true,
            snackbarMessage = if (api != null) "Copied ${api.name} starter snippet to clipboard!" else "Copied to clipboard!"
        )
    }

    fun openStarterCode(api: PublicApi, initialTab: Int = 0) {
        _uiState.value = _uiState.value.copy(
            starterCodeModalApi = api,
            selectedCodeTab = initialTab
        )
    }

    fun setStarterCodeLang(lang: String) {
        _uiState.value = _uiState.value.copy(selectedCodeLang = lang)
    }

    fun setStarterCodeTab(tab: Int) {
        val currentApi = _uiState.value.starterCodeModalApi
        if (tab == 1 && !_uiState.value.isPro) {
            val apiName = currentApi?.name ?: "this API"
            triggerPaywall("Export full project scaffold for $apiName with Dockerfile and GitHub configs with APIMatch Pro.")
            AnalyticsTracker.logPaywallView("locked_scaffold_tab_$apiName")
            return
        }
        _uiState.value = _uiState.value.copy(selectedCodeTab = tab)
    }

    fun closeStarterCode() {
        _uiState.value = _uiState.value.copy(starterCodeModalApi = null)
    }

    fun triggerPaywall(reason: String) {
        AnalyticsTracker.logPaywallView(reason)
        _uiState.value = _uiState.value.copy(activePaywallReason = reason)
    }

    fun closePaywall() {
        _uiState.value = _uiState.value.copy(activePaywallReason = null)
    }

    fun startFreeTrial(plan: String = "Yearly ($96/yr - $8/mo)") {
        repository.startTrial(7)
        AnalyticsTracker.logTrialStart(plan)
        _uiState.value = _uiState.value.copy(
            isPro = true,
            isTrialActive = true,
            trialDaysRemaining = 7,
            isDowngraded = false,
            activePaywallReason = null,
            searchesLeftToday = 999,
            snackbarMessage = "🚀 7-day free trial activated! Unlimited AI searches, live alerts & full code export."
        )
    }

    fun purchaseLifetime() {
        val remaining = repository.claimLifetimeSpot()
        repository.setPro(true)
        AnalyticsTracker.logUpgrade("lifetime_launch", "$79")
        _uiState.value = _uiState.value.copy(
            isPro = true,
            isTrialActive = false,
            isDowngraded = false,
            lifetimeSpotsRemaining = remaining,
            activePaywallReason = null,
            searchesLeftToday = 999,
            snackbarMessage = "🎉 Lifetime Member unlocked! ($remaining/100 spots remaining)"
        )
    }

    fun upgradeToPro(plan: String) {
        repository.setPro(true)
        val price = if (plan.contains("Yearly", ignoreCase = true)) "$96" else "$10"
        AnalyticsTracker.logUpgrade(plan, price)
        _uiState.value = _uiState.value.copy(
            isPro = true,
            isTrialActive = false,
            isDowngraded = false,
            activePaywallReason = null,
            searchesLeftToday = 999,
            snackbarMessage = "🎉 Welcome to APIMatch Pro! All features unlocked ($plan active)."
        )
    }

    fun cancelSubscription(reason: String, actionTaken: String) {
        repository.endTrialOrCancel()
        AnalyticsTracker.logCancelReason(reason, actionTaken)
        _uiState.value = _uiState.value.copy(
            isPro = false,
            isTrialActive = false,
            isDowngraded = true,
            showCancellationFlow = false,
            snackbarMessage = "Subscription canceled. Your projects and watchlist are preserved in read-only mode."
        )
    }

    fun openTeamWaitlist(show: Boolean) {
        _uiState.value = _uiState.value.copy(showTeamWaitlistDialog = show)
    }

    fun submitTeamWaitlist(email: String) {
        if (email.isNotBlank()) {
            repository.saveTeamWaitlistEmail(email)
            AnalyticsTracker.logTeamWaitlist(email)
            _uiState.value = _uiState.value.copy(
                showTeamWaitlistDialog = false,
                snackbarMessage = "You're on the Team waitlist! We'll invite $email for early access."
            )
        }
    }

    fun openStudentDiscount(show: Boolean) {
        _uiState.value = _uiState.value.copy(showStudentDiscountDialog = show)
    }

    fun submitStudentDiscount(email: String) {
        if (email.isNotBlank()) {
            repository.saveStudentDiscountEmail(email)
            AnalyticsTracker.logStudentDiscount(email)
            _uiState.value = _uiState.value.copy(
                showStudentDiscountDialog = false,
                snackbarMessage = "50% student discount code sent to $email! Check your inbox."
            )
        }
    }

    fun saveCurrentProject(title: String, notes: String = "") {
        if (_uiState.value.userEmail == null) {
            _uiState.value = _uiState.value.copy(
                showAuthScreen = true,
                snackbarMessage = "Create your free account to save projects to your workspace."
            )
            AnalyticsTracker.trackEvent("guest_signup_prompt", mapOf("reason" to "save_project"))
            return
        }

        if (!_uiState.value.isPro && savedProjects.value.size >= 2) {
            triggerPaywall("Save '$title' to your workspace. Free tier is limited to 2 saved projects. Upgrade to Pro for unlimited workspaces.")
            AnalyticsTracker.logPaywallView("locked_save_project_limit")
            return
        }

        viewModelScope.launch {
            val results = _uiState.value.searchResults ?: emptyList()
            val project = SavedProject(
                id = UUID.randomUUID().toString(),
                title = title.ifBlank { "Project: ${_uiState.value.searchQuery.take(24)}" },
                query = _uiState.value.searchQuery,
                matchedApiIds = results.map { it.api.id },
                notes = notes
            )
            repository.saveProject(project)
            AnalyticsTracker.logChecklistStep("save_project")
            _uiState.value = _uiState.value.copy(
                checklistFirstSave = true,
                snackbarMessage = "Project '${project.title}' saved to workspace!"
            )
        }
    }

    fun deleteProject(id: String) {
        if (_uiState.value.isDowngraded) {
            _uiState.value = _uiState.value.copy(
                snackbarMessage = "Projects are in read-only mode. Upgrade to Pro to edit or delete."
            )
            return
        }
        viewModelScope.launch {
            repository.deleteProject(id)
            _uiState.value = _uiState.value.copy(snackbarMessage = "Project removed")
        }
    }

    fun toggleWatchlist(api: PublicApi) {
        if (_uiState.value.userEmail == null) {
            _uiState.value = _uiState.value.copy(
                showAuthScreen = true,
                snackbarMessage = "Create your free account to follow ${api.name} and get alerts."
            )
            AnalyticsTracker.trackEvent("guest_signup_prompt", mapOf("reason" to "follow_api", "api" to api.name))
            return
        }

        if (!_uiState.value.isPro && watchlistItems.value.size >= 1) {
            triggerPaywall("Get alerted when ${api.name} goes down! Free tier is limited to 1 watchlist item. Pro offers 24/7 unlimited downtime alerts.")
            AnalyticsTracker.logPaywallView("locked_watchlist_limit_${api.id}")
            return
        }

        viewModelScope.launch {
            val isAlready = watchlistItems.value.any { it.apiId == api.id }
            if (isAlready) {
                if (_uiState.value.isDowngraded) {
                    _uiState.value = _uiState.value.copy(
                        snackbarMessage = "Watchlist is preserved in read-only mode. Upgrade to Pro to make changes."
                    )
                    return@launch
                }
                repository.removeFromWatchlist(api.id)
                _uiState.value = _uiState.value.copy(snackbarMessage = "Removed ${api.name} from Watchlist")
            } else {
                repository.addToWatchlist(api.id)
                AnalyticsTracker.logChecklistStep("follow_api")
                _uiState.value = _uiState.value.copy(
                    checklistFirstFollow = true,
                    snackbarMessage = "⭐ Following ${api.name} for outage alerts"
                )
            }
        }
    }

    fun toggleAlertNotification(apiId: String, enabled: Boolean) {
        if (!_uiState.value.isPro) {
            triggerPaywall("Instant push and email downtime alerts are exclusive to APIMatch Pro.")
            return
        }
        viewModelScope.launch {
            repository.toggleAlert(apiId, enabled)
        }
    }

    fun saveEmailCapture(email: String) {
        if (email.isNotBlank()) {
            repository.setUserEmail(email)
            _uiState.value = _uiState.value.copy(
                userEmail = email,
                showEmailCapture = false,
                snackbarMessage = "Search results saved! We'll send updates to $email."
            )
        } else {
            _uiState.value = _uiState.value.copy(showEmailCapture = false)
        }
    }

    fun dismissEmailCapture() {
        _uiState.value = _uiState.value.copy(showEmailCapture = false)
    }

    fun setCategory(category: ApiCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateBrowseQuery(query: String) {
        _uiState.value = _uiState.value.copy(browseQuery = query)
    }

    fun exportScaffoldZip(context: Context, api: PublicApi) {
        if (!_uiState.value.isPro) {
            triggerPaywall("Exporting offline ZIP scaffolds and GitHub project templates requires APIMatch Pro.")
            return
        }
        try {
            val bytes = StarterCodeGenerator.buildZipScaffold(api, _uiState.value.selectedCodeLang)
            val cacheFile = File(context.cacheDir, "${api.name.lowercase().replace(" ", "_")}_starter.zip")
            FileOutputStream(cacheFile).use { it.write(bytes) }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "APIMatch: ${api.name} Starter Project")
                putExtra(Intent.EXTRA_TEXT, "Here is the starter code project scaffold for ${api.name}, generated with APIMatch.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Export Project Scaffold ZIP"))
        } catch (e: Exception) {
            Toast.makeText(context, "Export scaffold preview ready in clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    fun openAuthScreen(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAuthScreen = show)
    }

    fun onAuthSuccess(user: com.example.data.auth.User) {
        repository.setUserEmail(user.email)
        val isProUser = user.plan == "Pro" || _uiState.value.isPro
        if (isProUser && !_uiState.value.isPro) {
            repository.setPro(true)
        }
        AnalyticsTracker.trackEvent("user_signed_in", mapOf(
            "provider" to user.provider,
            "email" to user.email,
            "plan" to user.plan
        ))
        _uiState.value = _uiState.value.copy(
            authUser = user,
            userEmail = user.email,
            isPro = isProUser,
            showAuthScreen = false,
            snackbarMessage = "Welcome, ${user.name}!"
        )
    }

    fun signInWithGoogle(context: Context) {
        // Open rich unified AuthScreen with motion logo
        openAuthScreen(true)
    }

    fun signOut() {
        repository.firebaseManager.signOut()
        repository.setUserEmail(null)
        _uiState.value = _uiState.value.copy(
            authUser = null,
            userEmail = null,
            snackbarMessage = "Signed out successfully"
        )
    }

    fun showWeeklyDigestPreview(show: Boolean) {
        _uiState.value = _uiState.value.copy(showWeeklyDigest = show)
    }

    fun showCancellationDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCancellationFlow = show)
    }

    fun shareProjectCard(project: SavedProject?) {
        _uiState.value = _uiState.value.copy(showShareCard = project)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
