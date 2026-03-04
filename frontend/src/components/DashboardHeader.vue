<script setup>
import { useAuthStore } from '@/stores/auth'
import { ref } from 'vue'
import { Bell } from 'lucide-vue-next'

const authStore = useAuthStore()
const sidebarOpen = ref(false)

defineEmits(['toggle-sidebar'])
</script>

<template>
  <header class="bg-white shadow-sm fixed top-0 right-0 left-64 z-20 border-b border-gray-200 rounded-t-2xl mx-1">
    <div class="flex items-center justify-between px-6 py-2">
      <div class="flex-1 text-xl font-semibold text-gray-800">
        Main Content Page Title
      </div>

      <div class="flex items-center space-x-4">
        <button class="relative p-2 rounded-full hover:bg-gray-100">
          <Bell class="w-6 h-6 text-gray-600" />
          <span
            class="absolute top-0 right-0 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">6</span>
        </button>

        <div v-if="authStore.isAuthenticated" class="flex items-center space-x-2">
          <div
            class="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center text-white font-semibold border-2 border-green-500">
            {{ authStore.user?.username?.charAt(0).toUpperCase() }}
          </div>
          <div class="pr-2">
            <p class="text-sm font-medium text-gray-700">{{ authStore.user?.username }}</p>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>
