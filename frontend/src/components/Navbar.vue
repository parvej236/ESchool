<script setup>
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { LayoutGrid } from 'lucide-vue-next'

const authStore = useAuthStore()
</script>

<template>
  <nav class="bg-white shadow-lg">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex">
          <div class="shrink-0 flex items-center">
            <router-link to="/"><img src="/src/assets/logo.jpg" alt="ESchool Logo" class="h-10 w-auto"></router-link>
          </div>
          <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
            <router-link to="/"
              class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              active-class="border-blue-500 text-gray-900">
              Home
            </router-link>
            <router-link to="/vision"
              class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              active-class="border-blue-500 text-gray-900">
              Vision
            </router-link>
          </div>
        </div>
        <div class="flex items-center space-x-4">
          <!-- Authenticated User Menu (only after we've validated token on load) -->
          <template v-if="authStore.isAuthenticatedAfterCheck">
            <router-link to="/dashboard" class="flex items-center px-4 py-2 hover:bg-gray-100 transition-colors border border-gray-700 text-gray-800 font-medium"
              active-class="bg-blue-100/70 text-blue-600 font-medium border-l-4 border-blue-600 rounded-l">
              <LayoutGrid class="w-4 h-6 me-2" />
              Dashboard
            </router-link>
          </template>
          <!-- Unauthenticated User Menu -->
          <template v-else>
            <router-link to="/login"
              class="border-2 border-blue-500 text-blue-600 hover:bg-blue-500 hover:text-white px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2">
              <iconify-icon icon="fa-solid:sign-in-alt" style="font-size: 20px;"></iconify-icon>
              Login
            </router-link>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>
