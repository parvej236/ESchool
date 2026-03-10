<script setup>
import { useThemeStore } from '@/stores/theme'
import { useLang }       from '@/composables/useLang'

const themeStore = useThemeStore()
const { t, isBn } = useLang()

const activities = [
  { en: 'ICT',         bn: 'আইসিটি'          },
  { en: 'Science',     bn: 'বিজ্ঞান'          },
  { en: 'Robotics',    bn: 'রোবটিক্স'         },
  { en: 'Cultural',    bn: 'মেধা সৃজন'        },
  { en: 'Sports',      bn: 'স্পোর্টস'         },
  { en: 'Band & Parade', bn: 'ব্যান্ড ও কুচকাওয়াজ' }
]
</script>

<template>
  <section
    class="relative min-h-screen flex items-center justify-center overflow-hidden px-4 sm:px-6 lg:px-8 transition-colors duration-500"
    :class="themeStore.isDark
      ? 'bg-linear-to-b from-[#0a0e1a] via-[#0f1520] to-[#0d1b2e]'
      : 'bg-linear-to-b from-[#0f172a] via-[#1e3a5f] to-[#1e40af]'"
  >
    <!-- Background layer -->
    <div
      class="absolute inset-0 z-0 transition-colors duration-500"
      :class="themeStore.isDark
        ? 'bg-linear-to-br from-[#080c18] via-[#0f1a2e] to-[#0c1628]'
        : 'bg-linear-to-br from-slate-950 via-blue-950 to-indigo-950'"
    />

    <div class="relative z-10 flex flex-col lg:flex-row items-center justify-center w-full max-w-7xl gap-8 sm:gap-12 min-h-[auto] lg:min-h-screen">

      <!-- Left: Text content -->
      <div class="flex-1 flex flex-col items-center space-y-2 text-center lg:text-left lg:items-start">
        <p
          class="text-lg sm:text-xl lg:text-2xl transition-colors duration-300"
          :class="[
            themeStore.isDark ? 'text-blue-300/80' : 'text-blue-200',
            isBn ? 'bn-font' : ''
          ]"
        >{{ t('hero.heroContent1') }}</p>

        <p
          class="text-lg sm:text-xl lg:text-2xl transition-colors duration-300"
          :class="[
            themeStore.isDark ? 'text-blue-300/80' : 'text-blue-200',
            isBn ? 'bn-font' : ''
          ]"
        >{{ t('hero.heroContent2') }}</p>

        <p
          class="text-lg sm:text-xl lg:text-2xl transition-colors duration-300"
          :class="[
            themeStore.isDark ? 'text-blue-300/80' : 'text-blue-200',
            isBn ? 'bn-font' : ''
          ]"
        >{{ t('hero.heroContent3') }}</p>

        <p
          class="text-lg sm:text-xl lg:text-2xl transition-colors duration-300"
          :class="[
            themeStore.isDark ? 'text-blue-300/80' : 'text-blue-200',
            isBn ? 'bn-font' : ''
          ]"
        >{{ t('hero.heroContent4') }}</p>

        <p
          class="text-lg sm:text-xl lg:text-2xl transition-colors duration-300"
          :class="[
            themeStore.isDark ? 'text-blue-300/80' : 'text-blue-200',
            isBn ? 'bn-font' : ''
          ]"
        >{{ t('hero.heroContent5') }}</p>

        <p
          class="text-lg sm:text-xl lg:text-2xl transition-colors duration-300"
          :class="[
            themeStore.isDark ? 'text-blue-300/80' : 'text-blue-200',
            isBn ? 'bn-font' : ''
          ]"
        >{{ t('hero.heroContent6') }}</p>

        <p
          class="text-lg sm:text-xl lg:text-2xl font-semibold transition-colors duration-300"
          :class="[
            themeStore.isDark ? 'text-emerald-400/90' : 'text-green-200',
            isBn ? 'bn-font' : ''
          ]"
        >{{ t('hero.heroTagline') }}</p>
      </div>

      <!-- Right: Earth Video + Sliding Activities -->
      <div class="flex-1 flex items-center justify-start">
        <div class="w-64 h-64 sm:w-72 sm:h-72 relative rounded-full overflow-hidden shadow-2xl">

          <!-- Earth Video -->
          <video
            autoplay loop muted playsinline
            class="absolute inset-0 w-full h-full object-cover rounded-full"
            style="transform: scale(1.8)"
          >
            <source src="https://cdn.pixabay.com/video/2016/08/24/4788-180289892_large.mp4" type="video/mp4" />
          </video>

          <!-- Animated Activities -->
          <div class="absolute inset-0 flex items-center justify-center">
            <div class="relative w-full h-[2rem] overflow-hidden flex items-center justify-center">
              <div class="animate-slide whitespace-nowrap text-white text-xl font-semibold drop-shadow-lg flex">
                <span
                  v-for="(activity, index) in activities"
                  :key="'a' + index"
                  class="inline-block mx-10"
                  :class="isBn ? 'bn-font' : ''"
                >{{ isBn ? activity.bn : activity.en }}</span>

                <span
                  v-for="(activity, index) in activities"
                  :key="'b' + index"
                  class="inline-block mx-10 animate-pulse"
                  :class="isBn ? 'bn-font' : ''"
                >{{ isBn ? activity.bn : activity.en }}</span>
              </div>
            </div>
          </div>

        </div>
      </div>

    </div>
  </section>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+Bengali:wght@400;600;700&display=swap');
.bn-font { font-family: 'Noto Sans Bengali', sans-serif; }

@keyframes slide {
  0%   { transform: translateX(0)      scale(0.9); opacity: 0.6; }
  25%  { transform: translateX(-12.5%) scale(1);   opacity: 1;   }
  75%  { transform: translateX(-37.5%) scale(1);   opacity: 1;   }
  100% { transform: translateX(-50%)   scale(0.9); opacity: 0.6; }
}

.animate-slide {
  display: flex;
  width: max-content;
  animation: slide 20s linear infinite;
}

@keyframes borderMove {
  0%   { background-position: 0%   50%; }
  50%  { background-position: 100% 50%; }
  100% { background-position: 0%   50%; }
}

.animate-border { animation: borderMove 4s linear infinite; }
</style>