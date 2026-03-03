<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)

const API_BASE_URL = 'http://localhost:8080/api/auth'

const handleChangePassword = async () => {
  error.value = ''
  success.value = ''

  if (!newPassword.value || newPassword.value.length < 6) {
    error.value = 'New password must be at least 6 characters'
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }

  loading.value = true

  try {
    const response = await fetch(`${API_BASE_URL}/change-password`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': authStore.getAuthHeader() // JWT
      },
      body: JSON.stringify({
        currentPassword: currentPassword.value,
        newPassword: newPassword.value
      }),
    })

    const data = await response.json().catch(() => ({}))

    if (response.ok) {
      success.value = 'Password changed successfully!'
      currentPassword.value = ''
      newPassword.value = ''
      confirmPassword.value = ''
      setTimeout(() => router.push('/login'), 2000)
    } else {
      error.value = data.error || 'Failed to change password'
    }
  } catch (err) {
    console.error('Change password error:', err)
    error.value = 'Network error. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center px-6">
    <div class="w-full max-w-md bg-white rounded-3xl shadow-xl p-12">

      <h1 class="text-3xl font-bold text-gray-900 mb-6">Change Password</h1>

      <form class="space-y-5" @submit.prevent="handleChangePassword">
        <input v-model="currentPassword" type="password" placeholder="Current Password" required
          class="w-full px-5 py-3 rounded-full border border-gray-300 focus:ring-2 focus:ring-green-500 outline-none" />
        <input v-model="newPassword" type="password" placeholder="New Password" required
          class="w-full px-5 py-3 rounded-full border border-gray-300 focus:ring-2 focus:ring-green-500 outline-none" />
        <input v-model="confirmPassword" type="password" placeholder="Confirm New Password" required
          class="w-full px-5 py-3 rounded-full border border-gray-300 focus:ring-2 focus:ring-green-500 outline-none" />

        <div v-if="error" class="text-red-500 text-sm text-center">{{ error }}</div>
        <div v-if="success" class="text-green-600 text-sm text-center">{{ success }}</div>

        <button type="submit" :disabled="loading"
          class="w-full bg-black text-white py-3 rounded-full font-semibold hover:bg-gray-900 transition">
          <span v-if="loading">Changing...</span>
          <span v-else>Change Password</span>
        </button>
      </form>

    </div>
  </div>
</template>