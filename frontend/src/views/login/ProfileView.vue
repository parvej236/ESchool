<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import { LogOut, KeyRound, Mail, User, Shield, Calendar, Pencil, X, Eye, EyeOff, AlertTriangle, CheckCircle } from 'lucide-vue-next'

const authStore = useAuthStore()
const router    = useRouter()

// ── Sidebar state
const isCollapsed  = ref(false)
const isMobileOpen = ref(false)

const user    = ref(null)
const loading = ref(true)
const error   = ref('')
const toast   = ref({ show: false, message: '', type: 'success' })

// ── Change password modal
const showPasswordModal = ref(false)
const showCurrent       = ref(false)
const showNew           = ref(false)
const showConfirm       = ref(false)
const pwSubmitting      = ref(false)
const pwError           = ref('')
const pwSuccess         = ref('')
const pwForm            = ref({ currentPassword: '', newPassword: '', confirmPassword: '' })

// ── Logout confirm
const showLogoutConfirm = ref(false)

// ── Computed
const initials = computed(() => {
  const name = user.value?.username ?? ''
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) || '??'
})

const joinedDate = computed(() => {
  const raw = user.value?.createdAt
  if (!raw) return '—'
  return new Date(raw).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })
})

const avatarColor = computed(() => {
  const COLORS = ['#7C3AED','#059669','#DC2626','#D97706','#2563EB','#DB2777','#0891B2','#65A30D']
  const id = user.value?.id ?? 0
  return COLORS[id % COLORS.length]
})

// ── Helpers
const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type }
  setTimeout(() => toast.value.show = false, 3500)
}

const openPasswordModal = () => {
  pwForm.value = { currentPassword: '', newPassword: '', confirmPassword: '' }
  pwError.value = ''
  pwSuccess.value = ''
  showCurrent.value = false
  showNew.value = false
  showConfirm.value = false
  showPasswordModal.value = true
}

// ── Load profile
onMounted(async () => {
  if (!authStore.isAuthenticated) { router.push('/login'); return }
  try {
    if (authStore.user) {
      user.value = authStore.user
    } else {
      const decoded = authStore.decodeToken(authStore.token)
      user.value = { username: decoded?.sub }
    }
    if (!user.value) error.value = 'Failed to load profile.'
  } catch (err) {
    console.error(err)
    error.value = 'Something went wrong while loading profile.'
  } finally {
    loading.value = false
  }
})

// ── Change password
const handleChangePassword = async () => {
  pwError.value = ''
  pwSuccess.value = ''
  if (!pwForm.value.currentPassword) return pwError.value = 'Current password is required.'
  if (pwForm.value.newPassword.length < 6) return pwError.value = 'New password must be at least 6 characters.'
  if (pwForm.value.newPassword !== pwForm.value.confirmPassword) return pwError.value = 'New passwords do not match.'
  if (pwForm.value.currentPassword === pwForm.value.newPassword) return pwError.value = 'New password must be different from current password.'

  pwSubmitting.value = true
  try {
    const result = await authStore.changePassword(pwForm.value.currentPassword, pwForm.value.newPassword)
    if (result.success) {
      pwSuccess.value = 'Password changed successfully!'
      setTimeout(() => { showPasswordModal.value = false; showToast('Password changed successfully!') }, 1500)
    } else {
      pwError.value = result.message || 'Failed to change password.'
    }
  } catch {
    pwError.value = 'Network error. Please try again.'
  } finally {
    pwSubmitting.value = false
  }
}

// ── Logout
const confirmLogout = () => { authStore.logout(); router.push('/login') }
</script>

<template>
  <!-- Sidebar -->
  <DashboardSidebar
    :is-collapsed="isCollapsed"
    :is-mobile-open="isMobileOpen"
    @update:is-collapsed="isCollapsed = $event"
    @update:is-mobile-open="isMobileOpen = $event"
  />

  <!-- Main content -->
  <div
    :class="[
      'transition-all duration-300 ml-0',
      isCollapsed ? 'lg:ml-18' : 'lg:ml-64'
    ]"
  >
    <DashboardHeader
      title="Account Settings"
      :is-sidebar-collapsed="isCollapsed"
      @toggle-sidebar="isMobileOpen = !isMobileOpen"
    />

    <main class="pt-3 px-3 sm:px-5 pb-8 mt-16">

      <!-- Toast -->
      <Transition
        enter-active-class="transition duration-300 ease-out"
        enter-from-class="opacity-0 -translate-y-2"
        enter-to-class="opacity-100 translate-y-0"
        leave-active-class="transition duration-200"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0">
        <div v-if="toast.show"
          :class="['fixed top-4 right-3 sm:right-5 z-50 flex items-center gap-3 px-4 py-3 rounded-xl shadow-xl text-sm font-medium text-white max-w-[calc(100vw-24px)] sm:min-w-55',
            toast.type === 'success' ? 'bg-green-500' : 'bg-red-500']">
          <CheckCircle v-if="toast.type === 'success'" class="w-4 h-4 shrink-0" />
          <AlertTriangle v-else class="w-4 h-4 shrink-0" />
          <span class="flex-1 truncate">{{ toast.message }}</span>
          <button @click="toast.show = false" class="opacity-70 hover:opacity-100 shrink-0">
            <X class="w-3.5 h-3.5" />
          </button>
        </div>
      </Transition>

      <!-- Page title -->
      <div class="mb-5">
        <h1 class="text-lg sm:text-2xl font-bold text-gray-800">Account Settings</h1>
        <p class="text-xs sm:text-sm text-gray-400 mt-0.5">Manage your profile and security preferences</p>
      </div>

      <!-- Loading skeleton -->
      <div v-if="loading" class="grid grid-cols-1 lg:grid-cols-3 gap-4 sm:gap-6">
        <div class="bg-white rounded-2xl border border-gray-100 p-6 animate-pulse flex flex-col items-center">
          <div class="w-20 h-20 sm:w-24 sm:h-24 bg-gray-200 rounded-2xl mb-4"></div>
          <div class="h-4 bg-gray-200 rounded w-32 mb-2"></div>
          <div class="h-3 bg-gray-100 rounded w-24"></div>
        </div>
        <div class="lg:col-span-2 bg-white rounded-2xl border border-gray-100 p-6 animate-pulse space-y-4">
          <div class="h-4 bg-gray-200 rounded w-1/3"></div>
          <div class="h-10 bg-gray-100 rounded-xl"></div>
          <div class="h-10 bg-gray-100 rounded-xl"></div>
        </div>
      </div>

      <!-- Error -->
      <div v-else-if="error"
        class="flex items-center gap-3 bg-red-50 border border-red-100 text-red-600 px-4 py-4 rounded-2xl">
        <AlertTriangle class="w-5 h-5 shrink-0" />
        <span class="text-sm font-medium">{{ error }}</span>
      </div>

      <!-- Profile content -->
      <div v-else-if="user" class="grid grid-cols-1 lg:grid-cols-3 gap-4 sm:gap-6">

        <!-- LEFT: Avatar card -->
        <!-- On mobile: horizontal layout; on lg: vertical centered -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 sm:p-6">

          <!-- Mobile: row layout; lg: column centered -->
          <div class="flex flex-row lg:flex-col items-center gap-4 lg:gap-0 lg:text-center">

            <!-- Avatar -->
            <div class="relative shrink-0 lg:mb-4">
              <div class="w-16 h-16 sm:w-20 sm:h-20 lg:w-24 lg:h-24 rounded-2xl flex items-center justify-center text-white text-2xl lg:text-3xl font-bold shadow-lg"
                :style="{ backgroundColor: avatarColor }">
                {{ initials }}
              </div>
              <div class="absolute -bottom-1 -right-1 w-6 h-6 lg:w-7 lg:h-7 rounded-lg flex items-center justify-center shadow-md"
                :style="{ backgroundColor: avatarColor }">
                <Shield class="w-3 h-3 lg:w-3.5 lg:h-3.5 text-white" />
              </div>
            </div>

            <!-- Name / email / badge -->
            <div class="flex-1 min-w-0 lg:flex lg:flex-col lg:items-center">
              <h2 class="text-base sm:text-lg font-bold text-gray-800 truncate">{{ user.username }}</h2>
              <p class="text-xs sm:text-sm text-gray-400 mt-0.5 truncate">{{ user.email ?? 'No email on record' }}</p>

              <span class="mt-2 inline-flex items-center gap-1.5 px-3 py-1 bg-blue-50 text-blue-600 text-xs font-semibold rounded-full">
                <Shield class="w-3 h-3" />
                Platform Member
              </span>

              <!-- Joined — visible inline on mobile -->
              <div class="flex items-center gap-1.5 text-xs text-gray-400 mt-2 lg:hidden">
                <Calendar class="w-3.5 h-3.5 shrink-0" />
                <span>Joined {{ joinedDate }}</span>
              </div>
            </div>
          </div>

          <!-- Joined date — desktop only (below the column) -->
          <div class="hidden lg:flex w-full border-t border-gray-100 mt-5 pt-5 items-center justify-center gap-2 text-xs text-gray-400">
            <Calendar class="w-3.5 h-3.5" />
            <span>Joined {{ joinedDate }}</span>
          </div>

          <!-- Quick actions -->
          <div class="w-full mt-4 grid grid-cols-2 lg:grid-cols-1 gap-2">
            <button @click="openPasswordModal"
              class="flex items-center justify-center gap-2 px-4 py-2.5 bg-blue-50 hover:bg-blue-100 text-blue-600 text-sm font-medium rounded-xl transition">
              <KeyRound class="w-4 h-4" />
              <span class="hidden sm:inline">Change Password</span>
              <span class="sm:hidden">Password</span>
            </button>
            <button @click="showLogoutConfirm = true"
              class="flex items-center justify-center gap-2 px-4 py-2.5 bg-red-50 hover:bg-red-100 text-red-500 text-sm font-medium rounded-xl transition">
              <LogOut class="w-4 h-4" />
              Logout
            </button>
          </div>
        </div>

        <!-- RIGHT: Profile details -->
        <div class="lg:col-span-2 space-y-4 sm:space-y-5">

          <!-- Account info card -->
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 sm:p-6">
            <div class="flex items-center justify-between mb-4 sm:mb-5">
              <div>
                <h3 class="text-sm sm:text-base font-bold text-gray-800">Profile Information</h3>
                <p class="text-xs text-gray-400 mt-0.5">Your account details</p>
              </div>
              <div class="w-8 h-8 bg-blue-50 rounded-xl flex items-center justify-center shrink-0">
                <User class="w-4 h-4 text-blue-500" />
              </div>
            </div>

            <div class="space-y-3 sm:space-y-4">
              <!-- Username -->
              <div>
                <label class="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Username</label>
                <div class="flex items-center gap-3 bg-gray-50 border border-gray-200 rounded-xl px-3 sm:px-4 py-2.5 sm:py-3">
                  <User class="w-4 h-4 text-gray-400 shrink-0" />
                  <span class="text-sm font-medium text-gray-800 truncate flex-1">{{ user.username }}</span>
                  <span class="shrink-0 text-xs text-gray-400 bg-white border border-gray-200 px-2 py-0.5 rounded-lg">
                    Read only
                  </span>
                </div>
              </div>

              <!-- Email -->
              <div>
                <label class="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Email Address</label>
                <div class="flex items-center gap-3 bg-gray-50 border border-gray-200 rounded-xl px-3 sm:px-4 py-2.5 sm:py-3">
                  <Mail class="w-4 h-4 text-gray-400 shrink-0" />
                  <span class="text-sm font-medium text-gray-800 truncate flex-1">{{ user.email ?? '—' }}</span>
                  <span class="shrink-0 text-xs text-gray-400 bg-white border border-gray-200 px-2 py-0.5 rounded-lg">
                    Read only
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Security card -->
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 sm:p-6">
            <div class="flex items-center justify-between mb-4 sm:mb-5">
              <div>
                <h3 class="text-sm sm:text-base font-bold text-gray-800">Security</h3>
                <p class="text-xs text-gray-400 mt-0.5">Manage your password and session</p>
              </div>
              <div class="w-8 h-8 bg-green-50 rounded-xl flex items-center justify-center shrink-0">
                <Shield class="w-4 h-4 text-green-500" />
              </div>
            </div>

            <div class="space-y-3">
              <!-- Password row -->
              <div class="flex items-center justify-between p-3 sm:p-4 bg-gray-50 rounded-xl border border-gray-200 gap-3">
                <div class="flex items-center gap-3 min-w-0">
                  <div class="w-8 h-8 sm:w-9 sm:h-9 bg-white rounded-lg border border-gray-200 flex items-center justify-center shrink-0">
                    <KeyRound class="w-4 h-4 text-gray-500" />
                  </div>
                  <div class="min-w-0">
                    <p class="text-sm font-semibold text-gray-700">Password</p>
                    <p class="text-xs text-gray-400 truncate">Last changed: unknown</p>
                  </div>
                </div>
                <button @click="openPasswordModal"
                  class="shrink-0 flex items-center gap-1.5 text-xs font-medium text-blue-600 bg-blue-50 hover:bg-blue-100 px-2.5 sm:px-3 py-1.5 rounded-lg transition">
                  <Pencil class="w-3 h-3" />
                  <span class="hidden sm:inline">Change</span>
                </button>
              </div>

              <!-- Session row -->
              <div class="flex items-center justify-between p-3 sm:p-4 bg-gray-50 rounded-xl border border-gray-200 gap-3">
                <div class="flex items-center gap-3 min-w-0">
                  <div class="w-8 h-8 sm:w-9 sm:h-9 bg-white rounded-lg border border-gray-200 flex items-center justify-center shrink-0">
                    <LogOut class="w-4 h-4 text-gray-500" />
                  </div>
                  <div class="min-w-0">
                    <p class="text-sm font-semibold text-gray-700">Active Session</p>
                    <p class="text-xs text-gray-400 truncate">Auto-expires after 10 min of inactivity</p>
                  </div>
                </div>
                <button @click="showLogoutConfirm = true"
                  class="shrink-0 flex items-center gap-1.5 text-xs font-medium text-red-500 bg-red-50 hover:bg-red-100 px-2.5 sm:px-3 py-1.5 rounded-lg transition">
                  <LogOut class="w-3 h-3" />
                  <span class="hidden sm:inline">Logout</span>
                </button>
              </div>
            </div>
          </div>

        </div>
      </div>

    </main>
  </div>

  <!-- ── Change Password Modal ── -->
  <Teleport to="body">
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0">
      <div v-if="showPasswordModal" class="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4">
        <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showPasswordModal = false"></div>

        <Transition
          enter-active-class="transition duration-200 ease-out"
          enter-from-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
          enter-to-class="opacity-100 translate-y-0 sm:scale-100">
          <div class="relative bg-white sm:rounded-2xl rounded-t-2xl shadow-2xl w-full sm:max-w-md z-10 overflow-hidden">

            <!-- Header -->
            <div class="flex items-center justify-between px-5 py-4 border-b border-gray-100">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 bg-blue-100 rounded-xl flex items-center justify-center">
                  <KeyRound class="w-4 h-4 text-blue-600" />
                </div>
                <div>
                  <h2 class="text-base font-bold text-gray-800">Change Password</h2>
                  <p class="text-xs text-gray-400">Enter your current and new password</p>
                </div>
              </div>
              <button @click="showPasswordModal = false"
                class="p-2 rounded-lg hover:bg-gray-100 text-gray-400 transition">
                <X class="w-4 h-4" />
              </button>
            </div>

            <!-- Body -->
            <div class="px-5 py-5 space-y-4">
              <div v-if="pwError"
                class="flex items-start gap-2 bg-red-50 border border-red-100 text-red-600 text-sm px-4 py-3 rounded-xl">
                <AlertTriangle class="w-4 h-4 shrink-0 mt-0.5" />
                <span>{{ pwError }}</span>
              </div>
              <div v-if="pwSuccess"
                class="flex items-center gap-2 bg-green-50 border border-green-100 text-green-600 text-sm px-4 py-3 rounded-xl">
                <CheckCircle class="w-4 h-4 shrink-0" />
                {{ pwSuccess }}
              </div>

              <!-- Current password -->
              <div>
                <label class="block text-xs font-semibold text-gray-600 mb-1.5">
                  Current Password <span class="text-red-400">*</span>
                </label>
                <div class="relative">
                  <input v-model="pwForm.currentPassword" :type="showCurrent ? 'text' : 'password'" placeholder="••••••••"
                    class="w-full border border-gray-200 rounded-xl px-4 py-2.5 pr-11 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 transition placeholder:text-gray-300" />
                  <button type="button" @click="showCurrent = !showCurrent"
                    class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 p-1 transition">
                    <EyeOff v-if="showCurrent" class="w-4 h-4" />
                    <Eye v-else class="w-4 h-4" />
                  </button>
                </div>
              </div>

              <!-- New password -->
              <div>
                <label class="block text-xs font-semibold text-gray-600 mb-1.5">
                  New Password <span class="text-red-400">*</span>
                </label>
                <div class="relative">
                  <input v-model="pwForm.newPassword" :type="showNew ? 'text' : 'password'" placeholder="Min. 6 characters"
                    class="w-full border border-gray-200 rounded-xl px-4 py-2.5 pr-11 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 transition placeholder:text-gray-300" />
                  <button type="button" @click="showNew = !showNew"
                    class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 p-1 transition">
                    <EyeOff v-if="showNew" class="w-4 h-4" />
                    <Eye v-else class="w-4 h-4" />
                  </button>
                </div>
                <div class="flex gap-1 mt-2">
                  <div v-for="i in 4" :key="i"
                    :class="['h-1 flex-1 rounded-full transition-all', {
                      'bg-red-400':    pwForm.newPassword.length > 0  && pwForm.newPassword.length < 4  && i === 1,
                      'bg-orange-400': pwForm.newPassword.length >= 4 && pwForm.newPassword.length < 6  && i <= 2,
                      'bg-yellow-400': pwForm.newPassword.length >= 6 && pwForm.newPassword.length < 10 && i <= 3,
                      'bg-green-400':  pwForm.newPassword.length >= 10 && i <= 4,
                      'bg-gray-200':   !(pwForm.newPassword.length > 0),
                    }]">
                  </div>
                </div>
              </div>

              <!-- Confirm password -->
              <div>
                <label class="block text-xs font-semibold text-gray-600 mb-1.5">
                  Confirm New Password <span class="text-red-400">*</span>
                </label>
                <div class="relative">
                  <input v-model="pwForm.confirmPassword" :type="showConfirm ? 'text' : 'password'" placeholder="••••••••"
                    class="w-full border border-gray-200 rounded-xl px-4 py-2.5 pr-11 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 transition placeholder:text-gray-300"
                    :class="{
                      'border-red-300 focus:ring-red-300': pwForm.confirmPassword && pwForm.newPassword !== pwForm.confirmPassword,
                      'border-green-300 focus:ring-green-300': pwForm.confirmPassword && pwForm.newPassword === pwForm.confirmPassword
                    }" />
                  <button type="button" @click="showConfirm = !showConfirm"
                    class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 p-1 transition">
                    <EyeOff v-if="showConfirm" class="w-4 h-4" />
                    <Eye v-else class="w-4 h-4" />
                  </button>
                </div>
                <p v-if="pwForm.confirmPassword && pwForm.newPassword !== pwForm.confirmPassword"
                  class="text-xs text-red-500 mt-1">Passwords do not match</p>
                <p v-if="pwForm.confirmPassword && pwForm.newPassword === pwForm.confirmPassword"
                  class="text-xs text-green-500 mt-1 flex items-center gap-1">
                  <CheckCircle class="w-3 h-3" /> Passwords match
                </p>
              </div>
            </div>

            <!-- Footer -->
            <div class="flex items-center justify-end gap-3 px-5 py-4 bg-gray-50 border-t border-gray-100">
              <button @click="showPasswordModal = false"
                class="px-4 py-2 text-sm font-medium text-gray-500 hover:text-gray-700 rounded-xl hover:bg-gray-100 transition">
                Cancel
              </button>
              <button @click="handleChangePassword" :disabled="pwSubmitting"
                class="flex items-center gap-2 px-5 py-2 bg-blue-600 hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed text-white text-sm font-medium rounded-xl transition shadow-sm">
                <svg v-if="pwSubmitting" class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                </svg>
                <KeyRound v-else class="w-3.5 h-3.5" />
                {{ pwSubmitting ? 'Updating...' : 'Update Password' }}
              </button>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>

  <!-- ── Logout Confirm Modal ── -->
  <Teleport to="body">
    <Transition
      enter-active-class="transition duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0">
      <div v-if="showLogoutConfirm" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="showLogoutConfirm = false"></div>

        <Transition
          enter-active-class="transition duration-200 ease-out"
          enter-from-class="opacity-0 scale-95"
          enter-to-class="opacity-100 scale-100">
          <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-sm p-5 sm:p-6 z-10 text-center">
            <div class="w-14 h-14 sm:w-16 sm:h-16 bg-red-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <LogOut class="w-6 h-6 sm:w-7 sm:h-7 text-red-500" />
            </div>
            <h2 class="text-base sm:text-lg font-bold text-gray-800 mb-1">Log Out</h2>
            <p class="text-sm text-gray-500 mb-5 sm:mb-6">Are you sure you want to end your session?</p>
            <div class="flex items-center gap-3">
              <button @click="showLogoutConfirm = false"
                class="flex-1 px-4 py-2.5 text-sm font-medium text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-xl transition">
                Cancel
              </button>
              <button @click="confirmLogout"
                class="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 text-sm font-medium text-white bg-red-500 hover:bg-red-600 rounded-xl transition">
                <LogOut class="w-4 h-4" />
                Yes, Logout
              </button>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>