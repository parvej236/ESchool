<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import DashboardHeader  from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import { useAuthStore } from '@/stores/auth'
import {
  ImagePlus, X, Pencil, Trash2, AlertTriangle, CheckCircle,
  Link, Search, LayoutGrid, List, SlidersHorizontal, ChevronUp,
  ChevronDown, ChevronsUpDown
} from 'lucide-vue-next'

const BASE_URL  = 'http://localhost:8080/api/home-slides'
const IMG_BASE  = 'http://localhost:8080'
const authStore = useAuthStore()
const router    = useRouter()
const route     = useRoute()

// ── State ──────────────────────────────────────────────
const slides  = ref([])
const loading = ref(false)
const toast   = ref({ show: false, message: '', type: 'success' })

// ── Filter / Sort / Pagination ─────────────────────────
const searchQuery   = ref('')
const viewMode      = ref('grid')       // 'grid' | 'list'
const showFilters   = ref(false)
const filterStatus  = ref('')           // '' | 'active' | 'hidden'
const sortKey       = ref('displayOrder')
const sortOrder     = ref('asc')
const currentPage   = ref(1)
const itemsPerPage  = ref(12)

// ── Delete modal ───────────────────────────────────────
const showDeleteModal = ref(false)
const selectedSlide   = ref(null)
const submitting      = ref(false)

// ── Helpers ────────────────────────────────────────────
const imgUrl = (path) => {
  if (!path) return null
  return `${IMG_BASE}${path.startsWith('/') ? path : '/' + path}`
}

const showToast = (message, type = 'success') => {
  toast.value = { show: true, message, type }
  setTimeout(() => toast.value.show = false, 3500)
}

// ── Computed ───────────────────────────────────────────
const filteredSlides = computed(() => {
  let result = [...slides.value]

  // Search — checks EN title, BN title, description
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(s =>
      s.title?.toLowerCase().includes(q)        ||
      s.titleBn?.toLowerCase().includes(q)      ||
      s.description?.toLowerCase().includes(q)  ||
      s.descriptionBn?.toLowerCase().includes(q)
    )
  }

  // Status filter
  if (filterStatus.value === 'active') result = result.filter(s => s.active)
  if (filterStatus.value === 'hidden') result = result.filter(s => !s.active)

  // Sort
  result.sort((a, b) => {
    let aV = a[sortKey.value] ?? ''
    let bV = b[sortKey.value] ?? ''
    if (typeof aV === 'string') aV = aV.toLowerCase()
    if (typeof bV === 'string') bV = bV.toLowerCase()
    if (aV < bV) return sortOrder.value === 'asc' ? -1 :  1
    if (aV > bV) return sortOrder.value === 'asc' ?  1 : -1
    return 0
  })

  return result
})

const totalPages      = computed(() => Math.max(1, Math.ceil(filteredSlides.value.length / itemsPerPage.value)))
const paginatedSlides = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value
  return filteredSlides.value.slice(start, start + itemsPerPage.value)
})
const hasActiveFilters = computed(() =>
  !!searchQuery.value.trim() || !!filterStatus.value
)

// ── Sort toggle ────────────────────────────────────────
const toggleSort = (key) => {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value   = key
    sortOrder.value = 'asc'
  }
  currentPage.value = 1
}

const clearFilters = () => {
  searchQuery.value = ''
  filterStatus.value = ''
  currentPage.value  = 1
}

// ── API ────────────────────────────────────────────────
const fetchSlides = async () => {
  loading.value = true
  try {
    const res = await authStore.authFetch(`${BASE_URL}/all`)
    if (res.ok && res.data.success) slides.value = res.data.data
    else showToast(res.data.message || 'Failed to load slides', 'error')
  } catch {
    showToast('Failed to connect to server', 'error')
  } finally {
    loading.value = false
  }
}

const confirmDelete = async () => {
  submitting.value = true
  try {
    const res = await authStore.authFetch(`${BASE_URL}/delete/${selectedSlide.value.id}`, {
      method: 'DELETE',
    })
    if (res.ok && res.data.success) {
      slides.value          = slides.value.filter(s => s.id !== selectedSlide.value.id)
      showDeleteModal.value = false
      selectedSlide.value   = null
      showToast('Slide deleted successfully!')
    } else {
      showToast(res.data.message || 'Failed to delete slide', 'error')
    }
  } catch {
    showToast('Failed to delete slide', 'error')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchSlides()
  // Show success toast when redirected back from create/edit
  if (route.query.toast === 'created') showToast('Slide created successfully!')
  if (route.query.toast === 'updated') showToast('Slide updated successfully!')
})
</script>

<template>
  <DashboardSidebar />
  <div class="ml-0 md:ml-64 transition-all">
    <DashboardHeader />
    <main class="pt-3 px-3 sm:px-6 pb-10">

      <!-- ── Toast ──────────────────────────────────── -->
      <Transition enter-active-class="transition duration-300 ease-out"
        enter-from-class="opacity-0 translate-y-[-8px]" enter-to-class="opacity-100 translate-y-0"
        leave-active-class="transition duration-200" leave-from-class="opacity-100" leave-to-class="opacity-0">
        <div v-if="toast.show"
          :class="['fixed top-5 right-5 z-100 flex items-center gap-3 px-4 py-3 rounded-xl shadow-xl text-sm font-medium text-white min-w-60',
            toast.type === 'success' ? 'bg-green-500' : 'bg-red-500']">
          <CheckCircle v-if="toast.type === 'success'" class="w-4 h-4 shrink-0" />
          <AlertTriangle v-else class="w-4 h-4 shrink-0" />
          <span class="flex-1">{{ toast.message }}</span>
          <button @click="toast.show = false"><X class="w-3.5 h-3.5 opacity-70 hover:opacity-100" /></button>
        </div>
      </Transition>

      <!-- ── Page Header ────────────────────────────── -->
      <div class="flex items-center justify-between mb-5">
        <div>
          <h1 class="text-xl sm:text-2xl font-bold text-gray-800">Home Slides</h1>
          <p class="text-sm text-gray-400 mt-0.5">
            {{ filteredSlides.length }} of {{ slides.length }} slides
          </p>
        </div>
        <button @click="router.push({ name: 'home-slides-create' })"
          class="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white
                 px-3 sm:px-4 py-2 rounded-xl text-sm font-medium transition shadow-sm whitespace-nowrap">
          <ImagePlus class="w-4 h-4" />
          <span class="hidden sm:inline">New Slide</span>
        </button>
      </div>

      <!-- ── Toolbar: Search + Filters + View Toggle ── -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 px-4 py-3 mb-4">
        <div class="flex flex-wrap items-center gap-3">

          <!-- Search -->
          <div class="relative flex-1 min-w-45">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
            <input
              v-model="searchQuery"
              @input="currentPage = 1"
              type="text"
              placeholder="Search slides..."
              class="w-full pl-9 pr-4 py-2 text-sm border border-gray-200 rounded-xl
                     focus:outline-none focus:ring-2 focus:ring-blue-400 transition
                     placeholder:text-gray-300"
            />
            <button v-if="searchQuery" @click="searchQuery = ''; currentPage = 1"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition">
              <X class="w-3.5 h-3.5" />
            </button>
          </div>

          <!-- Filter toggle -->
          <button @click="showFilters = !showFilters"
            :class="['flex items-center gap-2 px-3 py-2 rounded-xl border text-sm font-medium transition',
              showFilters || hasActiveFilters
                ? 'bg-blue-50 border-blue-200 text-blue-600'
                : 'bg-gray-50 border-gray-200 text-gray-600 hover:bg-gray-100']">
            <SlidersHorizontal class="w-4 h-4" />
            <span class="hidden sm:inline">Filters</span>
            <span v-if="hasActiveFilters"
              class="w-2 h-2 bg-blue-500 rounded-full"></span>
          </button>

          <!-- View mode toggle -->
          <div class="flex items-center border border-gray-200 rounded-xl overflow-hidden bg-gray-50">
            <button @click="viewMode = 'grid'"
              :class="['flex items-center gap-1.5 px-3 py-2 text-sm font-medium transition',
                viewMode === 'grid' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700']">
              <LayoutGrid class="w-4 h-4" />
              <span class="hidden sm:inline text-xs">Grid</span>
            </button>
            <button @click="viewMode = 'list'"
              :class="['flex items-center gap-1.5 px-3 py-2 text-sm font-medium transition',
                viewMode === 'list' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700']">
              <List class="w-4 h-4" />
              <span class="hidden sm:inline text-xs">List</span>
            </button>
          </div>

          <!-- Items per page -->
          <select v-model.number="itemsPerPage" @change="currentPage = 1"
            class="px-3 py-2 text-sm border border-gray-200 rounded-xl bg-gray-50
                   text-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-400 transition">
            <option :value="8">8 / page</option>
            <option :value="12">12 / page</option>
            <option :value="24">24 / page</option>
            <option :value="48">48 / page</option>
          </select>
        </div>

        <!-- ── Filter row (expandable) ──────────────── -->
        <Transition
          enter-active-class="transition duration-200 ease-out"
          enter-from-class="opacity-0 -translate-y-2"
          enter-to-class="opacity-100 translate-y-0"
          leave-active-class="transition duration-150"
          leave-from-class="opacity-100"
          leave-to-class="opacity-0">
          <div v-if="showFilters" class="flex flex-wrap items-center gap-3 mt-3 pt-3 border-t border-gray-100">

            <!-- Status filter -->
            <div class="flex items-center gap-2">
              <span class="text-xs font-semibold text-gray-500 uppercase tracking-wider">Status</span>
              <div class="flex gap-1">
                <button v-for="opt in [
                  { value: '',       label: 'All'    },
                  { value: 'active', label: 'Active' },
                  { value: 'hidden', label: 'Hidden' },
                ]" :key="opt.value"
                  @click="filterStatus = opt.value; currentPage = 1"
                  :class="['px-3 py-1 rounded-lg text-xs font-medium transition border',
                    filterStatus === opt.value
                      ? 'bg-blue-600 text-white border-blue-600'
                      : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50']">
                  {{ opt.label }}
                </button>
              </div>
            </div>

            <!-- Sort options -->
            <div class="flex items-center gap-2">
              <span class="text-xs font-semibold text-gray-500 uppercase tracking-wider">Sort by</span>
              <div class="flex gap-1">
                <button v-for="opt in [
                  { value: 'displayOrder', label: 'Order' },
                  { value: 'title',        label: 'Title' },
                  { value: 'id',           label: 'ID'    },
                ]" :key="opt.value"
                  @click="toggleSort(opt.value)"
                  :class="['flex items-center gap-1 px-3 py-1 rounded-lg text-xs font-medium transition border',
                    sortKey === opt.value
                      ? 'bg-blue-600 text-white border-blue-600'
                      : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50']">
                  {{ opt.label }}
                  <ChevronUp   v-if="sortKey === opt.value && sortOrder === 'asc'"  class="w-3 h-3" />
                  <ChevronDown v-if="sortKey === opt.value && sortOrder === 'desc'" class="w-3 h-3" />
                  <ChevronsUpDown v-if="sortKey !== opt.value" class="w-3 h-3 opacity-50" />
                </button>
              </div>
            </div>

            <!-- Clear filters -->
            <button v-if="hasActiveFilters" @click="clearFilters"
              class="flex items-center gap-1 px-3 py-1 rounded-lg text-xs font-medium
                     text-red-500 bg-red-50 hover:bg-red-100 border border-red-100 transition ml-auto">
              <X class="w-3 h-3" /> Clear filters
            </button>
          </div>
        </Transition>
      </div>

      <!-- ── Loading skeleton ───────────────────────── -->
      <div v-if="loading" class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
        <div v-for="i in 8" :key="i"
          class="bg-white rounded-2xl border border-gray-100 overflow-hidden animate-pulse">
          <div class="h-36 bg-gray-200"></div>
          <div class="p-4 space-y-2">
            <div class="h-3 bg-gray-200 rounded w-3/4"></div>
            <div class="h-2 bg-gray-100 rounded w-1/2"></div>
            <div class="h-2 bg-gray-100 rounded w-1/3"></div>
          </div>
        </div>
      </div>

      <template v-else>

        <!-- ── Empty state ─────────────────────────── -->
        <div v-if="filteredSlides.length === 0"
          class="text-center py-20 bg-white rounded-2xl border border-gray-100">
          <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <ImagePlus class="w-8 h-8 text-gray-300" />
          </div>
          <p class="font-semibold text-gray-500 mb-1">No slides found</p>
          <p v-if="hasActiveFilters" class="text-sm text-gray-400 mb-4">Try clearing your filters</p>
          <div class="flex items-center justify-center gap-3">
            <button v-if="hasActiveFilters" @click="clearFilters"
              class="text-sm text-blue-600 border border-blue-200 bg-blue-50 px-4 py-2 rounded-xl hover:bg-blue-100 transition">
              Clear filters
            </button>
            <button @click="router.push({ name: 'home-slides-create' })"
              class="text-sm bg-blue-600 text-white px-4 py-2 rounded-xl hover:bg-blue-700 transition">
              Create first slide
            </button>
          </div>
        </div>

        <!-- ── GRID VIEW ───────────────────────────── -->
        <div v-else-if="viewMode === 'grid'"
          class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          <div v-for="item in paginatedSlides" :key="item.id"
            class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden
                   hover:shadow-md transition-all hover:-translate-y-0.5 group relative">

            <!-- Hover actions -->
            <div class="absolute top-2 right-2 flex gap-1 opacity-0 group-hover:opacity-100 transition-all z-10">
              <button @click="router.push({ name: 'home-slides-edit', params: { id: item.id } })"
                class="p-1.5 rounded-lg bg-white/90 text-blue-500 hover:bg-blue-50 shadow-sm transition">
                <Pencil class="w-3.5 h-3.5" />
              </button>
              <button @click="selectedSlide = item; showDeleteModal = true"
                class="p-1.5 rounded-lg bg-white/90 text-red-400 hover:bg-red-50 shadow-sm transition">
                <Trash2 class="w-3.5 h-3.5" />
              </button>
            </div>

            <!-- Status badge -->
            <div class="absolute top-2 left-2 z-10">
              <span :class="['text-xs font-semibold px-2 py-0.5 rounded-full shadow-sm',
                item.active ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500']">
                {{ item.active ? 'Active' : 'Hidden' }}
              </span>
            </div>

            <!-- Image -->
            <div class="h-36 bg-gray-100 overflow-hidden">
              <img v-if="item.imageUrl" :src="imgUrl(item.imageUrl)" :alt="item.title"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                <ImagePlus class="w-8 h-8" />
              </div>
            </div>

            <!-- Info -->
            <div class="p-3">
              <h3 class="font-semibold text-gray-800 text-sm truncate">{{ item.title }}</h3>
              <p v-if="item.titleBn" class="text-xs text-indigo-500 truncate mt-0.5 font-medium">
                {{ item.titleBn }}
              </p>
              <p class="text-xs text-gray-400 mt-0.5 truncate">{{ item.description || '—' }}</p>
              <div class="flex items-center justify-between mt-2">
                <span class="text-xs text-gray-400 bg-gray-50 px-1.5 py-0.5 rounded-md">
                  #{{ item.displayOrder }}
                </span>
                <a v-if="item.linkUrl" :href="item.linkUrl" target="_blank"
                  class="text-xs text-blue-400 hover:text-blue-600 flex items-center gap-0.5 transition">
                  <Link class="w-3 h-3" /> Link
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- ── LIST VIEW ───────────────────────────── -->
        <div v-else class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">

          <!-- Table header -->
          <div class="grid grid-cols-[48px_80px_1fr_80px_90px_120px] gap-4 px-4 py-3
                      bg-gray-50 border-b border-gray-100 text-xs font-semibold text-gray-500 uppercase tracking-wider">
            <button class="text-left flex items-center gap-1 hover:text-gray-700 transition"
              @click="toggleSort('id')">
              ID
              <ChevronUp   v-if="sortKey === 'id' && sortOrder === 'asc'"  class="w-3 h-3" />
              <ChevronDown v-if="sortKey === 'id' && sortOrder === 'desc'" class="w-3 h-3" />
              <ChevronsUpDown v-if="sortKey !== 'id'" class="w-3 h-3 opacity-40" />
            </button>
            <span>Image</span>
            <button class="text-left flex items-center gap-1 hover:text-gray-700 transition"
              @click="toggleSort('title')">
              Title
              <ChevronUp   v-if="sortKey === 'title' && sortOrder === 'asc'"  class="w-3 h-3" />
              <ChevronDown v-if="sortKey === 'title' && sortOrder === 'desc'" class="w-3 h-3" />
              <ChevronsUpDown v-if="sortKey !== 'title'" class="w-3 h-3 opacity-40" />
            </button>
            <button class="text-center flex items-center justify-center gap-1 hover:text-gray-700 transition"
              @click="toggleSort('displayOrder')">
              Order
              <ChevronUp   v-if="sortKey === 'displayOrder' && sortOrder === 'asc'"  class="w-3 h-3" />
              <ChevronDown v-if="sortKey === 'displayOrder' && sortOrder === 'desc'" class="w-3 h-3" />
              <ChevronsUpDown v-if="sortKey !== 'displayOrder'" class="w-3 h-3 opacity-40" />
            </button>
            <span class="text-center">Status</span>
            <span class="text-center">Actions</span>
          </div>

          <!-- Table rows -->
          <div v-for="(row, idx) in paginatedSlides" :key="row.id"
            :class="['grid grid-cols-[48px_80px_1fr_80px_90px_120px] gap-4 px-4 py-3 items-center transition',
              idx % 2 === 0 ? 'bg-white' : 'bg-gray-50/50',
              'hover:bg-blue-50/30 border-b border-gray-100 last:border-0']">

            <!-- ID -->
            <span class="text-xs text-gray-400 font-mono">#{{ row.id }}</span>

            <!-- Image -->
            <div class="w-14 h-10 rounded-lg overflow-hidden bg-gray-100 shrink-0">
              <img v-if="row.imageUrl" :src="imgUrl(row.imageUrl)" :alt="row.title"
                class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex items-center justify-center">
                <ImagePlus class="w-4 h-4 text-gray-300" />
              </div>
            </div>

            <!-- Title (EN + BN) -->
            <div class="min-w-0">
              <p class="font-semibold text-gray-800 text-sm truncate">{{ row.title }}</p>
              <p v-if="row.titleBn" class="text-xs text-indigo-500 truncate">{{ row.titleBn }}</p>
              <p class="text-xs text-gray-400 truncate">{{ row.description || '—' }}</p>
            </div>

            <!-- Order -->
            <span class="text-center text-sm text-gray-600 font-mono font-medium">
              {{ row.displayOrder }}
            </span>

            <!-- Status -->
            <div class="flex justify-center">
              <span :class="['text-xs font-semibold px-2.5 py-1 rounded-full',
                row.active ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500']">
                {{ row.active ? 'Active' : 'Hidden' }}
              </span>
            </div>

            <!-- Actions -->
            <div class="flex items-center justify-center gap-2">
              <button @click="router.push({ name: 'home-slides-edit', params: { id: row.id } })"
                class="inline-flex items-center gap-1 px-2.5 py-1.5 rounded-lg text-xs font-medium
                       bg-blue-50 text-blue-600 hover:bg-blue-100 transition">
                <Pencil class="w-3 h-3" /> Edit
              </button>
              <button @click="selectedSlide = row; showDeleteModal = true"
                class="inline-flex items-center gap-1 px-2.5 py-1.5 rounded-lg text-xs font-medium
                       bg-red-50 text-red-500 hover:bg-red-100 transition">
                <Trash2 class="w-3 h-3" /> Delete
              </button>
            </div>
          </div>
        </div>

      </template>

      <!-- ── Pagination ──────────────────────────────── -->
      <div v-if="!loading && filteredSlides.length > 0"
        class="flex flex-col sm:flex-row items-center justify-between gap-3 mt-5
               bg-white rounded-2xl shadow-sm border border-gray-100 px-5 py-3">

        <p class="text-sm text-gray-500">
          Showing
          <span class="font-semibold text-gray-700">
            {{ (currentPage - 1) * itemsPerPage + 1 }}–{{ Math.min(currentPage * itemsPerPage, filteredSlides.length) }}
          </span>
          of <span class="font-semibold text-gray-700">{{ filteredSlides.length }}</span> slides
        </p>

        <div class="flex items-center gap-1">
          <!-- First -->
          <button @click="currentPage = 1" :disabled="currentPage === 1"
            class="px-2.5 py-1.5 rounded-lg text-xs font-medium border border-gray-200
                   text-gray-500 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition">
            «
          </button>
          <!-- Prev -->
          <button @click="currentPage--" :disabled="currentPage === 1"
            class="px-2.5 py-1.5 rounded-lg text-xs font-medium border border-gray-200
                   text-gray-500 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition">
            ‹
          </button>

          <!-- Page numbers -->
          <template v-for="page in totalPages" :key="page">
            <button v-if="page === 1 || page === totalPages || Math.abs(page - currentPage) <= 1"
              @click="currentPage = page"
              :class="['px-3 py-1.5 rounded-lg text-xs font-medium border transition',
                page === currentPage
                  ? 'bg-blue-600 text-white border-blue-600 shadow-sm'
                  : 'border-gray-200 text-gray-600 hover:bg-gray-50']">
              {{ page }}
            </button>
            <span v-else-if="Math.abs(page - currentPage) === 2"
              class="px-1 text-gray-400 text-xs">…</span>
          </template>

          <!-- Next -->
          <button @click="currentPage++" :disabled="currentPage === totalPages"
            class="px-2.5 py-1.5 rounded-lg text-xs font-medium border border-gray-200
                   text-gray-500 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition">
            ›
          </button>
          <!-- Last -->
          <button @click="currentPage = totalPages" :disabled="currentPage === totalPages"
            class="px-2.5 py-1.5 rounded-lg text-xs font-medium border border-gray-200
                   text-gray-500 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition">
            »
          </button>
        </div>
      </div>

    </main>
  </div>

  <!-- ── Delete Modal ────────────────────────────────── -->
  <Teleport to="body">
    <Transition enter-active-class="transition duration-200" enter-from-class="opacity-0"
      enter-to-class="opacity-100" leave-active-class="transition duration-150"
      leave-from-class="opacity-100" leave-to-class="opacity-0">
      <div v-if="showDeleteModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-black/40 backdrop-blur-sm"
          @click="showDeleteModal = false; selectedSlide = null"></div>
        <Transition enter-active-class="transition duration-200 ease-out"
          enter-from-class="opacity-0 scale-95" enter-to-class="opacity-100 scale-100">
          <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6 z-10 text-center">
            <div class="w-full h-28 rounded-xl overflow-hidden bg-gray-100 mb-4">
              <img v-if="selectedSlide?.imageUrl"
                :src="imgUrl(selectedSlide.imageUrl)" :alt="selectedSlide?.title"
                class="w-full h-full object-cover" />
            </div>
            <div class="w-12 h-12 bg-red-50 rounded-2xl flex items-center justify-center mx-auto mb-3">
              <Trash2 class="w-6 h-6 text-red-500" />
            </div>
            <h2 class="text-lg font-bold text-gray-800 mb-1">Delete Slide</h2>
            <p class="text-sm text-gray-500 mb-1">Permanently deleting</p>
            <p class="text-sm font-semibold text-gray-800 bg-gray-100 px-3 py-1 rounded-lg mb-4 inline-block">
              {{ selectedSlide?.title }}
            </p>
            <p class="text-xs text-gray-400 mb-5">
              The image file will also be removed from the server.
            </p>
            <div class="flex gap-3">
              <button @click="showDeleteModal = false; selectedSlide = null"
                class="flex-1 px-4 py-2.5 text-sm font-medium text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-xl transition">
                Cancel
              </button>
              <button @click="confirmDelete" :disabled="submitting"
                class="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 text-sm font-medium
                       text-white bg-red-500 hover:bg-red-600 disabled:opacity-60 rounded-xl transition">
                <svg v-if="submitting" class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                </svg>
                <Trash2 v-else class="w-4 h-4" />
                {{ submitting ? 'Deleting...' : 'Yes, Delete' }}
              </button>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>