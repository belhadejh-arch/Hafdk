package com.example.data

enum class AccountStatus(val labelAr: String, val labelEn: String) {
    INACTIVE("غير مفعل", "Inactive"),
    PENDING("قيد المعالجة", "Pending Review"),
    ACTIVE("مفعل نشط", "Active Premium"),
    SUSPENDED("موقوف", "Suspended")
}

data class UserAccount(
    val id: String,
    var username: String,
    var email: String,
    var password: String, // As requested: admin sees plain password
    var phone: String = "",
    var fullName: String = "",
    var beneficiaryType: String = "لنفسي", // لنفسي / شخص آخر
    var subscriptionPlan: String = "MONTHLY 6", // MONTHLY 6 or YEAR
    var status: AccountStatus = AccountStatus.INACTIVE,
    var receiptFileName: String? = null,
    var receiptNotes: String? = null,
    var registrationDate: String = "2026-09-30",
    var startDate: String? = null,
    var endDate: String? = null,
    var licenseKey: String? = null,
    var hwidResetCountdownMinutes: Int = 48
)

data class IosVersionGroup(
    val major: String, // e.g. "iOS 18", "iOS 17"
    val builds: List<String>
)

enum class DeviceType(val labelAr: String, val labelEn: String) {
    ALL("الكل", "All"),
    IPHONE("آيفون", "iPhone"),
    IPAD("آيباد", "iPad"),
    IPOD("آيبود تاتش", "iPod touch")
}

data class SupportedDevice(
    val id: String,
    val name: String,
    val identifier: String, // e.g. "iPhone12,1"
    val isNew: Boolean = false,
    val hardwareCodes: List<String>, // e.g. ["A2111", "A2223", "A2221", "A2222"]
    val category: DeviceType,
    val iosVersions: List<IosVersionGroup>
)

enum class AppLanguage {
    ARABIC,
    ENGLISH
}

enum class NavigationScreen {
    HOME,
    DEVICES,
    PATCHER,
    PRICING,
    USER_DASHBOARD,
    ADMIN_PANEL
}
