<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  LayoutGrid, SquareArrowOutUpRight, Images, Users,
  PanelLeftClose, PanelLeftOpen, Menu, X,
  BookOpen, Settings, ChevronDown,
  GraduationCap, Tag, Bookmark, UsersRound, Briefcase
} from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const isCollapsed = ref(false)
const isMobileOpen = ref(false)
const menuOpen = ref({ preferences: false })

const preferencesItems = [
  { name: 'Subjects', route: '/dashboard/subjects', icon: BookOpen },
  { name: 'Subject Types', route: '/dashboard/subject-types', icon: Tag },
  { name: 'Groups', route: '/view-profile', icon: UsersRound },
]

const isPreferencesActive = computed(() =>
  preferencesItems.some(item => route.path.startsWith(item.route))
)

// Keep submenu open whenever a child route is active
watch(
  () => route.path,
  () => {
    if (isPreferencesActive.value) menuOpen.value.preferences = true
  },
  { immediate: true }
)

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
  if (isCollapsed.value) menuOpen.value.preferences = false
}

const toggleMobile = () => {
  isMobileOpen.value = !isMobileOpen.value
}

const closeMobile = () => {
  isMobileOpen.value = false
}

const toggleMenu = (menu) => {
  if (isCollapsed.value) return
  menuOpen.value[menu] = !menuOpen.value[menu]
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
    'bg-gradient-to-br from-purple-300/40 via-white to-blue-50/75 text-gray-700 min-h-screen fixed left-0 top-0 z-40',
    'border-r border-gray-700/20 rounded-r-2xl flex flex-col transition-all duration-300 ease-in-out',
    isCollapsed ? 'w-[72px]' : 'w-64',
    isMobileOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
  ]">
    <!-- Logo -->
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

      <button @click="toggleCollapse"
        class="hidden lg:flex shrink-0 items-center justify-center w-7 h-7 rounded-md hover:bg-gray-200/70 text-gray-500 hover:text-gray-800 transition-colors"
        :title="isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'">
        <PanelLeftClose v-if="!isCollapsed" class="w-4 h-4" />
        <PanelLeftOpen v-else class="w-4 h-4" />
      </button>
    </div>

    <!-- Section label: Main Menu -->
    <div :class="[
      'px-4 py-2 text-xs font-semibold text-gray-500 tracking-wider whitespace-nowrap transition-all duration-300',
      isCollapsed ? 'opacity-0' : 'opacity-100'
    ]">
      Main Menu
    </div>

    <nav class="mx-1 flex flex-col gap-0.5 flex-1">

      <!-- Go to Website -->
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

      <!-- Dashboard -->
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

      <!-- Users -->
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

      <!-- Home Slides -->
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

      <!-- Section label: Academic -->
      <div :class="[
        'px-4 pt-3 pb-1 text-xs font-semibold text-gray-500 tracking-wider whitespace-nowrap transition-all duration-300',
        isCollapsed ? 'opacity-0' : 'opacity-100'
      ]">
        Academic
      </div>

      <!-- Preferences (accordion) -->
      <div>
        <button @click="toggleMenu('preferences')" :class="[
          'flex items-center w-full px-3 py-3 hover:bg-gray-100 transition-colors rounded-lg',
          isCollapsed ? 'justify-center' : 'gap-2',
          (menuOpen.preferences || isPreferencesActive) && !isCollapsed
            ? 'bg-blue-50 text-blue-600 font-medium'
            : ''
        ]" :title="isCollapsed ? 'Preferences' : ''">
          <Settings class="w-5 h-5 shrink-0" />
          <span :class="[
            'whitespace-nowrap transition-all duration-300 overflow-hidden flex-1 text-left',
            isCollapsed ? 'opacity-0 w-0' : 'opacity-100'
          ]">Preferences</span>
          <ChevronDown v-if="!isCollapsed" class="w-4 h-4 shrink-0 transition-transform duration-200 text-gray-400"
            :class="menuOpen.preferences ? 'rotate-180 text-blue-500' : ''" />
        </button>

        <!-- Submenu -->
        <Transition enter-active-class="transition-all duration-200 ease-out"
          enter-from-class="opacity-0 -translate-y-1" enter-to-class="opacity-100 translate-y-0"
          leave-active-class="transition-all duration-150 ease-in" leave-from-class="opacity-100 translate-y-0"
          leave-to-class="opacity-0 -translate-y-1">
          <div v-if="menuOpen.preferences && !isCollapsed"
            class="ml-3 mt-0.5 mb-1 flex flex-col gap-0.5 border-l-2 border-blue-100 pl-3">
            <router-link v-for="item in preferencesItems" :key="item.name" :to="item.route"
              class="flex items-center gap-2 px-3 py-2.5 rounded-lg text-sm text-gray-600 hover:bg-gray-100 hover:text-gray-900 transition-colors"
              active-class="bg-blue-100/70 text-blue-600 font-medium border-l-4 border-blue-600 rounded-l"
              @click="closeMobile">
              <component :is="item.icon" class="w-4 h-4 shrink-0 opacity-70" />
              <span class="whitespace-nowrap">{{ item.name }}</span>
            </router-link>
          </div>
        </Transition>
      </div>

      <router-link to="/classes" :class="[
        'flex items-center px-3 py-3 hover:bg-gray-100 transition-colors rounded-lg',
        isCollapsed ? 'justify-center' : 'gap-2'
      ]" active-class="bg-blue-100/70 text-blue-600 font-medium border-l-4 border-blue-600 rounded-l"
        :title="isCollapsed ? 'Classes' : ''" @click="closeMobile">
        <GraduationCap class="w-5 h-5 shrink-0" />
        <span :class="[
          'whitespace-nowrap transition-all duration-300 overflow-hidden',
          isCollapsed ? 'opacity-0 w-0' : 'opacity-100'
        ]">Classes</span>
      </router-link>

    </nav>
  </aside>
</template>