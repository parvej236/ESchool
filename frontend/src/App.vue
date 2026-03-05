<script setup>
import { RouterView, useRoute, useRouter } from 'vue-router'
import { computed, onMounted } from 'vue'
import Navbar from '@/components/Navbar.vue'
import { useAuthStore } from '@/stores/auth'

const route     = useRoute()
const router    = useRouter()
const authStore = useAuthStore()

// Don't show navbar on dashboard/admin pages (they have their own layout)
const showNavbar = computed(() =>
  !route.path.startsWith('/dashboard') &&
  !route.path.startsWith('/admin')
)

onMounted(async () => {
  // Validates token expiry, restores user from localStorage,
  // starts inactivity timer + schedules silent token refresh
  await authStore.validateAuth()

  // After validation, redirect to login if route requires auth
  // and the session is not valid
  const requiresAuth = route.meta.requiresAuth
  if (requiresAuth && !authStore.isAuthenticatedAfterCheck) {
    router.replace({
      name:  'login',
      query: { redirect: route.fullPath }
    })
  }
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <Navbar v-if="showNavbar" />
    <RouterView />
  </div>
</template>