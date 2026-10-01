package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.seed.ApiCatalogSeed
import com.example.model.ApiMatchResult
import com.example.model.DifficultyLevel
import com.example.model.PublicApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiMatcherService {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun matchApis(userQuery: String): List<ApiMatchResult> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiMatches = callGeminiApi(userQuery, apiKey)
                if (geminiMatches.isNotEmpty()) {
                    return@withContext geminiMatches
                }
            } catch (e: Exception) {
                Log.w("GeminiMatcher", "Gemini API call failed, falling back to local engine: ${e.message}")
            }
        }

        // High quality fallback local heuristic matcher
        return@withContext localHeuristicMatcher(userQuery)
    }

    private fun callGeminiApi(userQuery: String, apiKey: String): List<ApiMatchResult> {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val seedSummary = ApiCatalogSeed.apis.take(40).joinToString("\n") {
            "- id: ${it.id}, name: ${it.name}, cat: ${it.category.name}, auth: ${it.auth.name}, desc: ${it.description}"
        }

        val prompt = """
You are an expert API architect for APIMatch.
A developer described their project: "$userQuery"

Choose the BEST 3 matching APIs from this catalog or well-known public APIs:
$seedSummary

Respond ONLY with a valid JSON array of 3 objects with this exact structure:
[
  {
    "apiId": "id-from-list-or-name",
    "name": "API Name",
    "matchScore": 95,
    "why": "Two sentence explanation of why this API is perfect for this project.",
    "difficulty": "Easy",
    "step1": "Register or copy endpoint",
    "step2": "Make test GET call with query params",
    "step3": "Render json payload in UI"
  }
]
""".trimIndent()

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.4)
            })
        }

        val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code}: ${response.body?.string()}")
            }
            val responseString = response.body?.string() ?: throw Exception("Empty response")
            val rootJson = JSONObject(responseString)
            val candidates = rootJson.optJSONArray("candidates") ?: return emptyList()
            if (candidates.length() == 0) return emptyList()

            val text = candidates.getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val jsonArray = JSONArray(text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim())
            val results = mutableListOf<ApiMatchResult>()

            for (i in 0 until jsonArray.length().coerceAtMost(3)) {
                val item = jsonArray.getJSONObject(i)
                val apiId = item.optString("apiId")
                val apiName = item.optString("name")
                val score = item.optInt("matchScore", 90 - (i * 4))
                val why = item.optString("why", "Great fit for the user query with zero setup friction.")
                val diffStr = item.optString("difficulty", "Easy")
                val difficulty = when (diffStr.lowercase()) {
                    "medium" -> DifficultyLevel.MEDIUM
                    "hard" -> DifficultyLevel.HARD
                    else -> DifficultyLevel.EASY
                }
                val step1 = item.optString("step1", "Fetch endpoint using standard HTTP client")
                val step2 = item.optString("step2", "Parse JSON response attributes")
                val step3 = item.optString("step3", "Bind data into your view state")

                val matchedApi = ApiCatalogSeed.apis.find {
                    it.id.equals(apiId, ignoreCase = true) || it.name.equals(apiName, ignoreCase = true)
                } ?: ApiCatalogSeed.apis.firstOrNull {
                    it.name.contains(apiName, ignoreCase = true)
                } ?: ApiCatalogSeed.apis[i]

                results.add(
                    ApiMatchResult(
                        api = matchedApi,
                        matchScore = score,
                        why = why,
                        difficulty = difficulty,
                        miniPlan = listOf(step1, step2, step3)
                    )
                )
            }
            return results
        }
    }

    fun localHeuristicMatcher(userQuery: String): List<ApiMatchResult> {
        val queryLower = userQuery.lowercase()
        val tokens = queryLower.split(Regex("[^a-z0-9]+")).filter { it.length > 2 }

        val scored = ApiCatalogSeed.apis.map { api ->
            var score = 30 // baseline
            val nameLower = api.name.lowercase()
            val descLower = api.description.lowercase()
            val catLower = api.category.name.lowercase()

            for (token in tokens) {
                if (nameLower.contains(token)) score += 35
                if (descLower.contains(token)) score += 20
                if (catLower.contains(token)) score += 25
                if (api.tags.any { it.contains(token) }) score += 25
            }

            // Category semantic boosters
            if ((queryLower.contains("weather") || queryLower.contains("rain") || queryLower.contains("climate") || queryLower.contains("temperature"))
                && api.category.name == "WEATHER") score += 50
            if ((queryLower.contains("crypto") || queryLower.contains("stock") || queryLower.contains("currency") || queryLower.contains("finance") || queryLower.contains("bitcoin"))
                && api.category.name == "FINANCE") score += 50
            if ((queryLower.contains("game") || queryLower.contains("trivia") || queryLower.contains("pokemon") || queryLower.contains("quiz"))
                && api.category.name == "GAMES") score += 50
            if ((queryLower.contains("music") || queryLower.contains("song") || queryLower.contains("lyrics") || queryLower.contains("playlist") || queryLower.contains("audio"))
                && api.category.name == "MUSIC") score += 50
            if ((queryLower.contains("food") || queryLower.contains("recipe") || queryLower.contains("drink") || queryLower.contains("wine") || queryLower.contains("cocktail"))
                && api.category.name == "FOOD") score += 50
            if ((queryLower.contains("book") || queryLower.contains("read") || queryLower.contains("library") || queryLower.contains("poem"))
                && api.category.name == "BOOKS") score += 50
            if ((queryLower.contains("ai") || queryLower.contains("llm") || queryLower.contains("bot") || queryLower.contains("nlp") || queryLower.contains("model"))
                && api.category.name == "AI") score += 50
            if ((queryLower.contains("map") || queryLower.contains("geo") || queryLower.contains("city") || queryLower.contains("country") || queryLower.contains("location"))
                && api.category.name == "GEOCODING") score += 50
            if ((queryLower.contains("news") || queryLower.contains("article") || queryLower.contains("reddit") || queryLower.contains("hacker"))
                && api.category.name == "NEWS") score += 50
            if ((queryLower.contains("quote") || queryLower.contains("quote") || queryLower.contains("learn") || queryLower.contains("fact"))
                && (api.category.name == "EDUCATION" || api.id == "quotable")) score += 50

            api to score
        }.sortedByDescending { it.second }

        val top3 = scored.take(3)
        val defaultScores = listOf(96, 91, 86)

        return top3.mapIndexed { index, pair ->
            val api = pair.first
            val calculatedPercent = (pair.second).coerceIn(80, 98)
            val score = if (pair.second > 40) calculatedPercent else defaultScores.getOrElse(index) { 82 }

            val whyText = generateWhyExplanation(api, userQuery)
            val miniPlan = listOf(
                "Step 1: Test endpoint in APIMatch console to verify latency.",
                "Step 2: Copy the generated ${if (api.auth == com.example.model.AuthType.NONE) "zero-auth" else "API key"} starter snippet.",
                "Step 3: Parse response JSON into your data model in under 5 minutes."
            )

            ApiMatchResult(
                api = api,
                matchScore = score,
                why = whyText,
                difficulty = api.difficulty,
                miniPlan = miniPlan
            )
        }
    }

    private fun generateWhyExplanation(api: PublicApi, query: String): String {
        return "Ideal for '$query' because ${api.name} offers fast ${api.cors.displayName} with ${api.auth.displayName}. It provides turnkey payloads that map directly to your interface."
    }
}
