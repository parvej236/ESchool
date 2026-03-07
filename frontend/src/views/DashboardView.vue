<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import DashboardHeader from '@/components/DashboardHeader.vue'
import { Users, ShoppingBag, BookOpen, Eye, TrendingUp, TrendingDown, Minus } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const isCollapsed = ref(false)
const isMobileOpen = ref(false)
const pageLoading = ref(true)

const dashboardData = ref(null)
const statistics = ref({
  totalUsers: 0,
  totalOrders: 0,
  totalClasses: 0
})

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  // Simulate data load — replace with real API call
  await new Promise(r => setTimeout(r, 1400))
  pageLoading.value = false
})
</script>

<template>
  <div class="min-h-screen bg-gray-100/80">

    <DashboardSidebar
      :is-collapsed="isCollapsed"
      :is-mobile-open="isMobileOpen"
      @update:is-collapsed="isCollapsed = $event"
      @update:is-mobile-open="isMobileOpen = $event"
    />

    <div
      :class="[
        'transition-all duration-300 ml-0',
        isCollapsed ? 'lg:ml-18' : 'lg:ml-64'
      ]"
    >
      <DashboardHeader
        title="Dashboard"
        :is-sidebar-collapsed="isCollapsed"
        @toggle-sidebar="isMobileOpen = !isMobileOpen"
      />

      <main class="pt-20 px-3 sm:px-5 pb-10">

        <!-- ── SKELETON ─────────────────────────────────── -->
        <template v-if="pageLoading">

          <!-- Welcome banner skeleton -->
          <div class="relative bg-white rounded-2xl p-5 sm:p-8 overflow-hidden border border-gray-100 shadow-sm mb-5 animate-pulse">
            <div class="flex justify-between items-center gap-4">
              <div class="flex-1 space-y-3">
                <!-- Title line -->
                <div class="h-7 bg-gray-200 rounded-lg w-56 sm:w-72"></div>
                <!-- Subtitle lines -->
                <div class="space-y-2">
                  <div class="h-3.5 bg-gray-100 rounded w-full max-w-sm"></div>
                  <div class="h-3.5 bg-gray-100 rounded w-4/5 max-w-xs"></div>
                </div>
                <!-- Button -->
                <div class="h-9 bg-gray-200 rounded-lg w-32 mt-2"></div>
              </div>
              <!-- Illustration placeholder -->
              <div class="hidden md:block shrink-0 w-48 h-32 lg:w-56 lg:h-36 bg-gray-100 rounded-2xl"></div>
            </div>
          </div>

          <!-- Stats skeleton -->
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-5 mb-6">
            <div v-for="i in 3" :key="i"
              :class="['rounded-2xl p-5 sm:p-6 animate-pulse border border-gray-100 shadow-sm bg-white',
                i === 3 ? 'sm:col-span-2 lg:col-span-1' : '']">
              <div class="flex justify-between items-start">
                <div class="space-y-3 flex-1">
                  <div class="h-3 bg-gray-200 rounded w-24"></div>
                  <div class="h-9 bg-gray-200 rounded-lg w-16"></div>
                  <div class="h-3 bg-gray-100 rounded w-32"></div>
                </div>
                <div class="w-12 h-12 bg-gray-200 rounded-xl shrink-0 ml-4"></div>
              </div>
            </div>
          </div>

          <!-- Bottom section skeleton — two cards -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 sm:gap-5">
            <div v-for="j in 2" :key="j"
              class="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 sm:p-6 animate-pulse">
              <div class="flex items-center justify-between mb-5">
                <div class="h-4 bg-gray-200 rounded w-32"></div>
                <div class="h-3 bg-gray-100 rounded w-16"></div>
              </div>
              <div class="space-y-3">
                <div v-for="k in 4" :key="k" class="flex items-center gap-3">
                  <div class="w-8 h-8 bg-gray-200 rounded-lg shrink-0"></div>
                  <div class="flex-1 space-y-1.5">
                    <div class="h-3 bg-gray-200 rounded w-3/4"></div>
                    <div class="h-2.5 bg-gray-100 rounded w-1/2"></div>
                  </div>
                  <div class="h-3 bg-gray-100 rounded w-12"></div>
                </div>
              </div>
            </div>
          </div>
        </template>

        <!-- ── REAL CONTENT ────────────────────────────── -->
        <template v-else>

          <!-- Welcome Banner -->
          <div class="relative bg-linear-to-r from-blue-50 via-sky-50 to-white rounded-2xl p-5 sm:p-8 overflow-hidden border border-gray-100 shadow-sm mb-5">
            <div class="absolute top-0 right-52 w-52 h-52 bg-blue-100 opacity-40 rounded-full -translate-y-16 pointer-events-none"></div>
            <div class="absolute bottom-0 right-0 w-32 h-32 bg-indigo-100 opacity-20 rounded-full translate-x-8 translate-y-8 pointer-events-none"></div>

            <div class="flex justify-between items-center gap-4">
              <div class="flex-1 min-w-0">
                <p class="text-xs sm:text-sm font-semibold text-indigo-400 uppercase tracking-widest mb-1">
                  Welcome back 👋
                </p>
                <div class="text-gray-800 text-xl sm:text-2xl font-bold mb-1 truncate">
                  <span class="text-2xl sm:text-3xl font-extrabold text-indigo-600">
                    {{ dashboardData?.username || authStore.user?.username || 'User' }}
                  </span>
                </div>
                <p class="text-gray-500 text-sm mb-5 leading-relaxed">
                  Manage everything in one place. Be productive and stay organized.
                </p>
                <router-link to="/view-profile"
                  class="inline-flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2.5 rounded-xl font-medium text-sm transition-all shadow-sm hover:shadow-md hover:-translate-y-0.5">
                  <Eye class="w-4 h-4" />
                  View Profile
                </router-link>
              </div>

              <div class="hidden md:block shrink-0">
                <img src="@/assets/images/1.png" alt="Welcome"
                  class="w-44 h-28 lg:w-56 lg:h-36 object-contain drop-shadow-sm" />
              </div>
            </div>
          </div>

          <!-- Statistics Cards -->
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-5 mb-5">

            <!-- Total Users -->
            <div class="group relative bg-white rounded-2xl shadow-sm border border-gray-100 p-5 sm:p-6 overflow-hidden hover:shadow-md transition-all hover:-translate-y-0.5">
              <div class="absolute inset-0 bg-linear-to-br from-green-50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity rounded-2xl"></div>
              <div class="relative flex justify-between items-start">
                <div>
                  <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Total Users</p>
                  <p class="text-3xl sm:text-4xl font-extrabold text-gray-800 mb-2 tabular-nums">
                    {{ statistics.totalUsers.toLocaleString() }}
                  </p>
                  <div class="flex items-center gap-1 text-xs font-medium text-green-600">
                    <TrendingUp class="w-3.5 h-3.5" />
                    <span>+12% this month</span>
                  </div>
                </div>
                <div class="w-11 h-11 sm:w-12 sm:h-12 bg-green-100 group-hover:bg-green-200 rounded-xl flex items-center justify-center transition-colors shrink-0">
                  <Users class="w-5 h-5 sm:w-6 sm:h-6 text-green-600" />
                </div>
              </div>
              <div class="relative mt-4 h-1 bg-gray-100 rounded-full overflow-hidden">
                <div class="h-full bg-linear-to-r from-green-400 to-green-500 rounded-full w-3/4"></div>
              </div>
            </div>

            <!-- Total Orders -->
            <div class="group relative bg-white rounded-2xl shadow-sm border border-gray-100 p-5 sm:p-6 overflow-hidden hover:shadow-md transition-all hover:-translate-y-0.5">
              <div class="absolute inset-0 bg-linear-to-br from-blue-50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity rounded-2xl"></div>
              <div class="relative flex justify-between items-start">
                <div>
                  <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Total Orders</p>
                  <p class="text-3xl sm:text-4xl font-extrabold text-gray-800 mb-2 tabular-nums">
                    {{ statistics.totalOrders.toLocaleString() }}
                  </p>
                  <div class="flex items-center gap-1 text-xs font-medium text-blue-600">
                    <TrendingUp class="w-3.5 h-3.5" />
                    <span>+8% this month</span>
                  </div>
                </div>
                <div class="w-11 h-11 sm:w-12 sm:h-12 bg-blue-100 group-hover:bg-blue-200 rounded-xl flex items-center justify-center transition-colors shrink-0">
                  <ShoppingBag class="w-5 h-5 sm:w-6 sm:h-6 text-blue-600" />
                </div>
              </div>
              <div class="relative mt-4 h-1 bg-gray-100 rounded-full overflow-hidden">
                <div class="h-full bg-linear-to-r from-blue-400 to-blue-500 rounded-full w-1/2"></div>
              </div>
            </div>

            <!-- Total Classes -->
            <div class="group relative bg-white rounded-2xl shadow-sm border border-gray-100 p-5 sm:p-6 overflow-hidden hover:shadow-md transition-all hover:-translate-y-0.5 sm:col-span-2 lg:col-span-1">
              <div class="absolute inset-0 bg-linear-to-br from-purple-50 to-transparent opacity-0 group-hover:opacity-100 transition-opacity rounded-2xl"></div>
              <div class="relative flex justify-between items-start">
                <div>
                  <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">Total Classes</p>
                  <p class="text-3xl sm:text-4xl font-extrabold text-gray-800 mb-2 tabular-nums">
                    {{ statistics.totalClasses.toLocaleString() }}
                  </p>
                  <div class="flex items-center gap-1 text-xs font-medium text-gray-400">
                    <Minus class="w-3.5 h-3.5" />
                    <span>No change</span>
                  </div>
                </div>
                <div class="w-11 h-11 sm:w-12 sm:h-12 bg-purple-100 group-hover:bg-purple-200 rounded-xl flex items-center justify-center transition-colors shrink-0">
                  <BookOpen class="w-5 h-5 sm:w-6 sm:h-6 text-purple-600" />
                </div>
              </div>
              <div class="relative mt-4 h-1 bg-gray-100 rounded-full overflow-hidden">
                <div class="h-full bg-linear-to-r from-purple-400 to-purple-500 rounded-full w-1/4"></div>
              </div>
            </div>

          </div>

          <!-- Bottom placeholder cards — quick links / recent activity -->
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 sm:gap-5">

            <!-- Quick Links -->
            <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 sm:p-6">
              <div class="flex items-center justify-between mb-5">
                <h3 class="text-sm font-bold text-gray-800">Quick Links</h3>
                <span class="text-xs text-gray-400">Shortcuts</span>
              </div>
              <div class="space-y-2">
                <router-link v-for="link in [
                    { to: '/users',               label: 'Manage Users',   sub: 'View and edit all users',   color: 'bg-blue-100 text-blue-600' },
                    { to: '/dashboard/home-slides', label: 'Home Slides',    sub: 'Edit homepage carousel',    color: 'bg-indigo-100 text-indigo-600' },
                    { to: '/view-profile',          label: 'Your Profile',   sub: 'Update account settings',   color: 'bg-green-100 text-green-600' },
                  ]" :key="link.to" :to="link.to"
                  class="flex items-center gap-3 p-3 rounded-xl hover:bg-gray-50 transition-colors group">
                  <div :class="['w-9 h-9 rounded-lg flex items-center justify-center shrink-0 text-sm font-bold', link.color]">
                    {{ link.label.charAt(0) }}
                  </div>
                  <div class="flex-1 min-w-0">
                    <p class="text-sm font-semibold text-gray-700 group-hover:text-indigo-600 transition-colors truncate">{{ link.label }}</p>
                    <p class="text-xs text-gray-400 truncate">{{ link.sub }}</p>
                  </div>
                  <svg class="w-4 h-4 text-gray-300 group-hover:text-indigo-400 transition-colors shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                  </svg>
                </router-link>
              </div>
            </div>

            <!-- System Status -->
            <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 sm:p-6">
              <div class="flex items-center justify-between mb-5">
                <h3 class="text-sm font-bold text-gray-800">System Status</h3>
                <span class="inline-flex items-center gap-1.5 text-xs font-medium text-green-600 bg-green-50 px-2.5 py-1 rounded-full">
                  <span class="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse"></span>
                  All systems operational
                </span>
              </div>
              <div class="space-y-3">
                <div v-for="service in [
                    { name: 'API Server',    status: 'Operational', pct: 99, color: 'bg-green-500' },
                    { name: 'Database',      status: 'Operational', pct: 97, color: 'bg-green-500' },
                    { name: 'File Storage',  status: 'Operational', pct: 95, color: 'bg-green-500' },
                    { name: 'Auth Service',  status: 'Operational', pct: 100, color: 'bg-green-500' },
                  ]" :key="service.name"
                  class="flex items-center gap-3">
                  <div class="w-2 h-2 rounded-full bg-green-400 shrink-0"></div>
                  <span class="text-sm text-gray-700 flex-1 min-w-0 truncate">{{ service.name }}</span>
                  <div class="flex items-center gap-2 shrink-0">
                    <div class="w-20 sm:w-24 h-1.5 bg-gray-100 rounded-full overflow-hidden">
                      <div :class="['h-full rounded-full', service.color]" :style="{ width: service.pct + '%' }"></div>
                    </div>
                    <span class="text-xs font-mono text-gray-400 w-8 text-right">{{ service.pct }}%</span>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </template>

      </main>
    </div>
  </div>
</template>