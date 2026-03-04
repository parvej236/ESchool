<script setup>
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import { ref, onMounted } from 'vue'

const classes = ref([])
const homeSlides = ref([])
const loading = ref(false)
const error = ref('')
const currentSlideIndex = ref(0)

const API_BASE_URL = 'http://localhost:8080'

const fetchActiveClasses = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/classes/active`)
    if (response.ok) {
      const data = await response.json()
      classes.value = data.classes || []
    }
  } catch (err) {
    console.error('Classes error:', err)
  }
}

const fetchHomeSlides = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/home-slides/active`)
    if (response.ok) {
      const data = await response.json()
      homeSlides.value = data.slides || []
      console.log('Fetched home slides:', homeSlides.value) // Debug log
      // Auto-rotate slides every 5 seconds
      if (homeSlides.value.length > 1) {
        setInterval(() => {
          currentSlideIndex.value = (currentSlideIndex.value + 1) % homeSlides.value.length
        }, 5000)
      }
    }
  } catch (err) {
    console.error('Home slides error:', err)
  }
}

const goToSlide = (index) => {
  currentSlideIndex.value = index
}

const nextSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value + 1) % homeSlides.value.length
}

const prevSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value - 1 + homeSlides.value.length) % homeSlides.value.length
}

onMounted(() => {
  loading.value = true
  Promise.all([fetchActiveClasses(), fetchHomeSlides()]).finally(() => {
    loading.value = false
  })
})
</script>

<template>
  <div class="min-h-screen">
    <!-- Home Slides Carousel -->
    <section v-if="homeSlides.length > 0" class="relative w-full h-125 overflow-hidden bg-gray-900">
      <div class="relative h-full">
        <div v-for="(slide, index) in homeSlides" :key="slide.id"
          :class="{ 'opacity-100 z-10': index === currentSlideIndex, 'opacity-0 z-0': index !== currentSlideIndex }"
          class="absolute inset-0 transition-opacity duration-500 ease-in-out">
          <div class="w-full h-full bg-cover bg-center relative image-bg">
            <div class="absolute inset-0 bg-black bg-opacity-40"></div>
            <div class="relative h-full flex items-center justify-center bg-cover" :style="{ backgroundImage: `url(${slide.imageUrl})` }">
              <div class="text-center text-white px-4 max-w-4xl">
                <h1 class="text-5xl md:text-6xl font-bold mb-4">{{ slide.title }}</h1>
                <p v-if="slide.description" class="text-xl md:text-2xl mb-6">{{ slide.description }}</p>
                <a v-if="slide.linkUrl" :href="slide.linkUrl"
                  class="inline-block bg-blue-600 hover:bg-blue-700 text-white px-8 py-3 rounded-md font-semibold text-lg transition-colors">
                  Learn More
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Navigation Arrows -->
      <button v-if="homeSlides.length > 1" @click="prevSlide"
        class="absolute left-4 top-1/2 transform -translate-y-1/2 bg-white bg-opacity-75 hover:bg-opacity-100 rounded-full p-2 z-20 transition-all"
        aria-label="Previous slide">
        <svg class="w-6 h-6 text-gray-800" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
      <button v-if="homeSlides.length > 1" @click="nextSlide"
        class="absolute right-4 top-1/2 transform -translate-y-1/2 bg-white bg-opacity-75 hover:bg-opacity-100 rounded-full p-2 z-20 transition-all"
        aria-label="Next slide">
        <svg class="w-6 h-6 text-gray-800" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>

      <!-- Slide Indicators -->
      <div v-if="homeSlides.length > 1"
        class="absolute bottom-4 left-1/2 transform -translate-x-1/2 z-20 flex space-x-2">
        <button v-for="(slide, index) in homeSlides" :key="slide.id" @click="goToSlide(index)"
          :class="index === currentSlideIndex ? 'bg-white' : 'bg-white bg-opacity-50'"
          class="w-3 h-3 rounded-full transition-all" :aria-label="`Go to slide ${index + 1}`"></button>
      </div>
    </section>

    <!-- Hero Section (fallback if no slides) -->
    <section v-else class="bg-linear-to-r from-blue-600 to-purple-600 text-white py-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 class="text-5xl font-bold mb-4">Welcome to Our School</h1>
        <p class="text-xl mb-8">Excellence in Education, Innovation in Learning</p>
      </div>
    </section>

    <!-- Active Classes Section -->
    <section class="py-16 bg-linear-to-b from-gray-50 to-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center mb-12">
          <h2 class="text-4xl font-bold text-gray-900 mb-4">Active Classes</h2>
          <p class="text-xl text-gray-600">Explore our available courses and start your learning journey today</p>
        </div>

        <div v-if="loading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="mt-4 text-gray-600">Loading classes...</p>
        </div>

        <div v-else-if="error" class="text-center py-12">
          <p class="text-red-600">{{ error }}</p>
        </div>

        <div v-else-if="classes.length === 0" class="text-center py-12">
          <p class="text-gray-600 text-lg">No active classes available at the moment.</p>
        </div>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div v-for="classInfo in classes" :key="classInfo.id"
            class="bg-white rounded-xl shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2 overflow-hidden">
            <div class="p-6">
              <div class="flex items-center justify-between mb-4">
                <h3 class="text-2xl font-bold text-gray-900">{{ classInfo.className }}</h3>
                <span class="bg-green-100 text-green-800 px-3 py-1 rounded-full text-xs font-semibold">
                  Active
                </span>
              </div>

              <p class="text-gray-600 mb-4 line-clamp-3">{{ classInfo.description || 'No description available.' }}</p>

              <div class="space-y-2 mb-4">
                <div class="flex items-center text-sm text-gray-700">
                  <svg class="w-5 h-5 mr-2 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  <span class="font-medium">Instructor:</span>
                  <span class="ml-2">{{ classInfo.instructor }}</span>
                </div>

                <div class="flex items-center text-sm text-gray-700">
                  <svg class="w-5 h-5 mr-2 text-purple-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span class="font-medium">Schedule:</span>
                  <span class="ml-2">{{ classInfo.schedule }}</span>
                </div>

                <div class="flex items-center text-sm text-gray-700">
                  <svg class="w-5 h-5 mr-2 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                  </svg>
                  <span class="font-medium">Enrollment:</span>
                  <span class="ml-2">{{ classInfo.enrolled }} / {{ classInfo.capacity }} students</span>
                </div>
              </div>

              <div class="mt-4 pt-4 border-t border-gray-200">
                <div class="w-full bg-gray-200 rounded-full h-2.5">
                  <div class="bg-blue-600 h-2.5 rounded-full transition-all duration-300"
                    :style="{ width: `${(classInfo.enrolled / classInfo.capacity) * 100}%` }"></div>
                </div>
                <p class="text-xs text-gray-500 mt-2">
                  {{ Math.round((classInfo.enrolled / classInfo.capacity) * 100) }}% filled
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.image-bg {
  background-color: aqua;
}
</style>
