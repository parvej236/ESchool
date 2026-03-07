<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'
import { Bell, LogOut, User, Menu } from 'lucide-vue-next'

const authStore = useAuthStore()
const router = useRouter()
const profileMenuOpen = ref(false)
const profileMenuRef = ref(null)

const emit = defineEmits(['toggle-sidebar'])

const closeProfileMenu = () => {
  profileMenuOpen.value = false
}

const handleClickOutside = (event) => {
  if (profileMenuRef.value && !profileMenuRef.value.contains(event.target)) {
    closeProfileMenu()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

const handleLogout = async () => {
  await authStore.logout()
  router.push('/login')
  profileMenuOpen.value = false
}

const handleNavigation = (path) => {
  router.push(path)
  profileMenuOpen.value = false
}

defineProps({
  title: {
    type: String,
    required: false
  },
  isSidebarCollapsed: {
    type: Boolean,
    default: false
  }
})
</script>

<template>
  <header
    :class="[
      'bg-white shadow-sm fixed top-0 right-0 z-20 border-b border-gray-200 rounded-t-2xl mx-1 transition-all duration-300',
      isSidebarCollapsed ? 'left-18' : 'left-0 lg:left-64'
    ]"
  >
    <div class="flex items-center justify-between px-3 sm:px-6 py-2 gap-2">

      <!-- Left: mobile hamburger + title -->
      <div class="flex items-center gap-3 min-w-0">
        <!-- Mobile menu toggle -->
        <button
          @click="emit('toggle-sidebar')"
          class="lg:hidden shrink-0 p-2 rounded-lg hover:bg-gray-100 text-gray-600 transition-colors"
        >
          <Menu class="w-5 h-5" />
        </button>

        <h1 class="text-base sm:text-xl font-semibold text-gray-800 truncate">
          {{ title }}
        </h1>
      </div>

      <!-- Right: actions -->
      <div class="flex items-center gap-1 sm:gap-3 shrink-0">

        <!-- Bell -->
        <button class="relative p-2 rounded-full hover:bg-gray-100 transition-colors">
          <Bell class="w-5 h-5 text-gray-600" />
          <span
            class="absolute top-0.5 right-0.5 bg-red-500 text-white text-[10px] font-bold rounded-full w-4 h-4 flex items-center justify-center leading-none">
            6
          </span>
        </button>

        <!-- Profile -->
        <div v-if="authStore.isAuthenticated" ref="profileMenuRef" class="relative flex items-center">
          <button
            @click.stop="profileMenuOpen = !profileMenuOpen"
            class="flex items-center gap-2 bg-blue-200/50 hover:bg-blue-300/60 rounded-full pl-1 pr-2 py-1 transition-colors"
          >
            <div
              class="w-8 h-8 sm:w-9 sm:h-9 rounded-full bg-blue-500 flex items-center justify-center text-white font-semibold border-2 border-green-400 text-sm shrink-0">
              {{ authStore.user?.username?.charAt(0).toUpperCase() }}
            </div>
            <!-- Username hidden on very small screens -->
            <p class="hidden sm:block text-sm font-medium text-gray-700 max-w-30 truncate">
              {{ authStore.user?.username }}
            </p>
          </button>

          <!-- Dropdown -->
          <div
            v-if="profileMenuOpen"
            class="absolute right-0 top-full mt-2 w-52 bg-white rounded-xl shadow-lg border border-gray-200 z-50 py-2"
          >
            <div class="px-4 py-2 border-b border-gray-100 mb-1 sm:hidden">
              <p class="text-sm font-semibold text-gray-800 truncate">{{ authStore.user?.username }}</p>
            </div>

            <button
              @click="handleNavigation('/view-profile')"
              class="w-full px-4 py-2 text-left text-gray-700 hover:bg-gray-100 flex items-center gap-3 transition-colors text-sm"
            >
              <User class="w-4 h-4 shrink-0" />
              <span>View Profile</span>
            </button>

            <div class="border-t border-gray-100 my-1"></div>

            <button
              @click="handleLogout"
              class="w-full px-4 py-2 text-left text-red-600 hover:bg-red-50 flex items-center gap-3 transition-colors text-sm"
            >
              <LogOut class="w-4 h-4 shrink-0" />
              <span>Logout</span>
            </button>
          </div>
        </div>

      </div>
    </div>
  </header>
</template>