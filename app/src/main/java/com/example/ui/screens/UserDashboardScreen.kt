package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AccountStatus
import com.example.data.AppLanguage
import com.example.data.AppState
import com.example.data.NavigationScreen
import com.example.data.UserAccount
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.Sky400
import com.example.ui.theme.Sky500
import com.example.ui.theme.Sky600
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@Composable
fun UserDashboardScreen(
    onNavigate: (NavigationScreen) -> Unit,
    onOpenAuth: (isRegister: Boolean) -> Unit
) {
    val context = LocalContext.current
    val isArabic = AppState.language == AppLanguage.ARABIC
    val currentUser = AppState.currentUser

    if (currentUser == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Sky500, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isArabic) "يرجى تسجيل الدخول أولاً" else "Please login first",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(onClick = { onOpenAuth(false) }) {
                Text(if (isArabic) "تسجيل الدخول" else "Login")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PROFILE HEADER & STATUS BADGE
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when (currentUser.status) {
                    AccountStatus.ACTIVE -> SuccessGreen.copy(alpha = 0.5f)
                    AccountStatus.PENDING -> WarningOrange.copy(alpha = 0.5f)
                    AccountStatus.INACTIVE -> ErrorRed.copy(alpha = 0.5f)
                    AccountStatus.SUSPENDED -> ErrorRed.copy(alpha = 0.5f)
                }
            ),
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Sky500.copy(alpha = 0.15f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Sky500, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentUser.username,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentUser.email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Bold Status Indicator
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = when (currentUser.status) {
                            AccountStatus.ACTIVE -> SuccessGreen.copy(alpha = 0.15f)
                            AccountStatus.PENDING -> WarningOrange.copy(alpha = 0.15f)
                            AccountStatus.INACTIVE -> ErrorRed.copy(alpha = 0.15f)
                            AccountStatus.SUSPENDED -> ErrorRed.copy(alpha = 0.15f)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (currentUser.status) {
                                            AccountStatus.ACTIVE -> SuccessGreen
                                            AccountStatus.PENDING -> WarningOrange
                                            AccountStatus.INACTIVE -> ErrorRed
                                            AccountStatus.SUSPENDED -> ErrorRed
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) currentUser.status.labelAr else currentUser.status.labelEn,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = when (currentUser.status) {
                                    AccountStatus.ACTIVE -> SuccessGreen
                                    AccountStatus.PENDING -> WarningOrange
                                    AccountStatus.INACTIVE -> ErrorRed
                                    AccountStatus.SUSPENDED -> ErrorRed
                                }
                            )
                        }
                    }
                }
            }
        }

        // IF ACTIVE: SHOW SUBSCRIPTION DATES & TOOL ACCESS
        if (currentUser.status == AccountStatus.ACTIVE) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f)),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "تفاصيل الاشتراك المفعل" else "Active Subscription Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Start and End dates requested
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DateBox(
                            label = if (isArabic) "بداية الاشتراك" else "Start Date",
                            date = currentUser.startDate ?: "2026-09-30",
                            modifier = Modifier.weight(1f)
                        )
                        DateBox(
                            label = if (isArabic) "نهاية الاشتراك" else "Expiry Date",
                            date = currentUser.endDate ?: "2027-03-29",
                            isEnd = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Plan and License Key
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isArabic) "مفتاح الترخيص الخاص بك (License Key):" else "Your License Key:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentUser.licenseKey ?: "HFD-PREM-2026-X99",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Sky500
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("License", currentUser.licenseKey ?: ""))
                                        AppState.toastMessage = if (isArabic) "تم نسخ المفتاح" else "Copied Key"
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Sky500, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            AppState.toastMessage = if (isArabic) "جارٍ تحميل تطبيق Haafedk Tool المفعل..." else "Downloading Tool..."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Sky600),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isArabic) "تحميل أداة Haafedk iCloud Tool v4.8.2" else "Download Haafedk iCloud Tool v4.8.2")
                    }
                }
            }
        }

        // IF PENDING: SHOW PENDING STATUS NOTICE
        if (currentUser.status == AccountStatus.PENDING) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = WarningOrange.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "طلبك تم رفعه وهو قيد المعالجة" else "Your request is pending review",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isArabic)
                            "✅ طلبك تم رفعه بنجاح وهو قيد المعالجة من طرف الإدارة.\nستصلك رسالة في المنصة او رسالة SMS بخصوص تفعيل حسابك."
                        else
                            "✅ Your activation request is submitted and under review by administrators.\nYou will receive a notification or SMS once approved.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (currentUser.receiptFileName != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Sky500, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (isArabic) "الوصل المرفوع:" else "Uploaded receipt:"} ${currentUser.receiptFileName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Sky500
                            )
                        }
                    }
                }
            }
        }

        // IF INACTIVE OR PENDING: SHOW ACTIVATION FORM
        if (currentUser.status == AccountStatus.INACTIVE || currentUser.status == AccountStatus.PENDING) {
            ActivationFormSection(user = currentUser, isArabic = isArabic)
        }
    }
}

@Composable
private fun DateBox(
    label: String,
    date: String,
    isEnd: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isEnd) Sky500.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = if (isEnd) Sky500 else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isEnd) Sky500 else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ActivationFormSection(
    user: UserAccount,
    isArabic: Boolean
) {
    val context = LocalContext.current
    var beneficiaryType by remember { mutableStateOf(user.beneficiaryType.ifEmpty { "لنفسي" }) }
    var fullName by remember { mutableStateOf(user.fullName) }
    var phone by remember { mutableStateOf(user.phone) }
    var selectedPlan by remember { mutableStateOf(if (user.subscriptionPlan.contains("YEAR")) "YEAR" else "MONTHLY 6") }
    var uploadedFileName by remember { mutableStateOf(user.receiptFileName) }
    var isSubmittedSuccess by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Sky500.copy(alpha = 0.3f)),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Sky500.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Sky500, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isArabic) "استمارة الاشتراك" else "Subscription Form",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isArabic) "يرجى تعبئة البيانات وإرفاق وصل الدفع لتفعيل الأداة" else "Fill details and upload receipt for tool activation",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Beneficiary Type: لنفسي أو شخص آخر
            Text(
                text = if (isArabic) "نوع الطلب:" else "Beneficiary Type:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { beneficiaryType = "لنفسي" }
                ) {
                    RadioButton(
                        selected = beneficiaryType == "لنفسي",
                        onClick = { beneficiaryType = "لنفسي" }
                    )
                    Text(text = if (isArabic) "لنفسي" else "For myself", fontSize = 13.sp)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { beneficiaryType = "شخص آخر" }
                ) {
                    RadioButton(
                        selected = beneficiaryType == "شخص آخر",
                        onClick = { beneficiaryType = "شخص آخر" }
                    )
                    Text(text = if (isArabic) "شخص آخر" else "Another person", fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Name & Surname
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(if (isArabic) "الاسم واللقب" else "Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Sky500) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(if (isArabic) "رقم الهاتف" else "Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Sky500) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Choose Subscription First: 6 اشهر او سنة
            Text(
                text = if (isArabic) "اختر مدة الاشتراك:" else "Select Subscription Plan:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlanSelectOption(
                    title = if (isArabic) "6 أشهر (180 يوم)" else "6 Months",
                    price = "8000 دج",
                    isSelected = selectedPlan == "MONTHLY 6",
                    onClick = { selectedPlan = "MONTHLY 6" },
                    modifier = Modifier.weight(1f)
                )
                PlanSelectOption(
                    title = if (isArabic) "سنة (365 يوم)" else "1 year (365 days)",
                    price = "15000 دج",
                    isSelected = selectedPlan == "YEAR",
                    onClick = { selectedPlan = "YEAR" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Accounts Details: CCP / Baridimob / Paypal
            Text(
                text = if (isArabic) "معلومات الدفع والحسابات المعتمدة:" else "Payment Accounts:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Sky500
            )
            Spacer(modifier = Modifier.height(8.dp))

            // CCP
            PaymentInfoCard(
                methodName = "CCP",
                account = AppState.ccpAccount,
                holder = AppState.ccpHolder,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("CCP", AppState.ccpAccount))
                    AppState.toastMessage = if (isArabic) "تم نسخ حساب CCP" else "Copied CCP"
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // BaridiMob
            PaymentInfoCard(
                methodName = "BaridiMob",
                account = AppState.baridiMobAccount,
                holder = "Algerie Poste",
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("BaridiMob", AppState.baridiMobAccount))
                    AppState.toastMessage = if (isArabic) "تم نسخ حساب BaridiMob" else "Copied BaridiMob"
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // PayPal
            PaymentInfoCard(
                methodName = "PayPal",
                account = AppState.payPalAccount,
                holder = "Haroun Tech",
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("PayPal", AppState.payPalAccount))
                    AppState.toastMessage = if (isArabic) "تم نسخ حساب PayPal" else "Copied PayPal"
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Receipt (رفع وصل الدفع)
            Text(
                text = if (isArabic) "رفع وصل الدفع:" else "Upload Payment Receipt:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        // Mock file chooser selecting receipt
                        uploadedFileName = "recu_${System.currentTimeMillis() % 10000}_payment.jpg"
                    },
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (uploadedFileName != null) SuccessGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uploadedFileName != null) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = if (uploadedFileName != null) SuccessGreen else Sky500,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = uploadedFileName ?: (if (isArabic) "اضغط لاختيار صورة الوصل (JPG / PNG / PDF)" else "Click to select receipt file"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uploadedFileName != null) SuccessGreen else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (uploadedFileName != null)
                                (if (isArabic) "تم إرفاق الوصل بنجاح" else "Receipt attached")
                            else
                                (if (isArabic) "تأكد من وضوح المبلغ والتاريخ ورقم العملية" else "Ensure amount & date are clear"),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Submit Button
            Button(
                onClick = {
                    if (fullName.isBlank() || phone.isBlank()) {
                        AppState.toastMessage = if (isArabic) "يرجى كتابة الاسم ورقم الهاتف" else "Please enter name and phone"
                        return@Button
                    }
                    if (uploadedFileName == null) {
                        uploadedFileName = "recu_auto_${System.currentTimeMillis() % 1000}.jpg"
                    }
                    AppState.submitActivationRequest(
                        user = user,
                        beneficiary = beneficiaryType,
                        fullName = fullName,
                        phone = phone,
                        plan = selectedPlan,
                        receiptName = uploadedFileName ?: "recu.jpg"
                    )
                    isSubmittedSuccess = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Sky600),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "إرسال طلب التفعيل ورفع الوصل" else "Submit Activation & Upload Receipt",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Note after submitting
            if (isSubmittedSuccess || user.status == AccountStatus.PENDING) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = SuccessGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isArabic)
                                "طلبك تم رفعه وهو قيد المعالجة\nستصلك رسالة في المنصة او رسالة SMS بخصوص تفعيل حسابك"
                            else
                                "Your request has been uploaded and is under review.\nYou will receive a notification or SMS regarding your activation.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanSelectOption(
    title: String,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) Sky500.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) Sky500 else Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = price, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Sky500)
        }
    }
}

@Composable
private fun PaymentInfoCard(
    methodName: String,
    account: String,
    holder: String,
    onCopy: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Sky500,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = methodName,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = account,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = holder,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Sky500, modifier = Modifier.size(16.dp))
            }
        }
    }
}
