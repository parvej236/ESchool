<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DashboardHeader  from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import ToastNotification from '@/components/common/ToastNotification.vue'
import { useAuthStore } from '@/stores/auth'
import { useTranslate } from '@/composables/useTranslate'
import {
  ImagePlus, X, ArrowLeft, Save, Link,
  LayoutList, ToggleLeft, ToggleRight, ArrowLeftRight, AlertTriangle
} from 'lucide-vue-next'

const route     = useRoute()
const router    = useRouter()
const authStore = useAuthStore()
const { translateField, isTranslating } = useTranslate()

const BASE_URL = 'http://localhost:8080/api/home-slides'
const IMG_BASE = 'http://localhost:8080'

// ── Sidebar state
const isCollapsed  = ref(false)
const isMobileOpen = ref(false)
const loading    = ref(false)
const submitting = ref(false)
const formError  = ref('')
const toast      = ref(null)

</script>

<template>
  <!-- Sidebar -->
  <DashboardSidebar
    :is-collapsed="isCollapsed"
    :is-mobile-open="isMobileOpen"
    @update:is-collapsed="isCollapsed = $event"
    @update:is-mobile-open="isMobileOpen = $event"
  />

  <!-- Main content -->
  <div
    :class="[
      'transition-all duration-300 ml-0',
      isCollapsed ? 'lg:ml-18' : 'lg:ml-64'
    ]"
  >
    <DashboardHeader
      :title="'Chapter Page'"
      :is-sidebar-collapsed="isCollapsed"
      @toggle-sidebar="isMobileOpen = !isMobileOpen"
    />

    <main class="pt-3 px-3 sm:px-5 pb-10 mt-16">

      <ToastNotification ref="toast" />

      <!-- Page header -->
      <div class="flex items-start gap-3 mb-5">
        <h1 class="text-2xl font-semibold text-gray-800">Chapters</h1>

      </div>
        <!-- Content -->
        <div class="bg-white rounded-lg shadow p-5">
          <p class="text-gray-600">This is the Chapters page. You can manage your chapters here.</p>
        </div>
    </main>
  </div>
</template>