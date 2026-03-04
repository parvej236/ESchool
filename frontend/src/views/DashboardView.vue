<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import DashboardHeader from '@/components/DashboardHeader.vue'
import { Plus, Users, Handbag, BookOpen, Book } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const dashboardData = ref(null)
const statistics = ref({
  totalUsers: 0,
  totalOrders: 0,
  totalClasses: 0
})

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
})
</script>

<template>
  <div class="min-h-screen bg-gray-100">
    <DashboardSidebar />

    <div class="ml-64">
      <DashboardHeader />

      <!-- Main Content Area -->
      <main class="pt-20 px-4 pb-8">

        <div
          class="relative bg-linear-to-r from-blue-50 via-sky-50 to-white rounded-2xl p-8 overflow-hidden border border-gray-100 shadow-sm mb-4">
          <div class="absolute top-0 right-52 w-52 h-52 bg-blue-100 opacity-40 rounded-full -translate-y-16"></div>
          <div class="flex justify-between items-center">
            <div class="flex-1">
              <div class="text-gray-800 text-2xl font-bold mb-0">Welcome, <span
                  class="text-3xl font-extrabold text-indigo-600 mb-3"> {{ dashboardData?.username || 'User' }}</span>
              </div>
              <p class="text-gray-500 text-sm mb-6">
                Manage everything in one place. Be productive and stay organized with your dashboard.
              </p>
              <router-link to="/view-profile"
                class="inline-flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-5 py-2.5 rounded-lg font-medium text-sm transition-all shadow-sm">
                <Plus class="w-4 h-4" />
                View Profile
              </router-link>
            </div>
            <div class="hidden md:block">
              <img src="@/assets/images/1.png" alt="Welcome" class="w-56 h-36 object-contain" />
            </div>
          </div>
        </div>

        <!-- Statistics Cards -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
          <div class="bg-linear-to-br from-green-500 to-green-600 rounded-lg shadow-sm p-6 text-white">
            <div class="flex justify-between items-center">
              <div>
                <p class="text-green-100 text-sm font-medium mb-1">Total Users</p>
                <p class="text-3xl font-bold">{{ statistics.totalUsers }}</p>
              </div>
              <div class="bg-green-400 bg-opacity-30 rounded-full p-3">
              <Users class="w-8 h-8" />
              </div>
            </div>
          </div>

          <div class="bg-linear-to-br from-blue-500 to-blue-600 rounded-lg shadow-sm p-6 text-white">
            <div class="flex justify-between items-center">
              <div>
                <p class="text-blue-100 text-sm font-medium mb-1">Total Orders</p>
                <p class="text-3xl font-bold">{{ statistics.totalOrders }}</p>
              </div>
              <div class="bg-blue-400 bg-opacity-30 rounded-full p-3">
                <Handbag class="w-8 h-8" />
              </div>
            </div>
          </div>

          <div class="bg-linear-to-br from-purple-500 to-purple-600 rounded-lg shadow-sm p-6 text-white">
            <div class="flex justify-between items-center">
              <div>
                <p class="text-purple-100 text-sm font-medium mb-1">Total Classes</p>
                <p class="text-3xl font-bold">{{ statistics.totalClasses }}</p>
              </div>
              <div class="bg-purple-400 bg-opacity-30 rounded-full p-3">
              <BookOpen class="w-8 h-8" />
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>
