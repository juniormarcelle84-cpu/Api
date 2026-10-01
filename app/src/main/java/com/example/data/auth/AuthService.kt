package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.model.SavedProject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Supported auth backends: 'mock' | 'firebase' | 'supabase'
const val AUTH_BACKEND: String = "mock"
const val ENABLE_APPLE: Boolean = false

enum class AuthProvider(val id: String, val displayName: String) {
    GITHUB("github", "GitHub"),
    GOOGLE("google", "Google"),
    EMAIL("email", "Email"),
    APPLE("apple", "Apple"),
    GUEST("guest", "Guest")
}

data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatar: String? = null,
    val provider: String,
    val plan: String = "Free"
)

sealed class AuthException(message: String, val fixActionText: String? = null, val fixActionType: String? = null) : Exception(message) {
    class PopupBlocked : AuthException(
        message = "Pop-up was blocked. Allow pop-ups or use email sign-in.",
        fixActionText = "Use email sign-in",
        fixActionType = "switch_to_email"
    )
    class AccountExistsWithDifferentCredential(provider: String = "Google") : AuthException(
        message = "This email is linked to $provider. Continue with $provider.",
        fixActionText = "Continue with $provider",
        fixActionType = "continue_with_$provider"
    )
    class Offline : AuthException(
        message = "You're offline. Check your connection and try again.",
        fixActionText = "Try again",
        fixActionType = "retry"
    )
    class LinkExpired : AuthException(
        message = "This magic link has expired. Send a new one.",
        fixActionText = "Send new link",
        fixActionType = "resend_link"
    )
    class Generic(msg: String) : AuthException(msg, "Try again", "retry")
}

interface AuthService {
    suspend fun signIn(provider: AuthProvider): Result<User>
    suspend fun sendMagicLink(email: String): Result<Unit>
    suspend fun verifyMagicLink(email: String, codeOrToken: String): Result<User>
    suspend fun signOut()
    fun getCurrentUser(): User?
    fun onAuthChange(callback: (User?) -> Unit)
}

/**
 * Mock Auth Service (default):
 * Simulates real authentication after 800ms delay so the UI works smoothly before external keys exist.
 */
class MockAuthService(private val context: Context) : AuthService {
    private val prefs: SharedPreferences = context.getSharedPreferences("apimatch_auth", Context.MODE_PRIVATE)
    private val _currentUser = MutableStateFlow<User?>(loadSavedUser())
    val currentUserState: StateFlow<User?> = _currentUser.asStateFlow()

    private val authChangeCallbacks = mutableListOf<(User?) -> Unit>()

    private fun loadSavedUser(): User? {
        val id = prefs.getString("user_id", null) ?: return null
        val email = prefs.getString("user_email", "developer@apimatch.dev") ?: "developer@apimatch.dev"
        val name = prefs.getString("user_name", "Developer") ?: "Developer"
        val provider = prefs.getString("user_provider", "google") ?: "google"
        val plan = prefs.getString("user_plan", "Free") ?: "Free"
        val avatar = prefs.getString("user_avatar", null)
        return User(id = id, email = email, name = name, avatar = avatar, provider = provider, plan = plan)
    }

    private fun saveUser(user: User?) {
        val editor = prefs.edit()
        if (user != null) {
            editor.putString("user_id", user.id)
            editor.putString("user_email", user.email)
            editor.putString("user_name", user.name)
            editor.putString("user_provider", user.provider)
            editor.putString("user_plan", user.plan)
            editor.putString("user_avatar", user.avatar)
            editor.putString("last_used_provider", user.provider)
            editor.putString("last_used_name", user.name)
        } else {
            editor.remove("user_id")
            editor.remove("user_email")
            editor.remove("user_name")
            editor.remove("user_provider")
            editor.remove("user_plan")
            editor.remove("user_avatar")
        }
        editor.apply()
        _currentUser.value = user
        authChangeCallbacks.forEach { it(user) }
    }

    override suspend fun signIn(provider: AuthProvider): Result<User> {
        delay(800) // 800ms simulation as specified

        val user = when (provider) {
            AuthProvider.GITHUB -> User(
                id = "gh_usr_94102",
                email = "alex.dev@github.com",
                name = "Alex Dev",
                avatar = "https://avatars.githubusercontent.com/u/1024025?v=4",
                provider = AuthProvider.GITHUB.id,
                plan = "Free"
            )
            AuthProvider.GOOGLE -> User(
                id = "goog_usr_48291",
                email = "alex.developer@gmail.com",
                name = "Alex Johnson",
                avatar = null,
                provider = AuthProvider.GOOGLE.id,
                plan = "Free"
            )
            AuthProvider.APPLE -> User(
                id = "apple_usr_39104",
                email = "alex.apple@icloud.com",
                name = "Alex Appleseed",
                avatar = null,
                provider = AuthProvider.APPLE.id,
                plan = "Free"
            )
            AuthProvider.EMAIL -> User(
                id = "email_usr_10293",
                email = "alex@company.io",
                name = "Alex",
                avatar = null,
                provider = AuthProvider.EMAIL.id,
                plan = "Free"
            )
            AuthProvider.GUEST -> User(
                id = "guest_${System.currentTimeMillis()}",
                email = "guest@apimatch.local",
                name = "Guest Developer",
                avatar = null,
                provider = AuthProvider.GUEST.id,
                plan = "Free"
            )
        }

        saveUser(user)
        return Result.success(user)
    }

    override suspend fun sendMagicLink(email: String): Result<Unit> {
        delay(600)
        if (!email.contains("@") || !email.contains(".")) {
            return Result.failure(AuthException.Generic("Please enter a valid email address."))
        }
        // Save pending email in prefs
        prefs.edit().putString("pending_magic_link_email", email).apply()
        return Result.success(Unit)
    }

    override suspend fun verifyMagicLink(email: String, codeOrToken: String): Result<User> {
        delay(600)
        val user = User(
            id = "magic_${System.currentTimeMillis()}",
            email = email,
            name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
            provider = AuthProvider.EMAIL.id,
            plan = "Free"
        )
        saveUser(user)
        return Result.success(user)
    }

    override suspend fun signOut() {
        delay(200)
        saveUser(null)
    }

    override fun getCurrentUser(): User? = _currentUser.value

    override fun onAuthChange(callback: (User?) -> Unit) {
        authChangeCallbacks.add(callback)
        callback(_currentUser.value)
    }

    fun getLastUsedProvider(): String? = prefs.getString("last_used_provider", null)
    fun getLastUsedName(): String? = prefs.getString("last_used_name", null)
}

/**
 * Firebase Auth Service implementation template:
 * Ready for production activation when google-services.json and Firebase credentials are provided.
 */
class FirebaseAuthService(private val context: Context) : AuthService {
    // Note: Can live in client (public by design). Never put secret keys in the app.
    // In production, uses com.google.firebase.auth.FirebaseAuth
    // GoogleAuthProvider, GithubAuthProvider, OAuthProvider("apple.com"), sendSignInLinkToEmail
    private val fallback = MockAuthService(context)

    override suspend fun signIn(provider: AuthProvider): Result<User> {
        return fallback.signIn(provider)
    }

    override suspend fun sendMagicLink(email: String): Result<Unit> {
        return fallback.sendMagicLink(email)
    }

    override suspend fun verifyMagicLink(email: String, codeOrToken: String): Result<User> {
        return fallback.verifyMagicLink(email, codeOrToken)
    }

    override suspend fun signOut() {
        fallback.signOut()
    }

    override fun getCurrentUser(): User? = fallback.getCurrentUser()

    override fun onAuthChange(callback: (User?) -> Unit) {
        fallback.onAuthChange(callback)
    }
}

/**
 * Supabase Auth Service implementation template:
 * Ready for production activation with Supabase anon key and URL.
 */
class SupabaseAuthService(private val context: Context) : AuthService {
    // Supabase anon key can live in client (public by design).
    // In production, uses signInWithOAuth({provider}), signInWithOtp({email})
    private val fallback = MockAuthService(context)

    override suspend fun signIn(provider: AuthProvider): Result<User> {
        return fallback.signIn(provider)
    }

    override suspend fun sendMagicLink(email: String): Result<Unit> {
        return fallback.sendMagicLink(email)
    }

    override suspend fun verifyMagicLink(email: String, codeOrToken: String): Result<User> {
        return fallback.verifyMagicLink(email, codeOrToken)
    }

    override suspend fun signOut() {
        fallback.signOut()
    }

    override fun getCurrentUser(): User? = fallback.getCurrentUser()

    override fun onAuthChange(callback: (User?) -> Unit) {
        fallback.onAuthChange(callback)
    }
}

/**
 * AuthFactory providing the appropriate service based on AUTH_BACKEND.
 */
object AuthManager {
    private var instance: AuthService? = null

    fun getService(context: Context): AuthService {
        if (instance == null) {
            instance = when (AUTH_BACKEND.lowercase()) {
                "firebase" -> FirebaseAuthService(context.applicationContext)
                "supabase" -> SupabaseAuthService(context.applicationContext)
                else -> MockAuthService(context.applicationContext)
            }
        }
        return instance!!
    }
}
