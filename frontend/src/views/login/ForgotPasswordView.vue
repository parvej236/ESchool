<script setup>
import { ref } from 'vue'

const email = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)

const API_BASE_URL = 'http://localhost:8080/api/auth'

const handleForgotPassword = async () => {
  error.value = ''
  success.value = ''

  if (!email.value.trim()) {
    error.value = 'Email is required'
    return
  }

  loading.value = true

  try {
    const response = await fetch(`${API_BASE_URL}/forgot-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value.trim() }),
    })

    const data = await response.json().catch(() => ({}))

    if (response.ok) {
      success.value = 'Password reset link has been sent to your email.'
      email.value = ''
    } else {
      // Show backend error if email does not exist
      error.value = data.error || 'Failed to send reset link. Please try again.'
    }

  } catch (err) {
    console.error('Forgot password error:', err)
    error.value = 'Network error. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center px-6">
    <div class="w-full max-w-6xl bg-white rounded-3xl shadow-xl grid grid-cols-1 lg:grid-cols-2 overflow-hidden">

      <!-- Left Section -->
      <div class="p-12 flex flex-col justify-center">
        <h1 class="text-4xl font-bold text-gray-900 mb-3">
          Forgot Password
        </h1>
        <p class="text-gray-500 mb-10">
          Enter your email address and we’ll send you a link to reset your password.
        </p>

        <form class="space-y-5" @submit.prevent="handleForgotPassword">
          <input v-model="email" type="email" placeholder="Email Address" required class="w-full px-5 py-3 rounded-full border border-gray-300
            focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none" />

          <div v-if="error" class="text-red-500 text-sm text-center">
            {{ error }}
          </div>

          <div v-if="success" class="text-green-600 text-sm text-center">
            {{ success }}
          </div>

          <button type="submit" :disabled="loading"
            class="w-full bg-black text-white py-3 rounded-full font-semibold hover:bg-gray-900 transition">
            <span v-if="loading">Sending...</span>
            <span v-else>Send Reset Link</span>
          </button>
        </form>
      </div>

      <!-- Right Section -->
      <div class="hidden lg:flex items-center justify-center bg-green-50">
        <div class="text-center p-10">
          <img src="/src/assets/images/quantum-logo-9d5Rton5.png" class="h-24 mx-auto mb-6" alt="Logo" />
          <img src="/src/assets/images/vec_art_stu_1.png" class="w-[420px] mx-auto"
            alt="Forgot Password Illustration" />
          <h3 class="text-xl font-semibold text-gray-800 mt-6">
            Secure Your Account
          </h3>
          <p class="text-gray-500 mt-2">
            We’ll help you get back in safely.
          </p>
        </div>
      </div>

    </div>
  </div>
</template>