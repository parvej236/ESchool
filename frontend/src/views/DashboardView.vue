<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import DashboardHeader from '@/components/DashboardHeader.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const dashboardData = ref(null)
const statistics = ref({
  totalUsers: 0,
  totalOrders: 0,
  totalClasses: 0,
  totalRoles: 0
})
const users = ref([])
const classes = ref([])
const roles = ref([])
const homeSlides = ref([])
const loading = ref(false)
const error = ref('')
const activeTab = ref('overview')
const sidebarOpen = ref(false)

const API_BASE_URL = 'http://localhost:8080'

// Search and filter
const searchQuery = ref('')
const filterCategory = ref('all')

const filteredClasses = computed(() => {
  let filtered = classes.value

  // Apply search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(cls => 
      cls.className?.toLowerCase().includes(query) ||
      cls.description?.toLowerCase().includes(query) ||
      cls.instructor?.toLowerCase().includes(query)
    )
  }

  // Apply category filter (active/inactive)
  if (filterCategory.value !== 'all') {
    filtered = filtered.filter(cls => 
      filterCategory.value === 'active' ? cls.active : !cls.active
    )
  }

  return filtered
})

const fetchDashboardData = async () => {
  loading.value = true
  error.value = ''

  try {
    const [dataResponse, statsResponse] = await Promise.all([
      fetch(`${API_BASE_URL}/api/dashboard/data`, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': authStore.getAuthHeader(),
        },
      }),
      fetch(`${API_BASE_URL}/api/dashboard/statistics`, {
        headers: {
          'Authorization': authStore.getAuthHeader(),
        },
      })
    ])

    if (dataResponse.ok) {
      dashboardData.value = await dataResponse.json()
      await authStore.fetchUserInfo()
    }

    if (statsResponse.ok) {
      statistics.value = await statsResponse.json()
    }

    if (dataResponse.status === 401 || statsResponse.status === 401) {
      authStore.logout()
      router.push('/login')
    }
  } catch (err) {
    error.value = 'Network error. Please try again.'
    console.error('Dashboard error:', err)
  } finally {
    loading.value = false
  }
}

const fetchUsers = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/users`, {
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })
    if (response.ok) {
      const data = await response.json()
      users.value = data.users || []
    }
  } catch (err) {
    console.error('Failed to fetch users:', err)
  }
}

const fetchClasses = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/classes/all`, {
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })
    if (response.ok) {
      const data = await response.json()
      classes.value = data.classes || []
    }
  } catch (err) {
    console.error('Failed to fetch classes:', err)
  }
}

const fetchRoles = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/roles`, {
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })
    if (response.ok) {
      const data = await response.json()
      roles.value = data.roles || []
    }
  } catch (err) {
    console.error('Failed to fetch roles:', err)
  }
}

const fetchHomeSlides = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/home-slides/all`, {
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })
    if (response.ok) {
      const data = await response.json()
      homeSlides.value = data.slides || []
    }
  } catch (err) {
    console.error('Failed to fetch home slides:', err)
  }
}

const deleteHomeSlide = async (slideId) => {
  if (!confirm('Are you sure you want to delete this slide?')) return
  
  try {
    const response = await fetch(`${API_BASE_URL}/api/home-slides/${slideId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })
    if (response.ok) {
      await fetchHomeSlides()
    } else {
      const error = await response.json()
      alert(error.error || 'Failed to delete slide')
    }
  } catch (err) {
    alert('Failed to delete slide')
  }
}

const toggleSlideActive = async (slide) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/home-slides/${slide.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': authStore.getAuthHeader(),
      },
      body: JSON.stringify({
        ...slide,
        active: !slide.active
      }),
    })
    if (response.ok) {
      await fetchHomeSlides()
    }
  } catch (err) {
    alert('Failed to update slide')
  }
}

const deleteUser = async (userId) => {
  if (!confirm('Are you sure you want to delete this user?')) return
  
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/users/${userId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })
    if (response.ok) {
      await fetchUsers()
      await fetchDashboardData() // Refresh statistics
    } else {
      const error = await response.json()
      alert(error.error || 'Failed to delete user')
    }
  } catch (err) {
    alert('Failed to delete user')
  }
}

const deleteClass = async (classId) => {
  if (!confirm('Are you sure you want to delete this class?')) return
  
  try {
    const response = await fetch(`${API_BASE_URL}/api/classes/${classId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })
    if (response.ok) {
      await fetchClasses()
      await fetchDashboardData() // Refresh statistics
    } else {
      const error = await response.json()
      alert(error.error || 'Failed to delete class')
    }
  } catch (err) {
    alert('Failed to delete class')
  }
}

const toggleClassActive = async (classInfo) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/classes/${classInfo.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': authStore.getAuthHeader(),
      },
      body: JSON.stringify({
        ...classInfo,
        active: !classInfo.active
      }),
    })
    if (response.ok) {
      await fetchClasses()
    }
  } catch (err) {
    alert('Failed to update class')
  }
}

const updateUserRole = async (userId, newRole) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/users/${userId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': authStore.getAuthHeader(),
      },
      body: JSON.stringify({
        role: newRole
      }),
    })
    
    if (response.ok) {
      await fetchUsers()
    } else {
      const error = await response.json()
      alert(error.error || 'Failed to update user role')
      await fetchUsers()
    }
  } catch (err) {
    alert('Failed to update user role')
    await fetchUsers()
  }
}

const deleteRole = async (roleId) => {
  if (!confirm('Are you sure you want to delete this role? Users with this role will need to be reassigned.')) return
  
  try {
    const response = await fetch(`${API_BASE_URL}/api/admin/roles/${roleId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })
    if (response.ok) {
      await fetchRoles()
      await fetchDashboardData() // Refresh statistics
    } else {
      const error = await response.json()
      alert(error.error || 'Failed to delete role')
    }
  } catch (err) {
    alert('Failed to delete role')
  }
}

// Watch for tab changes and refresh data
watch(activeTab, (newTab) => {
  if (authStore.isAdmin) {
    if (newTab === 'users') {
      fetchUsers()
    } else if (newTab === 'classes') {
      fetchClasses()
    } else if (newTab === 'roles') {
      fetchRoles()
    } else if (newTab === 'home-slides') {
      fetchHomeSlides()
    }
  }
})

// Check route query for initial tab
onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  // Check if there's a tab in the route query
  const tabFromQuery = route.query.tab
  if (tabFromQuery && ['overview', 'users', 'classes', 'roles', 'home-slides'].includes(tabFromQuery)) {
    activeTab.value = tabFromQuery
  }

    await fetchDashboardData()
    if (authStore.isAdmin) {
      await fetchUsers()
      await fetchClasses()
      await fetchRoles()
      await fetchHomeSlides()
    }
})
</script>

<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Main Content -->
    <div class="ml-64">
      <!-- Header -->
      <DashboardHeader @toggle-sidebar="sidebarOpen = !sidebarOpen" />

      <!-- Main Content Area -->
      <main class="pt-20 px-6 pb-8">
        <!-- Welcome Section -->
        <div v-if="activeTab === 'overview'" class="bg-white rounded-lg shadow-sm p-8 mb-6">
          <div class="flex justify-between items-center">
            <div>
              <h1 class="text-3xl font-bold text-gray-900 mb-2">
                Welcome, {{ dashboardData?.username || 'User' }}
              </h1>
              <p class="text-gray-600">
                Here's what's happening on your store today. See the statistics at once.
              </p>
              <router-link
                to="/view-profile"
                class="inline-block mt-4 bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-md font-medium me-2"
              >
                View Profile
              </router-link>
              <router-link
                v-if="authStore.isAdmin"
                to="/admin/create-class"
                class="inline-block mt-4 bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-md font-medium"
              >
                + Add Class
              </router-link>
            </div>
            <div>
              <img src="@/assets/images/1.png" alt="Welcome Image" class="w-58 h-38 object-contain" />
            </div>
          </div>
        </div>

        <!-- Statistics Cards -->
        <div v-if="activeTab === 'overview'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
          <div class="bg-gradient-to-br from-green-500 to-green-600 rounded-lg shadow-sm p-6 text-white">
            <div class="flex justify-between items-center">
              <div>
                <p class="text-green-100 text-sm font-medium mb-1">Total Users</p>
                <p class="text-3xl font-bold">{{ statistics.totalUsers }}</p>
              </div>
              <div class="bg-green-400 bg-opacity-30 rounded-full p-3">
                <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              </div>
            </div>
          </div>

          <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg shadow-sm p-6 text-white">
            <div class="flex justify-between items-center">
              <div>
                <p class="text-blue-100 text-sm font-medium mb-1">Total Orders</p>
                <p class="text-3xl font-bold">{{ statistics.totalOrders }}</p>
              </div>
              <div class="bg-blue-400 bg-opacity-30 rounded-full p-3">
                <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
            </div>
          </div>

          <div class="bg-gradient-to-br from-purple-500 to-purple-600 rounded-lg shadow-sm p-6 text-white">
            <div class="flex justify-between items-center">
              <div>
                <p class="text-purple-100 text-sm font-medium mb-1">Total Classes</p>
                <p class="text-3xl font-bold">{{ statistics.totalClasses }}</p>
              </div>
              <div class="bg-purple-400 bg-opacity-30 rounded-full p-3">
                <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </div>
            </div>
          </div>

          <div class="bg-gradient-to-br from-red-500 to-red-600 rounded-lg shadow-sm p-6 text-white">
            <div class="flex justify-between items-center">
              <div>
                <p class="text-red-100 text-sm font-medium mb-1">Total Roles</p>
                <p class="text-3xl font-bold">{{ statistics.totalRoles }}</p>
              </div>
              <div class="bg-red-400 bg-opacity-30 rounded-full p-3">
                <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
              </div>
            </div>
          </div>
        </div>


        <!-- Loading State -->
        <div v-if="loading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="mt-4 text-gray-600">Loading dashboard data...</p>
        </div>

        <!-- Error State -->
        <div v-if="error && !loading" class="bg-red-50 border border-red-200 rounded-md p-4">
          <p class="text-red-800">{{ error }}</p>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
/* Ensure sidebar doesn't overlap on mobile */
@media (max-width: 1024px) {
  .ml-64 {
    margin-left: 0;
  }
}
</style>
