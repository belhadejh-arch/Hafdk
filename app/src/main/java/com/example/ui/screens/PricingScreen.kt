package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.AppState
import com.example.data.NavigationScreen
import com.example.ui.theme.Sky400
import com.example.ui.theme.Sky500
import com.example.ui.theme.Sky600
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@Composable
fun PricingScreen(
    onNavigate: (NavigationScreen) -> Unit,
    onOpenAuth: (isRegister: Boolean) -> Unit
) {
    val isArabic = AppState.language == AppLanguage.ARABIC

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isArabic) "الأسعار: اختر الخطة المناسبة" else "Pricing: Choose the Right Plan",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isArabic)
                    "اشتراكات مرنة بأسعار تنافسية مع دعم كامل لجميع الأجهزة المدعومة"
                else
                    "Flexible plans with competitive pricing and full support across all devices",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Plan 1: MONTHLY 6
        PricingPlanCard(
            planCode = "MONTHLY 6",
            title = if (isArabic) "MONTHLY 6 (6 أشهر)" else "MONTHLY 6 (6 Months)",
            price = "8000 دج",
            duration = if (isArabic) "180 يوم" else "180 Days",
            featuresAr = listOf(
                "دعم كامل لكل مستخدم طوال فترة الاشتراك",
                "إنشاء ملف خاص لجهازك بشكل مخصص",
                "إمكانية تغيير الكمبيوتر كل ساعة إلى جهاز مختلف"
            ),
            featuresEn = listOf(
                "Full support for every user throughout the subscription period",
                "Custom private profile setup for your device",
                "Ability to change the computer every hour to a different device"
            ),
            isFeatured = false,
            onSelect = {
                if (AppState.currentUser == null) {
                    onOpenAuth(true)
                } else {
                    AppState.currentUser?.subscriptionPlan = "MONTHLY 6"
                    onNavigate(NavigationScreen.USER_DASHBOARD)
                }
            }
        )

        // Plan 2: YEAR
        PricingPlanCard(
            planCode = "YEAR",
            title = if (isArabic) "YEAR (سنة كاملة)" else "YEAR (Full Year)",
            price = "15000 دج",
            duration = if (isArabic) "365 يوم" else "365 Days",
            featuresAr = listOf(
                "دعم كامل لكل مستخدم طوال فترة الاشتراك",
                "إنشاء ملف خاص لجهازك بشكل مخصص",
                "إمكانية تغيير الكمبيوتر كل ساعة إلى جهاز مختلف"
            ),
            featuresEn = listOf(
                "Full support for every user throughout the subscription period",
                "Custom private profile setup for your device",
                "Ability to change the computer every hour to a different device"
            ),
            isFeatured = true,
            onSelect = {
                if (AppState.currentUser == null) {
                    onOpenAuth(true)
                } else {
                    AppState.currentUser?.subscriptionPlan = "YEAR"
                    onNavigate(NavigationScreen.USER_DASHBOARD)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PricingPlanCard(
    planCode: String,
    title: String,
    price: String,
    duration: String,
    featuresAr: List<String>,
    featuresEn: List<String>,
    isFeatured: Boolean,
    onSelect: () -> Unit
) {
    val isArabic = AppState.language == AppLanguage.ARABIC

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            if (isFeatured) 2.dp else 1.dp,
            if (isFeatured) Sky500 else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        tonalElevation = if (isFeatured) 6.dp else 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (isFeatured) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Sky500,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "الأكثر توفيراً وشعبية" else "MOST POPULAR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Sky500
            )

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = price,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "/ $duration",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feature list
            featuresAr.forEachIndexed { index, featAr ->
                val featEn = featuresEn.getOrNull(index) ?: ""
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = featAr,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = featEn,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFeatured) Sky600 else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    text = if (isArabic) "اشترك الآن ($price)" else "Subscribe Now ($price)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFeatured) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
