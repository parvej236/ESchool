<script setup>
import { useAuthStore } from '@/stores/auth'
import { ref } from 'vue'

const authStore = useAuthStore()
const sidebarOpen = ref(false)

defineEmits(['toggle-sidebar'])
</script>

<template>
  <header class="bg-white shadow-sm fixed top-0 right-0 left-64 z-20 border-b border-gray-200">
    <div class="flex items-center justify-between px-6 py-4">
      <button
        @click="$emit('toggle-sidebar')"
        class="lg:hidden p-2 rounded-md hover:bg-gray-100"
      >
        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>

      <div class="flex-1"></div>

      <div class="flex items-center space-x-4">
        <!-- Notifications -->
        <button class="relative p-2 rounded-full hover:bg-gray-100">
          <svg class="w-6 h-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <span class="absolute top-0 right-0 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">6</span>
        </button>

        <!-- Profile -->
        <div class="flex items-center space-x-2">
          <div class="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center text-white font-semibold">
            {{ authStore.user?.username?.charAt(0).toUpperCase() || 'A' }}
          </div>
          <div class="hidden md:block">
            <p class="text-sm font-medium text-gray-700">{{ authStore.user?.username || 'User' }}</p>
            <p class="text-xs text-gray-500">{{ authStore.userRole || 'USER' }}</p>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

