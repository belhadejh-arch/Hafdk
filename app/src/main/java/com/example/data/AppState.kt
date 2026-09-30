package com.example.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object AppState {
    var language by mutableStateOf(AppLanguage.ARABIC)
    var isDarkMode by mutableStateOf(true)
    var currentScreen by mutableStateOf(NavigationScreen.HOME)

    // Payment details requested by user
    val ccpAccount = "0017955197 cle 16"
    val ccpHolder = "Belyamani Ibrahim"
    val baridiMobAccount = "00799999001795519773"
    val payPalAccount = "mohamedharoun329@gmail.com"
    val supportWhatsApp = "0774148015"
    val supportWhatsAppLink = "https://wa.me/213774148015"
    val supportTelegram = "https://t.me/haafedk_premium"

    // Initial users: includes one Inactive user needing activation, one Active user
    val users = mutableStateListOf(
        UserAccount(
            id = "usr_001",
            username = "belyamani_pro",
            email = "gocourse1@gmail.com",
            password = "password123",
            phone = "0774148015",
            fullName = "إبراهيم بليماني",
            beneficiaryType = "لنفسي",
            subscriptionPlan = "MONTHLY 6",
            status = AccountStatus.INACTIVE,
            registrationDate = "2026-09-30"
        ),
        UserAccount(
            id = "usr_002",
            username = "karim_apple",
            email = "karim.tech@gmail.com",
            password = "karimSecure2026",
            phone = "0550123456",
            fullName = "كريم منصوري",
            beneficiaryType = "لنفسي",
            subscriptionPlan = "YEAR",
            status = AccountStatus.ACTIVE,
            registrationDate = "2026-08-15",
            startDate = "2026-08-15",
            endDate = "2027-08-15",
            licenseKey = "HFD-PREM-9984-KLM3-2026",
            hwidResetCountdownMinutes = 24
        ),
        UserAccount(
            id = "usr_003",
            username = "samir_alger",
            email = "samir_gsm@yahoo.fr",
            password = "gsmSamir!99",
            phone = "0661987654",
            fullName = "سمير بوجمعة",
            beneficiaryType = "شخص آخر",
            subscriptionPlan = "MONTHLY 6",
            status = AccountStatus.PENDING,
            receiptFileName = "recu_baridimob_8000dz.jpg",
            receiptNotes = "تم الدفع عبر بريدي موب 8000 دج",
            registrationDate = "2026-09-29"
        )
    )

    // Currently logged-in user
    var currentUser by mutableStateOf<UserAccount?>(users[0])
    var showAuthModal by mutableStateOf(false)
    var authDefaultTabRegister by mutableStateOf(false)

    // Notification toast
    var toastMessage by mutableStateOf<String?>(null)

    fun registerNewAccount(username: String, email: String, pass: String): UserAccount {
        val newAcc = UserAccount(
            id = "usr_${System.currentTimeMillis() % 10000}",
            username = username.trim(),
            email = email.trim(),
            password = pass,
            status = AccountStatus.INACTIVE,
            registrationDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        )
        users.add(0, newAcc)
        currentUser = newAcc
        currentScreen = NavigationScreen.USER_DASHBOARD
        toastMessage = if (language == AppLanguage.ARABIC)
            "تم إنشاء الحساب بنجاح وهو غير مفعل"
        else
            "Account created as Inactive"
        return newAcc
    }

    fun login(usernameOrEmail: String, pass: String): Boolean {
        val found = users.firstOrNull {
            (it.username.equals(usernameOrEmail.trim(), ignoreCase = true) ||
             it.email.equals(usernameOrEmail.trim(), ignoreCase = true)) &&
            it.password == pass
        }
        if (found != null) {
            currentUser = found
            currentScreen = NavigationScreen.USER_DASHBOARD
            toastMessage = if (language == AppLanguage.ARABIC) "مرحباً بك ${found.username}" else "Welcome ${found.username}"
            return true
        }
        return false
    }

    fun submitActivationRequest(
        user: UserAccount,
        beneficiary: String,
        fullName: String,
        phone: String,
        plan: String,
        receiptName: String
    ) {
        user.beneficiaryType = beneficiary
        user.fullName = fullName
        user.phone = phone
        user.subscriptionPlan = plan
        user.receiptFileName = receiptName
        user.status = AccountStatus.PENDING

        // Update list trigger
        val idx = users.indexOfFirst { it.id == user.id }
        if (idx >= 0) {
            users[idx] = user
            currentUser = user
        }
    }

    fun adminActivateAccount(user: UserAccount, planMonths: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        val start = sdf.format(cal.time)
        val days = if (planMonths == 6) 180 else 365
        cal.add(Calendar.DAY_OF_YEAR, days)
        val end = sdf.format(cal.time)

        user.status = AccountStatus.ACTIVE
        user.subscriptionPlan = if (planMonths == 6) "MONTHLY 6" else "YEAR"
        user.startDate = start
        user.endDate = end
        user.licenseKey = "HFD-KEY-" + (1000..9999).random() + "-" + (1000..9999).random()

        val idx = users.indexOfFirst { it.id == user.id }
        if (idx >= 0) {
            users[idx] = user
            if (currentUser?.id == user.id) {
                currentUser = user
            }
        }
    }

    fun adminSuspendAccount(user: UserAccount) {
        user.status = AccountStatus.SUSPENDED
        val idx = users.indexOfFirst { it.id == user.id }
        if (idx >= 0) {
            users[idx] = user
            if (currentUser?.id == user.id) {
                currentUser = user
            }
        }
    }

    fun adminRenewAccount(user: UserAccount, daysToAdd: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        user.startDate = sdf.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, daysToAdd)
        user.endDate = sdf.format(cal.time)
        user.status = AccountStatus.ACTIVE

        val idx = users.indexOfFirst { it.id == user.id }
        if (idx >= 0) {
            users[idx] = user
            if (currentUser?.id == user.id) {
                currentUser = user
            }
        }
    }
}
