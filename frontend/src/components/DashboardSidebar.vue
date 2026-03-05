<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { LayoutGrid, SquareArrowOutUpRight, Images, LogOut, Users } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<template>
  <aside
    class="w-64 bg-linear-to-br from-purple-300/40 via-white to-blue-50/75 bg-white text-gray-700 min-h-screen fixed left-0 top-0 z-30 border-r border-gray-700/20 rounded-r-2xl">

    <router-link to="/dashboard" class="p-4.5 flex items-center justify-center gap-2">
      <img src="/src/assets/images/quantum-logo-9d5Rton5.png" alt="Amrapari Logo" class="w-9 h-9" />
      <div class="text-3xl font-extrabold text-gray-800">Amrapari</div>
    </router-link>

    <div class="px-4 py-1 text-xs font-semibold text-gray-500 tracking-wider">Main Menu</div>
    <nav class="mx-1">
      <router-link to="/" class="flex items-center px-4 py-3 hover:bg-gray-100 transition-colors">
        <SquareArrowOutUpRight class="w-4 h-6 me-2" />
        Go to Website
      </router-link>

      <router-link to="/dashboard" class="flex items-center px-4 py-3 hover:bg-gray-100 transition-colors"
        active-class="bg-blue-100/70 text-blue-600 font-medium border-l-4 border-blue-600 rounded-l">
        <LayoutGrid class="w-4 h-6 me-2" />
        Dashboard
      </router-link>

      <router-link to="/users" class="flex items-center px-4 py-3 hover:bg-gray-100 transition-colors"
        active-class="bg-blue-100/70 text-blue-600 font-medium border-l-4 border-blue-600 rounded-l">
        <Users class="w-4 h-6 me-2" />
        Users
      </router-link>

      <router-link to="/admin/create-home-slide" class="flex items-center px-4 py-3 hover:bg-gray-100 transition-colors"
        active-class="bg-blue-100/70 text-blue-600 font-medium border-l-4 border-blue-600 rounded-l">
        <Images class="w-4 h-6 me-2" />
        Home Slides
      </router-link>
    </nav>
  </aside>
</template>