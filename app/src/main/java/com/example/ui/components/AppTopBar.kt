package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AccountStatus
import com.example.data.AppLanguage
import com.example.data.AppState
import com.example.data.NavigationScreen
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.Sky400
import com.example.ui.theme.Sky500
import com.example.ui.theme.Sky600
import com.example.ui.theme.Sky700
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@Composable
fun AppTopBar(
    onNavigate: (NavigationScreen) -> Unit,
    onOpenAuth: (isRegister: Boolean) -> Unit
) {
    val context = LocalContext.current
    val isArabic = AppState.language == AppLanguage.ARABIC

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Main Top Branding Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigate(NavigationScreen.HOME) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Sky500, Sky700)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "حفضك",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = Sky500
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Haafedk",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "iCloud Premium",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Controls & WhatsApp Support
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // WhatsApp Support Direct Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://wa.me/213774148015")
                                )
                                context.startActivity(intent)
                            },
                        color = SuccessGreen.copy(alpha = 0.15f),
                        contentColor = SuccessGreen
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "WhatsApp",
                                tint = SuccessGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "0774148015",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }

                    // Language Switcher
                    IconButton(
                        onClick = {
                            AppState.language = if (isArabic) AppLanguage.ENGLISH else AppLanguage.ARABIC
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isArabic) "EN" else "عربي",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Dark / Light Mode Switcher
                    IconButton(
                        onClick = { AppState.isDarkMode = !AppState.isDarkMode },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (AppState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme",
                            tint = Sky400,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Auth / Profile Button
                    val user = AppState.currentUser
                    if (user != null) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onNavigate(NavigationScreen.USER_DASHBOARD) },
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (user.status) {
                                                AccountStatus.ACTIVE -> SuccessGreen
                                                AccountStatus.PENDING -> WarningOrange
                                                AccountStatus.INACTIVE -> ErrorRed
                                                AccountStatus.SUSPENDED -> ErrorRed
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = user.username.take(9),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { onOpenAuth(false) },
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Sky600)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "دخول" else "Login",
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Navigation Bar (Horizontal scrollable tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavTabItem(
                    title = if (isArabic) "الرئيسية" else "Home",
                    icon = Icons.Default.Home,
                    isSelected = AppState.currentScreen == NavigationScreen.HOME,
                    onClick = { onNavigate(NavigationScreen.HOME) }
                )
                NavTabItem(
                    title = if (isArabic) "الأجهزة المدعومة" else "Supported Devices",
                    icon = Icons.Default.Devices,
                    isSelected = AppState.currentScreen == NavigationScreen.DEVICES,
                    onClick = { onNavigate(NavigationScreen.DEVICES) }
                )
                NavTabItem(
                    title = if (isArabic) "الباتشر" else "Patcher",
                    icon = Icons.Default.VpnKey,
                    isSelected = AppState.currentScreen == NavigationScreen.PATCHER,
                    onClick = { onNavigate(NavigationScreen.PATCHER) }
                )
                NavTabItem(
                    title = if (isArabic) "الأسعار" else "Pricing",
                    icon = Icons.Default.PriceCheck,
                    isSelected = AppState.currentScreen == NavigationScreen.PRICING,
                    onClick = { onNavigate(NavigationScreen.PRICING) }
                )
                NavTabItem(
                    title = if (isArabic) "لوحة التحكم" else "Dashboard",
                    icon = Icons.Default.Person,
                    isSelected = AppState.currentScreen == NavigationScreen.USER_DASHBOARD,
                    onClick = { onNavigate(NavigationScreen.USER_DASHBOARD) }
                )
                NavTabItem(
                    title = if (isArabic) "لوحة الأدمن" else "Admin Panel",
                    icon = Icons.Default.AdminPanelSettings,
                    isSelected = AppState.currentScreen == NavigationScreen.ADMIN_PANEL,
                    isSpecial = true,
                    onClick = { onNavigate(NavigationScreen.ADMIN_PANEL) }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    isSpecial: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = when {
            isSelected -> Sky500.copy(alpha = 0.2f)
            isSpecial -> WarningOrange.copy(alpha = 0.12f)
            else -> Color.Transparent
        },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = when {
                    isSelected -> Sky500
                    isSpecial -> WarningOrange
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected || isSpecial) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isSelected -> Sky500
                    isSpecial -> WarningOrange
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
