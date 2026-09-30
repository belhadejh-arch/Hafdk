package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.AppState
import com.example.data.NavigationScreen
import com.example.ui.components.AppTopBar
import com.example.ui.components.AuthDialog
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PatcherScreen
import com.example.ui.screens.PricingScreen
import com.example.ui.screens.SupportedDevicesScreen
import com.example.ui.screens.UserDashboardScreen
import com.example.ui.theme.HaafedkTheme
import com.example.ui.theme.Sky500
import com.example.ui.theme.Sky600
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isArabic = AppState.language == AppLanguage.ARABIC
            val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

            LaunchedEffect(AppState.toastMessage) {
                if (AppState.toastMessage != null) {
                    delay(3500)
                    AppState.toastMessage = null
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                HaafedkTheme(darkTheme = AppState.isDarkMode) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            AppTopBar(
                                onNavigate = { screen ->
                                    AppState.currentScreen = screen
                                },
                                onOpenAuth = { isRegister ->
                                    AppState.authDefaultTabRegister = isRegister
                                    AppState.showAuthModal = true
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            // Active Screen Switcher
                            when (AppState.currentScreen) {
                                NavigationScreen.HOME -> HomeScreen(
                                    onNavigate = { AppState.currentScreen = it },
                                    onOpenAuth = { isRegister ->
                                        AppState.authDefaultTabRegister = isRegister
                                        AppState.showAuthModal = true
                                    }
                                )
                                NavigationScreen.DEVICES -> SupportedDevicesScreen()
                                NavigationScreen.PATCHER -> PatcherScreen(
                                    onNavigate = { AppState.currentScreen = it }
                                )
                                NavigationScreen.PRICING -> PricingScreen(
                                    onNavigate = { AppState.currentScreen = it },
                                    onOpenAuth = { isRegister ->
                                        AppState.authDefaultTabRegister = isRegister
                                        AppState.showAuthModal = true
                                    }
                                )
                                NavigationScreen.USER_DASHBOARD -> UserDashboardScreen(
                                    onNavigate = { AppState.currentScreen = it },
                                    onOpenAuth = { isRegister ->
                                        AppState.authDefaultTabRegister = isRegister
                                        AppState.showAuthModal = true
                                    }
                                )
                                NavigationScreen.ADMIN_PANEL -> AdminPanelScreen(
                                    onNavigate = { AppState.currentScreen = it }
                                )
                            }

                            // Notification Toast Overlay
                            AnimatedVisibility(
                                visible = AppState.toastMessage != null,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 24.dp)
                            ) {
                                Surface(
                                    color = Sky600,
                                    shape = RoundedCornerShape(12.dp),
                                    shadowElevation = 6.dp
                                ) {
                                    Text(
                                        text = AppState.toastMessage ?: "",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        // Auth Modal Dialog
                        if (AppState.showAuthModal) {
                            AuthDialog(
                                initialTabRegister = AppState.authDefaultTabRegister,
                                onDismiss = { AppState.showAuthModal = false },
                                onSuccess = { AppState.showAuthModal = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
