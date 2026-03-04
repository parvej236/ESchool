<template>
  <div class="p-8 max-w-3xl mx-auto">
    <h2 class="text-2xl font-bold mb-6">Account Settings</h2>

    <!-- Loading -->
    <div v-if="loading" class="text-gray-500">
      Loading profile...
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-red-500">
      {{ error }}
    </div>

    <!-- Profile Data -->
    <div v-else-if="user" class="space-y-3">

      <div class="bg-white shadow rounded p-4">
        <p><strong>Email:</strong> {{ user.email }}</p>
        <p><strong>Username:</strong> {{ user.username }}</p>
      </div>

      <div class="flex gap-4 mt-4">
        <router-link to="/change-password">
          <button
            class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
          >
            Change Password
          </button>
        </router-link>

        <button
          @click="logout"
          class="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 transition"
        >
          Logout
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const user = ref(null)
const loading = ref(true)
const error = ref('')

const API_BASE_URL = 'http://localhost:8080/api'

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  try {
    // If user already loaded in store, use it
    if (authStore.user) {
      user.value = authStore.user
    } else {
      await authStore.fetchUserInfo()
      user.value = authStore.user
    }

    if (!user.value) {
      error.value = 'Failed to load profile.'
    }

  } catch (err) {
    console.error(err)
    error.value = 'Something went wrong while loading profile.'
  } finally {
    loading.value = false
  }
})

function logout() {
  authStore.logout()
  router.push('/login')
}
</script>