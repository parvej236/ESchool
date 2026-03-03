<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)

const API_BASE_URL = 'http://localhost:8080/api/auth'

const handleRegister = async () => {
  error.value = ''
  success.value = ''

  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match'
    return
  }

  if (password.value.length < 6) {
    error.value = 'Password must be at least 6 characters'
    return
  }

  if (email.value.trim() === '') {
    error.value = 'Email is required'
    return
  }

  loading.value = true

  try {
    const response = await fetch(`${API_BASE_URL}/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value,
        email: email.value,
      }),
    })

    if (response.ok) {
      success.value = 'Registration successful! Redirecting to login...'
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      const errorData = await response.json().catch(() => ({ error: 'Registration failed' }))
      error.value = errorData.error || 'Registration failed. Please try again.'
    }
  } catch (err) {
    error.value = 'Network error. Please try again.'
    console.error('Registration error:', err)
  } finally {
    loading.value = false
  }
}
</script>
<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center px-6">
    <div class="w-full max-w-6xl bg-white rounded-3xl shadow-xl grid grid-cols-1 lg:grid-cols-2 overflow-hidden">
      <div class="p-12 flex flex-col justify-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-3">
          Create Account
        </h1>
        <p class="text-gray-500 mb-10">
          Start your journey with us. It only takes a few steps.
        </p>
        <form class="space-y-5" @submit.prevent="handleRegister">
          <input v-model="email" type="email" placeholder="Email" required class="w-full px-5 py-3 rounded-full border border-gray-300
            focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none" />
          <input v-model="username" type="text" placeholder="Username" required class="w-full px-5 py-3 rounded-full border border-gray-300
            focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none" />
          <input v-model="password" type="password" placeholder="Password" required class="w-full px-5 py-3 rounded-full border border-gray-300
            focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none" />
          <input v-model="confirmPassword" type="password" placeholder="Confirm Password" required class="w-full px-5 py-3 rounded-full border border-gray-300
            focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none" />
          <div v-if="error" class="text-red-500 text-sm text-center">
            {{ error }}
          </div>
          <div v-if="success" class="text-green-600 text-sm text-center">
            {{ success }}
          </div>

          <button type="submit" :disabled="loading"
            class="w-full bg-black text-white py-3 rounded-full font-semibold hover:bg-gray-900 transition">
            <span v-if="loading">Registering...</span>
            <span v-else>Create Account</span>
          </button>

          <p class="text-center text-sm text-gray-500 pt-4">
            Already have an account?
            <router-link to="/login" class="text-green-600 font-medium">
              Login here
            </router-link>
          </p>

        </form>
      </div>

      <div class="hidden lg:flex items-center justify-center bg-green-50">
        <div class="text-center p-10">
          <img src="/src/assets/logo.jpg" class="h-24 mx-auto mb-6" alt="Logo" />
          <img src="/src/assets/images/vec_art_stu_1.png" class="w-[420px] mx-auto" alt="Register Illustration" />
          <h3 class="text-xl font-semibold text-gray-800 mt-6">
            Build your workspace
          </h3>
          <p class="text-gray-500 mt-2">
            Organize everything in one place
          </p>
        </div>
      </div>

    </div>

  </div>
</template>
