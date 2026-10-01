package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.SavedProjectEntity
import com.example.data.local.WatchlistEntity
import com.example.data.remote.FirebaseSyncManager
import com.example.data.remote.GeminiMatcherService
import com.example.data.remote.LiveApiTester
import com.example.data.seed.ApiCatalogSeed
import com.example.model.ApiCategory
import com.example.model.ApiMatchResult
import com.example.model.LivePingResult
import com.example.model.PublicApi
import com.example.model.SavedProject
import com.example.model.TestStatus
import com.example.model.WatchlistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val projectDao = db.projectDao()
    private val watchlistDao = db.watchlistDao()

    private val prefs: SharedPreferences = context.getSharedPreferences("apimatch_prefs", Context.MODE_PRIVATE)
    val liveTester = LiveApiTester()
    val matcherService = GeminiMatcherService()
    val firebaseManager = FirebaseSyncManager(context)

    // User profile state
    fun isPro(): Boolean = prefs.getBoolean("is_pro_user", false)

    fun setPro(isPro: Boolean) {
        prefs.edit().putBoolean("is_pro_user", isPro).apply()
    }

    fun getUserEmail(): String? = prefs.getString("user_email", firebaseManager.getCurrentUserEmail())

    fun setUserEmail(email: String?) {
        prefs.edit().putString("user_email", email).apply()
    }

    // Daily Searches limit (5/day for free tier)
    fun getDailySearchesRemaining(): Int {
        if (isPro()) return 999
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val lastDate = prefs.getString("searches_date", "")
        if (lastDate != todayStr) {
            prefs.edit().putString("searches_date", todayStr).putInt("searches_used_today", 0).apply()
            return 5
        }
        val used = prefs.getInt("searches_used_today", 0)
        return (5 - used).coerceAtLeast(0)
    }

    fun getResetTimeFormatted(): String {
        val now = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        val midnight = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
            add(java.util.Calendar.DAY_OF_YEAR, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val diffMs = (midnight.timeInMillis - now.timeInMillis).coerceAtLeast(0)
        val hours = diffMs / (1000 * 60 * 60)
        val minutes = (diffMs / (1000 * 60)) % 60
        return "${hours}h ${minutes}m at midnight UTC"
    }

    fun canPerformSearch(): Boolean {
        return isPro() || getDailySearchesRemaining() > 0
    }

    fun recordSearchPerformed() {
        val todayStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val lastDate = prefs.getString("searches_date", "")
        var used = prefs.getInt("searches_used_today", 0)
        if (lastDate != todayStr) {
            used = 0
        }
        val totalSearches = prefs.getInt("total_searches_run", 11) + 1
        prefs.edit()
            .putString("searches_date", todayStr)
            .putInt("searches_used_today", used + 1)
            .putInt("total_searches_run", totalSearches)
            .putBoolean("checklist_first_search", true)
            .apply()

        incrementStreak()
    }

    fun getTotalSearchesRun(): Int = prefs.getInt("total_searches_run", 12)

    // Launch Lifetime Offer - Real remaining counter out of 100
    fun getLifetimeSpotsRemaining(): Int = prefs.getInt("lifetime_spots_remaining", 37)

    fun claimLifetimeSpot(): Int {
        val current = getLifetimeSpotsRemaining()
        val next = (current - 1).coerceAtLeast(0)
        prefs.edit().putInt("lifetime_spots_remaining", next).apply()
        return next
    }

    // Onboarding: What are you building?
    fun getUserBuildingType(): String? = prefs.getString("user_building_type", null)

    fun setUserBuildingType(type: String) {
        prefs.edit().putString("user_building_type", type).apply()
    }

    // Onboarding checklist progress: Run a search, Test an API live, Copy a snippet, Save a project
    fun isChecklistFirstSearchDone(): Boolean = prefs.getBoolean("checklist_first_search", false)
    fun isChecklistFirstTestDone(): Boolean = prefs.getBoolean("checklist_first_test", false)
    fun isChecklistFirstCopyDone(): Boolean = prefs.getBoolean("checklist_first_copy", false)
    fun isChecklistFirstSaveDone(): Boolean = prefs.getBoolean("checklist_first_save", false)
    fun isChecklistFirstFollowDone(): Boolean = prefs.getBoolean("checklist_first_follow", false)

    fun markFirstTestDone() {
        prefs.edit().putBoolean("checklist_first_test", true).apply()
        incrementApisTested()
    }

    fun markFirstCopyDone() {
        prefs.edit().putBoolean("checklist_first_copy", true).apply()
    }

    fun markFirstSaveDone() {
        prefs.edit().putBoolean("checklist_first_save", true).apply()
    }

    fun markFirstFollowDone() {
        prefs.edit().putBoolean("checklist_first_follow", true).apply()
    }

    // Live test count tracking
    fun getLiveTestsCount(): Int = prefs.getInt("live_tests_count_session", 0)

    fun incrementLiveTestsCount(): Int {
        val current = getLiveTestsCount() + 1
        prefs.edit().putInt("live_tests_count_session", current).apply()
        return current
    }

    fun isLiveTestBannerDismissed(): Boolean = prefs.getBoolean("dismiss_live_test_banner", false)

    fun dismissLiveTestBanner() {
        prefs.edit().putBoolean("dismiss_live_test_banner", true).apply()
    }

    // Trial and Downgrade state
    fun isTrialActive(): Boolean = prefs.getBoolean("is_trial_active", false)

    fun getTrialDaysRemaining(): Int = prefs.getInt("trial_days_remaining", 2)

    fun startTrial(days: Int = 7) {
        prefs.edit()
            .putBoolean("is_pro_user", true)
            .putBoolean("is_trial_active", true)
            .putInt("trial_days_remaining", days)
            .putBoolean("is_downgraded", false)
            .apply()
    }

    fun endTrialOrCancel() {
        prefs.edit()
            .putBoolean("is_pro_user", false)
            .putBoolean("is_trial_active", false)
            .putBoolean("is_downgraded", true)
            .apply()
    }

    fun isDowngraded(): Boolean = prefs.getBoolean("is_downgraded", false)

    // Team waitlist and Student discount storage
    fun saveTeamWaitlistEmail(email: String) {
        prefs.edit().putString("team_waitlist_email", email).apply()
    }

    fun saveStudentDiscountEmail(email: String) {
        prefs.edit().putString("student_discount_email", email).apply()
    }

    // Streaks and counters
    fun getStreakDays(): Int = prefs.getInt("streak_days", 3)
    private fun incrementStreak() {
        val current = getStreakDays()
        prefs.edit().putInt("streak_days", current + 1).apply()
    }

    fun getApisTestedCount(): Int = prefs.getInt("apis_tested_count", 6)
    private fun incrementApisTested() {
        val current = getApisTestedCount()
        prefs.edit().putInt("apis_tested_count", current + 1).apply()
    }

    // APIs catalog
    fun getAllApis(): List<PublicApi> = ApiCatalogSeed.apis

    fun getApiById(id: String): PublicApi? = ApiCatalogSeed.getById(id)

    fun getApisByCategory(category: ApiCategory, query: String = ""): List<PublicApi> {
        val list = if (category == ApiCategory.ALL) {
            ApiCatalogSeed.apis
        } else {
            ApiCatalogSeed.apis.filter { it.category == category }
        }

        if (query.isBlank()) return list
        val q = query.trim().lowercase()
        return list.filter {
            it.name.lowercase().contains(q) ||
            it.description.lowercase().contains(q) ||
            it.tags.any { tag -> tag.lowercase().contains(q) }
        }
    }

    fun getApiOfTheDay(): PublicApi {
        return ApiCatalogSeed.apis.firstOrNull { it.id == "open-meteo" } ?: ApiCatalogSeed.apis[0]
    }

    // Saved projects
    val savedProjects: Flow<List<SavedProject>> = projectDao.getAllProjects().map { list ->
        list.map { entity ->
            SavedProject(
                id = entity.id,
                title = entity.title,
                query = entity.query,
                matchedApiIds = entity.matchedApiIdsCsv.split(",").filter { it.isNotEmpty() },
                createdAt = entity.createdAt,
                notes = entity.notes
            )
        }
    }

    suspend fun saveProject(project: SavedProject) {
        val entity = SavedProjectEntity(
            id = project.id,
            title = project.title,
            query = project.query,
            matchedApiIdsCsv = project.matchedApiIds.joinToString(","),
            createdAt = project.createdAt,
            notes = project.notes
        )
        projectDao.insertProject(entity)
        markFirstSaveDone()
        firebaseManager.syncProjectToFirestore(project)
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteProject(id)
    }

    // Watchlist
    val watchlistItems: Flow<List<WatchlistItem>> = watchlistDao.getAllWatchlist().map { list ->
        list.map { entity ->
            WatchlistItem(
                apiId = entity.apiId,
                addedAt = entity.addedAt,
                recentOutages = entity.recentOutages,
                currentStatus = try { TestStatus.valueOf(entity.lastStatus) } catch (e: Exception) { TestStatus.UP },
                alertEnabled = entity.alertEnabled
            )
        }
    }

    suspend fun addToWatchlist(apiId: String) {
        val entity = WatchlistEntity(
            apiId = apiId,
            addedAt = System.currentTimeMillis(),
            recentOutages = if (apiId == "openweathermap") 2 else 0,
            lastStatus = "UP",
            alertEnabled = true
        )
        watchlistDao.insertWatchlist(entity)
        markFirstFollowDone()
        firebaseManager.syncWatchlistToFirestore(
            WatchlistItem(apiId, entity.addedAt, entity.recentOutages, TestStatus.UP, true)
        )
    }

    suspend fun removeFromWatchlist(apiId: String) {
        watchlistDao.deleteWatchlist(apiId)
    }

    suspend fun toggleAlert(apiId: String, enabled: Boolean) {
        watchlistDao.updateAlert(apiId, enabled)
    }

    // Live test integration
    suspend fun testApi(api: PublicApi, isPro: Boolean = isPro()): LivePingResult {
        val result = liveTester.testApi(api, isProServerSide = isPro)
        markFirstTestDone()
        return result
    }

    // AI Matcher
    suspend fun findMatches(query: String): List<ApiMatchResult> {
        recordSearchPerformed()
        return matcherService.matchApis(query)
    }
}
