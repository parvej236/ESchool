<script setup>
import { RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'
import Navbar from '@/components/Navbar.vue'

const route = useRoute()

// Don't show navbar on dashboard page (it has its own layout)
const showNavbar = computed(() => !route.path.startsWith('/dashboard') && !route.path.startsWith('/admin'))
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <Navbar v-if="showNavbar" />
    <RouterView />
  </div>
</template>

<style scoped></style>
