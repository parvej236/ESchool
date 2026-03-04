<script setup>
import { RouterView, useRoute, useRouter } from 'vue-router'
import { computed, onMounted } from 'vue'
import Navbar from '@/components/Navbar.vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// Don't show navbar on dashboard page (it has its own layout)
const showNavbar = computed(() => !route.path.startsWith('/dashboard') && !route.path.startsWith('/admin'))

// Validate token on app load – clear it if backend returns 401 (e.g. no user in DB, expired token)
onMounted(async () => {
  await authStore.validateAuth()
  const requiresAuth = route.meta.requiresAuth
  if (requiresAuth && !authStore.isAuthenticatedAfterCheck) {
    router.replace({ name: 'login', query: { redirect: route.fullPath } })
  }
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <Navbar v-if="showNavbar" />
    <RouterView />
  </div>
</template>

<style scoped></style>
