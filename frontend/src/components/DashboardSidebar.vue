<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { LayoutGrid, SquareArrowOutUpRight, Images, LogOut, Users, PanelLeftClose, PanelLeftOpen, Menu, X } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const isCollapsed = ref(false)
const isMobileOpen = ref(false)

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const toggleMobile = () => {
  isMobileOpen.value = !isMobileOpen.value
}

const closeMobile = () => {
  isMobileOpen.value = false
}

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<template>
  <!-- Mobile menu button -->
  <button @click="toggleMobile"
    class="lg:hidden fixed top-4 left-4 z-50 p-2 rounded-lg bg-white shadow-md border border-gray-200 text-gray-700 hover:bg-gray-50 transition-colors">
    <Menu v-if="!isMobileOpen" class="w-5 h-5" />
    <X v-else class="w-5 h-5" />
  </button>

  <!-- Mobile overlay -->
  <div v-if="isMobileOpen" @click="closeMobile" class="lg:hidden fixed inset-0 bg-black/30 z-30 backdrop-blur-sm" />

  <!-- Sidebar -->
  <aside :class="[
    'bg-linear-to-br from-purple-300/40 via-white to-blue-50/75 text-gray-700 min-h-screen fixed left-0 top-0 z-40 border-r border-gray-700/20 rounded-r-2xl flex flex-col transition-all duration-300 ease-in-out',
    isCollapsed ? 'w-18' : 'w-64',
    isMobileOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
  ]">
    <!-- Logo + App name + Collapse button -->
    <div class="flex items-center justify-between px-2 py-3 border-b border-gray-200/60">
      <router-link to="/dashboard" class="flex items-center gap-2 overflow-hidden" @click="closeMobile">
        <img src="/src/assets/images/quantum-logo-9d5Rton5.png" alt="Amrapari Logo" class="w-9 h-9 shrink-0" />
        <div :class="[
          'text-2xl font-extrabold text-gray-800 whitespace-nowrap transition-all duration-300',
          isCollapsed ? 'opacity-0 w-0 overflow-hidden' : 'opacity-100 w-auto'
        ]">
          Amrapari
        </div>
      </router-link>

      <!-- Collapse button — hidden on mobile -->
      <button @click="toggleCollapse"
        class="hidden lg:flex shrink-0 items-center justify-center w-7 h-7 rounded-md hover:bg-gray-200/70 text-gray-500 hover:text-gray-800 transition-colors"
        :title="isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'">
        <PanelLeftClose v-if="!isCollapsed" class="w-4 h-4" />
        <PanelLeftOpen v-else class="w-4 h-4" />
      </button>
    </div>

    <!-- Section label -->
    <div :class="[
      'px-4 py-2 text-xs font-semibold text-gray-500 tracking-wider whitespace-nowrap transition-all duration-300',
      isCollapsed ? 'opacity-0' : 'opacity-100'
    ]">
      Main Menu
    </div>

    <!-- Nav links -->
    <nav class="mx-1 flex flex-col gap-0.5 flex-1">
      <router-link to="/" :class="[
        'flex items-center px-3 py-3 hover:bg-gray-100 transition-colors rounded-lg',
        isCollapsed ? 'justify-center' : 'gap-2'
      ]" :title="isCollapsed ? 'Go to Website' : ''" @click="closeMobile">
        <SquareArrowOutUpRight class="w-5 h-5 shrink-0" />
        <span :class="[
          'whitespace-nowrap transition-all duration-300 overflow-hidden',
          isCollapsed ? 'opacity-0 w-0' : 'opacity-100'
        ]">Go to Website</span>
      </router-link>

      <router-link to="/dashboard" :class="[
        'flex items-center px-3 py-3 hover:bg-gray-100 transition-colors rounded-lg',
        isCollapsed ? 'justify-center' : 'gap-2'
      ]" active-class="bg-blue-100/70 text-blue-600 font-medium border-l-4 border-blue-600 rounded-l"
        :title="isCollapsed ? 'Dashboard' : ''" @click="closeMobile">
        <LayoutGrid class="w-5 h-5 shrink-0" />
        <span :class="[
          'whitespace-nowrap transition-all duration-300 overflow-hidden',
          isCollapsed ? 'opacity-0 w-0' : 'opacity-100'
        ]">Dashboard</span>
      </router-link>

      <router-link to="/users" :class="[
        'flex items-center px-3 py-3 hover:bg-gray-100 transition-colors rounded-lg',
        isCollapsed ? 'justify-center' : 'gap-2'
      ]" active-class="bg-blue-100/70 text-blue-600 font-medium border-l-4 border-blue-600 rounded-l"
        :title="isCollapsed ? 'Users' : ''" @click="closeMobile">
        <Users class="w-5 h-5 shrink-0" />
        <span :class="[
          'whitespace-nowrap transition-all duration-300 overflow-hidden',
          isCollapsed ? 'opacity-0 w-0' : 'opacity-100'
        ]">Users</span>
      </router-link>

      <router-link to="/dashboard/home-slides" :class="[
        'flex items-center px-3 py-3 hover:bg-gray-100 transition-colors rounded-lg',
        isCollapsed ? 'justify-center' : 'gap-2'
      ]" active-class="bg-blue-100/70 text-blue-600 font-medium border-l-4 border-blue-600 rounded-l"
        :title="isCollapsed ? 'Home Slides' : ''" @click="closeMobile">
        <Images class="w-5 h-5 shrink-0" />
        <span :class="[
          'whitespace-nowrap transition-all duration-300 overflow-hidden',
          isCollapsed ? 'opacity-0 w-0' : 'opacity-100'
        ]">Home Slides</span>
      </router-link>
    </nav>

    <!-- Logout at bottom -->
    <div class="mx-1 mb-3">
      <button @click="handleLogout" :class="[
        'w-full flex items-center px-3 py-3 hover:bg-red-50 hover:text-red-600 transition-colors rounded-lg text-gray-600',
        isCollapsed ? 'justify-center' : 'gap-2'
      ]" :title="isCollapsed ? 'Logout' : ''">
        <LogOut class="w-5 h-5 shrink-0" />
        <span :class="[
          'whitespace-nowrap transition-all duration-300 overflow-hidden',
          isCollapsed ? 'opacity-0 w-0' : 'opacity-100'
        ]">Logout</span>
      </button>
    </div>
  </aside>

  <!-- Main content offset — add this class to your layout wrapper -->
  <!-- lg:pl-64 (expanded) or lg:pl-[72px] (collapsed) — manage via parent if needed -->
</template>