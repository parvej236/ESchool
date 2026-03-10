<script setup>
import { useThemeStore } from '@/stores/theme'
import { useLang }       from '@/composables/useLang'
import { BookOpenIcon, UsersIcon, TargetIcon } from 'lucide-vue-next'

const themeStore = useThemeStore()
const { t, isBn } = useLang()
</script>

<template>
  <section
    id="about"
    class="relative py-28 overflow-hidden transition-colors duration-500"
    :class="themeStore.isDark ? 'bg-[#0f1318]' : 'bg-[#f8f9fc]'"
  >

    <!-- ── Background decoration ── -->
    <div class="absolute inset-0 pointer-events-none overflow-hidden">
      <!-- Top-left orb -->
      <div
        class="absolute -top-32 -left-32 w-[500px] h-[500px] rounded-full blur-[120px] opacity-30 transition-colors duration-500"
        :class="themeStore.isDark ? 'bg-blue-900' : 'bg-blue-100'"
      />
      <!-- Bottom-right orb -->
      <div
        class="absolute -bottom-32 -right-32 w-[500px] h-[500px] rounded-full blur-[120px] opacity-25 transition-colors duration-500"
        :class="themeStore.isDark ? 'bg-emerald-900' : 'bg-emerald-100'"
      />
      <!-- Grid lines -->
      <div
        class="absolute inset-0 opacity-[0.025] transition-opacity duration-500"
        :class="themeStore.isDark ? 'opacity-[0.04]' : 'opacity-[0.025]'"
        style="background-image: linear-gradient(currentColor 1px, transparent 1px), linear-gradient(90deg, currentColor 1px, transparent 1px); background-size: 64px 64px;"
      />
    </div>

    <div class="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

      <!-- ── Section header ── -->
      <div class="text-center mb-20">
        <div
          class="inline-flex items-center gap-2 text-xs font-bold tracking-[0.2em] uppercase px-4 py-2 rounded-full mb-6 transition-colors duration-300"
          :class="themeStore.isDark
            ? 'bg-blue-500/10 border border-blue-500/20 text-blue-400'
            : 'bg-blue-50 border border-blue-200 text-blue-600'"
        >
          <span class="w-1.5 h-1.5 rounded-full bg-current animate-pulse" />
          {{ isBn ? 'আমাদের সম্পর্কে' : 'About Us' }}
        </div>

        <h2
          class="font-bold mb-5 leading-tight transition-colors duration-300"
          :class="[
            themeStore.isDark ? 'text-white' : 'text-slate-900',
            isBn ? 'bn-font' : 'display-font'
          ]"
          style="font-size: clamp(2rem, 4vw, 3rem);"
        >
          {{ t('about.aboutTitle') }}
        </h2>

        <!-- Decorative divider -->
        <div class="flex items-center justify-center gap-3">
          <div class="h-px w-16 rounded-full" :class="themeStore.isDark ? 'bg-slate-700' : 'bg-slate-300'" />
          <div class="w-2 h-2 rounded-full" :class="themeStore.isDark ? 'bg-emerald-500' : 'bg-emerald-500'" />
          <div class="h-px w-16 rounded-full" :class="themeStore.isDark ? 'bg-slate-700' : 'bg-slate-300'" />
        </div>
      </div>

      <!-- ── Content grid ── -->
      <div class="grid lg:grid-cols-2 gap-16 items-center">

        <!-- Left: Text + Pillars -->
        <div class="space-y-8">

          <!-- Desc 1 -->
          <div
            class="group flex gap-5 p-6 rounded-2xl border transition-all duration-300 hover:-translate-y-0.5"
            :class="themeStore.isDark
              ? 'bg-[#151c27] border-[#1e2d3d] hover:border-[#2a3f5c] hover:bg-[#1a2535]'
              : 'bg-white border-slate-200 hover:border-blue-200 shadow-sm hover:shadow-md'"
          >
            <div
              class="shrink-0 w-12 h-12 rounded-xl flex items-center justify-center transition-colors duration-300"
              :class="themeStore.isDark
                ? 'bg-emerald-500/10 group-hover:bg-emerald-500/20'
                : 'bg-emerald-50 group-hover:bg-emerald-100'"
            >
              <BookOpenIcon
                class="w-5 h-5 transition-colors duration-300"
                :class="themeStore.isDark ? 'text-emerald-400' : 'text-emerald-600'"
              />
            </div>
            <p
              class="text-[15px] leading-relaxed pt-1 transition-colors duration-300"
              :class="[
                themeStore.isDark ? 'text-slate-300' : 'text-slate-600',
                isBn ? 'bn-font' : ''
              ]"
            >{{ t('about.aboutDesc1') }}</p>
          </div>

          <!-- Desc 2 -->
          <div
            class="group flex gap-5 p-6 rounded-2xl border transition-all duration-300 hover:-translate-y-0.5"
            :class="themeStore.isDark
              ? 'bg-[#151c27] border-[#1e2d3d] hover:border-[#2a3f5c] hover:bg-[#1a2535]'
              : 'bg-white border-slate-200 hover:border-blue-200 shadow-sm hover:shadow-md'"
          >
            <div
              class="shrink-0 w-12 h-12 rounded-xl flex items-center justify-center transition-colors duration-300"
              :class="themeStore.isDark
                ? 'bg-blue-500/10 group-hover:bg-blue-500/20'
                : 'bg-blue-50 group-hover:bg-blue-100'"
            >
              <UsersIcon
                class="w-5 h-5 transition-colors duration-300"
                :class="themeStore.isDark ? 'text-blue-400' : 'text-blue-600'"
              />
            </div>
            <p
              class="text-[15px] leading-relaxed pt-1 transition-colors duration-300"
              :class="[
                themeStore.isDark ? 'text-slate-300' : 'text-slate-600',
                isBn ? 'bn-font' : ''
              ]"
            >{{ t('about.aboutDesc2') }}</p>
          </div>

          <!-- Three pillars -->
          <div
            class="grid grid-cols-3 gap-4 pt-2"
          >
            <div
              v-for="(pillar, i) in [
                { icon: TargetIcon,   color: 'emerald', labelEn: 'Excellence', labelBn: 'শ্রেষ্ঠত্ব' },
                { icon: BookOpenIcon, color: 'blue',    labelEn: 'Innovation',  labelBn: 'উদ্ভাবন'   },
                { icon: UsersIcon,    color: 'violet',  labelEn: 'Unity',       labelBn: 'ঐক্য'       }
              ]"
              :key="i"
              class="flex flex-col items-center gap-3 p-5 rounded-2xl border text-center transition-all duration-300 hover:-translate-y-1 cursor-default"
              :class="themeStore.isDark
                ? 'bg-[#151c27] border-[#1e2d3d] hover:border-[#2a3f5c]'
                : 'bg-white border-slate-200 hover:border-slate-300 shadow-sm'"
            >
              <div
                class="w-10 h-10 rounded-xl flex items-center justify-center"
                :class="{
                  'bg-emerald-500/10': pillar.color === 'emerald' && themeStore.isDark,
                  'bg-emerald-50':     pillar.color === 'emerald' && !themeStore.isDark,
                  'bg-blue-500/10':    pillar.color === 'blue'    && themeStore.isDark,
                  'bg-blue-50':        pillar.color === 'blue'    && !themeStore.isDark,
                  'bg-violet-500/10':  pillar.color === 'violet'  && themeStore.isDark,
                  'bg-violet-50':      pillar.color === 'violet'  && !themeStore.isDark,
                }"
              >
                <component
                  :is="pillar.icon"
                  class="w-5 h-5"
                  :class="{
                    'text-emerald-400': pillar.color === 'emerald' && themeStore.isDark,
                    'text-emerald-600': pillar.color === 'emerald' && !themeStore.isDark,
                    'text-blue-400':    pillar.color === 'blue'    && themeStore.isDark,
                    'text-blue-600':    pillar.color === 'blue'    && !themeStore.isDark,
                    'text-violet-400':  pillar.color === 'violet'  && themeStore.isDark,
                    'text-violet-600':  pillar.color === 'violet'  && !themeStore.isDark,
                  }"
                />
              </div>
              <p
                class="text-xs font-semibold tracking-wide uppercase transition-colors duration-300"
                :class="[
                  themeStore.isDark ? 'text-slate-400' : 'text-slate-500',
                  isBn ? 'bn-font' : ''
                ]"
              >{{ isBn ? pillar.labelBn : pillar.labelEn }}</p>
            </div>
          </div>
        </div>

        <!-- Right: Video -->
        <div class="relative">

          <!-- Glow effects behind video -->
          <div
            class="absolute -inset-4 rounded-3xl blur-2xl opacity-20 transition-colors duration-500"
            :class="themeStore.isDark
              ? 'bg-linear-to-br from-blue-600 to-emerald-600'
              : 'bg-linear-to-br from-blue-300 to-emerald-300'"
          />

          <!-- Video card -->
          <div
            class="relative rounded-2xl overflow-hidden border shadow-2xl transition-colors duration-300"
            :class="themeStore.isDark
              ? 'border-[#1e2d3d] shadow-black/40'
              : 'border-slate-200 shadow-slate-200/80'"
          >
            <!-- Top bar decoration -->
            <div
              class="flex items-center gap-2 px-5 py-3.5 border-b transition-colors duration-300"
              :class="themeStore.isDark
                ? 'bg-[#141c28] border-[#1e2d3d]'
                : 'bg-slate-50 border-slate-200'"
            >
              <span class="w-3 h-3 rounded-full bg-red-400/80" />
              <span class="w-3 h-3 rounded-full bg-amber-400/80" />
              <span class="w-3 h-3 rounded-full bg-emerald-400/80" />
              <span
                class="ml-3 text-xs font-medium tracking-wide"
                :class="themeStore.isDark ? 'text-slate-500' : 'text-slate-400'"
              >{{ isBn ? 'আমরা পারি — ভিডিও' : 'Amra Pari — Video' }}</span>
            </div>

            <!-- iframe -->
            <div class="aspect-video">
              <iframe
                class="w-full h-full"
                src="https://www.youtube.com/embed/lirOtwgGcgs"
                title="আমরা পারি Video"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowfullscreen
              />
            </div>
          </div>

          <!-- Floating badge -->
          <div
            class="absolute -bottom-5 -left-5 flex items-center gap-3 px-5 py-3 rounded-2xl border shadow-xl transition-colors duration-300"
            :class="themeStore.isDark
              ? 'bg-[#141c28] border-[#1e2d3d] shadow-black/30'
              : 'bg-white border-slate-200 shadow-slate-200'"
          >
            <div class="w-9 h-9 rounded-xl bg-emerald-500/15 flex items-center justify-center">
              <UsersIcon class="w-4 h-4" :class="themeStore.isDark ? 'text-emerald-400' : 'text-emerald-600'" />
            </div>
            <div>
              <p class="text-xs font-bold" :class="themeStore.isDark ? 'text-white' : 'text-slate-800'">1200+</p>
              <p
                class="text-[11px] leading-none mt-0.5"
                :class="[
                  themeStore.isDark ? 'text-slate-500' : 'text-slate-400',
                  isBn ? 'bn-font' : ''
                ]"
              >{{ isBn ? 'শিক্ষার্থী' : 'Students' }}</p>
            </div>
          </div>

        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+Bengali:wght@400;600;700&family=DM+Serif+Display:ital@0;1&display=swap');

.display-font { font-family: 'DM Serif Display', Georgia, serif; }
.bn-font      { font-family: 'Noto Sans Bengali', sans-serif; }
</style>