<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { useLang }       from '@/composables/useLang'
import Earth             from '@/components/homepage/Earth.vue'
import About            from '@/components/homepage/About.vue'

const themeStore = useThemeStore()
const { t, isBn, tField } = useLang()

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

const startAutoPlay = () => {
  stopAutoPlay()
  autoPlayInterval = setInterval(() => {
    currentSlideIndex.value = (currentSlideIndex.value + 1) % homeSlides.value.length
  }, 5000)
}

const stopAutoPlay = () => clearInterval(autoPlayInterval)

const goToSlide = (index) => { currentSlideIndex.value = index; startAutoPlay() }
const nextSlide = () => { currentSlideIndex.value = (currentSlideIndex.value + 1) % homeSlides.value.length; startAutoPlay() }
const prevSlide = () => { currentSlideIndex.value = (currentSlideIndex.value - 1 + homeSlides.value.length) % homeSlides.value.length; startAutoPlay() }

onMounted(() => {
  loading.value = true
  fetchHomeSlides().finally(() => loading.value = false)
})
onUnmounted(() => stopAutoPlay())
</script>

<template>

  <Earth />
  <About />
  <div :class="themeStore.isDark ? 'bg-[#0f1318]' : 'bg-slate-50'">



    <!-- ── Loading Skeleton ─────────────────────────── -->
    <section
      v-if="loading"
      class="w-full relative overflow-hidden"
      style="height: clamp(400px, 56vw, 620px);"
      :class="themeStore.isDark ? 'bg-[#141920]' : 'bg-slate-200'"
    >
      <div
        class="absolute inset-0 animate-pulse"
        :class="themeStore.isDark ? 'bg-[#1c2430]' : 'bg-slate-300/60'"
      />
      <div class="absolute inset-0 flex flex-col items-center justify-center gap-4">
        <div
          class="w-12 h-12 rounded-full border-[3px] animate-spin"
          :class="themeStore.isDark
            ? 'border-white/10 border-t-white/40'
            : 'border-slate-300 border-t-slate-500'"
        />
        <p
          class="text-sm font-medium tracking-widest uppercase"
          :class="themeStore.isDark ? 'text-white/30' : 'text-slate-400'"
        >{{ isBn ? 'লোড হচ্ছে...' : 'Loading...' }}</p>
      </div>
    </section>

    <!-- ── Carousel ─────────────────────────────────── -->
    <section
      v-else-if="homeSlides.length > 0"
      class="relative w-full overflow-hidden group"
      style="height: clamp(400px, 56vw, 620px);"
    >
      <!-- Slides -->
      <div
        v-for="(slide, index) in homeSlides"
        :key="slide.id"
        class="absolute inset-0 transition-opacity duration-700 ease-in-out"
        :class="index === currentSlideIndex ? 'opacity-100 z-10' : 'opacity-0 z-0'"
      >
        <!-- Background image -->
        <div
          class="absolute inset-0 bg-cover bg-center bg-no-repeat transition-transform duration-[8000ms]"
          :class="index === currentSlideIndex ? 'scale-100' : 'scale-105'"
          :style="slide.imageUrl ? { backgroundImage: `url(${imgUrl(slide.imageUrl)})` } : {}"
        />

        <!-- Gradient overlay — adapts to theme -->
        <div
          class="absolute inset-0 transition-colors duration-500"
          :class="themeStore.isDark
            ? 'bg-linear-to-b from-black/55 via-black/45 to-black/75'
            : 'bg-linear-to-b from-black/35 via-black/30 to-black/60'"
        />

        <!-- Slide content -->
        <div class="relative h-full flex items-center justify-center z-10 px-4 sm:px-8">
          <div class="text-center text-white max-w-4xl mx-auto">

            <!-- Badge -->
            <div
              class="inline-flex items-center gap-2 backdrop-blur-md border text-xs font-semibold
                     px-3.5 py-1.5 rounded-full mb-6 tracking-widest uppercase"
              :class="themeStore.isDark
                ? 'bg-white/10 border-white/20 text-white/75'
                : 'bg-white/20 border-white/30 text-white/90'"
            >
              <span class="w-1.5 h-1.5 bg-emerald-400 rounded-full animate-pulse" />
              {{ index + 1 }} / {{ homeSlides.length }}
            </div>

            <!-- Title -->
            <h1
              class="font-bold mb-4 leading-tight drop-shadow-lg transition-all duration-300"
              :class="isBn ? 'bn-font' : ''"
              style="font-size: clamp(1.75rem, 5vw, 3.5rem);"
            >
              {{ tField(slide, 'title') }}
            </h1>

            <!-- Description -->
            <p
              v-if="tField(slide, 'description')"
              class="text-white/85 mb-8 max-w-2xl mx-auto leading-relaxed transition-all duration-300"
              :class="isBn ? 'bn-font' : ''"
              style="font-size: clamp(1rem, 2vw, 1.2rem);"
            >
              {{ tField(slide, 'description') }}
            </p>

            <!-- CTA -->
            <a
              v-if="slide.linkUrl"
              :href="slide.linkUrl"
              class="inline-flex items-center gap-2.5 px-8 py-3.5 rounded-full font-semibold
                     text-[15px] transition-all duration-200 shadow-xl
                     hover:-translate-y-0.5 hover:shadow-2xl active:translate-y-0"
              :class="themeStore.isDark
                ? 'bg-white/15 backdrop-blur-md border border-white/25 text-white hover:bg-white/25'
                : 'bg-white text-gray-900 hover:bg-gray-50'"
            >
              <span :class="isBn ? 'bn-font' : ''">
                {{ t('hero.learnMore') }}
              </span>
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
              </svg>
            </a>
          </div>
        </div>
      </div>

      <!-- Prev arrow -->
      <button
        v-if="homeSlides.length > 1"
        @click="prevSlide"
        class="absolute left-4 sm:left-6 top-1/2 -translate-y-1/2 z-20
               w-10 h-10 sm:w-12 sm:h-12 flex items-center justify-center
               rounded-full backdrop-blur-md border text-white
               transition-all duration-200 opacity-0 group-hover:opacity-100
               hover:scale-110 active:scale-95"
        :class="themeStore.isDark
          ? 'bg-white/10 border-white/15 hover:bg-white/20'
          : 'bg-black/20 border-white/30 hover:bg-black/35'"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M15 19l-7-7 7-7"/>
        </svg>
      </button>

      <!-- Next arrow -->
      <button
        v-if="homeSlides.length > 1"
        @click="nextSlide"
        class="absolute right-4 sm:right-6 top-1/2 -translate-y-1/2 z-20
               w-10 h-10 sm:w-12 sm:h-12 flex items-center justify-center
               rounded-full backdrop-blur-md border text-white
               transition-all duration-200 opacity-0 group-hover:opacity-100
               hover:scale-110 active:scale-95"
        :class="themeStore.isDark
          ? 'bg-white/10 border-white/15 hover:bg-white/20'
          : 'bg-black/20 border-white/30 hover:bg-black/35'"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M9 5l7 7-7 7"/>
        </svg>
      </button>

      <!-- Dot indicators -->
      <div
        v-if="homeSlides.length > 1"
        class="absolute bottom-7 left-1/2 -translate-x-1/2 z-20 flex items-center gap-2"
      >
        <button
          v-for="(slide, index) in homeSlides"
          :key="slide.id"
          @click="goToSlide(index)"
          class="transition-all duration-300 rounded-full"
          :class="index === currentSlideIndex
            ? 'w-7 h-2 bg-white'
            : 'w-2 h-2 bg-white/40 hover:bg-white/70'"
          :aria-label="`Go to slide ${index + 1}`"
        />
      </div>

      <!-- Progress bar -->
      <div
        class="absolute bottom-0 left-0 right-0 z-20 h-[2px]"
        :class="themeStore.isDark ? 'bg-white/10' : 'bg-white/20'"
      >
        <div
          class="h-full transition-all duration-500"
          :class="themeStore.isDark ? 'bg-white/45' : 'bg-white/75'"
          :style="{ width: `${((currentSlideIndex + 1) / homeSlides.length) * 100}%` }"
        />
      </div>
    </section>

    <!-- ── Fallback hero (no slides) ────────────────── -->
    <section
      v-else
      class="relative overflow-hidden text-white"
      style="height: clamp(400px, 56vw, 620px);"
      :class="themeStore.isDark
        ? 'bg-linear-to-br from-[#141920] via-[#1a2235] to-[#0f1a2e]'
        : 'bg-linear-to-br from-blue-700 via-blue-600 to-indigo-700'"
    >
      <!-- Decorative blobs -->
      <div class="absolute inset-0 overflow-hidden pointer-events-none">
        <div
          class="absolute -top-20 -left-20 w-72 h-72 rounded-full blur-3xl opacity-20"
          :class="themeStore.isDark ? 'bg-blue-500' : 'bg-white'"
        />
        <div
          class="absolute -bottom-20 -right-20 w-96 h-96 rounded-full blur-3xl opacity-15"
          :class="themeStore.isDark ? 'bg-indigo-400' : 'bg-purple-300'"
        />
      </div>

      <div class="relative h-full flex items-center justify-center text-center px-4 z-10">
        <div class="max-w-3xl">
          <h1
            class="font-bold mb-4 drop-shadow-lg"
            :class="isBn ? 'bn-font' : ''"
            style="font-size: clamp(2rem, 5vw, 3.75rem);"
          >
            {{ t('hero.welcome') }}
          </h1>
          <p
            class="text-white/80 leading-relaxed"
            :class="isBn ? 'bn-font' : ''"
            style="font-size: clamp(1rem, 2.5vw, 1.35rem);"
          >
            {{ t('hero.tagline') }}
          </p>
        </div>
      </div>
    </section>

  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+Bengali:wght@400;600;700&display=swap');
.bn-font { font-family: 'Noto Sans Bengali', sans-serif; }
</style>