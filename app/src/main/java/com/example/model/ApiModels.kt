package com.example.model

enum class ApiCategory(val displayName: String, val iconName: String) {
    ALL("All", "Apps"),
    WEATHER("Weather", "Cloud"),
    FINANCE("Finance", "AttachMoney"),
    GAMES("Games", "SportsEsports"),
    MUSIC("Music", "MusicNote"),
    HEALTH("Health", "Favorite"),
    ANIMALS("Animals", "Pets"),
    FOOD("Food", "Restaurant"),
    SPORTS("Sports", "FitnessCenter"),
    BOOKS("Books", "MenuBook"),
    AI("AI & ML", "Psychology"),
    GEOCODING("Geocoding", "Public"),
    NEWS("News", "Newspaper"),
    EDUCATION("Education", "School"),
    OPEN_DATA("Open Data", "Storage"),
    DEV_TOOLS("Dev Tools", "Code")
}

enum class AuthType(val displayName: String) {
    NONE("No Auth"),
    API_KEY("API Key"),
    OAUTH("OAuth 2.0")
}

enum class CorsType(val displayName: String) {
    YES("CORS: Yes"),
    NO("CORS: No"),
    UNKNOWN("CORS: Unknown")
}

enum class DifficultyLevel(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

enum class TestStatus(val label: String) {
    IDLE("Ready"),
    TESTING("Pinging..."),
    UP("Up"),
    SLOW("Slow"),
    DOWN("Down"),
    CORS_RESTRICTED("CORS Restricted")
}

data class PublicApi(
    val id: String,
    val name: String,
    val description: String,
    val category: ApiCategory,
    val auth: AuthType,
    val https: Boolean = true,
    val cors: CorsType = CorsType.YES,
    val docsUrl: String,
    val testEndpoint: String? = null,
    val difficulty: DifficultyLevel = DifficultyLevel.EASY,
    val tags: List<String> = emptyList(),
    val sampleJs: String = "",
    val samplePy: String = "",
    val sampleDart: String = "",
    val scaffoldSummary: String = ""
)

data class ApiMatchResult(
    val api: PublicApi,
    val matchScore: Int, // e.g. 96%
    val why: String, // 2-line explanation
    val difficulty: DifficultyLevel,
    val miniPlan: List<String> // 3 steps: "Your first API call in 5 min"
)

data class LivePingResult(
    val apiId: String,
    val status: TestStatus,
    val responseTimeMs: Long = 0L,
    val httpStatusCode: Int = 0,
    val checkedAt: Long = System.currentTimeMillis(),
    val message: String? = null
)

data class SavedProject(
    val id: String,
    val title: String,
    val query: String,
    val matchedApiIds: List<String>,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

data class WatchlistItem(
    val apiId: String,
    val addedAt: Long = System.currentTimeMillis(),
    val recentOutages: Int = 0,
    val currentStatus: TestStatus = TestStatus.UP,
    val alertEnabled: Boolean = true
)

data class DailyProjectChallenge(
    val id: String,
    val title: String,
    val description: String,
    val primaryApiId: String,
    val secondaryApiId: String,
    val difficulty: DifficultyLevel,
    val estimatedTime: String = "45 mins"
)

data class Testimonial(
    val author: String,
    val role: String,
    val quote: String,
    val avatarEmoji: String
)
