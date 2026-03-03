<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const API_BASE_URL = 'http://localhost:8080/api/auth'

// Redirect if already authenticated
onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push('/dashboard')
  }
})

const handleLogin = async () => {
  error.value = ''
  loading.value = true

  try {
    const response = await fetch(`${API_BASE_URL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value,
      }),
    })

    if (response.ok) {
      const data = await response.json()
      if (data.token) {
        authStore.setToken(data.token)
        // Redirect to dashboard or the original destination
        const redirect = route.query.redirect || '/dashboard'
        router.push(redirect)
      } else {
        error.value = 'Invalid credentials'
      }
    } else {
      const errorData = await response.json().catch(() => ({ error: 'Login failed' }))
      error.value = errorData.error || 'Login failed. Please check your credentials.'
    }
  } catch (err) {
    error.value = 'Network error. Please try again.'
    console.error('Login error:', err)
  } finally {
    loading.value = false
  }
}
</script>
<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center px-6">
    <div class="w-full max-w-6xl bg-white rounded-3xl shadow-xl grid grid-cols-1 lg:grid-cols-2 overflow-hidden">
      <!-- LEFT SIDE (LOGIN FORM) -->
      <div class="p-12 flex flex-col justify-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-3">
          Welcome back!
        </h1>
        <p class="text-gray-500 mb-10">
          Simplify your workflow and boost your productivity.
        </p>
        <form class="space-y-6" @submit.prevent="handleLogin">
          <div>
            <input v-model="username" type="text" placeholder="Username" required class="w-full px-5 py-3 rounded-full border border-gray-300
              focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none" />
          </div>
          <div>
            <input v-model="password" type="password" placeholder="Password" required class="w-full px-5 py-3 rounded-full border border-gray-300
              focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none" />
          </div>
          <div v-if="error" class="text-red-500 text-sm text-center">
            {{ error }}
          </div>
          <div class="text-right text-sm text-gray-500">
            <router-link to="/forgot-password" class="hover:underline">
              Forgot Password?
            </router-link>
          </div>
          <button type="submit" :disabled="loading"
            class="w-full bg-black text-white py-3 rounded-full font-semibold hover:bg-gray-900 transition">
            <span v-if="loading">Signing in...</span>
            <span v-else>Login</span>
          </button>

          <p class="text-center text-sm text-gray-500 pt-4">
            Not a member?
            <router-link to="/register" class="text-green-600 font-medium">
              Register now
            </router-link>
          </p>

        </form>
      </div>

      <!-- RIGHT SIDE (ILLUSTRATION PANEL) -->
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
