<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'

const router = useRouter()
const route = useRoute()
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
const fetching = ref(true)

const API_BASE_URL = 'http://localhost:8080'
const slideId = route.params.id

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/dashboard')
    return
  }

  try {
    fetching.value = true
    const response = await fetch(`${API_BASE_URL}/api/home-slides/details/${slideId}`, {
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })

    if (response.ok) {
      const slideData = await response.json()
      title.value = slideData.title || ''
      description.value = slideData.description || ''
      imageUrl.value = slideData.imageUrl || ''
      linkUrl.value = slideData.linkUrl || ''
      displayOrder.value = slideData.displayOrder || 0
      active.value = slideData.active !== undefined ? slideData.active : true
    } else if (response.status === 404) {
      error.value = 'Slide not found'
      setTimeout(() => router.push('/dashboard'), 2000)
    } else {
      error.value = 'Failed to load slide details'
    }
  } catch (err) {
    error.value = 'Network error. Please try again.'
    console.error('Fetch slide error:', err)
  } finally {
    fetching.value = false
  }
})

const handleUpdateSlide = async () => {
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
    const response = await fetch(`${API_BASE_URL}/api/home-slides/${slideId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': authStore.getAuthHeader(),
      },
      body: JSON.stringify({
        title: title.value.trim(),
        description: description.value ? description.value.trim() : null,
        imageUrl: imageUrl.value.trim(),
        linkUrl: linkUrl.value ? linkUrl.value.trim() : null,
        displayOrder: displayOrder.value || 0,
        active: active.value,
      }),
    })

    if (response.ok) {
      success.value = 'Slide updated successfully!'
      setTimeout(() => {
        router.push('/dashboard')
      }, 1500)
    } else {
      const errorData = await response.json().catch(() => ({ error: 'Failed to update slide' }))
      error.value = errorData.error || 'Failed to update slide. Please try again.'
    }
  } catch (err) {
    error.value = 'Network error. Please try again.'
    console.error('Update slide error:', err)
  } finally {
    loading.value = false
  }
}

const handleDeleteSlide = async () => {
  if (!confirm('Are you sure you want to delete this slide? This action cannot be undone.')) {
    return
  }

  loading.value = true
  error.value = ''

  try {
    const response = await fetch(`${API_BASE_URL}/api/home-slides/${slideId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': authStore.getAuthHeader(),
      },
    })

    if (response.ok) {
      success.value = 'Slide deleted successfully!'
      setTimeout(() => {
        router.push('/dashboard')
      }, 1500)
    } else {
      const errorData = await response.json().catch(() => ({ error: 'Failed to delete slide' }))
      error.value = errorData.error || 'Failed to delete slide. Please try again.'
    }
  } catch (err) {
    error.value = 'Network error. Please try again.'
    console.error('Delete slide error:', err)
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
          <h2 class="text-3xl font-bold text-gray-900">Edit Home Slide</h2>
          <p class="mt-2 text-gray-600">Update home slide information</p>
        </div>

        <div v-if="fetching" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="mt-4 text-gray-600">Loading slide details...</p>
        </div>

        <form v-else @submit.prevent="handleUpdateSlide" class="space-y-6">
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
              placeholder="Enter slide description"
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
                  Active
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
              <span v-if="loading">Updating...</span>
              <span v-else>Update Slide</span>
            </button>
            <button
              type="button"
              @click="handleDeleteSlide"
              :disabled="loading"
              class="flex-1 bg-red-600 hover:bg-red-700 text-white px-6 py-3 rounded-md font-medium disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="loading">Deleting...</span>
              <span v-else>Delete Slide</span>
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

