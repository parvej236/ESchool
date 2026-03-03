<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const expandedSections = ref({
  homeSlides: false,
  category: false,
  products: false,
  users: false,
  orders: false,
  banners: false,
  blogs: false
})

const toggleSection = (section) => {
  expandedSections.value[section] = !expandedSections.value[section]
}

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<template>
  <aside class="w-64 bg-white text-gray-700 min-h-screen fixed left-0 top-0 z-30 border-r border-gray-200">
    <!-- Logo -->
    <div class="p-[18px] border-b border-gray-200 flex items-center justify-center">
      <img src="/src/assets/logo.jpg" alt="ESchool Logo" class="w-10 h-10" />
      <router-link to="/dashboard">
        <h2 class="text-3xl font-extrabold text-gray-900">ESchool</h2>
      </router-link>
    </div>

    <nav class="mt-2">
      <!-- Home -->
      <router-link to="/" class="flex items-center px-4 py-3 hover:bg-gray-100 transition-colors">
        <svg class="w-5 h-5 mr-3 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M3 12l7-7 7 7v8a1 1 0 01-1 1h-4v-4H9v4H5a1 1 0 01-1-1z" />
        </svg>
        Go to Site
      </router-link>

      <!-- Dashboard -->
      <router-link to="/dashboard" class="flex items-center px-4 py-3 hover:bg-gray-100 transition-colors"
        active-class="bg-blue-50 text-blue-600 font-medium">
        <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
        </svg>
        Dashboard
      </router-link>

      <!-- Admin Sections -->
      <div v-if="authStore.isAdmin">
        <!-- Home Slides -->
        <button @click="toggleSection('homeSlides')"
          class="w-full flex items-center justify-between px-4 py-3 hover:bg-gray-100 transition-colors">
          <div class="flex items-center">
            <svg class="w-5 h-5 mr-3 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14" />
            </svg>
            Home Slides
          </div>
          <svg class="w-4 h-4 text-gray-400 transition-transform" :class="{ 'rotate-180': expandedSections.homeSlides }"
            fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
          </svg>
        </button>

        <div v-if="expandedSections.homeSlides" class="bg-gray-50 pl-12 border-l border-gray-200">
          <router-link to="/dashboard?tab=home-slides" class="block px-4 py-2 text-sm hover:bg-gray-100 rounded">
            Home Banners List
          </router-link>
          <router-link to="/admin/create-home-slide" class="block px-4 py-2 text-sm hover:bg-gray-100 rounded">
            Add Home Banner Slide
          </router-link>
        </div>
      </div>
      <!-- Logout -->
      <button @click="handleLogout"
        class="w-full flex items-center px-4 py-3 hover:bg-red-50 text-red-600 transition-colors text-left">
        <svg class="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7" />
        </svg>
        Logout
      </button>
    </nav>
  </aside>
</template>