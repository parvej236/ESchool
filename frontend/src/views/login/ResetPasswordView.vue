<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const token = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)
const isTokenValid = ref(true)

const API_BASE_URL = 'http://localhost:8080/api/auth'

onMounted(() => {
  token.value = route.query.token

  if (!token.value) {
    error.value = 'Reset link is invalid or expired.'
    isTokenValid.value = false
  }
})

const handleResetPassword = async () => {
  error.value = ''
  success.value = ''

  if (!password.value || password.value.length < 6) {
    error.value = 'Password must be at least 6 characters'
    return
  }

  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }

  loading.value = true

  try {
    const response = await fetch(`${API_BASE_URL}/reset-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        token: token.value,
        password: password.value
      })
    })

    const data = await response.json().catch(() => ({}))

    if (response.ok) {
      success.value = 'Password reset successful! Redirecting to login...'
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      error.value = data.error || 'Reset failed. Please try again.'
    }

  } catch (err) {
    error.value = 'Network error. Please try again.'
    console.error('Reset password error:', err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center px-6">
    <div class="w-full max-w-lg bg-white rounded-3xl shadow-xl p-12">

      <h1 class="text-3xl font-bold text-gray-900 mb-3">
        Reset Password
      </h1>

      <p class="text-gray-500 mb-8">
        Enter your new password below.
      </p>

      <div v-if="!isTokenValid" class="text-center text-red-500 mb-4">
        Reset link is invalid or expired.
      </div>

      <form class="space-y-5" @submit.prevent="handleResetPassword">
        
        <input
          v-model="password"
          type="password"
          placeholder="New Password"
          required
          :disabled="!isTokenValid"
          class="w-full px-5 py-3 rounded-full border border-gray-300
          focus:ring-2 focus:ring-green-500 outline-none"
        />

        <input
          v-model="confirmPassword"
          type="password"
          placeholder="Confirm Password"
          required
          :disabled="!isTokenValid"
          class="w-full px-5 py-3 rounded-full border border-gray-300
          focus:ring-2 focus:ring-green-500 outline-none"
        />

        <div v-if="error" class="text-red-500 text-sm text-center">
          {{ error }}
        </div>

        <div v-if="success" class="text-green-600 text-sm text-center">
          {{ success }}
        </div>

        <button
          type="submit"
          :disabled="loading || !isTokenValid"
          class="w-full bg-black text-white py-3 rounded-full font-semibold hover:bg-gray-900 transition"
        >
          <span v-if="loading">Resetting...</span>
          <span v-else>Reset Password</span>
        </button>

      </form>

    </div>
  </div>
</template>