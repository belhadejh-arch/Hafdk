import { supportedDevices } from '/devicesData.js';

let CURRENT_USERS = [];
let currentUser = null;
let isArabic = true;
let isDarkMode = true;
let activeTab = 'home';
let isSidebarOpen = false;
let selectedDeviceCategory = 'all';
let searchQuery = '';
let showAuthModal = false;
let authIsRegister = false;
let toastMessage = null;
let previewReceiptUser = null;

const deviceCounts = Object.fromEntries(
  ['iphone', 'ipad', 'ipod'].map((category) => [
    category,
    supportedDevices.filter((device) => device.category === category).length
  ])
);
const totalSupportedDevices = supportedDevices.length;
const patcherDownloadUrl = 'https://drive.google.com/uc?export=download&id=1y8YnlQYAtOmwea7RyKdagxEBeD3VTFTv';

// Inline HTML handlers execute outside this ES module's lexical scope.
Object.defineProperties(window, {
  CURRENT_USERS: { configurable: true, get: () => CURRENT_USERS, set: (value) => { CURRENT_USERS = value; } },
  currentUser: { configurable: true, get: () => currentUser, set: (value) => { currentUser = value; } },
  isArabic: { configurable: true, get: () => isArabic, set: (value) => { isArabic = value; } },
  isSidebarOpen: { configurable: true, get: () => isSidebarOpen, set: (value) => { isSidebarOpen = value; } },
  selectedDeviceCategory: { configurable: true, get: () => selectedDeviceCategory, set: (value) => { selectedDeviceCategory = value; } },
  searchQuery: { configurable: true, get: () => searchQuery, set: (value) => { searchQuery = value; } },
  showAuthModal: { configurable: true, get: () => showAuthModal, set: (value) => { showAuthModal = value; } },
  authIsRegister: { configurable: true, get: () => authIsRegister, set: (value) => { authIsRegister = value; } },
  previewReceiptUser: { configurable: true, get: () => previewReceiptUser, set: (value) => { previewReceiptUser = value; } },
  render: { configurable: true, value: render },
  showToast: { configurable: true, value: showToast }
});

function showToast(msg) {
  toastMessage = msg;
  render();
  setTimeout(() => {
    toastMessage = null;
    render();
  }, 3500);
}

window.toggleLanguage = function() {
  isArabic = !isArabic;
  document.documentElement.dir = isArabic ? 'rtl' : 'ltr';
  document.documentElement.lang = isArabic ? 'ar' : 'en';
  render();
};

window.toggleTheme = function() {
  isDarkMode = !isDarkMode;
  if (isDarkMode) {
    document.documentElement.classList.add('dark');
    document.body.classList.replace('bg-slate-50', 'bg-slate-950');
    document.body.classList.replace('text-slate-900', 'text-slate-100');
  } else {
    document.documentElement.classList.remove('dark');
    document.body.classList.replace('bg-slate-950', 'bg-slate-50');
    document.body.classList.replace('text-slate-100', 'text-slate-900');
  }
  render();
};

window.toggleSidebar = function() {
  isSidebarOpen = !isSidebarOpen;
  render();
};

window.setTab = function(tab) {
  activeTab = tab;
  isSidebarOpen = false;
  window.scrollTo({ top: 0, behavior: 'smooth' });
  render();
  if (tab === 'admin' && currentUser?.role === 'ADMIN') {
    refreshAdminUsers().catch((error) => showToast(error.message));
  }
};

window.copyToClipboard = function(text, label) {
  navigator.clipboard.writeText(text).then(() => {
    showToast((isArabic ? 'تم نسخ ' : 'Copied ') + label);
  });
};

async function apiRequest(url, options = {}) {
  const response = await fetch(url, {
    credentials: 'same-origin',
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...options.headers
    }
  });
  const data = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(data.error || `Request failed (${response.status})`);
  return data;
}

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, (character) => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  })[character]);
}

async function refreshAdminUsers() {
  const { users } = await apiRequest('/api/admin/users');
  CURRENT_USERS = users;
  render();
}

async function restoreSession() {
  try {
    const { user } = await apiRequest('/api/auth/me');
    currentUser = user;
    if (user.role === 'ADMIN') {
      const result = await apiRequest('/api/admin/users');
      CURRENT_USERS = result.users;
    }
  } catch {
    currentUser = null;
  }
  render();
}

window.handleLogin = async function(username, password) {
  try {
    const { user } = await apiRequest('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
    currentUser = user;
    showAuthModal = false;
    activeTab = user.role === 'ADMIN' ? 'admin' : 'dashboard';
    if (user.role === 'ADMIN') await refreshAdminUsers();
    showToast(user.role === 'ADMIN'
      ? (isArabic ? 'تم تسجيل الدخول كمدير' : 'Logged in as administrator')
      : (isArabic ? 'مرحباً بك ' : 'Welcome ') + user.username);
  } catch (error) {
    alert(error.message);
  }
};

window.handleRegister = async function(username, email, password) {
  try {
    const { user } = await apiRequest('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, email, password })
    });
    currentUser = user;
    showAuthModal = false;
    activeTab = 'dashboard';
    showToast(isArabic ? 'تم إنشاء الحساب في حالة غير مفعل' : 'Account created as Inactive');
  } catch (error) {
    alert(error.message);
  }
};

window.handleLogout = async function() {
  try {
    await apiRequest('/api/auth/logout', { method: 'POST' });
  } catch {
    // Clear the local view even if the session has already expired.
  }
  currentUser = null;
  CURRENT_USERS = [];
  activeTab = 'home';
  isSidebarOpen = false;
  render();
  showToast(isArabic ? 'تم تسجيل الخروج بنجاح' : 'Logged out');
};

window.handleActivationSubmit = async function(beneficiary, name, phone, plan, receiptName) {
  if (!name || !phone) {
    alert(isArabic ? 'يرجى كتابة الاسم ورقم الهاتف' : 'Please enter name and phone');
    return;
  }
  try {
    const { user } = await apiRequest('/api/account/activation', {
      method: 'PATCH',
      body: JSON.stringify({
        beneficiaryType: beneficiary,
        fullName: name,
        phone,
        subscriptionPlan: plan,
        receiptFileName: receiptName || null
      })
    });
    currentUser = user;
    render();
    showToast(isArabic ? 'تم حفظ طلب التفعيل للمراجعة' : 'Activation request saved for review');
  } catch (error) {
    alert(error.message);
  }
};

async function runAdminAction(userId, action, months = 6) {
  try {
    const { user } = await apiRequest(`/api/admin/users/${encodeURIComponent(userId)}`, {
      method: 'PATCH',
      body: JSON.stringify({ action, months })
    });
    if (currentUser?.id === user.id) currentUser = user;
    await refreshAdminUsers();
    showToast(action === 'suspend'
      ? (isArabic ? 'تم إيقاف حساب ' : 'Suspended ')
      : (isArabic ? 'تم تحديث حساب ' : 'Updated ') + user.username);
  } catch (error) {
    alert(error.message);
  }
}

window.adminActivate = (userId, months) => runAdminAction(userId, 'activate', months);
window.adminSuspend = (userId) => runAdminAction(userId, 'suspend');
window.adminRenew = (userId, days) => runAdminAction(userId, 'renew', days >= 365 ? 12 : 6);

function render() {
  const app = document.getElementById('app');
  const isAdmin = currentUser && currentUser.role === 'ADMIN';

  const filteredDevices = supportedDevices.filter(d => {
    const catMatch = selectedDeviceCategory === 'all' || d.category === selectedDeviceCategory;
    const q = searchQuery.toLowerCase().trim();
    if (!q) return catMatch;
    const nameMatch = d.name.toLowerCase().includes(q) || d.identifier.toLowerCase().includes(q);
    const codeMatch = d.hardwareCodes.some(c => c.toLowerCase().includes(q));
    const verMatch = d.iosVersions.some(g => g.major.toLowerCase().includes(q) || g.builds.some(b => b.toLowerCase().includes(q)));
    return catMatch && (nameMatch || codeMatch || verMatch);
  });

  app.innerHTML = `
    <!-- HEADER (No tabs, only 3-dots button and branding) -->
    <header class="sticky top-0 z-40 w-full backdrop-blur-md border-b ${isDarkMode ? 'bg-slate-950/85 border-slate-800' : 'bg-white/85 border-slate-200'}">
      <div class="max-w-7xl mx-auto px-3.5 sm:px-6 py-2.5 flex items-center justify-between">
        
        <!-- Brand -->
        <div class="flex items-center space-x-2.5 rtl:space-x-reverse cursor-pointer" onclick="setTab('home')">
          <div class="w-9 h-9 sm:w-10 sm:h-10 rounded-xl bg-gradient-to-tr from-sky-600 to-sky-400 flex items-center justify-center text-white shadow-lg shadow-sky-500/25">
            <svg class="w-5 h-5 sm:w-6 sm:h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path></svg>
          </div>
          <div>
            <div class="flex items-center space-x-1.5 rtl:space-x-reverse font-extrabold text-base sm:text-lg leading-tight">
              <span class="text-sky-500">هفيدك</span>
            </div>
            <div class="text-[9px] sm:text-[10px] tracking-wider text-sky-400 font-bold uppercase">iCloud Premium</div>
          </div>
        </div>

        <!-- Controls: Support + 3-dots Menu Button -->
        <div class="flex items-center space-x-2 rtl:space-x-reverse">
          <!-- User quick status if logged in -->
          ${currentUser ? `
            <button onclick="setTab('${isAdmin ? 'admin' : 'dashboard'}')" class="hidden sm:flex items-center space-x-1 rtl:space-x-reverse px-2.5 py-1 rounded-xl text-xs font-bold ${isAdmin ? 'bg-amber-500/15 border border-amber-500/40 text-amber-400' : currentUser.status === 'ACTIVE' ? 'bg-emerald-500/15 border border-emerald-500/40 text-emerald-400' : 'bg-rose-500/15 border border-rose-500/40 text-rose-400'}">
              <span class="w-1.5 h-1.5 rounded-full ${isAdmin ? 'bg-amber-400' : currentUser.status === 'ACTIVE' ? 'bg-emerald-400' : 'bg-rose-400'}"></span>
              <span>${currentUser.username}</span>
            </button>
          ` : `
            <button onclick="showAuthModal = true; authIsRegister = false; render();" class="bg-sky-600 hover:bg-sky-500 text-white px-3 py-1.5 rounded-xl text-xs font-bold transition">
              ${isArabic ? 'دخول' : 'Login'}
            </button>
          `}

          <!-- 3-DOTS BUTTON (زر الثلاث نقاط لفتح الأقسام الجانبية) -->
          <button onclick="toggleSidebar()" class="w-10 h-10 rounded-xl flex items-center justify-center border transition ${isDarkMode ? 'bg-slate-900 border-slate-800 text-sky-400 hover:bg-slate-800' : 'bg-slate-100 border-slate-300 text-sky-600 hover:bg-slate-200'}" title="${isArabic ? 'الأقسام' : 'Menu'}">
            <!-- 3 Dots Icon -->
            <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 6a2 2 0 100-4 2 2 0 000 4zM12 14a2 2 0 100-4 2 2 0 000 4zM12 22a2 2 0 100-4 2 2 0 000 4z"></path>
            </svg>
          </button>
        </div>
      </div>
    </header>

    <!-- SIDE DRAWER OVERLAY (الأقسام تظهر بشكل جانبي) -->
    <div id="sideDrawerBackdrop" class="fixed inset-0 z-50 transition-opacity duration-300 ${isSidebarOpen ? 'opacity-100 pointer-events-auto bg-slate-950/70 backdrop-blur-sm' : 'opacity-0 pointer-events-none'}" onclick="toggleSidebar()">
    </div>

    <aside class="fixed top-0 bottom-0 ${isArabic ? 'right-0' : 'left-0'} z-50 w-72 max-w-[85vw] ${isDarkMode ? 'bg-slate-900 border-slate-800' : 'bg-white border-slate-200'} border-x shadow-2xl transform transition-transform duration-300 flex flex-col ${isSidebarOpen ? 'translate-x-0' : (isArabic ? 'translate-x-full' : '-translate-x-full')}">
      <!-- Drawer Header -->
      <div class="p-5 border-b ${isDarkMode ? 'border-slate-800' : 'border-slate-200'} flex items-center justify-between">
        <div class="flex items-center space-x-2 rtl:space-x-reverse">
          <div class="w-8 h-8 rounded-lg bg-sky-600 text-white flex items-center justify-center font-black text-sm">
            H
          </div>
          <div>
            <div class="font-extrabold text-sm text-sky-500">هفيدك</div>
            <div class="text-[10px] text-slate-400 font-semibold">${isArabic ? 'القائمة الرئيسية والأقسام' : 'Navigation Menu'}</div>
          </div>
        </div>
        <button onclick="toggleSidebar()" class="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
        </button>
      </div>

      <!-- User card in drawer -->
      <div class="p-4 border-b ${isDarkMode ? 'border-slate-800/80 bg-slate-950/40' : 'border-slate-200 bg-slate-50'}">
        ${currentUser ? `
          <div class="flex items-center justify-between">
            <div>
              <div class="font-bold text-sm text-white flex items-center space-x-1.5 rtl:space-x-reverse">
                <span>${currentUser.username}</span>
                ${isAdmin ? '<span class="bg-amber-500/20 text-amber-400 text-[10px] px-1.5 py-0.5 rounded font-black border border-amber-500/30">ADMIN</span>' : ''}
              </div>
              <div class="text-[11px] text-slate-400 truncate max-w-[170px]">${escapeHtml(currentUser.email)}</div>
            </div>
            <span class="text-[10px] font-bold px-2 py-0.5 rounded-full ${currentUser.status === 'ACTIVE' ? 'bg-emerald-500/20 text-emerald-400' : currentUser.status === 'PENDING' ? 'bg-amber-500/20 text-amber-400' : 'bg-rose-500/20 text-rose-400'}">
              ${isArabic ? (currentUser.status === 'ACTIVE' ? 'مفعل' : currentUser.status === 'PENDING' ? 'قيد المراجعة' : 'غير مفعل') : currentUser.status}
            </span>
          </div>
        ` : `
          <div class="space-y-2">
            <div class="text-xs text-slate-400 font-medium">${isArabic ? 'قم بتسجيل الدخول للوصول إلى لوحتك' : 'Login to access dashboard'}</div>
            <div class="grid grid-cols-2 gap-2">
              <button onclick="showAuthModal = true; authIsRegister = false; isSidebarOpen = false; render();" class="w-full bg-sky-600 hover:bg-sky-500 text-white font-bold py-1.5 rounded-lg text-xs transition">
                ${isArabic ? 'تسجيل دخول' : 'Login'}
              </button>
              <button onclick="showAuthModal = true; authIsRegister = true; isSidebarOpen = false; render();" class="w-full border border-sky-500 text-sky-400 hover:bg-sky-500/10 font-bold py-1.5 rounded-lg text-xs transition">
                ${isArabic ? 'إنشاء حساب' : 'Register'}
              </button>
            </div>
          </div>
        `}
      </div>

      <!-- Navigation Links List -->
      <nav class="flex-1 overflow-y-auto p-3 space-y-1.5 text-sm custom-scroll">
        <button onclick="setTab('home')" class="w-full flex items-center space-x-3 rtl:space-x-reverse px-3.5 py-2.5 rounded-xl font-bold transition ${activeTab === 'home' ? 'bg-sky-600/15 text-sky-400 border border-sky-500/30' : 'text-slate-300 hover:bg-slate-800'}">
          <svg class="w-5 h-5 text-sky-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"></path></svg>
          <span>${isArabic ? 'الرئيسية' : 'Home'}</span>
        </button>

        <button onclick="setTab('devices')" class="w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl font-bold transition ${activeTab === 'devices' ? 'bg-sky-600/15 text-sky-400 border border-sky-500/30' : 'text-slate-300 hover:bg-slate-800'}">
          <div class="flex items-center space-x-3 rtl:space-x-reverse">
            <svg class="w-5 h-5 text-sky-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z"></path></svg>
            <span>${isArabic ? 'الأجهزة المدعومة' : 'Supported Devices'}</span>
          </div>
          <span class="text-[10px] bg-sky-500/20 text-sky-400 px-2 py-0.5 rounded-full font-black">${totalSupportedDevices}</span>
        </button>

        <button onclick="setTab('patcher')" class="w-full flex items-center space-x-3 rtl:space-x-reverse px-3.5 py-2.5 rounded-xl font-bold transition ${activeTab === 'patcher' ? 'bg-sky-600/15 text-sky-400 border border-sky-500/30' : 'text-slate-300 hover:bg-slate-800'}">
          <svg class="w-5 h-5 text-amber-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z"></path></svg>
          <span>${isArabic ? 'الباتشر (Haafedk Patcher)' : 'Haafedk Patcher'}</span>
        </button>

        <button onclick="setTab('pricing')" class="w-full flex items-center space-x-3 rtl:space-x-reverse px-3.5 py-2.5 rounded-xl font-bold transition ${activeTab === 'pricing' ? 'bg-sky-600/15 text-sky-400 border border-sky-500/30' : 'text-slate-300 hover:bg-slate-800'}">
          <svg class="w-5 h-5 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          <span>${isArabic ? 'الأسعار والاشتراكات' : 'Pricing Plans'}</span>
        </button>

        <button onclick="setTab('dashboard')" class="w-full flex items-center space-x-3 rtl:space-x-reverse px-3.5 py-2.5 rounded-xl font-bold transition ${activeTab === 'dashboard' ? 'bg-sky-600/15 text-sky-400 border border-sky-500/30' : 'text-slate-300 hover:bg-slate-800'}">
          <svg class="w-5 h-5 text-sky-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
          <span>${isArabic ? 'لوحة تحكم المستخدم (حسابي)' : 'User Dashboard'}</span>
        </button>

        <!-- ADMIN PANEL ONLY SHOWN IF LOGGED IN AS ADMIN -->
        ${isAdmin ? `
          <button onclick="setTab('admin')" class="w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl font-bold transition bg-amber-500/15 border border-amber-500/40 text-amber-400">
            <div class="flex items-center space-x-3 rtl:space-x-reverse">
              <svg class="w-5 h-5 text-amber-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"></path></svg>
              <span>${isArabic ? 'لوحة تحكم الأدمن' : 'Admin Panel'}</span>
            </div>
            <span class="w-2 h-2 rounded-full bg-amber-400 animate-ping"></span>
          </button>
        ` : ''}

        <div class="pt-3 pb-1 border-t ${isDarkMode ? 'border-slate-800' : 'border-slate-200'}">
          <div class="text-[11px] font-bold text-slate-500 uppercase px-3.5 mb-1">${isArabic ? 'الدعم والتواصل' : 'Support'}</div>
          
          <a href="https://wa.me/213774148015" target="_blank" rel="noopener noreferrer" class="w-full flex items-center space-x-3 rtl:space-x-reverse px-3.5 py-2 rounded-xl text-slate-300 hover:bg-slate-800 font-semibold text-xs">
            <span class="text-emerald-400 font-bold">${isArabic ? 'تواصل مع الدعم الفني' : 'Contact Technical Support'}</span>
          </a>
        </div>
      </nav>

      <!-- Drawer Footer Controls: Dark mode & Language & Logout -->
      <div class="p-3 border-t ${isDarkMode ? 'border-slate-800 bg-slate-950/60' : 'border-slate-200 bg-slate-50'} space-y-2">
        <div class="grid grid-cols-2 gap-2 text-xs font-bold">
          <button onclick="toggleTheme()" class="flex items-center justify-center space-x-1.5 rtl:space-x-reverse py-2 rounded-xl border border-slate-700 bg-slate-800/80 text-slate-300">
            <span>${isDarkMode ? (isArabic ? 'الوضع الفاتح' : 'Light Mode') : (isArabic ? 'الوضع المظلم' : 'Dark Mode')}</span>
          </button>
          <button onclick="toggleLanguage()" class="flex items-center justify-center py-2 rounded-xl border border-slate-700 bg-slate-800/80 text-sky-400">
            <span>${isArabic ? 'English (LTR)' : 'العربية (RTL)'}</span>
          </button>
        </div>

        ${currentUser ? `
          <button onclick="handleLogout()" class="w-full text-center text-xs font-bold text-rose-400 hover:text-rose-300 py-1.5">
            ${isArabic ? 'تسجيل الخروج' : 'Logout'}
          </button>
        ` : ''}
      </div>
    </aside>

    <!-- MAIN BODY -->
    <main class="max-w-7xl mx-auto px-3.5 sm:px-6 py-5 pb-20">
      ${activeTab === 'home' ? renderHomeScreen() : ''}
      ${activeTab === 'devices' ? renderDevicesScreen(filteredDevices) : ''}
      ${activeTab === 'patcher' ? renderPatcherScreen() : ''}
      ${activeTab === 'pricing' ? renderPricingScreen() : ''}
      ${activeTab === 'dashboard' ? renderDashboardScreen() : ''}
      ${activeTab === 'admin' ? (isAdmin ? renderAdminScreen() : renderAccessDeniedScreen()) : ''}
    </main>

    <!-- TOAST NOTIFICATION -->
    ${toastMessage ? `
      <div class="fixed bottom-5 inset-x-0 flex justify-center z-50 pointer-events-none px-4">
        <div class="bg-sky-600 text-white px-4 py-2.5 rounded-xl shadow-2xl font-bold text-xs sm:text-sm flex items-center space-x-2 rtl:space-x-reverse border border-sky-400 animate-bounce pointer-events-auto">
          <svg class="w-4 h-4 text-white shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          <span>${toastMessage}</span>
        </div>
      </div>
    ` : ''}

    <!-- AUTH MODAL -->
    ${showAuthModal ? renderAuthModal() : ''}

    <!-- RECEIPT PREVIEW MODAL -->
    ${previewReceiptUser ? renderReceiptModal() : ''}
  `;
}

function renderHomeScreen() {
  return `
    <div class="space-y-8 sm:space-y-12">
      <!-- Hero Banner (Fully Mobile-Friendly) -->
      <div class="relative overflow-hidden rounded-2xl sm:rounded-3xl border border-sky-500/30 bg-gradient-to-b from-sky-950/60 via-slate-900/40 to-slate-950 p-5 sm:p-10 text-center shadow-xl">
        <div class="inline-flex items-center space-x-2 rtl:space-x-reverse px-3 py-1 rounded-full bg-sky-500/10 border border-sky-500/30 text-sky-400 text-[11px] sm:text-xs font-bold mb-4 sm:mb-6">
          <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
          <span>${isArabic ? 'الإصدار الأحدث v4.8.2 متاح الآن' : 'Latest Release v4.8.2 Online'}</span>
        </div>

        <h1 class="text-2xl sm:text-4xl md:text-5xl font-black tracking-tight mb-3 sm:mb-4 ${isDarkMode ? 'text-white' : 'text-slate-900'} leading-tight">
          ${isArabic ? 'هفيدك iCloud Premium — الحل الأقوى لتخطي iCloud' : 'هفيدك iCloud Premium — Ultimate Bypass Solution'}
        </h1>

        <p class="max-w-2xl mx-auto text-xs sm:text-base text-slate-300 font-medium mb-6 sm:mb-8 leading-relaxed">
          ${isArabic ? 'أداة احترافية لتخطي قفل iCloud على iPhone و iPad — سريعة، آمنة، ومدعومة بفريق محترف' : 'Professional tool to bypass iCloud lock on iPhone & iPad — Fast, secure, and backed by a dedicated expert team'}
        </p>

        <!-- CTA Buttons -->
        <div class="flex flex-col sm:flex-row items-center justify-center gap-3 mb-6">
          <button onclick="showToast(isArabic ? 'جارٍ بدء تحميل نسخة Windows...' : 'Downloading Windows tool...')" class="w-full sm:w-auto bg-sky-600 hover:bg-sky-500 text-white font-bold px-6 py-3 rounded-xl shadow-lg shadow-sky-600/30 flex items-center justify-center space-x-2 rtl:space-x-reverse transition text-sm">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"></path></svg>
            <span>${isArabic ? 'تحميل الأداة الآن' : 'Download Tool Now'}</span>
          </button>

          <button onclick="setTab('pricing')" class="w-full sm:w-auto border border-sky-500 text-sky-400 hover:bg-sky-500/10 font-bold px-6 py-3 rounded-xl transition text-sm">
            ${isArabic ? 'عرض الأسعار' : 'View Pricing'}
          </button>
        </div>

        <!-- Technical Support Direct Link -->
        <div class="pt-4 border-t border-slate-800/80 flex justify-center">
          <a href="https://wa.me/213774148015" target="_blank" rel="noopener noreferrer" class="w-full sm:w-auto inline-flex items-center justify-center bg-emerald-500/10 border border-emerald-500/40 text-emerald-300 px-4 py-2 rounded-xl hover:bg-emerald-500/20 transition text-xs font-bold">
            ${isArabic ? 'تواصل مع الدعم الفني' : 'Contact Technical Support'}
          </a>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
        <div class="rounded-xl border border-sky-500/20 bg-slate-900/60 p-3.5 text-center">
          <div class="text-xl sm:text-2xl font-black text-sky-400 mb-0.5">+5,000</div>
          <div class="text-[11px] text-slate-400 font-semibold">${isArabic ? 'عميل سعيد' : 'Happy Clients'}</div>
        </div>
        <div class="rounded-xl border border-sky-500/20 bg-slate-900/60 p-3.5 text-center">
          <div class="text-xl sm:text-2xl font-black text-emerald-400 mb-0.5">99.8%</div>
          <div class="text-[11px] text-slate-400 font-semibold">${isArabic ? 'معدل النجاح' : 'Success Rate'}</div>
        </div>
        <div class="rounded-xl border border-sky-500/20 bg-slate-900/60 p-3.5 text-center">
          <div class="text-xl sm:text-2xl font-black text-sky-400 mb-0.5">24/7</div>
          <div class="text-[11px] text-slate-400 font-semibold">${isArabic ? 'دعم فني' : 'Live Support'}</div>
        </div>
        <div class="rounded-xl border border-sky-500/20 bg-slate-900/60 p-3.5 text-center">
          <div class="text-xl sm:text-2xl font-black text-amber-400 mb-0.5">A12+</div>
          <div class="text-[11px] text-slate-400 font-semibold">${isArabic ? 'أجهزة ومعالجات' : 'Supported Chips'}</div>
        </div>
      </div>

      <!-- Badges -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
        <div class="flex items-center space-x-3 rtl:space-x-reverse rounded-xl border border-slate-800 bg-slate-900/40 p-3.5">
          <div class="w-9 h-9 rounded-lg bg-sky-500/10 text-sky-400 flex items-center justify-center shrink-0">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path></svg>
          </div>
          <div>
            <div class="font-bold text-xs sm:text-sm text-white">${isArabic ? 'دفع تلقائي مرن' : 'Auto Payments'}</div>
            <div class="text-[11px] text-slate-400">${isArabic ? 'باي بال · USDT · عملات رقمية · بريدي موب' : 'PayPal · USDT · Crypto · BaridiMob'}</div>
          </div>
        </div>

        <div class="flex items-center space-x-3 rtl:space-x-reverse rounded-xl border border-slate-800 bg-slate-900/40 p-3.5">
          <div class="w-9 h-9 rounded-lg bg-sky-500/10 text-sky-400 flex items-center justify-center shrink-0">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5 0a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
          </div>
          <div>
            <div class="font-bold text-xs sm:text-sm text-white">${isArabic ? 'دعم فني مستمر 24/7' : '24/7 Support'}</div>
            <div class="text-[11px] text-slate-400">${isArabic ? 'دعم مباشر عبر واتساب وتذاكر الدعم' : 'Direct support through WhatsApp and support tickets'}</div>
          </div>
        </div>

        <div class="flex items-center space-x-3 rtl:space-x-reverse rounded-xl border border-slate-800 bg-slate-900/40 p-3.5">
          <div class="w-9 h-9 rounded-lg bg-sky-500/10 text-sky-400 flex items-center justify-center shrink-0">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path></svg>
          </div>
          <div>
            <div class="font-bold text-xs sm:text-sm text-white">${isArabic ? 'تحديثات دورية' : 'Regular Updates'}</div>
            <div class="text-[11px] text-slate-400">${isArabic ? 'أحدث الإصدارات وأجهزة جديدة باستمرار' : 'Continuous iOS updates and new models'}</div>
          </div>
        </div>
      </div>

      <!-- Features Section (6 Cards) -->
      <div>
        <div class="mb-4">
          <h2 class="text-xl sm:text-2xl font-black text-white">${isArabic ? 'المميزات: كل ما تحتاجه في أداة واحدة' : 'Features: Everything You Need'}</h2>
          <p class="text-xs sm:text-sm text-slate-400 mt-0.5">${isArabic ? 'أداة احترافية بمميزات متقدمة لتخطي iCloud بكل سهولة وأمان' : 'Professional tool with advanced capabilities to bypass iCloud smoothly'}</p>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          ${[
            { title: isArabic ? 'تخطي iCloud لأجهزة iPhone و iPad' : 'iCloud Bypass for iPhone & iPad', icon: 'M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z' },
            { title: isArabic ? 'يدعم أحدث إصدارات iOS' : 'Supports Latest iOS Versions', icon: 'M13 10V3L4 14h7v7l9-11h-7z' },
            { title: isArabic ? 'بدون فقد بيانات الجهاز' : 'No Device Data Loss', icon: 'M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4m0 5c0 2.21-3.582 4-8 4s-8-1.79-8-4' },
            { title: isArabic ? 'تحديثات دورية مجانية' : 'Free Continuous Updates', icon: 'M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15' },
            { title: isArabic ? 'دعم فني سريع 24/7' : '24/7 Fast Tech Support', icon: 'M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5 0a4 4 0 11-8 0 4 4 0 018 0z' },
            { title: isArabic ? 'واجهة سهلة الاستخدام' : 'User-Friendly Interface', icon: 'M9.75 17L9 20l-1 1h8l-1-1-.75-3M3 13h18M5 17h14a2 2 0 002-2V5a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z' }
          ].map(f => `
            <div class="rounded-xl border border-slate-800 bg-slate-900/50 p-4">
              <div class="w-10 h-10 rounded-lg bg-sky-500/10 text-sky-400 flex items-center justify-center mb-3">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="${f.icon}"></path></svg>
              </div>
              <h3 class="text-sm font-bold text-white mb-1">${f.title}</h3>
              <p class="text-xs text-slate-400 leading-relaxed">${isArabic ? 'دعم احترافي ومضمون مع تحديثات دورية لأحدث إصدارات iOS' : 'Professional verified support with continuous updates for newest iOS releases'}</p>
            </div>
          `).join('')}
        </div>
      </div>

      <!-- Supported Devices Teaser Card -->
      <div class="rounded-2xl border border-sky-500/30 bg-slate-900/70 p-5 sm:p-7 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div class="text-xs font-bold text-sky-400 mb-1">${isArabic ? 'الأجهزة المسجلة: ' + totalSupportedDevices : totalSupportedDevices + ' Devices Listed'}</div>
          <h2 class="text-xl font-black text-white">${isArabic ? 'الأجهزة المدعومة: توافق واسع مع موديلات Apple' : 'Supported Apple Devices'}</h2>
          <p class="text-xs text-slate-400 mt-1 max-w-xl">${isArabic ? 'تصفح أجهزة iPhone و iPad و iPod المسجلة واستخدم أزرار التصنيف للعثور على جهازك.' : 'Browse the listed iPhone, iPad, and iPod devices using the category filters.'}</p>
        </div>
        <button onclick="setTab('devices')" class="w-full sm:w-auto bg-sky-600 hover:bg-sky-500 text-white font-bold px-5 py-2.5 rounded-xl transition text-xs sm:text-sm">
          ${isArabic ? 'عرض كل الأجهزة (' + totalSupportedDevices + ')' : 'View All ' + totalSupportedDevices + ' Devices'}
        </button>
      </div>

      <!-- Patcher Section -->
      <div class="rounded-2xl border border-amber-500/40 bg-slate-900/70 p-5 sm:p-7 space-y-4">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div>
            <span class="text-[10px] font-bold text-amber-400 bg-amber-500/10 px-2.5 py-0.5 rounded-full border border-amber-500/30">${isArabic ? 'أداة باتشر متكاملة — مجانية لجميع أعضاء هفيدك Premium المفعّلين.' : 'A complete patcher, free for active Haafedk Premium members.'}</span>
            <h2 class="text-xl font-black text-white mt-1.5">Haafedk Patcher — ${isArabic ? 'أداة باتشر احترافية' : 'Pro Patcher'}</h2>
            <p class="text-xs text-slate-300 mt-1 max-w-2xl leading-relaxed">${isArabic ? 'تطبيق احترافي لتطبيق الباتشات على ملفات النظام لأجهزة iPhone و iPad، يتيح العمل على الملفات المختلفة بسهولة وأمان.' : 'Haafedk Patcher is a professional tool for applying patches to iPhone and iPad system files with ease and safety.'}</p>
          </div>
          <div class="flex gap-2">
            <a href="${patcherDownloadUrl}" target="_blank" rel="noopener noreferrer" class="bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold px-4 py-2 rounded-xl text-xs">
              ${isArabic ? 'تحميل مباشر' : 'Direct Download'}
            </a>
            <button onclick="setTab('patcher')" class="border border-slate-700 text-slate-300 font-bold px-3 py-2 rounded-xl text-xs">
              ${isArabic ? 'التفاصيل' : 'Details'}
            </button>
          </div>
        </div>
      </div>

      <!-- Pricing Plans -->
      <div>
        <div class="text-center mb-6">
          <h2 class="text-xl sm:text-2xl font-black text-white">${isArabic ? 'الأسعار: اختر الخطة المناسبة' : 'Pricing: Choose Your Plan'}</h2>
          <p class="text-xs text-slate-400 mt-0.5">${isArabic ? 'اشتراكات مرنة بأسعار تنافسية مع دعم كامل' : 'Flexible plans with full support'}</p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 max-w-4xl mx-auto">
          <!-- 6 Months -->
          <div class="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 flex flex-col justify-between">
            <div>
              <div class="text-sky-400 font-black text-xs uppercase tracking-wider">MONTHLY 6</div>
              <div class="text-2xl sm:text-3xl font-black text-white mt-1">8000 دج <span class="text-xs font-semibold text-slate-400">/ 180 ${isArabic ? 'يوم' : 'Days'}</span></div>
              <div class="my-4 border-t border-slate-800"></div>
              <ul class="space-y-2 text-xs text-slate-300">
                <li class="flex items-start space-x-2 rtl:space-x-reverse">
                  <span class="text-emerald-400 font-bold">✓</span>
                  <span>دعم كامل لكل مستخدم طوال فترة الاشتراك (Full support)</span>
                </li>
                <li class="flex items-start space-x-2 rtl:space-x-reverse">
                  <span class="text-emerald-400 font-bold">✓</span>
                  <span>إنشاء ملف خاص لجهازك بشكل مخصص (Custom profile)</span>
                </li>
                <li class="flex items-start space-x-2 rtl:space-x-reverse">
                  <span class="text-emerald-400 font-bold">✓</span>
                  <span>إمكانية تغيير الكمبيوتر كل ساعة (Hourly PC switch)</span>
                </li>
              </ul>
            </div>
            <button onclick="currentUser ? (currentUser.subscriptionPlan='MONTHLY 6', setTab('dashboard')) : (showAuthModal=true, authIsRegister=true, render())" class="mt-6 w-full bg-slate-800 hover:bg-sky-600 text-white font-bold py-2.5 rounded-xl transition text-xs sm:text-sm">
              ${isArabic ? 'اشترك الآن' : 'Subscribe Now'}
            </button>
          </div>

          <!-- 1 Year -->
          <div class="rounded-2xl border-2 border-sky-500 bg-slate-900/80 p-5 flex flex-col justify-between shadow-xl">
            <div>
              <div class="text-sky-400 font-black text-xs uppercase tracking-wider">YEAR (الأكثر توفيراً)</div>
              <div class="text-2xl sm:text-3xl font-black text-white mt-1">15000 دج <span class="text-xs font-semibold text-slate-400">/ 365 ${isArabic ? 'يوم' : 'Days'}</span></div>
              <div class="my-4 border-t border-slate-800"></div>
              <ul class="space-y-2 text-xs text-slate-300">
                <li class="flex items-start space-x-2 rtl:space-x-reverse">
                  <span class="text-emerald-400 font-bold">✓</span>
                  <span>دعم كامل لكل مستخدم طوال فترة الاشتراك</span>
                </li>
                <li class="flex items-start space-x-2 rtl:space-x-reverse">
                  <span class="text-emerald-400 font-bold">✓</span>
                  <span>إنشاء ملف خاص لجهازك بشكل مخصص</span>
                </li>
                <li class="flex items-start space-x-2 rtl:space-x-reverse">
                  <span class="text-emerald-400 font-bold">✓</span>
                  <span>إمكانية تغيير الكمبيوتر كل ساعة</span>
                </li>
              </ul>
            </div>
            <button onclick="currentUser ? (currentUser.subscriptionPlan='YEAR', setTab('dashboard')) : (showAuthModal=true, authIsRegister=true, render())" class="mt-6 w-full bg-sky-600 hover:bg-sky-500 text-white font-bold py-2.5 rounded-xl transition text-xs sm:text-sm shadow-lg shadow-sky-600/30">
              ${isArabic ? 'اشترك الآن' : 'Subscribe Now'}
            </button>
          </div>
        </div>
      </div>
    </div>
  `;
}

function renderDevicesScreen(devices) {
  return `
    <div class="space-y-5">
      <div>
        <h1 class="text-2xl sm:text-3xl font-black text-white">${isArabic ? 'الأجهزة المدعومة' : 'Supported Devices'}</h1>
        <p class="text-xs text-slate-400 mt-0.5">${isArabic ? 'الأجهزة المدرجة حالياً: ' + totalSupportedDevices : 'Currently listed devices: ' + totalSupportedDevices}</p>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-3 sm:grid-cols-6 gap-2 text-center text-xs">
        <div class="bg-slate-900 border border-slate-800 rounded-xl p-2.5">
          <div class="font-black text-sky-400 text-base">${totalSupportedDevices}</div>
          <div class="text-[10px] text-slate-400">${isArabic ? 'جهاز فريد' : 'Devices'}</div>
        </div>
        <div class="bg-slate-900 border border-slate-800 rounded-xl p-2.5">
          <div class="font-black text-sky-400 text-base">2007</div>
          <div class="text-[10px] text-slate-400">${isArabic ? 'تفعيلات' : 'Total'}</div>
        </div>
        <div class="bg-slate-900 border border-slate-800 rounded-xl p-2.5">
          <div class="font-black text-emerald-400 text-base">${deviceCounts.iphone}</div>
          <div class="text-[10px] text-slate-400">${isArabic ? 'آيفون' : 'iPhone'}</div>
        </div>
        <div class="bg-slate-900 border border-slate-800 rounded-xl p-2.5">
          <div class="font-black text-sky-400 text-base">${deviceCounts.ipad}</div>
          <div class="text-[10px] text-slate-400">${isArabic ? 'آيباد' : 'iPad'}</div>
        </div>
        <div class="bg-slate-900 border border-slate-800 rounded-xl p-2.5">
          <div class="font-black text-amber-400 text-base">3</div>
          <div class="text-[10px] text-slate-400">${isArabic ? 'آيبود' : 'iPod'}</div>
        </div>
        <div class="bg-slate-900 border border-slate-800 rounded-xl p-2.5">
          <div class="font-black text-slate-300 text-[11px] mt-1">2026-09-30</div>
          <div class="text-[9px] text-slate-400">${isArabic ? 'آخر تحديث' : 'Updated'}</div>
        </div>
      </div>

      <!-- Search Bar -->
      <input 
        type="text" 
        placeholder="${isArabic ? 'بحث بالاسم (iPhone 14), الموديل (iPhone15,2), الكود (A2650), أو iOS 18...' : 'Search by name, model, hardware code, or iOS...'}"
        value="${searchQuery}"
        oninput="searchQuery = this.value; render();"
        class="w-full bg-slate-900 border border-slate-800 focus:border-sky-500 rounded-xl px-4 py-3 text-xs sm:text-sm text-white placeholder-slate-500 outline-none"
      >

      <!-- Category Tabs -->
      <div class="flex items-center space-x-2 rtl:space-x-reverse overflow-x-auto pb-1 text-xs">
        <button onclick="selectedDeviceCategory = 'all'; render();" class="px-3.5 py-1.5 rounded-lg font-bold shrink-0 ${selectedDeviceCategory === 'all' ? 'bg-sky-600 text-white' : 'bg-slate-900 text-slate-400 border border-slate-800'}">
          ${isArabic ? 'الكل (' + totalSupportedDevices + ')' : 'All (' + totalSupportedDevices + ')'}
        </button>
        <button onclick="selectedDeviceCategory = 'iphone'; render();" class="px-3.5 py-1.5 rounded-lg font-bold shrink-0 ${selectedDeviceCategory === 'iphone' ? 'bg-sky-600 text-white' : 'bg-slate-900 text-slate-400 border border-slate-800'}">
          ${isArabic ? 'آيفون (' + deviceCounts.iphone + ')' : 'iPhone (' + deviceCounts.iphone + ')'}
        </button>
        <button onclick="selectedDeviceCategory = 'ipad'; render();" class="px-3.5 py-1.5 rounded-lg font-bold shrink-0 ${selectedDeviceCategory === 'ipad' ? 'bg-sky-600 text-white' : 'bg-slate-900 text-slate-400 border border-slate-800'}">
          ${isArabic ? 'آيباد (' + deviceCounts.ipad + ')' : 'iPad (' + deviceCounts.ipad + ')'}
        </button>
        <button onclick="selectedDeviceCategory = 'ipod'; render();" class="px-3.5 py-1.5 rounded-lg font-bold shrink-0 ${selectedDeviceCategory === 'ipod' ? 'bg-sky-600 text-white' : 'bg-slate-900 text-slate-400 border border-slate-800'}">
          ${isArabic ? 'آيبود تاتش (' + deviceCounts.ipod + ')' : 'iPod (' + deviceCounts.ipod + ')'}
        </button>
      </div>

      <!-- Device Count -->
      <div class="text-[11px] text-sky-400 font-bold">${isArabic ? 'عرض ' + devices.length + ' من ' + totalSupportedDevices + ' جهاز' : 'Showing ' + devices.length + ' of ' + totalSupportedDevices + ' devices'}</div>

      <!-- Cards Grid -->
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        ${devices.map(d => `
          <div class="rounded-xl border border-slate-800 bg-slate-900/60 p-4 space-y-2.5">
            <div class="flex items-center justify-between">
              <div>
                <div class="flex items-center space-x-1.5 rtl:space-x-reverse">
                  <h3 class="font-extrabold text-sm text-white">${d.name}</h3>
                  ${d.isNew ? '<span class="bg-emerald-500/20 text-emerald-400 text-[9px] font-black px-1.5 py-0.2 rounded border border-emerald-500/30">' + (isArabic ? 'جديد' : 'NEW') + '</span>' : ''}
                </div>
                <div class="text-xs text-sky-400 font-mono font-bold">${d.identifier}</div>
              </div>
              <span class="text-[9px] uppercase font-bold text-slate-400 bg-slate-800 px-2 py-0.5 rounded">${d.category}</span>
            </div>

            <!-- Hardware Codes -->
            <div class="flex items-center space-x-1 rtl:space-x-reverse flex-wrap text-xs">
              <span class="text-slate-400 text-[11px]">${isArabic ? 'كود الهاردوير:' : 'Chip:'}</span>
              ${d.hardwareCodes.map(c => '<span class="bg-slate-800 text-slate-300 px-1.5 py-0.5 rounded text-[10px] font-mono">' + c + '</span>').join('')}
            </div>

            <!-- Builds -->
            <div class="pt-2 border-t border-slate-800/80 space-y-1">
              ${d.iosVersions.map(v => `
                <div class="bg-slate-950/50 rounded p-1.5 text-[11px]">
                  <span class="font-bold text-sky-400">${v.major}: </span>
                  <span class="text-slate-400 font-mono text-[10px]">${v.builds.join(' • ')}</span>
                </div>
              `).join('')}
            </div>
          </div>
        `).join('')}
      </div>
    </div>
  `;
}

function renderPatcherScreen() {
  return `
    <div class="space-y-6 max-w-4xl mx-auto">
      <div class="rounded-2xl border border-amber-500/30 bg-slate-900/60 p-5 sm:p-7 space-y-4">
        <span class="px-2.5 py-0.5 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-400 text-xs font-bold">${isArabic ? 'أداة باتشر متكاملة — مجانية لجميع أعضاء هفيدك Premium المفعّلين.' : 'A complete patcher, free for all active Haafedk Premium members.'}</span>
        <h1 class="text-2xl font-black text-white">Haafedk Patcher — ${isArabic ? 'أداة باتشر احترافية' : 'Pro Patcher'}</h1>
        <p class="text-xs sm:text-sm text-slate-300 leading-relaxed">
          ${isArabic ? 'Haafedk Patcher أداة احترافية لتطبيق الباتشات على ملفات النظام الخاصة بأجهزة iPhone و iPad. تتيح لك العمل على الملفات المختلفة بكل سهولة وأمان.' : 'Haafedk Patcher is a professional tool for applying patches to iPhone and iPad system files, making it easy to work with different files safely.'}
        </p>
        <p class="text-xs sm:text-sm text-slate-300 leading-relaxed">
          ${isArabic ? 'الأداة مدمجة مع نظام هفيدك Premium ومجانية للأعضاء المفعّلين.' : 'The tool is integrated with Haafedk Premium and is free for active members.'}
        </p>
        <div class="flex flex-wrap gap-3 pt-1">
          <a href="${patcherDownloadUrl}" target="_blank" rel="noopener noreferrer" class="bg-sky-600 hover:bg-sky-500 text-white font-bold px-5 py-2.5 rounded-xl text-xs transition">
            ${isArabic ? 'تحميل Haafedk Patcher مباشرة' : 'Download Haafedk Patcher'}
          </a>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
        ${[
          isArabic ? 'يعمل على جميع الأجهزة المدعومة' : 'Works with all supported devices',
          isArabic ? 'لا يحتاج إلى Jailbreak' : 'No Jailbreak required',
          isArabic ? 'نتائج فورية وسريعة' : 'Fast, immediate results',
          isArabic ? 'آمن 100% — بدون فقد بيانات' : '100% safe — no data loss',
          isArabic ? 'تحديثات دورية مجانية' : 'Free regular updates',
          isArabic ? 'دعم فني متواصل' : 'Ongoing technical support'
        ].map(item => `
          <div class="flex items-center space-x-2.5 rtl:space-x-reverse rounded-xl border border-slate-800 bg-slate-900/50 p-3">
            <span class="text-emerald-400 font-bold">✓</span>
            <span class="font-bold text-white">${item}</span>
          </div>
        `).join('')}
      </div>
    </div>
  `;
}

function renderPricingScreen() {
  return `
    <div class="space-y-6 max-w-4xl mx-auto">
      <div class="text-center mb-6">
        <h2 class="text-2xl sm:text-3xl font-black text-white">${isArabic ? 'الأسعار: اختر الخطة المناسبة' : 'Pricing: Choose Your Plan'}</h2>
        <p class="text-xs sm:text-sm text-slate-400 mt-1">${isArabic ? 'اشتراكات مرنة بأسعار تنافسية مع دعم كامل' : 'Flexible plans with full support'}</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- 6 Months -->
        <div class="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 flex flex-col justify-between">
          <div>
            <div class="text-sky-400 font-black text-xs uppercase tracking-wider">MONTHLY 6</div>
            <div class="text-2xl sm:text-3xl font-black text-white mt-1">8000 دج <span class="text-xs font-semibold text-slate-400">/ 180 ${isArabic ? 'يوم' : 'Days'}</span></div>
            <div class="my-4 border-t border-slate-800"></div>
            <ul class="space-y-2 text-xs text-slate-300">
              <li class="flex items-start space-x-2 rtl:space-x-reverse">
                <span class="text-emerald-400 font-bold">✓</span>
                <span>دعم كامل لكل مستخدم طوال فترة الاشتراك (Full support)</span>
              </li>
              <li class="flex items-start space-x-2 rtl:space-x-reverse">
                <span class="text-emerald-400 font-bold">✓</span>
                <span>إنشاء ملف خاص لجهازك بشكل مخصص (Custom profile)</span>
              </li>
              <li class="flex items-start space-x-2 rtl:space-x-reverse">
                <span class="text-emerald-400 font-bold">✓</span>
                <span>إمكانية تغيير الكمبيوتر كل ساعة (Hourly PC switch)</span>
              </li>
            </ul>
          </div>
          <button onclick="currentUser ? (currentUser.subscriptionPlan='MONTHLY 6', setTab('dashboard')) : (showAuthModal=true, authIsRegister=true, render())" class="mt-6 w-full bg-slate-800 hover:bg-sky-600 text-white font-bold py-2.5 rounded-xl transition text-xs sm:text-sm">
            ${isArabic ? 'اشترك الآن' : 'Subscribe Now'}
          </button>
        </div>

        <!-- 1 Year -->
        <div class="rounded-2xl border-2 border-sky-500 bg-slate-900/80 p-5 flex flex-col justify-between shadow-xl">
          <div>
            <div class="text-sky-400 font-black text-xs uppercase tracking-wider">YEAR (الأكثر توفيراً)</div>
            <div class="text-2xl sm:text-3xl font-black text-white mt-1">15000 دج <span class="text-xs font-semibold text-slate-400">/ 365 ${isArabic ? 'يوم' : 'Days'}</span></div>
            <div class="my-4 border-t border-slate-800"></div>
            <ul class="space-y-2 text-xs text-slate-300">
              <li class="flex items-start space-x-2 rtl:space-x-reverse">
                <span class="text-emerald-400 font-bold">✓</span>
                <span>دعم كامل لكل مستخدم طوال فترة الاشتراك</span>
              </li>
              <li class="flex items-start space-x-2 rtl:space-x-reverse">
                <span class="text-emerald-400 font-bold">✓</span>
                <span>إنشاء ملف خاص لجهازك بشكل مخصص</span>
              </li>
              <li class="flex items-start space-x-2 rtl:space-x-reverse">
                <span class="text-emerald-400 font-bold">✓</span>
                <span>إمكانية تغيير الكمبيوتر كل ساعة</span>
              </li>
            </ul>
          </div>
          <button onclick="currentUser ? (currentUser.subscriptionPlan='YEAR', setTab('dashboard')) : (showAuthModal=true, authIsRegister=true, render())" class="mt-6 w-full bg-sky-600 hover:bg-sky-500 text-white font-bold py-2.5 rounded-xl transition text-xs sm:text-sm shadow-lg shadow-sky-600/30">
            ${isArabic ? 'اشترك الآن' : 'Subscribe Now'}
          </button>
        </div>
      </div>
    </div>
  `;
}

function renderDashboardScreen() {
  if (!currentUser) {
    return `
      <div class="text-center py-16">
        <h2 class="text-lg font-bold mb-3">${isArabic ? 'يرجى تسجيل الدخول للوصول إلى لوحتك' : 'Please login to access dashboard'}</h2>
        <button onclick="showAuthModal = true; render();" class="bg-sky-600 text-white font-bold px-5 py-2 rounded-xl text-xs">${isArabic ? 'تسجيل الدخول' : 'Login'}</button>
      </div>
    `;
  }

  return `
    <div class="space-y-6 max-w-4xl mx-auto">
      <!-- Profile Header -->
      <div class="rounded-2xl border border-slate-800 bg-slate-900/70 p-4 sm:p-5 flex items-center justify-between gap-3">
        <div class="flex items-center space-x-3 rtl:space-x-reverse">
          <div class="w-11 h-11 rounded-xl bg-sky-500/15 border border-sky-500/30 flex items-center justify-center text-sky-400 font-black text-sm">
            ${currentUser.username.substring(0, 2).toUpperCase()}
          </div>
          <div>
            <h1 class="text-base font-extrabold text-white">${currentUser.username}</h1>
            <div class="text-xs text-slate-400">${escapeHtml(currentUser.email)}</div>
          </div>
        </div>

        <div>
          ${currentUser.status === 'ACTIVE' ? `
            <span class="bg-emerald-500/15 border border-emerald-500/40 text-emerald-400 px-3 py-1 rounded-xl font-bold text-xs">
              ${isArabic ? 'مفعل نشط' : 'Active'}
            </span>
          ` : currentUser.status === 'PENDING' ? `
            <span class="bg-amber-500/15 border border-amber-500/40 text-amber-400 px-3 py-1 rounded-xl font-bold text-xs">
              ${isArabic ? 'قيد المعالجة' : 'Pending'}
            </span>
          ` : `
            <span class="bg-rose-500/15 border border-rose-500/40 text-rose-400 px-3 py-1 rounded-xl font-black text-xs animate-pulse">
              ${isArabic ? 'غير مفعل' : 'Inactive'}
            </span>
          `}
        </div>
      </div>

      <!-- ACTIVE INFO -->
      ${currentUser.status === 'ACTIVE' ? `
        <div class="rounded-2xl border border-emerald-500/40 bg-slate-900/60 p-5 space-y-4">
          <div class="text-emerald-400 font-extrabold text-sm flex items-center space-x-2 rtl:space-x-reverse">
            <span>✓</span>
            <span>${isArabic ? 'اشتراكك مفعل ونشط' : 'Subscription Active'}</span>
          </div>

          <div class="grid grid-cols-2 gap-3 text-xs">
            <div class="rounded-xl border border-slate-800 bg-slate-800/40 p-3">
              <div class="text-slate-400 text-[10px]">${isArabic ? 'بداية الاشتراك:' : 'Start:'}</div>
              <div class="font-black text-white mt-0.5 text-sm">${currentUser.startDate || '2026-09-30'}</div>
            </div>
            <div class="rounded-xl border border-sky-500/30 bg-sky-500/10 p-3">
              <div class="text-sky-300 text-[10px]">${isArabic ? 'نهاية الاشتراك:' : 'Expiry:'}</div>
              <div class="font-black text-sky-400 mt-0.5 text-sm">${currentUser.endDate || '2027-03-29'}</div>
            </div>
          </div>

          <div class="rounded-xl border border-slate-800 bg-slate-950 p-3 flex items-center justify-between text-xs">
            <div>
              <div class="text-slate-400 text-[10px]">${isArabic ? 'مفتاح الترخيص:' : 'License:'}</div>
              <div class="font-mono font-bold text-sky-400 mt-0.5">${currentUser.licenseKey || 'HFD-PREM-2026-X99'}</div>
            </div>
            <button onclick="copyToClipboard('${currentUser.licenseKey || 'HFD-PREM-2026-X99'}', 'مفتاح الترخيص')" class="px-2.5 py-1 rounded bg-sky-600/20 text-sky-400 font-bold text-[11px]">
              ${isArabic ? 'نسخ' : 'Copy'}
            </button>
          </div>

          <button onclick="showToast(isArabic ? 'جارٍ تحميل أداة هفيدك iCloud Tool...' : 'Downloading tool...')" class="w-full bg-sky-600 hover:bg-sky-500 text-white font-bold py-2.5 rounded-xl text-xs transition">
            ${isArabic ? 'تحميل أداة هفيدك iCloud Tool v4.8.2' : 'Download Tool v4.8.2'}
          </button>
        </div>
      ` : ''}

      <!-- PENDING NOTICE -->
      ${currentUser.status === 'PENDING' ? `
        <div class="rounded-2xl border border-amber-500/40 bg-amber-500/10 p-4 space-y-2 text-xs">
          <div class="font-bold text-amber-400">${isArabic ? 'طلبك تم رفعه وهو قيد المعالجة' : 'Request Pending Review'}</div>
          <p class="text-slate-200 leading-relaxed">
            ${isArabic ? '✅ طلبك تم رفعه وهو قيد المعالجة من طرف الإدارة.\\nستصلك رسالة في المنصة او رسالة SMS بخصوص تفعيل حسابك.' : '✅ Your request has been uploaded and is under review.'}
          </p>
        </div>
      ` : ''}

      <!-- ACTIVATION FORM (IF INACTIVE OR PENDING) -->
      ${currentUser.status !== 'ACTIVE' ? `
        <div class="rounded-2xl border border-sky-500/30 bg-slate-900/60 p-5 space-y-4">
          <div>
            <h2 class="text-base font-black text-white">${isArabic ? 'تفعيل الحساب' : 'Activate Account'}</h2>
            <p class="text-xs text-slate-400">${isArabic ? 'املاً الاستمارة واختر الحساب المناسب وارفع الوصل' : 'Fill details & upload receipt'}</p>
          </div>

          <form onsubmit="event.preventDefault(); handleActivationSubmit(this.beneficiary.value, this.fullName.value, this.phone.value, this.plan.value, this.receiptInputWrapper.dataset.filename);" class="space-y-3.5 text-xs">
            <div>
              <label class="block font-bold text-slate-300 mb-1.5">${isArabic ? 'نوع الطلب:' : 'For:'}</label>
              <div class="flex items-center space-x-4 rtl:space-x-reverse">
                <label class="flex items-center space-x-1.5 rtl:space-x-reverse cursor-pointer">
                  <input type="radio" name="beneficiary" value="لنفسي" checked class="text-sky-500">
                  <span>${isArabic ? 'لنفسي' : 'For myself'}</span>
                </label>
                <label class="flex items-center space-x-1.5 rtl:space-x-reverse cursor-pointer">
                  <input type="radio" name="beneficiary" value="شخص آخر" class="text-sky-500">
                  <span>${isArabic ? 'شخص آخر' : 'Another person'}</span>
                </label>
              </div>
            </div>

            <div>
              <label class="block font-bold text-slate-300 mb-1">${isArabic ? 'الاسم واللقب' : 'Full Name'}</label>
              <input type="text" name="fullName" required placeholder="${isArabic ? 'اكتب اسمك الكامل...' : 'Full name...'}" value="${escapeHtml(currentUser.fullName)}" class="w-full bg-slate-800 border border-slate-700 rounded-xl px-3.5 py-2.5 text-white outline-none focus:border-sky-500">
            </div>

            <div>
              <label class="block font-bold text-slate-300 mb-1">${isArabic ? 'رقم الهاتف' : 'Phone Number'}</label>
              <input type="tel" name="phone" required placeholder="0774148015" value="${escapeHtml(currentUser.phone)}" class="w-full bg-slate-800 border border-slate-700 rounded-xl px-3.5 py-2.5 text-white font-mono outline-none focus:border-sky-500">
            </div>

            <div>
              <label class="block font-bold text-slate-300 mb-1.5">${isArabic ? 'اختر الاشتراك:' : 'Select Plan:'}</label>
              <div class="grid grid-cols-2 gap-2">
                <label class="rounded-xl border border-slate-700 bg-slate-800/50 p-2.5 flex items-center justify-between cursor-pointer hover:border-sky-500">
                  <div class="flex items-center space-x-2 rtl:space-x-reverse">
                    <input type="radio" name="plan" value="MONTHLY 6" checked class="text-sky-500">
                    <span class="font-bold text-white">6 أشهر</span>
                  </div>
                  <span class="text-sky-400 font-black">8000 دج</span>
                </label>
                <label class="rounded-xl border border-slate-700 bg-slate-800/50 p-2.5 flex items-center justify-between cursor-pointer hover:border-sky-500">
                  <div class="flex items-center space-x-2 rtl:space-x-reverse">
                    <input type="radio" name="plan" value="YEAR" class="text-sky-500">
                    <span class="font-bold text-white">سنة</span>
                  </div>
                  <span class="text-sky-400 font-black">15000 دج</span>
                </label>
              </div>
            </div>

            <!-- Payment Wallets -->
            <div class="space-y-1.5 pt-1">
              <div class="font-bold text-sky-400 text-[11px]">${isArabic ? 'حسابات الدفع المعتمدة:' : 'Payment Accounts:'}</div>
              
              <div class="rounded-lg border border-slate-800 bg-slate-950 p-2 flex items-center justify-between">
                <div>
                  <div class="font-bold text-sky-400 text-[10px]">CCP</div>
                  <div class="font-mono font-black text-white">0017955197 cle 16</div>
                  <div class="text-[9px] text-slate-400">Belyamani Ibrahim</div>
                </div>
                <button type="button" onclick="copyToClipboard('0017955197 cle 16', 'CCP')" class="px-2 py-1 rounded bg-sky-600/20 text-sky-400 font-bold text-[10px]">
                  ${isArabic ? 'نسخ' : 'Copy'}
                </button>
              </div>

              <div class="rounded-lg border border-slate-800 bg-slate-950 p-2 flex items-center justify-between">
                <div>
                  <div class="font-bold text-emerald-400 text-[10px]">BaridiMob</div>
                  <div class="font-mono font-black text-white">00799999001795519773</div>
                </div>
                <button type="button" onclick="copyToClipboard('00799999001795519773', 'BaridiMob')" class="px-2 py-1 rounded bg-emerald-600/20 text-emerald-400 font-bold text-[10px]">
                  ${isArabic ? 'نسخ' : 'Copy'}
                </button>
              </div>

              <div class="rounded-lg border border-slate-800 bg-slate-950 p-2 flex items-center justify-between">
                <div>
                  <div class="font-bold text-sky-400 text-[10px]">PayPal</div>
                  <div class="font-mono font-black text-white">mohamedharoun329@gmail.com</div>
                </div>
                <button type="button" onclick="copyToClipboard('mohamedharoun329@gmail.com', 'PayPal')" class="px-2 py-1 rounded bg-sky-600/20 text-sky-400 font-bold text-[10px]">
                  ${isArabic ? 'نسخ' : 'Copy'}
                </button>
              </div>
            </div>

            <!-- Upload Receipt -->
            <div>
              <label class="block font-bold text-slate-300 mb-1">${isArabic ? 'رفع وصل الدفع:' : 'Receipt:'}</label>
              <div id="receiptInputWrapper" data-filename="" class="border border-dashed border-slate-700 hover:border-sky-500 rounded-xl p-4 text-center cursor-pointer" onclick="document.getElementById('receiptInput').click()">
                <input type="file" id="receiptInput" accept="image/*,application/pdf" class="hidden" onchange="
                  const file = this.files[0];
                  if(file) {
                    document.getElementById('receiptStatusText').innerText = file.name;
                    document.getElementById('receiptInputWrapper').dataset.filename = file.name;
                    showToast(isArabic ? 'تم إرفاق: ' + file.name : 'Attached: ' + file.name);
                  }
                ">
                <div id="receiptStatusText" class="font-bold text-slate-300">${isArabic ? 'اضغط لاختيار صورة الوصل' : 'Click to select receipt'}</div>
              </div>
            </div>

            <button type="submit" class="w-full bg-sky-600 hover:bg-sky-500 text-white font-bold py-3 rounded-xl transition text-xs shadow-lg shadow-sky-600/30">
              ${isArabic ? 'إرسال طلب التفعيل' : 'Submit Activation Request'}
            </button>
          </form>
        </div>
      ` : ''}
    </div>
  `;
}

function renderAdminScreen() {
  return `
    <div class="space-y-5 max-w-5xl mx-auto">
      <div class="rounded-2xl border border-amber-500/40 bg-slate-900/80 p-4 sm:p-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div>
          <div class="text-amber-400 font-black text-base flex items-center space-x-1.5 rtl:space-x-reverse">
            <span>🛡️</span>
            <span>${isArabic ? 'لوحة تحكم الأدمن' : 'Admin Control Panel'}</span>
          </div>
          <p class="text-xs text-slate-400">${isArabic ? 'إدارة الحسابات والاشتراكات' : 'Manage accounts and subscriptions'}</p>
        </div>
        <div class="text-xs font-bold text-slate-300 bg-slate-800 px-3 py-1 rounded-lg">
          ${isArabic ? 'إجمالي الحسابات: ' : 'Total: '} ${CURRENT_USERS.length}
        </div>
      </div>

      <div class="space-y-3">
        ${CURRENT_USERS.length ? CURRENT_USERS.map(u => `
          <div class="rounded-xl border border-slate-800 bg-slate-900/60 p-4 space-y-3 text-xs">
            <div class="flex items-center justify-between border-b border-slate-800/80 pb-2.5">
              <div>
                <span class="font-bold text-sm text-white">${escapeHtml(u.username)}</span>
                <span class="text-slate-400 text-[11px] block">${escapeHtml(u.email)}</span>
              </div>
              <span class="px-2 py-0.5 rounded text-[10px] font-black ${u.status === 'ACTIVE' ? 'bg-emerald-500/20 text-emerald-400' : u.status === 'PENDING' ? 'bg-amber-500/20 text-amber-400' : 'bg-rose-500/20 text-rose-400'}">
                ${u.status}
              </span>
            </div>

            <div class="grid grid-cols-2 sm:grid-cols-3 gap-2 bg-slate-950 p-2.5 rounded-lg font-mono">
              <div>
                <span class="text-slate-500 text-[10px] block font-sans">${isArabic ? 'الهاتف:' : 'Phone:'}</span>
                <span class="text-white">${escapeHtml(u.phone || '—')}</span>
              </div>
              <div>
                <span class="text-slate-500 text-[10px] block font-sans">${isArabic ? 'الخطة:' : 'Plan:'}</span>
                <span class="text-amber-400">${escapeHtml(u.subscriptionPlan)}</span>
              </div>
              <div>
                <span class="text-slate-500 text-[10px] block font-sans">${isArabic ? 'النوع:' : 'Type:'}</span>
                <span class="text-slate-300">${escapeHtml(u.beneficiaryType || 'لنفسي')}</span>
              </div>
            </div>

            ${u.startDate ? `
              <div class="text-[11px] text-emerald-400 font-bold bg-emerald-500/10 p-2 rounded">
                ${isArabic ? 'الاشتراك:' : 'Period:'} ${escapeHtml(u.startDate)} ⬅ ${escapeHtml(u.endDate)}
              </div>
            ` : ''}

            ${u.receiptFileName ? `
              <div class="flex items-center justify-between bg-slate-800/50 p-2 rounded text-[11px]">
                <span class="text-sky-400 font-bold">${isArabic ? 'وصل الدفع: ' : 'Receipt: '} ${escapeHtml(u.receiptFileName)}</span>
                <button onclick="previewReceiptUser = CURRENT_USERS.find(x => x.id === '${u.id}'); render();" class="text-sky-400 font-bold underline">
                  ${isArabic ? 'معاينة' : 'View'}
                </button>
              </div>
            ` : ''}

            <div class="flex flex-wrap gap-2 pt-1 border-t border-slate-800">
              <button onclick="adminActivate('${u.id}', 6)" class="bg-emerald-600 hover:bg-emerald-500 text-white font-bold px-2.5 py-1.5 rounded text-[11px]">
                ${isArabic ? 'تفعيل 6 أشهر' : 'Act 6M'}
              </button>
              <button onclick="adminActivate('${u.id}', 12)" class="bg-sky-600 hover:bg-sky-500 text-white font-bold px-2.5 py-1.5 rounded text-[11px]">
                ${isArabic ? 'تفعيل سنة' : 'Act 1Yr'}
              </button>
              <button onclick="adminSuspend('${u.id}')" class="bg-rose-600 hover:bg-rose-500 text-white font-bold px-2.5 py-1.5 rounded text-[11px]">
                ${isArabic ? 'توقيف' : 'Suspend'}
              </button>
              <button onclick="adminRenew('${u.id}', ${u.subscriptionPlan.includes('YEAR') ? 365 : 180})" class="border border-sky-500 text-sky-400 font-bold px-2.5 py-1.5 rounded text-[11px]">
                ${isArabic ? 'تجديد' : 'Renew'}
              </button>
            </div>
          </div>
        `).join('') : `
          <div class="rounded-xl border border-slate-800 bg-slate-900/60 p-5 text-center text-sm text-slate-400">
            ${isArabic ? 'لا توجد حسابات مسجلة بعد.' : 'No accounts have registered yet.'}
          </div>
        `}
      </div>
    </div>
  `;
}

function renderAccessDeniedScreen() {
  return `
    <div class="text-center py-20">
      <h2 class="text-lg font-bold text-rose-400 mb-2">${isArabic ? 'عفواً، الدخول إلى لوحة الأدمن متاح فقط للمدير' : 'Access Restricted to Administrators'}</h2>
      <p class="text-xs text-slate-400 mb-4">${isArabic ? 'سجّل الدخول باستخدام بيانات المدير التي أُنشئت على الخادم.' : 'Sign in with the administrator account created on the server.'}</p>
      <button onclick="showAuthModal = true; authIsRegister = false; render();" class="bg-sky-600 text-white font-bold px-4 py-2 rounded-xl text-xs">
        ${isArabic ? 'تسجيل الدخول' : 'Login'}
      </button>
    </div>
  `;
}

function renderAuthModal() {
  return `
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
      <div class="w-full max-w-sm bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-2xl relative">
        <button onclick="showAuthModal = false; render();" class="absolute top-4 left-4 rtl:left-auto rtl:right-4 text-slate-400 hover:text-white">
          ✕
        </button>

        <!-- Tabs -->
        <div class="flex items-center space-x-2 rtl:space-x-reverse mb-4 border-b border-slate-800 pb-2 text-xs font-bold">
          <button onclick="authIsRegister = false; render();" class="px-3 py-1.5 ${!authIsRegister ? 'text-sky-400 border-b-2 border-sky-400' : 'text-slate-400'}">
            ${isArabic ? 'تسجيل دخول' : 'Login'}
          </button>
          <button onclick="authIsRegister = true; render();" class="px-3 py-1.5 ${authIsRegister ? 'text-sky-400 border-b-2 border-sky-400' : 'text-slate-400'}">
            ${isArabic ? 'إنشاء حساب' : 'Register'}
          </button>
        </div>

        ${authIsRegister ? `
          <div class="rounded-xl border border-amber-500/40 bg-amber-500/10 p-3 mb-3 text-[11px] leading-relaxed text-slate-200">
            Your account will be created as Inactive.<br>
            🔐 The account must be activated through any supported activation server.<br>
            🌍 To purchase activation, please visit the Authorized Resellers page:<br>
            👉 Open Resellers Page<br>
            ⚠️ سيتم إنشاء الحساب في حالة غير مفعل<br>
            🔐 يتم تفعيل الحساب بعد اتمام انشاء الحساب ثم الدفع و رفع وصل الدفع
          </div>
        ` : ''}

        <form onsubmit="event.preventDefault(); authIsRegister ? handleRegister(this.username.value, this.email.value, this.password.value) : handleLogin(this.username.value, this.password.value);" class="space-y-3 text-xs">
          <div>
            <label class="block font-bold text-slate-300 mb-1">${isArabic ? 'اسم المستخدم' : 'Username'}</label>
            <input id="authUsername" type="text" name="username" required placeholder="${isArabic ? 'اسم المستخدم...' : 'Username...'}" class="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-white outline-none focus:border-sky-500">
          </div>

          ${authIsRegister ? `
            <div>
              <label class="block font-bold text-slate-300 mb-1">${isArabic ? 'البريد الإلكتروني' : 'Email'}</label>
              <input type="email" name="email" required placeholder="you@domain.com" class="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-white outline-none focus:border-sky-500">
            </div>
          ` : ''}

          <div>
            <label class="block font-bold text-slate-300 mb-1">${isArabic ? 'كلمة السر' : 'Password'}</label>
            <input id="authPassword" type="password" name="password" required ${authIsRegister ? 'minlength="12"' : ''} placeholder="••••••••" class="w-full bg-slate-800 border border-slate-700 rounded-xl px-3 py-2 text-white outline-none focus:border-sky-500">
          </div>

          <button type="submit" class="w-full bg-sky-600 hover:bg-sky-500 text-white font-bold py-2.5 rounded-xl transition shadow-lg shadow-sky-600/30">
            ${authIsRegister ? (isArabic ? 'إنشاء الحساب' : 'Register') : (isArabic ? 'تسجيل الدخول' : 'Login')}
          </button>
        </form>
      </div>
    </div>
  `;
}

function renderReceiptModal() {
  return `
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
      <div class="w-full max-w-sm bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-2xl relative space-y-3 text-xs">
        <button onclick="previewReceiptUser = null; render();" class="absolute top-4 left-4 rtl:left-auto rtl:right-4 text-slate-400 hover:text-white">
          ✕
        </button>

        <h3 class="font-extrabold text-white text-sm">${isArabic ? 'معاينة الوصل: ' : 'Receipt: '} ${escapeHtml(previewReceiptUser.username)}</h3>
        
        <div class="rounded-xl border border-slate-800 bg-slate-950 p-5 text-center space-y-2">
          <div class="font-mono font-bold text-sky-400">${escapeHtml(previewReceiptUser.receiptFileName || 'recu.jpg')}</div>
          <div class="text-emerald-400 font-bold">${isArabic ? 'المبلغ: ' : 'Amount: '} ${previewReceiptUser.subscriptionPlan.includes('YEAR') ? '15000 دج' : '8000 دج'}</div>
          <div class="text-slate-400">${isArabic ? 'تاريخ العملية: ' : 'Date: '} ${previewReceiptUser.registrationDate}</div>
        </div>

        <button onclick="previewReceiptUser = null; render();" class="w-full py-2 rounded-xl bg-slate-800 text-white font-bold">
          ${isArabic ? 'إغلاق' : 'Close'}
        </button>
      </div>
    </div>
  `;
}

render();
restoreSession();
