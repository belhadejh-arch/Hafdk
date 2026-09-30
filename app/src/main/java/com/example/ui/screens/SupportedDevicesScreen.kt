package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.AppLanguage
import com.example.data.AppState
import com.example.data.DeviceCatalog
import com.example.data.DeviceType
import com.example.data.SupportedDevice
import com.example.ui.theme.Sky400
import com.example.ui.theme.Sky500
import com.example.ui.theme.Sky600
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SupportedDevicesScreen() {
    val isArabic = AppState.language == AppLanguage.ARABIC
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DeviceType.ALL) }

    // Filter devices based on category and query (name, identifier, A-code, iOS version)
    val filteredDevices = remember(selectedCategory, searchQuery) {
        DeviceCatalog.devices.filter { device ->
            val matchCategory = when (selectedCategory) {
                DeviceType.ALL -> true
                DeviceType.IPHONE -> device.category == DeviceType.IPHONE
                DeviceType.IPAD -> device.category == DeviceType.IPAD
                DeviceType.IPOD -> device.category == DeviceType.IPOD
            }
            val q = searchQuery.trim().lowercase()
            val matchQuery = if (q.isEmpty()) true else {
                device.name.lowercase().contains(q) ||
                device.identifier.lowercase().contains(q) ||
                device.hardwareCodes.any { it.lowercase().contains(q) } ||
                device.iosVersions.any { group ->
                    group.major.lowercase().contains(q) ||
                    group.builds.any { it.lowercase().contains(q) }
                }
            }
            matchCategory && matchQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // HEADER TITLE
        item {
            Column {
                Text(
                    text = if (isArabic) "الأجهزة المدعومة" else "Supported Devices",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isArabic)
                        "يتم التحديث تلقائياً بعد كل تخطي مؤكد — توافق شامل مع أقوى ثغرات النواة"
                    else
                        "Automatically updated after every confirmed bypass — Deep kernel compatibility",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // STATS SUMMARY ROW
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DeviceStatBadge(
                    number = "131",
                    title = if (isArabic) "جهاز فريد" else "Unique Devices",
                    subtitle = if (isArabic) "كل الموديلات" else "All Models",
                    modifier = Modifier.weight(1f)
                )
                DeviceStatBadge(
                    number = "2007",
                    title = if (isArabic) "إجمالي التفعيلات" else "Total Activations",
                    subtitle = if (isArabic) "سجلات مؤكدة" else "Confirmed Logs",
                    modifier = Modifier.weight(1f)
                )
                DeviceStatBadge(
                    number = "56",
                    title = if (isArabic) "آيفون" else "iPhone",
                    subtitle = if (isArabic) "موديل مدعوم" else "Supported",
                    modifier = Modifier.weight(1f)
                )
                DeviceStatBadge(
                    number = "72",
                    title = if (isArabic) "آيباد" else "iPad",
                    subtitle = if (isArabic) "موديل مدعوم" else "Supported",
                    modifier = Modifier.weight(1f)
                )
                DeviceStatBadge(
                    number = "3",
                    title = if (isArabic) "آيبود تاتش" else "iPod touch",
                    subtitle = if (isArabic) "موديل مدعوم" else "Supported",
                    modifier = Modifier.weight(1f)
                )
                DeviceStatBadge(
                    number = DeviceCatalog.lastUpdateDate,
                    title = if (isArabic) "آخر تحديث" else "Last Updated",
                    subtitle = if (isArabic) "أحدث تخطي" else "Latest Bypass",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // SEARCH BAR
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        if (isArabic)
                            "بحث بالاسم (iPhone 13), الموديل (iPhone14,5), الكود (A2482), أو إصدار iOS"
                        else
                            "Search by name, model, hardware code (A2482), or iOS version"
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Sky500) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
        }

        // FILTER TABS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == DeviceType.ALL,
                    onClick = { selectedCategory = DeviceType.ALL },
                    label = { Text(if (isArabic) "الكل (131)" else "All (131)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Sky600,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedCategory == DeviceType.IPHONE,
                    onClick = { selectedCategory = DeviceType.IPHONE },
                    label = { Text(if (isArabic) "آيفون (56)" else "iPhone (56)") },
                    leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Sky600,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedCategory == DeviceType.IPAD,
                    onClick = { selectedCategory = DeviceType.IPAD },
                    label = { Text(if (isArabic) "آيباد (72)" else "iPad (72)") },
                    leadingIcon = { Icon(Icons.Default.TabletMac, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Sky600,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedCategory == DeviceType.IPOD,
                    onClick = { selectedCategory = DeviceType.IPOD },
                    label = { Text(if (isArabic) "آيبود (3)" else "iPod (3)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Sky600,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // COUNT INDICATOR
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic)
                        "عرض ${filteredDevices.size} من 131 جهاز مدعوم"
                    else
                        "Showing ${filteredDevices.size} of 131 supported devices",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Sky500
                )
            }
        }

        // DEVICE CARDS LIST
        items(filteredDevices, key = { it.id }) { device ->
            DeviceItemCard(device = device, isArabic = isArabic)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DeviceStatBadge(
    number: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Sky500.copy(alpha = 0.25f)),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = number, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Sky500)
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DeviceItemCard(
    device: SupportedDevice,
    isArabic: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        tonalElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Main info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Sky500.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (device.category) {
                                DeviceType.IPHONE -> Icons.Default.PhoneIphone
                                DeviceType.IPAD -> Icons.Default.TabletMac
                                else -> Icons.Default.Devices
                            },
                            contentDescription = null,
                            tint = Sky500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = device.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (device.isNew) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = SuccessGreen,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (isArabic) "جديد" else "NEW",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = device.identifier,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Sky500
                        )
                    }
                }

                // Expand button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { expanded = !expanded },
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (expanded)
                                (if (isArabic) "إخفاء" else "Hide")
                            else
                                (if (isArabic) "إصدارات iOS" else "iOS Builds"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hardware A-codes
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "أكواد الهاردوير: " else "Hardware: ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                device.hardwareCodes.forEach { code ->
                    Surface(
                        modifier = Modifier.padding(horizontal = 2.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = code,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            // Expandable Supported iOS versions list
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = if (isArabic) "إصدارات iOS المدعومة بالكامل:" else "Supported iOS Versions:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Sky500
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    device.iosVersions.forEach { versionGroup ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = versionGroup.major,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Sky400
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = versionGroup.builds.joinToString(" • "),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
