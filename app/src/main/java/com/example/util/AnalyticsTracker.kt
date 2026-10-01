package com.example.util

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AnalyticsEvent(
    val name: String,
    val params: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(timestamp))
}

object AnalyticsTracker {
    private const val TAG = "APIMatchAnalytics"

    private val _eventsList = mutableListOf<AnalyticsEvent>()
    private val _eventsFlow = MutableStateFlow<List<AnalyticsEvent>>(emptyList())
    val eventsFlow: StateFlow<List<AnalyticsEvent>> = _eventsFlow.asStateFlow()

    @Synchronized
    fun logEvent(name: String, params: Map<String, String> = emptyMap()) {
        val event = AnalyticsEvent(name, params)
        _eventsList.add(0, event)
        // Keep at most 200 events in memory
        if (_eventsList.size > 200) {
            _eventsList.removeAt(_eventsList.lastIndex)
        }
        _eventsFlow.value = _eventsList.toList()

        val paramsStr = if (params.isNotEmpty()) {
            params.entries.joinToString(", ") { "${it.key}=${it.value}" }
        } else {
            "none"
        }
        Log.d(TAG, "📊 [$name] params: {$paramsStr}")
    }

    fun logSearchRun(query: String, matchCount: Int) {
        logEvent("search_run", mapOf("query" to query.take(30), "match_count" to matchCount.toString()))
    }

    fun logTestLive(apiId: String, status: String, responseTimeMs: Long, totalTests: Int) {
        logEvent("test_live", mapOf(
            "api_id" to apiId,
            "status" to status,
            "latency_ms" to responseTimeMs.toString(),
            "total_tests" to totalTests.toString()
        ))
    }

    fun logPaywallView(trigger: String) {
        logEvent("paywall_view", mapOf("trigger" to trigger.take(60)))
    }

    fun logTrialStart(plan: String) {
        logEvent("trial_start", mapOf("plan" to plan))
    }

    fun logUpgrade(plan: String, price: String) {
        logEvent("upgrade", mapOf("plan" to plan, "price" to price))
    }

    fun logCancelReason(reason: String, actionTaken: String) {
        logEvent("cancel_reason", mapOf("reason" to reason, "action" to actionTaken))
    }

    fun logChecklistStep(step: String) {
        logEvent("checklist_step_complete", mapOf("step" to step))
    }

    fun logOnboardingAnswered(buildingType: String) {
        logEvent("onboarding_answered", mapOf("building_type" to buildingType))
    }

    fun logTeamWaitlist(email: String) {
        logEvent("team_waitlist_join", mapOf("email" to email))
    }

    fun logStudentDiscount(email: String) {
        logEvent("student_discount_request", mapOf("email" to email))
    }

    fun trackEvent(name: String, params: Map<String, String> = emptyMap()) {
        logEvent(name, params)
    }
}
