<script setup>
import { ref } from 'vue'
import { useAuthStore }  from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import { useLang }       from '@/composables/useLang'
import { LayoutGrid, Sun, Moon, Menu, X, ChevronRight } from 'lucide-vue-next'

const authStore  = useAuthStore()
const themeStore = useThemeStore()
const { t, isBn, toggleLang } = useLang()

const mobileOpen = ref(false)
</script>

<template>
  <nav class="z-50 navbar-font sticky top-0">

    <!-- Main bar -->
    <div
      class="transition-all duration-300"
      :class="themeStore.isDark
        ? 'bg-[#141920] border-b border-[#232c3a]'
        : 'bg-white/95 border-b border-slate-200 shadow-sm backdrop-blur-xl'"
    >
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16.5">

          <!-- Left: Logo + Links -->
          <div class="flex items-center gap-8">
            <router-link to="/" class="flex items-center shrink-0 group">
              <img
                src="/src/assets/images/quantum-logo-9d5Rton5.png"
                alt="ESchool"
                class="h-9 w-auto transition-transform duration-300 group-hover:scale-105"
              />
            </router-link>

            <div class="hidden md:flex items-center gap-0.5">
              <router-link
                to="/"
                class="px-3.5 py-1.75 rounded-lg text-[13px] font-medium tracking-wide transition-all duration-200"
                :class="themeStore.isDark
                  ? 'text-[#7a8a9e] hover:text-[#c9d4e0] hover:bg-[#1e2733]'
                  : 'text-slate-500 hover:text-slate-900 hover:bg-slate-100'"
                active-class="!text-[#7eb8f7] !bg-[#1a2a3d] !font-semibold"
              >{{ t('nav.home') }}</router-link>

              <router-link
                to="/vision"
                class="px-3.5 py-1.75 rounded-lg text-[13px] font-medium tracking-wide transition-all duration-200"
                :class="themeStore.isDark
                  ? 'text-[#7a8a9e] hover:text-[#c9d4e0] hover:bg-[#1e2733]'
                  : 'text-slate-500 hover:text-slate-900 hover:bg-slate-100'"
                active-class="!text-[#7eb8f7] !bg-[#1a2a3d] !font-semibold"
              >{{ t('nav.vision') }}</router-link>
            </div>
          </div>

          <!-- Right: Controls -->
          <div class="flex items-center gap-2">

            <!-- Language Toggle Pill -->
            <button
              @click="toggleLang"
              class="relative flex items-center rounded-full overflow-hidden select-none transition-all duration-200 focus:outline-none"
              :class="themeStore.isDark
                ? 'bg-[#1c2430] border border-[#2b3849] hover:border-[#3a4d63]'
                : 'bg-slate-100 border border-slate-200 hover:border-slate-300'"
              style="width: 76px; height: 30px; padding: 2px;"
              :title="isBn ? 'Switch to English' : 'বাংলায় পরিবর্তন করুন'"
            >
              <span
                class="absolute top-0.5 bottom-0.5 rounded-full shadow-lg transition-all duration-300"
                :class="[
                  themeStore.isDark
                    ? 'bg-linear-to-br from-[#3a6fa8] to-[#2d5789]'
                    : 'bg-linear-to-br from-blue-500 to-blue-600',
                  isBn ? 'left-9.5 right-0.5' : 'left-0.5 right-9.5'
                ]"
                style="transition-timing-function: cubic-bezier(0.34, 1.56, 0.64, 1);"
              />
              <span
                class="relative z-10 w-1/2 text-center font-bold tracking-wider transition-colors duration-200"
                style="font-size: 10px;"
                :class="!isBn ? 'text-white' : (themeStore.isDark ? 'text-[#4a5a6e]' : 'text-slate-400')"
              >EN</span>
              <span
                class="relative z-10 w-1/2 text-center font-bold transition-colors duration-200"
                style="font-size: 9.5px;"
                :class="isBn ? 'text-white' : (themeStore.isDark ? 'text-[#4a5a6e]' : 'text-slate-400')"
              >বাং</span>
            </button>

            <!-- Theme Toggle -->
            <button
              @click="themeStore.toggleTheme"
              class="w-8.5 h-8.5 flex items-center justify-center rounded-xl transition-all duration-200 focus:outline-none"
              :class="themeStore.isDark
                ? 'bg-[#1c2430] border border-[#2b3849] hover:border-[#3a4d63] text-[#c8a84b] hover:text-[#d4b86a]'
                : 'bg-slate-100 border border-slate-200 hover:bg-slate-200 text-slate-500 hover:text-slate-800'"
              :title="themeStore.isDark ? 'Light mode' : 'Dark mode'"
            >
              <transition name="theme-spin" mode="out-in">
                <Sun  v-if="themeStore.isDark" key="sun"  class="w-3.5 h-3.5" />
                <Moon v-else                   key="moon" class="w-3.5 h-3.5" />
              </transition>
            </button>

            <!-- Separator -->
            <div
              class="w-px h-5 mx-0.5"
              :class="themeStore.isDark ? 'bg-[#232c3a]' : 'bg-slate-200'"
            />

            <!-- Dashboard (authenticated) -->
            <template v-if="authStore.isAuthenticatedAfterCheck">
              <router-link
                to="/dashboard"
                class="hidden md:flex items-center gap-2 px-4 py-1.75 rounded-xl text-[13px] font-semibold transition-all duration-200"
                :class="themeStore.isDark
                  ? 'bg-[#1a2a3d] text-[#7eb8f7] border border-[#2a3f5c] hover:bg-[#1f3250] hover:border-[#3a5578]'
                  : 'bg-blue-50 text-blue-600 border border-blue-200 hover:bg-blue-100'"
                active-class="ring-1 ring-[#3a6fa8]/50"
              >
                <LayoutGrid class="w-3.5 h-3.5" />
                {{ t('nav.dashboard') }}
              </router-link>
            </template>

            <!-- Login (unauthenticated) -->
            <template v-else>
              <router-link
                to="/login"
                class="hidden md:flex items-center gap-2 px-4 py-1.75 rounded-xl text-[13px] font-semibold text-white transition-all duration-200 hover:-translate-y-px active:translate-y-0 focus:outline-none"
                :style="themeStore.isDark
                  ? 'background: linear-gradient(135deg, #2d5789 0%, #1f3f6a 100%); box-shadow: 0 0 0 1px rgba(74,120,175,0.3), 0 2px 12px rgba(30,55,100,0.5);'
                  : 'background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); box-shadow: 0 2px 12px rgba(59,130,246,0.35);'"
              >
                <iconify-icon icon="fa-solid:sign-in-alt" style="font-size:12px" />
                {{ t('nav.login') }}
              </router-link>
            </template>

            <!-- Mobile Hamburger -->
            <button
              @click="mobileOpen = !mobileOpen"
              class="md:hidden w-8.5 h-8.5 flex items-center justify-center rounded-xl transition-all duration-200 focus:outline-none"
              :class="themeStore.isDark
                ? 'bg-[#1c2430] border border-[#2b3849] hover:border-[#3a4d63] text-[#7a8a9e]'
                : 'bg-slate-100 border border-slate-200 hover:bg-slate-200 text-slate-600'"
            >
              <transition name="theme-spin" mode="out-in">
                <X    v-if="mobileOpen" key="x"    class="w-4 h-4" />
                <Menu v-else            key="menu" class="w-4 h-4" />
              </transition>
            </button>

          </div>
        </div>
      </div>
    </div>

    <!-- Accent underline -->
    <div
      class="h-px w-full"
      :class="themeStore.isDark
        ? 'bg-linear-to-r from-transparent via-[#3a6fa8]/25 to-transparent'
        : 'bg-linear-to-r from-transparent via-blue-400/20 to-transparent'"
    />

    <!-- Mobile Dropdown -->
    <transition
      enter-active-class="transition-all duration-250 ease-out"
      enter-from-class="opacity-0 -translate-y-1"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition-all duration-150 ease-in"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 -translate-y-1"
    >
      <div
        v-if="mobileOpen"
        class="md:hidden absolute left-0 right-0 z-40"
        :class="themeStore.isDark
          ? 'bg-[#141920] border-b border-[#232c3a]'
          : 'bg-white border-b border-slate-200 shadow-xl'"
      >
        <div class="max-w-7xl mx-auto px-4 py-3 space-y-0.5">

          <router-link
            to="/"
            @click="mobileOpen = false"
            class="flex items-center justify-between px-4 py-3 rounded-xl text-[13px] font-medium transition-all duration-200"
            :class="themeStore.isDark
              ? 'text-[#7a8a9e] hover:text-[#c9d4e0] hover:bg-[#1c2633]'
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
            active-class="!text-[#7eb8f7] !bg-[#1a2a3d] !font-semibold"
          >
            {{ t('nav.home') }}
            <ChevronRight class="w-3.5 h-3.5 opacity-30" />
          </router-link>

          <router-link
            to="/vision"
            @click="mobileOpen = false"
            class="flex items-center justify-between px-4 py-3 rounded-xl text-[13px] font-medium transition-all duration-200"
            :class="themeStore.isDark
              ? 'text-[#7a8a9e] hover:text-[#c9d4e0] hover:bg-[#1c2633]'
              : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'"
            active-class="!text-[#7eb8f7] !bg-[#1a2a3d] !font-semibold"
          >
            {{ t('nav.vision') }}
            <ChevronRight class="w-3.5 h-3.5 opacity-30" />
          </router-link>

          <div
            class="h-px"
            :class="themeStore.isDark ? 'bg-[#232c3a]' : 'bg-slate-100'"
            style="margin: 6px 0;"
          />

          <template v-if="authStore.isAuthenticatedAfterCheck">
            <router-link
              to="/dashboard"
              @click="mobileOpen = false"
              class="flex items-center gap-2.5 px-4 py-3 rounded-xl text-[13px] font-semibold transition-all duration-200"
              :class="themeStore.isDark
                ? 'text-[#7eb8f7] bg-[#1a2a3d] hover:bg-[#1f3250] border border-[#2a3f5c]'
                : 'text-blue-600 bg-blue-50 hover:bg-blue-100'"
            >
              <LayoutGrid class="w-4 h-4" />
              {{ t('nav.dashboard') }}
            </router-link>
          </template>

          <template v-else>
            <router-link
              to="/login"
              @click="mobileOpen = false"
              class="flex items-center gap-2.5 px-4 py-3 rounded-xl text-[13px] font-semibold text-white"
              :style="themeStore.isDark
                ? 'background: linear-gradient(135deg, #2d5789 0%, #1f3f6a 100%); box-shadow: 0 0 0 1px rgba(74,120,175,0.25);'
                : 'background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);'"
            >
              <iconify-icon icon="fa-solid:sign-in-alt" style="font-size:12px" />
              {{ t('nav.login') }}
            </router-link>
          </template>

        </div>
        <div class="h-3" />
      </div>
    </transition>

  </nav>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

.navbar-font { font-family: 'Plus Jakarta Sans', system-ui, sans-serif; }

.theme-spin-enter-active,
.theme-spin-leave-active { transition: opacity 0.12s ease, transform 0.16s ease; }
.theme-spin-enter-from   { opacity: 0; transform: rotate(-35deg) scale(0.65); }
.theme-spin-leave-to     { opacity: 0; transform: rotate(35deg)  scale(0.65); }
</style>