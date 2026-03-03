<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'

const router = useRouter()
const authStore = useAuthStore()

const title = ref('')
const description = ref('')
const imageUrl = ref('')
const linkUrl = ref('')
const displayOrder = ref(0)
const active = ref(true)
const error = ref('')
const success = ref('')
const loading = ref(false)

const API_BASE_URL = 'http://localhost:8080'

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  
  if (!authStore.user) {
    await authStore.fetchUserInfo()
  }
  
  if (!authStore.isAdmin) {
    router.push('/dashboard')
  }
})

const handleCreateSlide = async () => {
  error.value = ''
  success.value = ''
  
  if (!title.value || title.value.trim() === '') {
    error.value = 'Title is required'
    return
  }

  if (!imageUrl.value || imageUrl.value.trim() === '') {
    error.value = 'Image URL is required'
    return
  }

  loading.value = true

  try {
    const requestBody = {
      title: title.value.trim(),
      description: description.value ? description.value.trim() : null,
      imageUrl: imageUrl.value.trim(),
      linkUrl: linkUrl.value ? linkUrl.value.trim() : null,
      displayOrder: displayOrder.value || 0,
      active: active.value,
    }

    console.log('Creating slide with data:', requestBody)

    const response = await fetch(`${API_BASE_URL}/api/home-slides`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': authStore.getAuthHeader(),
      },
      body: JSON.stringify(requestBody),
    })

    const responseData = await response.json().catch(() => null)
    console.log('Response status:', response.status, 'Data:', responseData)

    if (response.ok) {
      success.value = 'Home slide created successfully!'
      setTimeout(() => {
        router.push('/dashboard')
      }, 1500)
    } else {
      error.value = responseData?.error || `Failed to create slide. Status: ${response.status}`
    }
  } catch (err) {
    error.value = 'Network error. Please try again.'
    console.error('Create slide error:', err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <DashboardSidebar />
  <div class="ml-64">
    <DashboardHeader @toggle-sidebar="sidebarOpen = !sidebarOpen"/>
    <div class="pt-20 px-6 pb-8">
      <div class="bg-white shadow-lg rounded-lg p-8">
        <div class="mb-6">
          <h2 class="text-3xl font-bold text-gray-900">Create Home Slide</h2>
          <p class="mt-2 text-gray-600">Add a new banner slide for the home page</p>
        </div>

        <form @submit.prevent="handleCreateSlide" class="space-y-6">
          <div>
            <label for="title" class="block text-sm font-medium text-gray-700 mb-1">
              Title <span class="text-red-500">*</span>
            </label>
            <input
              id="title"
              v-model="title"
              type="text"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Enter slide title"
            />
          </div>

          <div>
            <label for="description" class="block text-sm font-medium text-gray-700 mb-1">
              Description
            </label>
            <textarea
              id="description"
              v-model="description"
              rows="3"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="Enter slide description (optional)"
            ></textarea>
          </div>

          <div>
            <label for="imageUrl" class="block text-sm font-medium text-gray-700 mb-1">
              Image URL <span class="text-red-500">*</span>
            </label>
            <input
              id="imageUrl"
              v-model="imageUrl"
              type="url"
              required
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="https://example.com/image.jpg"
            />
            <p class="mt-1 text-xs text-gray-500">Enter the full URL of the image</p>
          </div>

          <div>
            <label for="linkUrl" class="block text-sm font-medium text-gray-700 mb-1">
              Link URL
            </label>
            <input
              id="linkUrl"
              v-model="linkUrl"
              type="url"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              placeholder="https://example.com (optional)"
            />
            <p class="mt-1 text-xs text-gray-500">URL to redirect when slide is clicked (optional)</p>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label for="displayOrder" class="block text-sm font-medium text-gray-700 mb-1">
                Display Order
              </label>
              <input
                id="displayOrder"
                v-model.number="displayOrder"
                type="number"
                min="0"
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
              />
              <p class="mt-1 text-xs text-gray-500">Lower numbers appear first</p>
            </div>

            <div class="flex items-end">
              <div class="flex items-center">
                <input
                  id="active"
                  v-model="active"
                  type="checkbox"
                  class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <label for="active" class="ml-2 block text-sm text-gray-700">
                  Active (visible on home page)
                </label>
              </div>
            </div>
          </div>

          <div v-if="error" class="bg-red-50 border border-red-200 rounded-md p-4">
            <p class="text-red-800 text-sm">{{ error }}</p>
          </div>

          <div v-if="success" class="bg-green-50 border border-green-200 rounded-md p-4">
            <p class="text-green-800 text-sm">{{ success }}</p>
          </div>

          <div class="flex space-x-4">
            <button
              type="submit"
              :disabled="loading"
              class="flex-1 bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-md font-medium disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="loading">Creating...</span>
              <span v-else>Create Slide</span>
            </button>
            <button
              type="button"
              @click="router.push('/dashboard')"
              class="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 px-6 py-3 rounded-md font-medium"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

