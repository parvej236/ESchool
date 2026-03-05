<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router    = useRouter()
const route     = useRoute()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const error    = ref('')
const loading  = ref(false)

// ── Session expired banner ─────────────────────────────
// Shows when redirected here with ?reason=session_expired
const sessionExpired = computed(() => route.query.reason === 'session_expired')

// ── Redirect if already logged in ─────────────────────
onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push('/dashboard')
  }
})

// ── Login ──────────────────────────────────────────────
const handleLogin = async () => {
  error.value   = ''
  loading.value = true

  try {
    const result = await authStore.login(username.value, password.value)

    if (result.success) {
      const redirect = route.query.redirect || '/dashboard'
      router.push(redirect)
    } else {
      error.value = result.message
    }
  } catch (err) {
    error.value = 'Something went wrong. Please try again.'
    console.error('Login error:', err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center px-6">
    <div class="w-full max-w-6xl bg-white rounded-3xl shadow-xl grid grid-cols-1 lg:grid-cols-2 overflow-hidden">

      <!-- LEFT: Login Form -->
      <div class="p-12 flex flex-col justify-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-3">Welcome back!</h1>
        <p class="text-gray-500 mb-6">Simplify your workflow and boost your productivity.</p>

        <!-- ✅ Session expired banner — inside the left column, above the form -->
        <div v-if="sessionExpired"
          class="mb-6 flex items-center gap-2 bg-amber-50 border border-amber-200 text-amber-700 text-sm px-4 py-3 rounded-xl">
          <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M12 9v2m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/>
          </svg>
          Your session expired due to inactivity. Please log in again.
        </div>

        <form class="space-y-6" @submit.prevent="handleLogin">

          <div>
            <input
              v-model="username"
              type="text"
              placeholder="Username"
              required
              autocomplete="username"
              class="w-full px-5 py-3 rounded-full border border-gray-300 focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none"
            />
          </div>

          <div>
            <input
              v-model="password"
              type="password"
              placeholder="Password"
              required
              autocomplete="current-password"
              class="w-full px-5 py-3 rounded-full border border-gray-300 focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none"
            />
          </div>

          <!-- Error message -->
          <div v-if="error"
            class="flex items-center gap-2 bg-red-50 border border-red-100 text-red-600 text-sm px-4 py-3 rounded-xl">
            <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M12 9v2m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/>
            </svg>
            {{ error }}
          </div>

          <div class="text-right text-sm text-gray-500">
            <router-link to="/forgot-password" class="hover:underline">Forgot Password?</router-link>
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-black text-white py-3 rounded-full font-semibold hover:bg-gray-900 transition disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2">
            <svg v-if="loading" class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
            </svg>
            {{ loading ? 'Signing in...' : 'Login' }}
          </button>

          <p class="text-center text-sm text-gray-500 pt-4">
            Not a member?
            <router-link to="/register" class="text-green-600 font-medium">Register now</router-link>
          </p>

        </form>
      </div>

      <!-- RIGHT: Illustration -->
      <div class="hidden lg:flex items-center justify-center bg-green-50">
        <div class="text-center p-10">
          <img src="/src/assets/images/quantum-logo-9d5Rton5.png" class="h-24 mx-auto mb-6" alt="Logo" />
          <img src="/src/assets/images/vec_art_stu_1.png" class="w-105 mx-auto" alt="Illustration" />
          <h3 class="text-xl font-semibold text-gray-800 mt-6">Build your workspace</h3>
          <p class="text-gray-500 mt-2">Organize everything in one place</p>
        </div>
      </div>

    </div>
  </div>
</template>