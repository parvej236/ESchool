<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'
import { Bell, LogOut, Settings, Lock, User } from 'lucide-vue-next'

const authStore = useAuthStore()
const router = useRouter()
const sidebarOpen = ref(false)
const profileMenuOpen = ref(false)
const profileMenuRef = ref(null)

defineEmits(['toggle-sidebar'])

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
  }
})

</script>

<template>
  <header class="bg-white shadow-sm fixed top-0 right-0 left-64 z-20 border-b border-gray-200 rounded-t-2xl mx-1">
    <div class="flex items-center justify-between px-6 py-2">
      <div class="text-xl font-semibold text-gray-800">
        {{ title }}
      </div>

      <div class="flex items-center space-x-4">
        <button class="relative p-2 rounded-full hover:bg-gray-100">
          <Bell class="w-6 h-6 text-gray-600" />
          <span
            class="absolute top-0 right-0 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">6</span>
        </button>

        <div v-if="authStore.isAuthenticated" ref="profileMenuRef" class="relative flex items-center">
          <button @click.stop="profileMenuOpen = !profileMenuOpen"
            class="flex items-center space-x-2 bg-blue-200/50 hover:bg-blue-300 rounded-full p-1 transition">
            <div
              class="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center text-white font-semibold border-2 border-green-500">
              {{ authStore.user?.username?.charAt(0).toUpperCase() }}
            </div>
            <div class="pr-2">
              <p class="text-sm font-medium text-gray-700">{{ authStore.user?.username }}</p>
            </div>
          </button>

          <!-- Dropdown Menu -->
          <div v-if="profileMenuOpen"
            class="absolute right-0 mt-2 top-full w-56 bg-white rounded-lg shadow-lg border border-gray-200 z-50 py-2">

            <button @click="handleNavigation('/view-profile')"
              class="w-full px-4 py-2 text-left text-gray-700 hover:bg-gray-100 flex items-center space-x-3 transition">
              <User class="w-4 h-4" />
              <span>View Profile</span>
            </button>

            <div class="border-t border-gray-200 my-2"></div>
            <button @click="handleLogout"
              class="w-full px-4 py-2 text-left text-red-600 hover:bg-red-50 flex items-center space-x-3 transition">
              <LogOut class="w-4 h-4" />
              <span>Logout</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>
