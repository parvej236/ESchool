<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useLanguageStore } from '@/stores/language'

const langStore = useLanguageStore()

const classes           = ref([])
const homeSlides        = ref([])
const loading           = ref(false)
const currentSlideIndex = ref(0)
let   autoPlayInterval  = null

const API_BASE_URL = 'http://localhost:8080'

const imgUrl = (path) => {
  if (!path) return null
  const clean = path.startsWith('/') ? path : `/${path}`
  return `${API_BASE_URL}${clean}`
}

// ── Fetch ──────────────────────────────────────────────
const fetchActiveClasses = async () => {
  try {
    const res  = await fetch(`${API_BASE_URL}/api/classes/active`)
    const data = await res.json()
    classes.value = data.data || data.classes || []
  } catch (err) {
    console.error('Classes error:', err)
  }
}

const fetchHomeSlides = async () => {
  try {
    const res  = await fetch(`${API_BASE_URL}/api/home-slides/active`)
    const data = await res.json()
    homeSlides.value = data.data || data.slides || []
    if (homeSlides.value.length > 1) startAutoPlay()
  } catch (err) {
    console.error('Home slides error:', err)
  }
}

// ── Slide controls ─────────────────────────────────────
const startAutoPlay = () => {
  stopAutoPlay()
  autoPlayInterval = setInterval(() => {
    currentSlideIndex.value = (currentSlideIndex.value + 1) % homeSlides.value.length
  }, 5000)
}

const stopAutoPlay = () => clearInterval(autoPlayInterval)

const goToSlide = (index) => {
  currentSlideIndex.value = index
  startAutoPlay()
}

const nextSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value + 1) % homeSlides.value.length
  startAutoPlay()
}

const prevSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value - 1 + homeSlides.value.length) % homeSlides.value.length
  startAutoPlay()
}

onMounted(() => {
  loading.value = true
  Promise.all([fetchActiveClasses(), fetchHomeSlides()])
    .finally(() => loading.value = false)
})

onUnmounted(() => stopAutoPlay())
</script>

<template>
  <div class="min-h-screen bg-gray-50">

    <!-- ── Language Switcher ───────────────────────────── -->
    <div class="fixed top-4 right-4 z-50">
      <button @click="langStore.toggle()"
        class="flex items-center gap-2 px-4 py-2 bg-white/90 backdrop-blur-sm border border-gray-200
               rounded-full text-sm font-semibold text-gray-700 hover:bg-white shadow-md
               hover:shadow-lg transition-all hover:-translate-y-0.5 active:translate-y-0">
        <span class="text-base">{{ langStore.isEnglish ? '🇧🇩' : '🇬🇧' }}</span>
        <span>{{ langStore.isEnglish ? 'বাংলা' : 'English' }}</span>
      </button>
    </div>

    <!-- ── Hero Carousel ──────────────────────────────── -->

    <!-- Loading skeleton -->
    <section v-if="loading" class="w-full bg-gray-800 animate-pulse" style="height: 520px;">
      <div class="h-full flex items-center justify-center">
        <div class="text-center">
          <div class="w-16 h-16 border-4 border-white/20 border-t-white rounded-full animate-spin mx-auto mb-4"></div>
          <p class="text-white/50 text-sm">Loading...</p>
        </div>
      </div>
    </section>

    <!-- Carousel with slides -->
    <section v-else-if="homeSlides.length > 0"
      class="relative w-full overflow-hidden bg-gray-900"
      style="height: 520px;">

      <!-- Slides -->
      <div v-for="(slide, index) in homeSlides" :key="slide.id"
        class="absolute inset-0 transition-opacity duration-700 ease-in-out"
        :class="index === currentSlideIndex ? 'opacity-100 z-10' : 'opacity-0 z-0'">

        <!-- Background image -->
        <div class="absolute inset-0 bg-cover bg-center bg-no-repeat transition-transform duration-700"
          :style="{ backgroundImage: slide.imageUrl ? `url(${imgUrl(slide.imageUrl)})` : 'none' }">
        </div>

        <!-- Gradient overlay -->
        <div class="absolute inset-0 bg-linear-to-b from-black/40 via-black/35 to-black/65"></div>

        <!-- Slide content -->
        <div class="relative h-full flex items-center justify-center z-10 px-4">
          <div class="text-center text-white max-w-4xl mx-auto">

            <!-- Slide counter badge -->
            <div class="inline-flex items-center gap-2 bg-white/15 backdrop-blur-sm
                        border border-white/25 text-white/90 text-xs font-semibold
                        px-3 py-1 rounded-full mb-5 tracking-widest uppercase">
              <span class="w-1.5 h-1.5 bg-green-400 rounded-full animate-pulse"></span>
              {{ index + 1 }} / {{ homeSlides.length }}
            </div>

            <!-- Title — picks EN or BN based on language -->
            <h1
              class="text-4xl sm:text-5xl md:text-6xl font-bold mb-4 leading-tight drop-shadow-lg transition-all"
              :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
              {{ langStore.t(slide, 'title') }}
            </h1>

            <!-- Description -->
            <p v-if="langStore.t(slide, 'description')"
              class="text-lg sm:text-xl text-white/85 mb-8 max-w-2xl mx-auto leading-relaxed transition-all"
              :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
              {{ langStore.t(slide, 'description') }}
            </p>

            <!-- CTA button -->
            <a v-if="slide.linkUrl" :href="slide.linkUrl"
              class="inline-flex items-center gap-2 bg-white text-gray-900 hover:bg-gray-100
                     px-8 py-3.5 rounded-full font-semibold text-base transition-all
                     shadow-lg hover:shadow-xl hover:-translate-y-0.5 active:translate-y-0">
              {{ langStore.isEnglish ? 'Learn More' : 'আরও জানুন' }}
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </a>
          </div>
        </div>
      </div>

      <!-- Prev arrow -->
      <button v-if="homeSlides.length > 1" @click="prevSlide"
        class="absolute left-4 top-1/2 -translate-y-1/2 z-20
               w-11 h-11 flex items-center justify-center
               bg-white/20 hover:bg-white/40 backdrop-blur-sm
               border border-white/30 rounded-full text-white
               transition-all hover:scale-110 active:scale-95">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M15 19l-7-7 7-7"/>
        </svg>
      </button>

      <!-- Next arrow -->
      <button v-if="homeSlides.length > 1" @click="nextSlide"
        class="absolute right-4 top-1/2 -translate-y-1/2 z-20
               w-11 h-11 flex items-center justify-center
               bg-white/20 hover:bg-white/40 backdrop-blur-sm
               border border-white/30 rounded-full text-white
               transition-all hover:scale-110 active:scale-95">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M9 5l7 7-7 7"/>
        </svg>
      </button>

      <!-- Dot indicators -->
      <div v-if="homeSlides.length > 1"
        class="absolute bottom-6 left-1/2 -translate-x-1/2 z-20 flex items-center gap-2">
        <button v-for="(slide, index) in homeSlides" :key="slide.id"
          @click="goToSlide(index)"
          :class="[
            'transition-all duration-300 rounded-full',
            index === currentSlideIndex
              ? 'w-8 h-2.5 bg-white'
              : 'w-2.5 h-2.5 bg-white/50 hover:bg-white/80'
          ]"
          :aria-label="`Go to slide ${index + 1}`">
        </button>
      </div>

      <!-- Progress bar -->
      <div class="absolute bottom-0 left-0 right-0 z-20 h-0.5 bg-white/20">
        <div class="h-full bg-white/70 transition-all duration-500"
          :style="{ width: `${((currentSlideIndex + 1) / homeSlides.length) * 100}%` }">
        </div>
      </div>
    </section>

    <!-- Fallback hero (no slides) -->
    <section v-else
      class="relative bg-linear-to-br from-blue-700 via-blue-600 to-purple-600 text-white overflow-hidden"
      style="height: 520px;">
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-10 left-10 w-64 h-64 bg-white rounded-full blur-3xl"></div>
        <div class="absolute bottom-10 right-10 w-96 h-96 bg-purple-300 rounded-full blur-3xl"></div>
      </div>
      <div class="relative h-full flex items-center justify-center text-center px-4 z-10">
        <div>
          <h1 class="text-5xl md:text-6xl font-bold mb-4 drop-shadow"
            :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
            {{ langStore.isEnglish ? 'Welcome to Our School' : 'আমাদের স্কুলে স্বাগতম' }}
          </h1>
          <p class="text-xl md:text-2xl text-white/80"
            :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
            {{ langStore.isEnglish ? 'Excellence in Education, Innovation in Learning' : 'শিক্ষায় শ্রেষ্ঠত্ব, শেখায় উদ্ভাবন' }}
          </p>
        </div>
      </div>
    </section>

    <!-- ── Active Classes ─────────────────────────────── -->
    <section class="py-16 bg-linear-to-b from-gray-50 to-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

        <div class="text-center mb-12">
          <span class="inline-block bg-blue-100 text-blue-700 text-xs font-bold px-3 py-1
                       rounded-full uppercase tracking-wider mb-3">
            {{ langStore.isEnglish ? 'Courses' : 'কোর্সসমূহ' }}
          </span>
          <h2 class="text-4xl font-bold text-gray-900 mb-4"
            :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
            {{ langStore.isEnglish ? 'Active Classes' : 'সক্রিয় ক্লাসসমূহ' }}
          </h2>
          <p class="text-xl text-gray-500 max-w-2xl mx-auto"
            :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
            {{ langStore.isEnglish
              ? 'Explore our available courses and start your learning journey today'
              : 'আমাদের উপলব্ধ কোর্সগুলো দেখুন এবং আজই আপনার শেখার যাত্রা শুরু করুন' }}
          </p>
        </div>

        <!-- Loading skeletons -->
        <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div v-for="i in 3" :key="i"
            class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 animate-pulse">
            <div class="h-6 bg-gray-200 rounded w-3/4 mb-4"></div>
            <div class="space-y-2 mb-6">
              <div class="h-3 bg-gray-100 rounded"></div>
              <div class="h-3 bg-gray-100 rounded w-5/6"></div>
              <div class="h-3 bg-gray-100 rounded w-4/6"></div>
            </div>
            <div class="h-2 bg-gray-200 rounded-full"></div>
          </div>
        </div>

        <!-- Empty state -->
        <div v-else-if="classes.length === 0"
          class="text-center py-16 bg-white rounded-2xl border border-gray-100 shadow-sm">
          <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
            </svg>
          </div>
          <p class="font-semibold text-gray-500"
            :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
            {{ langStore.isEnglish ? 'No active classes available at the moment.' : 'এই মুহূর্তে কোনো সক্রিয় ক্লাস নেই।' }}
          </p>
        </div>

        <!-- Classes grid -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <div v-for="classInfo in classes" :key="classInfo.id"
            class="bg-white rounded-2xl shadow-sm border border-gray-100
                   hover:shadow-xl transition-all duration-300 hover:-translate-y-1 overflow-hidden group">

            <!-- Top accent -->
            <div class="h-1.5 bg-linear-to-r from-blue-500 to-purple-500"></div>

            <div class="p-6">
              <div class="flex items-start justify-between mb-3">
                <h3 class="text-xl font-bold text-gray-900 group-hover:text-blue-600 transition-colors leading-tight"
                  :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
                  {{ langStore.t(classInfo, 'className') || classInfo.className }}
                </h3>
                <span class="shrink-0 ml-3 bg-green-100 text-green-700 px-2.5 py-0.5 rounded-full text-xs font-semibold">
                  {{ langStore.isEnglish ? 'Active' : 'সক্রিয়' }}
                </span>
              </div>

              <p class="text-gray-500 text-sm mb-5 line-clamp-3 leading-relaxed"
                :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
                {{ langStore.t(classInfo, 'description') || classInfo.description || (langStore.isEnglish ? 'No description available.' : 'কোনো বিবরণ নেই।') }}
              </p>

              <div class="space-y-2.5 mb-5">

                <!-- Instructor -->
                <div class="flex items-center gap-2.5 text-sm text-gray-600">
                  <div class="w-7 h-7 bg-blue-50 rounded-lg flex items-center justify-center shrink-0">
                    <svg class="w-3.5 h-3.5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                    </svg>
                  </div>
                  <span class="text-gray-400 text-xs font-medium w-16 shrink-0"
                    :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
                    {{ langStore.isEnglish ? 'Instructor' : 'শিক্ষক' }}
                  </span>
                  <span class="font-medium text-gray-700 truncate">{{ classInfo.instructor }}</span>
                </div>

                <!-- Schedule -->
                <div class="flex items-center gap-2.5 text-sm text-gray-600">
                  <div class="w-7 h-7 bg-purple-50 rounded-lg flex items-center justify-center shrink-0">
                    <svg class="w-3.5 h-3.5 text-purple-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                    </svg>
                  </div>
                  <span class="text-gray-400 text-xs font-medium w-16 shrink-0"
                    :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
                    {{ langStore.isEnglish ? 'Schedule' : 'সময়সূচী' }}
                  </span>
                  <span class="font-medium text-gray-700 truncate">{{ classInfo.schedule }}</span>
                </div>

                <!-- Enrollment -->
                <div class="flex items-center gap-2.5 text-sm text-gray-600">
                  <div class="w-7 h-7 bg-green-50 rounded-lg flex items-center justify-center shrink-0">
                    <svg class="w-3.5 h-3.5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"/>
                    </svg>
                  </div>
                  <span class="text-gray-400 text-xs font-medium w-16 shrink-0"
                    :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
                    {{ langStore.isEnglish ? 'Enrolled' : 'ভর্তি' }}
                  </span>
                  <span class="font-medium text-gray-700">
                    {{ classInfo.enrolled }} / {{ classInfo.capacity }}
                    {{ langStore.isEnglish ? 'students' : 'শিক্ষার্থী' }}
                  </span>
                </div>
              </div>

              <!-- Enrollment progress bar -->
              <div class="pt-4 border-t border-gray-100">
                <div class="flex items-center justify-between mb-1.5">
                  <span class="text-xs text-gray-400 font-medium"
                    :style="langStore.isBangla ? 'font-family: Noto Sans Bengali, sans-serif' : ''">
                    {{ langStore.isEnglish ? 'Capacity' : 'ধারণক্ষমতা' }}
                  </span>
                  <span :class="[
                    'text-xs font-bold',
                    (classInfo.enrolled / classInfo.capacity) >= 0.9 ? 'text-red-500' :
                    (classInfo.enrolled / classInfo.capacity) >= 0.7 ? 'text-orange-500' : 'text-green-500'
                  ]">
                    {{ Math.round((classInfo.enrolled / classInfo.capacity) * 100) }}%
                    {{ langStore.isEnglish ? 'filled' : 'পূর্ণ' }}
                  </span>
                </div>
                <div class="w-full bg-gray-100 rounded-full h-2 overflow-hidden">
                  <div class="h-2 rounded-full transition-all duration-500"
                    :class="[
                      (classInfo.enrolled / classInfo.capacity) >= 0.9 ? 'bg-red-500' :
                      (classInfo.enrolled / classInfo.capacity) >= 0.7 ? 'bg-orange-400' :
                      'bg-linear-to-r from-blue-500 to-purple-500'
                    ]"
                    :style="{ width: `${Math.min((classInfo.enrolled / classInfo.capacity) * 100, 100)}%` }">
                  </div>
                </div>
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
</style>