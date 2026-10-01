package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.model.SavedProject
import com.example.model.WatchlistItem
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseSyncManager(private val context: Context) {

    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w("FirebaseSync", "FirebaseAuth not initialized: ${e.message}")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w("FirebaseSync", "FirebaseFirestore not initialized: ${e.message}")
            null
        }
    }

    fun isFirebaseAvailable(): Boolean {
        return auth != null && firestore != null
    }

    fun getCurrentUserEmail(): String? {
        return auth?.currentUser?.email
    }

    suspend fun signInWithGoogle(webClientId: String = ""): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId.ifEmpty { "123456789-apimatch.apps.googleusercontent.com" })
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(context, request)
            val credential = response.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth?.signInWithCredential(authCredential)?.await()
                val email = authResult?.user?.email ?: googleIdTokenCredential.id
                Result.success(email)
            } else {
                Result.failure(Exception("Unknown credential format"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Google Sign-In exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun syncProjectToFirestore(project: SavedProject) {
        val user = auth?.currentUser ?: return
        try {
            val docData = hashMapOf(
                "id" to project.id,
                "title" to project.title,
                "query" to project.query,
                "matchedApiIds" to project.matchedApiIds,
                "createdAt" to project.createdAt,
                "notes" to project.notes,
                "userId" to user.uid,
                "userEmail" to (user.email ?: "")
            )
            firestore?.collection("users")?.document(user.uid)
                ?.collection("saved_projects")?.document(project.id)
                ?.set(docData, SetOptions.merge())?.await()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Error syncing project to Firestore: ${e.message}")
        }
    }

    suspend fun syncWatchlistToFirestore(item: WatchlistItem) {
        val user = auth?.currentUser ?: return
        try {
            val docData = hashMapOf(
                "apiId" to item.apiId,
                "addedAt" to item.addedAt,
                "recentOutages" to item.recentOutages,
                "currentStatus" to item.currentStatus.name,
                "alertEnabled" to item.alertEnabled,
                "userId" to user.uid
            )
            firestore?.collection("users")?.document(user.uid)
                ?.collection("watchlist")?.document(item.apiId)
                ?.set(docData, SetOptions.merge())?.await()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Error syncing watchlist to Firestore: ${e.message}")
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w("FirebaseSync", "Error signing out: ${e.message}")
        }
    }
}
