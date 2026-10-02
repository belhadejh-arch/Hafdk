package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.AppState
import com.example.data.NavigationScreen
import com.example.ui.theme.Sky300
import com.example.ui.theme.Sky400
import com.example.ui.theme.Sky500
import com.example.ui.theme.Sky600
import com.example.ui.theme.Sky700
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onNavigate: (NavigationScreen) -> Unit,
    onOpenAuth: (isRegister: Boolean) -> Unit
) {
    val context = LocalContext.current
    val isArabic = AppState.language == AppLanguage.ARABIC

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // HERO SECTION
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Sky500.copy(alpha = 0.3f)
            ),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Sky600.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Badge
                Surface(
                    color = Sky500.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Sky400.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "الإصدار الأحدث v4.8.2 متاح الآن" else "Latest Release v4.8.2 Online",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Sky400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = if (isArabic)
                        "Haafedk iCloud Premium — الحل الأقوى لتخطي iCloud"
                    else
                        "Haafedk iCloud Premium — The Ultimate iCloud Bypass Solution",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = if (isArabic)
                        "أداة احترافية لتخطي قفل iCloud على iPhone و iPad — سريعة، آمنة، ومدعومة بفريق محترف"
                    else
                        "Professional tool to bypass iCloud lock on iPhone & iPad — Fast, secure, and backed by a dedicated expert team",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // CTAs
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            AppState.toastMessage = if (isArabic) "جارٍ بدء تحميل نسخة Windows..." else "Starting Windows download..."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Sky600),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "تحميل الأداة الآن" else "Download Tool Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { onNavigate(NavigationScreen.PRICING) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(44.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Sky500)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = Sky500, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "عرض الأسعار" else "View Pricing",
                            fontWeight = FontWeight.Bold,
                            color = Sky500,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Direct WhatsApp Button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppState.supportWhatsAppLink))
                            context.startActivity(intent)
                        },
                    color = SuccessGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isArabic) "تواصل مع الدعم مباشرة واتساب" else "Direct WhatsApp Support",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Text(
                                text = AppState.supportWhatsApp,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // STATS CARDS
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(number = "+5,000", label = if (isArabic) "عميل سعيد" else "Happy Clients", icon = Icons.Default.ThumbUp, modifier = Modifier.weight(1f))
            StatCard(number = "99.8%", label = if (isArabic) "معدل النجاح" else "Success Rate", icon = Icons.Default.VerifiedUser, modifier = Modifier.weight(1f))
            StatCard(number = "24/7", label = if (isArabic) "دعم فني" else "Live Support", icon = Icons.Default.HeadsetMic, modifier = Modifier.weight(1f))
            StatCard(number = "A12+", label = if (isArabic) "أجهزة مدعومة" else "Supported Chips", icon = Icons.Default.Bolt, modifier = Modifier.weight(1f))
        }

        // HIGHLIGHT BADGES
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HighlightBadge(
                icon = Icons.Default.Payment,
                title = if (isArabic) "دفع تلقائي" else "Auto Payment",
                subtitle = if (isArabic) "باي بال · USDT · عملات رقمية · بريدي موب" else "PayPal · USDT · Crypto · BaridiMob"
            )
            HighlightBadge(
                icon = Icons.Default.SupportAgent,
                title = if (isArabic) "دعم فني مستمر 24/7" else "24/7 Continuous Support",
                subtitle = if (isArabic) "دائماً متاح — تيليجرام وتذاكر وواتساب" else "Always Available — Telegram, Tickets & WhatsApp"
            )
            HighlightBadge(
                icon = Icons.Default.Autorenew,
                title = if (isArabic) "تحديثات دورية" else "Regular Updates",
                subtitle = if (isArabic) "أحدث الإصدارات وأجهزة جديدة باستمرار" else "Latest iOS versions & new devices added regularly"
            )
        }

        // FEATURES SECTION
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isArabic) "المميزات: كل ما تحتاجه في أداة واحدة" else "Features: Everything in One Tool",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isArabic)
                    "أداة احترافية بمميزات متقدمة لتخطي iCloud بكل سهولة وأمان"
                else
                    "Professional tool with advanced capabilities to bypass iCloud smoothly and securely",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val features = listOf(
                Pair(
                    if (isArabic) "تخطي iCloud لأجهزة iPhone و iPad" else "iCloud Bypass for iPhone & iPad",
                    Icons.Default.LockOpen
                ),
                Pair(
                    if (isArabic) "يدعم أحدث إصدارات iOS" else "Supports Latest iOS Versions",
                    Icons.Default.Speed
                ),
                Pair(
                    if (isArabic) "بدون فقد بيانات الجهاز" else "No Device Data Loss",
                    Icons.Default.Storage
                ),
                Pair(
                    if (isArabic) "تحديثات دورية مجانية" else "Free Regular Updates",
                    Icons.Default.Autorenew
                ),
                Pair(
                    if (isArabic) "دعم فني سريع 24/7" else "Fast 24/7 Technical Support",
                    Icons.Default.HeadsetMic
                ),
                Pair(
                    if (isArabic) "واجهة سهلة الاستخدام" else "User-Friendly Interface",
                    Icons.Default.Security
                )
            )

            features.forEach { (title, icon) ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Sky500.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = Sky500, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isArabic)
                                    "دعم احترافي ومضمون مع تحديثات دورية لأحدث إصدارات iOS"
                                else
                                    "Professional verified support with continuous updates for newest iOS releases",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // SUPPORTED DEVICES QUICK TEASER
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Sky500.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isArabic) "الأجهزة المدعومة (131 جهاز)" else "Supported Devices (131 Total)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isArabic) "توافق واسع: من iPhone 5s حتى iPhone 17 Pro Max" else "Wide compatibility from iPhone 5s to 17 Pro Max",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { onNavigate(NavigationScreen.DEVICES) },
                        colors = ButtonDefaults.buttonColors(containerColor = Sky600),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isArabic) "عرض الكل" else "View All", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DeviceCountChip(label = if (isArabic) "56 آيفون" else "56 iPhone", modifier = Modifier.weight(1f))
                    DeviceCountChip(label = if (isArabic) "72 آيباد" else "72 iPad", modifier = Modifier.weight(1f))
                    DeviceCountChip(label = if (isArabic) "3 آيبود" else "3 iPod", modifier = Modifier.weight(1f))
                }
            }
        }

        // PATCHER SECTION
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarningOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "Haafedk Patcher — أداة باتشر احترافية" else "Haafedk Patcher — Pro Patcher Tool",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isArabic) "مجانية لجميع أعضاء Haafedk Premium المفعّلين" else "Free for all active Haafedk Premium members",
                            fontSize = 11.sp,
                            color = WarningOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isArabic)
                        "Haafedk Patcher هو أداة احترافية لتطبيق الباتشات على ملفات النظام الخاصة بأجهزة iPhone و iPad. يتيح لك الأداة العمل على الملفات المختلفة بكل سهولة وأمان بدون جيلبريك."
                    else
                        "Haafedk Patcher is a specialized utility to apply system file patches for iPhone and iPad without Jailbreak.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppState.supportTelegram))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Sky600),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isArabic) "احصل عليه من تيليجرام" else "Get via Telegram", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { onNavigate(NavigationScreen.PATCHER) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Sky500)
                    ) {
                        Text(if (isArabic) "تفاصيل الباتشر" else "More Details", fontSize = 11.sp, color = Sky500)
                    }
                }
            }
        }

        // PRICING PREVIEW
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (isArabic) "الأسعار: اختر الخطة المناسبة" else "Pricing: Choose Your Plan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 6 Months Plan
                PlanCard(
                    title = if (isArabic) "6 أشهر" else "6 Months",
                    duration = if (isArabic) "180 يوم (6 أشهر)" else "180 Days (6 Months)",
                    price = "8000 دج",
                    isFeatured = false,
                    modifier = Modifier.weight(1f),
                    onSubscribe = { onOpenAuth(true) }
                )

                // 1 Year Plan
                PlanCard(
                    title = if (isArabic) "سنة" else "1 year",
                    duration = if (isArabic) "365 يوم (سنة كاملة)" else "365 Days (Full Year)",
                    price = "15000 دج",
                    isFeatured = true,
                    modifier = Modifier.weight(1f),
                    onSubscribe = { onOpenAuth(true) }
                )
            }
        }

        // CALL TO ACTION FOOTER
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Sky600.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Sky500.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isArabic) "ابدأ الآن — استمتع بأقوى أداة لتخطي iCloud" else "Get Started Now — Unlock with Ease",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isArabic) "سجّل حساب جديد وفعّل اشتراكك خلال دقائق" else "Create your account and activate within minutes",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onOpenAuth(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = Sky600),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isArabic) "إنشاء حساب" else "Register", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onOpenAuth(false) },
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Sky500)
                    ) {
                        Text(if (isArabic) "تسجيل دخول" else "Login", color = Sky500, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun StatCard(
    number: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Sky500.copy(alpha = 0.2f)),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Sky500, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = number, fontSize = 16.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HighlightBadge(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Sky500, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DeviceCountChip(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Sky500.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Sky500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

@Composable
private fun PlanCard(
    title: String,
    duration: String,
    price: String,
    isFeatured: Boolean,
    modifier: Modifier = Modifier,
    onSubscribe: () -> Unit
) {
    val isArabic = AppState.language == AppLanguage.ARABIC

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            if (isFeatured) 2.dp else 1.dp,
            if (isFeatured) Sky500 else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        tonalElevation = if (isFeatured) 6.dp else 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isFeatured) {
                Surface(
                    color = Sky500,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = if (isArabic) "الأكثر طلباً" else "BEST VALUE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Sky500)
            Text(text = price, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = duration, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onSubscribe,
                colors = ButtonDefaults.buttonColors(containerColor = if (isFeatured) Sky600 else MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Text(
                    text = if (isArabic) "اشترك الآن" else "Subscribe",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFeatured) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
