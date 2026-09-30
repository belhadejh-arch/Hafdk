package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun AdminPanelScreen(
    onNavigate: (NavigationScreen) -> Unit
) {
    val isArabic = AppState.language == AppLanguage.ARABIC
    var previewReceiptUser by remember { mutableStateOf<UserAccount?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ADMIN TITLE & METRICS
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f)),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(WarningOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = WarningOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "لوحة تحكم الإدارة (الأدمن)" else "Admin Dashboard",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isArabic)
                                        "إدارة حسابات المستخدمين، كلمات السر، وصولات الدفع، والتفعيل"
                                    else
                                        "Manage users, passwords, payment receipts & subscription dates",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminStatCard(
                            label = if (isArabic) "المستخدمين" else "Users",
                            count = AppState.users.size.toString(),
                            color = Sky500,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            label = if (isArabic) "قيد المعالجة" else "Pending",
                            count = AppState.users.count { it.status == AccountStatus.PENDING }.toString(),
                            color = WarningOrange,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            label = if (isArabic) "مفعل" else "Active",
                            count = AppState.users.count { it.status == AccountStatus.ACTIVE }.toString(),
                            color = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            label = if (isArabic) "غير مفعل" else "Inactive",
                            count = AppState.users.count { it.status == AccountStatus.INACTIVE }.toString(),
                            color = ErrorRed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = if (isArabic) "قائمة تسجيلات المستخدمين وطلبات التفعيل:" else "User Registrations & Activation Requests:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // USERS LIST
        items(AppState.users, key = { it.id }) { user ->
            UserAdminCard(
                user = user,
                isArabic = isArabic,
                onViewReceipt = { previewReceiptUser = user },
                onActivate6Months = {
                    AppState.adminActivateAccount(user, 6)
                    AppState.toastMessage = if (isArabic) "تم تفعيل 6 أشهر للمستخدم ${user.username}" else "Activated 6 Months"
                },
                onActivate1Year = {
                    AppState.adminActivateAccount(user, 12)
                    AppState.toastMessage = if (isArabic) "تم تفعيل سنة كاملة للمستخدم ${user.username}" else "Activated 1 Year"
                },
                onSuspend = {
                    AppState.adminSuspendAccount(user)
                    AppState.toastMessage = if (isArabic) "تم توقيف اشتراك ${user.username}" else "Suspended ${user.username}"
                },
                onRenew = {
                    val days = if (user.subscriptionPlan.contains("YEAR")) 365 else 180
                    AppState.adminRenewAccount(user, days)
                    AppState.toastMessage = if (isArabic) "تم تجديد الاشتراك بـ $days يوم" else "Renewed for $days days"
                },
                onSwitchToUser = {
                    AppState.currentUser = user
                    onNavigate(NavigationScreen.USER_DASHBOARD)
                    AppState.toastMessage = if (isArabic) "تم التبديل إلى حساب ${user.username}" else "Switched to ${user.username}"
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // RECEIPT PREVIEW DIALOG
    if (previewReceiptUser != null) {
        val target = previewReceiptUser!!
        Dialog(onDismissRequest = { previewReceiptUser = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isArabic) "وصل الدفع — ${target.username}" else "Receipt — ${target.username}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Mock Receipt Image Container
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Sky500.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Sky500, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = target.receiptFileName ?: "recu_payment.jpg",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "المبلغ المحول: ${if (target.subscriptionPlan.contains("YEAR")) "15000 دج" else "8000 دج"}",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "حساب المحول إليه: CCP / BaridiMob",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "تاريخ العملية: ${target.registrationDate}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { previewReceiptUser = null },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isArabic) "إغلاق" else "Close")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun UserAdminCard(
    user: UserAccount,
    isArabic: Boolean,
    onViewReceipt: () -> Unit,
    onActivate6Months: () -> Unit,
    onActivate1Year: () -> Unit,
    onSuspend: () -> Unit,
    onRenew: () -> Unit,
    onSwitchToUser: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        tonalElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Username + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user.username,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (user.status) {
                        AccountStatus.ACTIVE -> SuccessGreen.copy(alpha = 0.15f)
                        AccountStatus.PENDING -> WarningOrange.copy(alpha = 0.15f)
                        AccountStatus.INACTIVE -> ErrorRed.copy(alpha = 0.15f)
                        AccountStatus.SUSPENDED -> ErrorRed.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = if (isArabic) user.status.labelAr else user.status.labelEn,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (user.status) {
                            AccountStatus.ACTIVE -> SuccessGreen
                            AccountStatus.PENDING -> WarningOrange
                            AccountStatus.INACTIVE -> ErrorRed
                            AccountStatus.SUSPENDED -> ErrorRed
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Credentials & Details (Admin can inspect full details including plain password as requested)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${if (isArabic) "البريد:" else "Email:"} ${user.email}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        // Explicit user password display for Admin
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = Sky500, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (isArabic) "كلمة السر:" else "Pass:"} ${user.password}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Sky500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${if (isArabic) "رقم الهاتف:" else "Phone:"} ${user.phone.ifEmpty { if (isArabic) "غير محدد" else "N/A" }}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${if (isArabic) "الاسم:" else "Name:"} ${user.fullName.ifEmpty { user.username }}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${if (isArabic) "الاشتراك المختار:" else "Selected Plan:"} ${user.subscriptionPlan}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Sky400
                        )
                        Text(
                            text = "${if (isArabic) "نوع الطلب:" else "Type:"} ${user.beneficiaryType}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // If has dates
                    if (user.startDate != null && user.endDate != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${if (isArabic) "فترة الاشتراك:" else "Period:"} ${user.startDate} ⬅ ${user.endDate}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Payment Receipt action
            if (user.receiptFileName != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onViewReceipt),
                    color = Sky500.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Sky500, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${if (isArabic) "وصل الدفع المرفوع:" else "Receipt:"} ${user.receiptFileName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Sky500
                            )
                        }
                        Icon(Icons.Default.Visibility, contentDescription = "View", tint = Sky500, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // ADMIN ACTIONS ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Activate 6 Months
                Button(
                    onClick = onActivate6Months,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text(if (isArabic) "تفعيل 6 أشهر" else "Act 6M", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Activate 1 Year
                Button(
                    onClick = onActivate1Year,
                    colors = ButtonDefaults.buttonColors(containerColor = Sky600),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text(if (isArabic) "تفعيل سنة" else "Act 1Yr", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Suspend
                Button(
                    onClick = onSuspend,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp)
                ) {
                    Text(if (isArabic) "توقيف" else "Stop", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Renew
                OutlinedButton(
                    onClick = onRenew,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Sky500)
                ) {
                    Text(if (isArabic) "تجديد" else "Renew", fontSize = 10.sp, color = Sky500, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Switch User View
            OutlinedButton(
                onClick = onSwitchToUser,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(32.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "معاينة لوحة المستخدم بهذا الحساب" else "Preview User View for this Account",
                    fontSize = 10.sp
                )
            }
        }
    }
}
