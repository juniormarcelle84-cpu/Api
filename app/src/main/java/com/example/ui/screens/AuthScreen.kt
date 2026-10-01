package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthException
import com.example.data.auth.AuthManager
import com.example.data.auth.AuthProvider
import com.example.data.auth.ENABLE_APPLE
import com.example.data.auth.User
import com.example.ui.components.MotionLogo
import com.example.ui.components.MotionLogoMode
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onContinueGuest: () -> Unit,
    onAuthSuccess: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authService = remember { AuthManager.getService(context) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var isLoading by remember { mutableStateOf(false) }
    var successUser by remember { mutableStateOf<User?>(null) }
    var activeError by remember { mutableStateOf<AuthException?>(null) }

    var emailInput by remember { mutableStateOf("") }
    var magicLinkSentTo by remember { mutableStateOf<String?>(null) }
    var resendCountdown by remember { mutableIntStateOf(30) }

    // Last used provider & returning user
    val prefs = remember { context.getSharedPreferences("apimatch_auth", Context.MODE_PRIVATE) }
    val lastUsedProvider = remember { prefs.getString("last_used_provider", null) }
    val lastUsedName = remember { prefs.getString("last_used_name", null) }
    var showReturningUserHero by remember { mutableStateOf(lastUsedName != null) }

    // Motion logo mode: NORMAL, LOADING, SUCCESS
    val logoMode = when {
        successUser != null -> MotionLogoMode.SUCCESS
        isLoading -> MotionLogoMode.LOADING
        else -> MotionLogoMode.NORMAL
    }

    // 30s countdown timer for magic link resend
    LaunchedEffect(magicLinkSentTo) {
        if (magicLinkSentTo != null) {
            resendCountdown = 30
            while (resendCountdown > 0) {
                delay(1000)
                resendCountdown--
            }
        }
    }

    // On success: hold checkmark, show "Welcome, {name}", then route after 1.2s
    LaunchedEffect(successUser) {
        val user = successUser
        if (user != null) {
            delay(1200)
            onAuthSuccess(user)
        }
    }

    // Back handling: if in magic link confirmation, back returns to provider list
    BackHandler {
        if (magicLinkSentTo != null) {
            magicLinkSentTo = null
        } else {
            onContinueGuest()
        }
    }

    fun handleSignIn(provider: AuthProvider) {
        if (isLoading) return
        isLoading = true
        activeError = null
        scope.launch {
            try {
                val result = authService.signIn(provider)
                result.fold(
                    onSuccess = { user ->
                        isLoading = false
                        successUser = user
                    },
                    onFailure = { error ->
                        isLoading = false
                        activeError = when (error) {
                            is AuthException -> error
                            else -> AuthException.Generic(error.message ?: "Authentication failed. Try again.")
                        }
                    }
                )
            } catch (e: Exception) {
                isLoading = false
                activeError = AuthException.Generic(e.message ?: "Authentication failed. Try again.")
            }
        }
    }

    fun handleSendMagicLink() {
        val trimmed = emailInput.trim()
        if (trimmed.isEmpty() || !trimmed.contains("@")) {
            activeError = AuthException.Generic("Please enter a valid email address.")
            return
        }
        if (isLoading) return
        isLoading = true
        activeError = null
        focusManager.clearFocus()
        scope.launch {
            val result = authService.sendMagicLink(trimmed)
            isLoading = false
            result.fold(
                onSuccess = {
                    magicLinkSentTo = trimmed
                },
                onFailure = { error ->
                    activeError = when (error) {
                        is AuthException -> error
                        else -> AuthException.Generic(error.message ?: "Could not send sign-in link.")
                    }
                }
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .testTag("auth_screen")
    ) {
        val isDesktop = maxWidth > 900.dp

        if (isDesktop) {
            // Desktop Split Screen (>900px)
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Form
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 48.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AuthFormContent(
                        isDesktop = true,
                        logoMode = logoMode,
                        isLoading = isLoading,
                        successUser = successUser,
                        activeError = activeError,
                        lastUsedProvider = lastUsedProvider,
                        lastUsedName = lastUsedName,
                        showReturningUserHero = showReturningUserHero,
                        emailInput = emailInput,
                        magicLinkSentTo = magicLinkSentTo,
                        resendCountdown = resendCountdown,
                        onEmailChange = { emailInput = it },
                        onSendMagicLink = { handleSendMagicLink() },
                        onResendMagicLink = { handleSendMagicLink() },
                        onResetMagicLink = { magicLinkSentTo = null },
                        onDismissReturningHero = { showReturningUserHero = false },
                        onProviderClick = { handleSignIn(it) },
                        onContinueGuest = onContinueGuest,
                        onClearError = { activeError = null }
                    )
                }

                // Right Blue Panel with 3 tilted stacked cards showing sample match
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .background(AppBlue)
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DesktopTiltedCardsPreview()
                }
            }
        } else {
            // Mobile View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                AuthFormContent(
                    isDesktop = false,
                    logoMode = logoMode,
                    isLoading = isLoading,
                    successUser = successUser,
                    activeError = activeError,
                    lastUsedProvider = lastUsedProvider,
                    lastUsedName = lastUsedName,
                    showReturningUserHero = showReturningUserHero,
                    emailInput = emailInput,
                    magicLinkSentTo = magicLinkSentTo,
                    resendCountdown = resendCountdown,
                    onEmailChange = { emailInput = it },
                    onSendMagicLink = { handleSendMagicLink() },
                    onResendMagicLink = { handleSendMagicLink() },
                    onResetMagicLink = { magicLinkSentTo = null },
                    onDismissReturningHero = { showReturningUserHero = false },
                    onProviderClick = { handleSignIn(it) },
                    onContinueGuest = onContinueGuest,
                    onClearError = { activeError = null }
                )
            }
        }
    }
}

@Composable
private fun AuthFormContent(
    isDesktop: Boolean,
    logoMode: MotionLogoMode,
    isLoading: Boolean,
    successUser: User?,
    activeError: AuthException?,
    lastUsedProvider: String?,
    lastUsedName: String?,
    showReturningUserHero: Boolean,
    emailInput: String,
    magicLinkSentTo: String?,
    resendCountdown: Int,
    onEmailChange: (String) -> Unit,
    onSendMagicLink: () -> Unit,
    onResendMagicLink: () -> Unit,
    onResetMagicLink: () -> Unit,
    onDismissReturningHero: () -> Unit,
    onProviderClick: (AuthProvider) -> Unit,
    onContinueGuest: () -> Unit,
    onClearError: () -> Unit
) {
    val context = LocalContext.current
    val formWidth = if (isDesktop) Modifier.widthIn(max = 440.dp) else Modifier.fillMaxWidth()

    Column(
        modifier = formWidth,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Motion Logo with wordmark and SVG physics
        MotionLogo(
            size = 96.dp,
            mode = logoMode,
            showWordmark = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Headlines & Sublines
        if (successUser != null) {
            Text(
                text = "Welcome, ${successUser.name}!",
                color = AppYellow,
                fontSize = 26.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Routing to your personalized developer workspace...",
                color = AppWhite.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "Create your free account",
                color = AppWhite,
                fontSize = 24.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "5 free searches a day. No credit card.",
                color = AppWhite.copy(alpha = 0.75f),
                fontSize = 14.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Error Alert with plain words & actionable fix
        if (activeError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        liveRegion = LiveRegionMode.Assertive
                        contentDescription = "Authentication error: ${activeError.message}"
                    }
                    .testTag("auth_error_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppCoral.copy(alpha = 0.15f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AppCoral))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = AppCoral,
                            modifier = Modifier.size(18.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeError.message ?: "Authentication error",
                                color = AppWhite,
                                fontSize = 13.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Bold
                            )

                            if (activeError.fixActionText != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = activeError.fixActionText,
                                    color = AppYellow,
                                    fontSize = 12.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier
                                        .clickable {
                                            when (activeError.fixActionType) {
                                                "continue_with_Google" -> onProviderClick(AuthProvider.GOOGLE)
                                                "resend_link" -> onSendMagicLink()
                                                else -> onClearError()
                                            }
                                        }
                                        .padding(vertical = 2.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = onClearError,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss error", tint = AppWhite.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Magic Link Confirmation State
        if (magicLinkSentTo != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("magic_link_confirmation"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = AppCardNav)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(AppSage),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = AppBackground,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Check your inbox",
                        color = AppWhite,
                        fontSize = 20.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "We sent a magic sign-in link to:",
                        color = AppWhite.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = magicLinkSentTo,
                        color = AppSage,
                        fontSize = 14.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Open Gmail shortcut if gmail.com
                    val isGmail = magicLinkSentTo.lowercase().endsWith("@gmail.com")
                    if (isGmail) {
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    val mailIntent = Intent(Intent.ACTION_MAIN).apply {
                                        addCategory(Intent.CATEGORY_APP_EMAIL)
                                    }
                                    context.startActivity(Intent.createChooser(mailIntent, "Open Email"))
                                }
                            },
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppWhite, contentColor = AppBackground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("open_gmail_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GoogleMark(size = 18.dp)
                                Text(
                                    text = "OPEN GMAIL",
                                    fontSize = 14.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    } else {
                        Button(
                            onClick = {
                                try {
                                    val mailIntent = Intent(Intent.ACTION_MAIN).apply {
                                        addCategory(Intent.CATEGORY_APP_EMAIL)
                                    }
                                    context.startActivity(Intent.createChooser(mailIntent, "Open Email App"))
                                } catch (e: Exception) {
                                    // fallback
                                }
                            },
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppWhite, contentColor = AppBackground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = "OPEN EMAIL APP",
                                fontSize = 14.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Resend after 30s countdown
                    OutlinedButton(
                        onClick = {
                            if (resendCountdown == 0) {
                                onResendMagicLink()
                            }
                        },
                        enabled = resendCountdown == 0 && !isLoading,
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppWhite,
                            disabledContentColor = AppWhite.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = if (resendCountdown > 0) "RESEND LINK IN ${resendCountdown}S" else "RESEND SIGN-IN LINK",
                            fontSize = 13.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Use another email or provider",
                        color = AppWhite.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onResetMagicLink() }
                            .padding(8.dp)
                    )
                }
            }
        } else {
            // 5. Normal Sign-In Options (or Returning User Hero)

            // Returning user view: "Welcome back, {name}" with one large button
            if (showReturningUserHero && lastUsedName != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("returning_user_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AppCardNav)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome back, $lastUsedName",
                            color = AppWhite,
                            fontSize = 18.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Continue with your previous session",
                            color = AppWhite.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val provider = when (lastUsedProvider?.lowercase()) {
                                    "github" -> AuthProvider.GITHUB
                                    "apple" -> AuthProvider.APPLE
                                    else -> AuthProvider.GOOGLE
                                }
                                onProviderClick(provider)
                            },
                            enabled = !isLoading,
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppYellow, contentColor = AppBackground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("continue_as_user_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AppBackground, strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = "CONTINUE AS ${lastUsedName.uppercase()}",
                                    fontSize = 14.sp,
                                    fontFamily = ArchivoFontFamily,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Or use a different account",
                            color = AppWhite.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onDismissReturningHero() }
                                .padding(6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // PROVIDER BUTTONS IN EXACT ORDER:
            // 1. Continue with GitHub: charcoal pill, white GitHub mark, label "Recommended for developers"
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onProviderClick(AuthProvider.GITHUB) },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppCardNav,
                        contentColor = AppWhite,
                        disabledContainerColor = AppCardNav.copy(alpha = 0.5f),
                        disabledContentColor = AppWhite.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("signin_github_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GitHubMark(size = 22.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with GitHub",
                                fontSize = 14.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (lastUsedProvider == "github") {
                                LastUsedBadge()
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = "Dev choice",
                                color = AppSage,
                                fontSize = 11.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Continue with Google: white pill, 4-color G
            Button(
                onClick = { onProviderClick(AuthProvider.GOOGLE) },
                enabled = !isLoading,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppWhite,
                    contentColor = AppBackground,
                    disabledContainerColor = AppWhite.copy(alpha = 0.5f),
                    disabledContentColor = AppBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("signin_google_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GoogleMark(size = 20.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            color = AppBackground,
                            fontSize = 14.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (lastUsedProvider == "google") {
                        LastUsedBadge()
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Divider "or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = AppCardNav)
                Text(
                    text = "or",
                    color = AppWhite.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = AppCardNav)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Email field + "Email me a sign-in link" (magic link, no password)
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = onEmailChange,
                    placeholder = {
                        Text(
                            text = "developer@company.com",
                            color = AppWhite.copy(alpha = 0.4f),
                            fontSize = 14.sp,
                            fontFamily = ArchivoFontFamily
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Send,
                        autoCorrectEnabled = false
                    ),
                    keyboardActions = KeyboardActions(onSend = { onSendMagicLink() }),
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppCardNav,
                        unfocusedContainerColor = AppCardNav,
                        disabledContainerColor = AppCardNav.copy(alpha = 0.5f),
                        focusedBorderColor = AppYellow,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = AppWhite,
                        unfocusedTextColor = AppWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("email_magic_link_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onSendMagicLink,
                    enabled = !isLoading && emailInput.isNotBlank(),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppSage,
                        contentColor = AppBackground,
                        disabledContainerColor = AppSage.copy(alpha = 0.3f),
                        disabledContentColor = AppBackground.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("send_magic_link_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = AppBackground, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = "Email me a sign-in link",
                            fontSize = 14.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // 5. Apple: build the button but hide it behind const ENABLE_APPLE = false
            if (ENABLE_APPLE) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onProviderClick(AuthProvider.APPLE) },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = AppWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppleMark(size = 20.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Apple",
                                fontSize = 14.sp,
                                fontFamily = ArchivoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (lastUsedProvider == "apple") {
                            LastUsedBadge()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. "Continue without account": guest mode with 5 free searches stored locally
            TextButton(
                onClick = onContinueGuest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("continue_without_account_button")
            ) {
                Text(
                    text = "Continue without account",
                    color = AppWhite.copy(alpha = 0.75f),
                    fontSize = 13.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Trust Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AppSage, modifier = Modifier.size(16.dp))
            Text(
                text = "No credit card, cancel anytime",
                color = AppWhite.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontFamily = ArchivoFontFamily,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Footer
        Text(
            text = "By continuing you agree to the Terms and Privacy Policy.",
            color = AppWhite.copy(alpha = 0.45f),
            fontSize = 11.sp,
            fontFamily = ArchivoFontFamily,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LastUsedBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AppYellow)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "LAST USED",
            color = AppBackground,
            fontSize = 9.sp,
            fontFamily = ArchivoFontFamily,
            fontWeight = FontWeight.Black
        )
    }
}

/**
 * Desktop Right Panel:
 * 3 tilted stacked cards (coral, yellow, sage) showing a sample match ("Weather API 95%", "Up, 120 ms")
 * with a slow float animation.
 */
@Composable
private fun DesktopTiltedCardsPreview() {
    val infiniteTransition = rememberInfiniteTransition(label = "desktop_float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = floatY.dp),
        contentAlignment = Alignment.Center
    ) {
        // Bottom Sage Card (tilted -8 deg)
        Card(
            modifier = Modifier
                .width(360.dp)
                .rotate(-8f)
                .offset(x = (-20).dp, y = 30.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppSage)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("STRIPE PAYMENTS", color = AppBackground, fontSize = 16.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                Text("Financial infrastructure with instant webhook simulator", color = AppBackground.copy(alpha = 0.8f), fontSize = 12.sp, fontFamily = ArchivoFontFamily)
            }
        }

        // Middle Coral Card (tilted 6 deg)
        Card(
            modifier = Modifier
                .width(360.dp)
                .rotate(6f)
                .offset(x = 24.dp, y = (-15).dp)
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppCoral)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Text("GITHUB REST & GRAPHQL", color = AppBackground, fontSize = 16.sp, fontFamily = ArchivoFontFamily, fontWeight = FontWeight.Black)
                Text("99.98% uptime · 45 ms ping · Zero setup starter kit", color = AppBackground.copy(alpha = 0.8f), fontSize = 12.sp, fontFamily = ArchivoFontFamily)
            }
        }

        // Top Yellow Card (tilted -2 deg) featuring sample match requested: "Weather API 95%", "Up, 120 ms"
        Card(
            modifier = Modifier
                .width(370.dp)
                .rotate(-2f)
                .shadow(24.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = AppYellow)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "WEATHER API 95%",
                            color = AppYellow,
                            fontSize = 12.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "UP · 120 MS",
                            color = AppSage,
                            fontSize = 11.sp,
                            fontFamily = ArchivoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Open-Meteo Weather Forecast",
                    color = AppBackground,
                    fontSize = 20.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Free weather forecasts, historical data, and climate APIs without requiring an API key. 10,000 req/day.",
                    color = AppBackground.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontFamily = ArchivoFontFamily,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant TypeScript & Python code ready",
                        color = AppBackground.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontFamily = ArchivoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = null,
                        tint = AppBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Native Canvas GitHub Octocat mark
 */
@Composable
private fun GitHubMark(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.width / 24f
        val path = Path().apply {
            moveTo(12f * s, 1.75f * s)
            cubicTo(6.34f * s, 1.75f * s, 1.75f * s, 6.34f * s, 1.75f * s, 12f * s)
            cubicTo(1.75f * s, 16.53f * s, 4.69f * s, 20.36f * s, 8.75f * s, 21.71f * s)
            cubicTo(9.26f * s, 21.81f * s, 9.45f * s, 21.49f * s, 9.45f * s, 21.22f * s)
            lineTo(9.45f * s, 19.49f * s)
            cubicTo(6.59f * s, 20.11f * s, 6f * s, 18.11f * s, 6f * s, 18.11f * s)
            cubicTo(5.53f * s, 16.92f * s, 4.86f * s, 16.6f * s, 4.86f * s, 16.6f * s)
            cubicTo(3.93f * s, 15.96f * s, 4.93f * s, 15.97f * s, 4.93f * s, 15.97f * s)
            cubicTo(5.96f * s, 16.04f * s, 6.5f * s, 17.03f * s, 6.5f * s, 17.03f * s)
            cubicTo(7.41f * s, 18.6f * s, 8.89f * s, 18.14f * s, 9.48f * s, 17.88f * s)
            cubicTo(9.57f * s, 17.22f * s, 9.84f * s, 16.77f * s, 10.13f * s, 16.51f * s)
            cubicTo(7.85f * s, 16.25f * s, 5.45f * s, 15.37f * s, 5.45f * s, 11.45f * s)
            cubicTo(5.45f * s, 10.33f * s, 5.85f * s, 9.42f * s, 6.5f * s, 8.7f * s)
            cubicTo(6.4f * s, 8.44f * s, 6.05f * s, 7.4f * s, 6.6f * s, 6f * s)
            cubicTo(6.6f * s, 6f * s, 7.46f * s, 5.72f * s, 9.42f * s, 7.05f * s)
            cubicTo(10.24f * s, 6.82f * s, 11.12f * s, 6.71f * s, 12f * s, 6.71f * s)
            cubicTo(12.88f * s, 6.71f * s, 13.76f * s, 6.82f * s, 14.58f * s, 7.05f * s)
            cubicTo(16.54f * s, 5.72f * s, 17.4f * s, 6f * s, 17.4f * s, 6f * s)
            cubicTo(17.95f * s, 7.4f * s, 17.6f * s, 8.44f * s, 17.5f * s, 8.7f * s)
            cubicTo(18.15f * s, 9.42f * s, 18.55f * s, 10.33f * s, 18.55f * s, 11.45f * s)
            cubicTo(18.55f * s, 15.38f * s, 16.14f * s, 16.25f * s, 13.85f * s, 16.5f * s)
            cubicTo(14.22f * s, 16.82f * s, 14.55f * s, 17.44f * s, 14.55f * s, 18.4f * s)
            lineTo(14.55f * s, 21.22f * s)
            cubicTo(14.55f * s, 21.49f * s, 14.73f * s, 21.82f * s, 15.26f * s, 21.71f * s)
            cubicTo(19.31f * s, 20.35f * s, 22.25f * s, 16.53f * s, 22.25f * s, 12f * s)
            cubicTo(22.25f * s, 6.34f * s, 17.66f * s, 1.75f * s, 12f * s, 1.75f * s)
            close()
        }
        drawPath(path = path, color = Color.White)
    }
}

/**
 * Native Canvas Google 4-Color G mark
 */
@Composable
private fun GoogleMark(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.width / 24f

        // Blue bar & right curve
        val bluePath = Path().apply {
            moveTo(23.75f * s, 12.27f * s)
            cubicTo(23.75f * s, 11.48f * s, 23.68f * s, 10.73f * s, 23.55f * s, 10.01f * s)
            lineTo(12f * s, 10.01f * s)
            lineTo(12f * s, 14.73f * s)
            lineTo(18.59f * s, 14.73f * s)
            cubicTo(18.31f * s, 16.24f * s, 17.46f * s, 17.52f * s, 16.18f * s, 18.38f * s)
            lineTo(16.18f * s, 21.42f * s)
            lineTo(20.08f * s, 21.42f * s)
            cubicTo(22.36f * s, 19.32f * s, 23.75f * s, 16.13f * s, 23.75f * s, 12.27f * s)
            close()
        }
        drawPath(path = bluePath, color = Color(0xFF4285F4))

        // Green bottom curve
        val greenPath = Path().apply {
            moveTo(12f * s, 24f * s)
            cubicTo(15.24f * s, 24f * s, 17.96f * s, 22.92f * s, 20.08f * s, 20.97f * s)
            lineTo(16.18f * s, 17.93f * s)
            cubicTo(15.1f * s, 18.66f * s, 13.67f * s, 19.13f * s, 12f * s, 19.13f * s)
            cubicTo(8.87f * s, 19.13f * s, 6.22f * s, 17.01f * s, 5.27f * s, 14.16f * s)
            lineTo(1.23f * s, 14.16f * s)
            lineTo(1.23f * s, 17.29f * s)
            cubicTo(3.29f * s, 21.39f * s, 7.36f * s, 24f * s, 12f * s, 24f * s)
            close()
        }
        drawPath(path = greenPath, color = Color(0xFF34A853))

        // Yellow left curve
        val yellowPath = Path().apply {
            moveTo(5.27f * s, 14.16f * s)
            cubicTo(5.03f * s, 13.44f * s, 4.9f * s, 12.67f * s, 4.9f * s, 11.88f * s)
            cubicTo(4.9f * s, 11.08f * s, 5.03f * s, 10.31f * s, 5.27f * s, 9.59f * s)
            lineTo(5.27f * s, 6.46f * s)
            lineTo(1.23f * s, 6.46f * s)
            cubicTo(0.44f * s, 8.03f * s, 0f * s, 9.8f * s, 0f * s, 11.88f * s)
            cubicTo(0f * s, 13.95f * s, 0.44f * s, 15.72f * s, 1.23f * s, 17.29f * s)
            lineTo(5.27f * s, 14.16f * s)
            close()
        }
        drawPath(path = yellowPath, color = Color(0xFFFBBC05))

        // Red top curve
        val redPath = Path().apply {
            moveTo(12f * s, 4.63f * s)
            cubicTo(13.76f * s, 4.63f * s, 15.34f * s, 5.23f * s, 16.58f * s, 6.42f * s)
            lineTo(20.17f * s, 2.83f * s)
            cubicTo(18.04f * s, 0.84f * s, 15.24f * s, 0f * s, 12f * s, 0f * s)
            cubicTo(7.36f * s, 0f * s, 3.29f * s, 2.61f * s, 1.23f * s, 6.46f * s)
            lineTo(5.27f * s, 9.59f * s)
            cubicTo(6.22f * s, 6.74f * s, 8.87f * s, 4.63f * s, 12f * s, 4.63f * s)
            close()
        }
        drawPath(path = redPath, color = Color(0xFFEA4335))
    }
}

/**
 * Native Canvas Apple mark
 */
@Composable
private fun AppleMark(size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.width / 24f
        val path = Path().apply {
            moveTo(18.71f * s, 19.5f * s)
            cubicTo(17.88f * s, 20.74f * s, 17.0f * s, 21.95f * s, 15.66f * s, 21.97f * s)
            cubicTo(14.32f * s, 22.0f * s, 13.88f * s, 21.18f * s, 12.35f * s, 21.18f * s)
            cubicTo(10.82f * s, 21.18f * s, 10.34f * s, 21.95f * s, 9.05f * s, 22.0f * s)
            cubicTo(7.74f * s, 22.05f * s, 6.84f * s, 20.69f * s, 6.0f * s, 19.47f * s)
            cubicTo(4.29f * s, 17.0f * s, 3.0f * s, 13.06f * s, 4.77f * s, 9.98f * s)
            cubicTo(5.65f * s, 8.46f * s, 7.22f * s, 7.5f * s, 8.92f * s, 7.47f * s)
            cubicTo(10.22f * s, 7.45f * s, 11.45f * s, 8.35f * s, 12.25f * s, 8.35f * s)
            cubicTo(13.04f * s, 8.35f * s, 14.52f * s, 7.26f * s, 16.08f * s, 7.43f * s)
            cubicTo(16.74f * s, 7.46f * s, 18.57f * s, 7.69f * s, 19.74f * s, 9.41f * s)
            cubicTo(19.64f * s, 9.47f * s, 17.55f * s, 10.69f * s, 17.57f * s, 13.22f * s)
            cubicTo(17.6f * s, 16.25f * s, 20.24f * s, 17.26f * s, 20.27f * s, 17.27f * s)
            cubicTo(20.24f * s, 17.37f * s, 19.83f * s, 18.79f * s, 18.71f * s, 19.5f * s)
            close()
            moveTo(15.1f * s, 5.25f * s)
            cubicTo(15.77f * s, 4.43f * s, 16.22f * s, 3.28f * s, 16.1f * s, 2.12f * s)
            cubicTo(15.11f * s, 2.16f * s, 13.91f * s, 2.78f * s, 13.2f * s, 3.61f * s)
            cubicTo(12.58f * s, 4.33f * s, 12.03f * s, 5.51f * s, 12.18f * s, 6.64f * s)
            cubicTo(13.29f * s, 6.72f * s, 14.43f * s, 6.07f * s, 15.1f * s, 5.25f * s)
            close()
        }
        drawPath(path = path, color = Color.White)
    }
}
