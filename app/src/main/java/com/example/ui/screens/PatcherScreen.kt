package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.AppState
import com.example.data.NavigationScreen
import com.example.ui.theme.Sky500
import com.example.ui.theme.Sky600
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@Composable
fun PatcherScreen(
    onNavigate: (NavigationScreen) -> Unit
) {
    val context = LocalContext.current
    val isArabic = AppState.language == AppLanguage.ARABIC

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // HEADER CARD
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f)),
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarningOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isArabic) "Haafedk Patcher — أداة باتشر احترافية" else "Haafedk Patcher — Pro Patcher",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isArabic)
                                "أداة باتشر متكاملة — مجانية لجميع أعضاء Haafedk Premium المفعّلين"
                            else
                                "All-in-one patcher — 100% Free for active Haafedk Premium subscribers",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarningOrange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isArabic)
                        "Haafedk Patcher هو أداة احترافية لتطبيق الباتشات على ملفات النظام الخاصة بأجهزة iPhone و iPad. يتيح لك الأداة العمل على الملفات المختلفة بكل سهولة وأمان.\n\nالأداة مدمجة مع نظام Haafedk Premium ومجانية للأعضاء المفعّلين."
                    else
                        "Haafedk Patcher is an advanced tool for applying patches to system files on iPhone and iPad. It allows you to modify system files seamlessly and securely.\n\nThe tool is natively bundled with Haafedk Premium and free for active accounts.",
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isArabic) "احصل عليه من تيليجرام" else "Get via Telegram", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            AppState.toastMessage = if (isArabic) "جارٍ بدء تحميل حزمة الباتشر..." else "Starting Patcher download..."
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Sky500)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Sky500, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isArabic) "تحميل مباشر" else "Direct Download", fontSize = 12.sp, color = Sky500)
                    }
                }
            }
        }

        // HIGHLIGHTS CHECKLIST
        Text(
            text = if (isArabic) "مميزات باتشر حفضك:" else "Haafedk Patcher Highlights:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        val highlights = listOf(
            Pair(if (isArabic) "يعمل على جميع الأجهزة المدعومة" else "Works on all supported devices", Icons.Default.CheckCircle),
            Pair(if (isArabic) "لا يحتاج إلى Jailbreak" else "Does not require Jailbreak", Icons.Default.Shield),
            Pair(if (isArabic) "نتائج فورية وسريعة" else "Instant & ultra-fast execution", Icons.Default.Bolt),
            Pair(if (isArabic) "آمن 100% — بدون فقد بيانات" else "100% Secure — Zero data loss", Icons.Default.Security),
            Pair(if (isArabic) "تحديثات دورية مجانية" else "Free continuous software updates", Icons.Default.Sync),
            Pair(if (isArabic) "دعم فني متواصل" else "Continuous expert live support", Icons.Default.HeadsetMic)
        )

        highlights.forEach { (text, icon) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // ACTIVATION CTA
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isArabic) "حافظ باتشر — مجاني للأعضاء المفعّلين" else "Haafedk Patcher — Free for Active Members",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic) "فعّل حسابك الآن للحصول على ترخيص الباتشر التلقائي" else "Activate your account to receive your patcher license key",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onNavigate(NavigationScreen.USER_DASHBOARD) },
                    colors = ButtonDefaults.buttonColors(containerColor = Sky600),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isArabic) "تفعيل حسابي الآن" else "Activate My Account")
                }
            }
        }
    }
}
