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

const isEditing  = computed(() => !!route.params.id)
const pageTitle  = computed(() => isEditing.value ? 'Edit Slide' : 'Create New Slide')

const loading    = ref(false)
const submitting = ref(false)
const formError  = ref('')
const toast      = ref(null)

// ── Form
const form = ref({
  title:         '',
  description:   '',
  titleBn:       '',
  descriptionBn: '',
  linkUrl:       '',
  displayOrder:  0,
  active:        true,
})

// ── Image
const imageFile        = ref(null)
const imagePreview     = ref(null)
const existingImageUrl = ref(null)
const imageInputRef    = ref(null)

const imgUrl = (path) => {
  if (!path) return null
  return `${IMG_BASE}${path.startsWith('/') ? path : '/' + path}`
}

// ── Load slide for edit
const loadSlide = async () => {
  loading.value = true
  try {
    const res = await authStore.authFetch(`${BASE_URL}/${route.params.id}`)
    if (res.ok && res.data.success) {
      const s = res.data.data
      form.value = {
        title:         s.title         ?? '',
        description:   s.description   ?? '',
        titleBn:       s.titleBn       ?? '',
        descriptionBn: s.descriptionBn ?? '',
        linkUrl:       s.linkUrl       ?? '',
        displayOrder:  s.displayOrder  ?? 0,
        active:        s.active        ?? true,
      }
      existingImageUrl.value = s.imageUrl
    } else {
      formError.value = 'Failed to load slide data.'
    }
  } catch {
    formError.value = 'Network error while loading slide.'
  } finally {
    loading.value = false
  }
}

// ── Image handling
const onImageChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  const allowed = ['image/jpeg', 'image/png', 'image/webp', 'image/gif']
  if (!allowed.includes(file.type)) { formError.value = 'Only JPG, PNG, WEBP or GIF images allowed.'; return }
  if (file.size > 10 * 1024 * 1024) { formError.value = 'Image must be smaller than 10MB.'; return }
  imageFile.value = file
  formError.value = ''
  const reader = new FileReader()
  reader.onload = (ev) => imagePreview.value = ev.target.result
  reader.readAsDataURL(file)
}

const clearImage = () => {
  imageFile.value = null
  imagePreview.value = null
  if (imageInputRef.value) imageInputRef.value.value = ''
}

const buildFormData = () => {
  const fd = new FormData()
  fd.append('title',         form.value.title)
  fd.append('description',   form.value.description   ?? '')
  fd.append('titleBn',       form.value.titleBn       ?? '')
  fd.append('descriptionBn', form.value.descriptionBn ?? '')
  fd.append('linkUrl',       form.value.linkUrl       ?? '')
  fd.append('displayOrder',  String(form.value.displayOrder ?? 0))
  fd.append('active',        String(form.value.active ?? true))
  if (imageFile.value) fd.append('image', imageFile.value)
  return fd
}

// ── Translation
const translateTitle = async (direction) => {
  const [from, to] = direction === 'en-to-bn' ? ['en', 'bn'] : ['bn', 'en']
  const sourceText = direction === 'en-to-bn' ? form.value.title : form.value.titleBn
  const result = await translateField(sourceText, from, to, `title-${direction}`)
  if (result) {
    if (direction === 'en-to-bn') form.value.titleBn = result
    else form.value.title = result
  }
}

const translateDescription = async (direction) => {
  const [from, to] = direction === 'en-to-bn' ? ['en', 'bn'] : ['bn', 'en']
  const sourceText = direction === 'en-to-bn' ? form.value.description : form.value.descriptionBn
  const result = await translateField(sourceText, from, to, `desc-${direction}`)
  if (result) {
    if (direction === 'en-to-bn') form.value.descriptionBn = result
    else form.value.description = result
  }
}

// ── Submit
const handleSubmit = async () => {
  formError.value = ''
  if (!form.value.title.trim()) return formError.value = 'English title is required.'
  if (!isEditing.value && !imageFile.value) return formError.value = 'Please select an image.'

  submitting.value = true
  try {
    const url    = isEditing.value ? `${BASE_URL}/update/${route.params.id}` : `${BASE_URL}/create`
    const method = isEditing.value ? 'PUT' : 'POST'
    const res = await authStore.authFetch(url, { method, body: buildFormData() })
    if (res.ok && res.data.success) {
      router.push({ name: 'home-slides', query: { toast: isEditing.value ? 'updated' : 'created' } })
    } else {
      formError.value = res.data.message || res.data.error || 'Failed to save slide.'
    }
  } catch {
    formError.value = 'Network error. Please try again.'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (isEditing.value) loadSlide()
})
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
      :title="pageTitle"
      :is-sidebar-collapsed="isCollapsed"
      @toggle-sidebar="isMobileOpen = !isMobileOpen"
    />

    <main class="pt-3 px-3 sm:px-5 pb-10 mt-16">

      <ToastNotification ref="toast" />

      <!-- Page header -->
      <div class="flex items-start gap-3 mb-5">
        <button
          @click="router.push({ name: 'home-slides' })"
          class="mt-0.5 p-2 rounded-xl hover:bg-gray-200 text-gray-500 transition shrink-0"
        >
          <ArrowLeft class="w-5 h-5" />
        </button>
        <div>
          <h1 class="text-lg sm:text-2xl font-bold text-gray-800">{{ pageTitle }}</h1>
          <p class="text-xs sm:text-sm text-gray-400 mt-0.5">
            {{ isEditing ? 'Update slide content in English and Bangla' : 'Add a new slide with bilingual content' }}
          </p>
        </div>
      </div>

      <!-- Loading skeleton -->
      <div v-if="loading" class="bg-white rounded-2xl border border-gray-100 p-6 sm:p-8 animate-pulse space-y-4">
        <div class="h-40 sm:h-48 bg-gray-200 rounded-xl"></div>
        <div class="h-4 bg-gray-200 rounded w-1/2"></div>
        <div class="h-10 bg-gray-100 rounded-xl"></div>
      </div>

      <!-- Form grid: stacks on mobile, side-by-side on xl -->
      <div v-else class="grid grid-cols-1 xl:grid-cols-3 gap-4 sm:gap-6">

        <!-- LEFT: Image + Settings -->
        <!-- On mobile this renders after the content (order-last), feels more natural -->
        <div class="order-last xl:order-first space-y-4 sm:space-y-5">

          <!-- Image upload card -->
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 sm:p-5">
            <h3 class="text-sm font-bold text-gray-700 mb-4 flex items-center gap-2">
              <ImagePlus class="w-4 h-4 text-blue-500" />
              Slide Image
              <span v-if="!isEditing" class="text-red-400 text-xs">*</span>
            </h3>

            <!-- Preview -->
            <div class="relative w-full h-40 sm:h-48 rounded-xl overflow-hidden bg-gray-50 border-2 border-dashed border-gray-200 mb-3 group">
              <img v-if="imagePreview" :src="imagePreview" alt="New preview" class="w-full h-full object-cover" />
              <img v-else-if="isEditing && existingImageUrl" :src="imgUrl(existingImageUrl)"
                alt="Current image" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex flex-col items-center justify-center text-gray-300 gap-2">
                <ImagePlus class="w-10 h-10 sm:w-12 sm:h-12" />
                <span class="text-xs">Click below to upload</span>
              </div>
              <button v-if="imagePreview" @click="clearImage" type="button"
                class="absolute top-2 right-2 p-1 bg-black/50 hover:bg-black/70 text-white rounded-lg transition">
                <X class="w-3.5 h-3.5" />
              </button>
              <div v-if="isEditing && existingImageUrl && !imagePreview"
                class="absolute bottom-2 left-2 bg-black/50 text-white text-xs px-2 py-1 rounded-lg">
                Current image
              </div>
            </div>

            <input ref="imageInputRef" type="file"
              accept="image/jpeg,image/png,image/webp,image/gif"
              class="hidden" @change="onImageChange" />

            <button type="button" @click="imageInputRef.click()"
              class="w-full flex items-center justify-center gap-2 px-4 py-2.5 border border-gray-200 rounded-xl text-sm text-gray-600 hover:bg-gray-50 hover:border-blue-300 transition">
              <ImagePlus class="w-4 h-4 text-blue-500" />
              {{ imageFile ? 'Change Image' : (isEditing ? 'Replace Image' : 'Choose Image') }}
            </button>

            <p v-if="imageFile" class="text-xs text-gray-400 mt-1.5 text-center truncate">
              ✓ {{ imageFile.name }}
            </p>
            <p v-if="isEditing && !imageFile" class="text-xs text-gray-400 mt-1.5 text-center">
              Leave empty to keep current image
            </p>
          </div>

          <!-- Settings card -->
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 sm:p-5">
            <h3 class="text-sm font-bold text-gray-700 mb-4 flex items-center gap-2">
              <LayoutList class="w-4 h-4 text-purple-500" />
              Settings
            </h3>

            <!-- Link URL -->
            <div class="mb-4">
              <label class="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
                Link URL <span class="text-gray-400 font-normal normal-case">(optional)</span>
              </label>
              <div class="relative">
                <Link class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                <input v-model="form.linkUrl" type="url" placeholder="https://example.com"
                  class="w-full border border-gray-200 rounded-xl pl-10 pr-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 transition placeholder:text-gray-300" />
              </div>
            </div>

            <!-- Display order -->
            <div class="mb-4">
              <label class="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
                Display Order
              </label>
              <input v-model.number="form.displayOrder" type="number" min="0"
                class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 transition" />
            </div>

            <!-- Active toggle -->
            <div>
              <label class="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1.5">
                Status
              </label>
              <button type="button" @click="form.active = !form.active"
                :class="['w-full flex items-center justify-between px-4 py-2.5 rounded-xl border text-sm font-medium transition',
                  form.active ? 'bg-green-50 border-green-200 text-green-700' : 'bg-gray-50 border-gray-200 text-gray-500']">
                <span class="text-xs sm:text-sm">{{ form.active ? 'Active — visible on homepage' : 'Hidden — not shown' }}</span>
                <ToggleRight v-if="form.active" class="w-5 h-5 text-green-500 shrink-0" />
                <ToggleLeft  v-else             class="w-5 h-5 text-gray-400 shrink-0" />
              </button>
            </div>
          </div>
        </div>

        <!-- RIGHT: Bilingual content -->
        <div class="xl:col-span-2 space-y-4 sm:space-y-5">

          <!-- Error banner -->
          <div v-if="formError"
            class="flex items-start gap-2 bg-red-50 border border-red-100 text-red-600 text-sm px-4 py-3 rounded-xl">
            <AlertTriangle class="w-4 h-4 shrink-0 mt-0.5" />
            <span>{{ formError }}</span>
          </div>

          <!-- Title card -->
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 sm:p-5">
            <h3 class="text-sm font-bold text-gray-700 mb-4">Title</h3>

            <!-- Stacks to 1-col on mobile, 2-col on sm+ -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">

              <!-- English title -->
              <div>
                <div class="flex items-center justify-between mb-1.5">
                  <label class="text-xs font-semibold text-gray-500 uppercase tracking-wider">
                    English <span class="text-red-400">*</span>
                  </label>
                  <button type="button" @click="translateTitle('en-to-bn')"
                    :disabled="!form.title.trim() || isTranslating('title-en-to-bn')"
                    class="flex items-center gap-1 text-xs text-indigo-500 hover:text-indigo-700 disabled:opacity-40 disabled:cursor-not-allowed transition">
                    <span v-if="isTranslating('title-en-to-bn')" class="flex items-center gap-1">
                      <svg class="animate-spin w-3 h-3" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                      </svg>
                      Translating...
                    </span>
                    <span v-else class="flex items-center gap-1">
                      <ArrowLeftRight class="w-3 h-3" />
                      → বাংলা
                    </span>
                  </button>
                </div>
                <input v-model="form.title" type="text" placeholder="Enter English title"
                  class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 transition placeholder:text-gray-300" />
              </div>

              <!-- Bangla title -->
              <div>
                <div class="flex items-center justify-between mb-1.5">
                  <label class="text-xs font-semibold text-gray-500 uppercase tracking-wider">বাংলা</label>
                  <button type="button" @click="translateTitle('bn-to-en')"
                    :disabled="!form.titleBn.trim() || isTranslating('title-bn-to-en')"
                    class="flex items-center gap-1 text-xs text-indigo-500 hover:text-indigo-700 disabled:opacity-40 disabled:cursor-not-allowed transition">
                    <span v-if="isTranslating('title-bn-to-en')" class="flex items-center gap-1">
                      <svg class="animate-spin w-3 h-3" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                      </svg>
                      Translating...
                    </span>
                    <span v-else class="flex items-center gap-1">
                      <ArrowLeftRight class="w-3 h-3" />
                      → English
                    </span>
                  </button>
                </div>
                <input v-model="form.titleBn" type="text" placeholder="বাংলা শিরোনাম লিখুন"
                  class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 transition placeholder:text-gray-300"
                  style="font-family: 'Noto Sans Bengali', sans-serif;" />
              </div>
            </div>
          </div>

          <!-- Description card -->
          <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 sm:p-5">
            <h3 class="text-sm font-bold text-gray-700 mb-4">Description</h3>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">

              <!-- English description -->
              <div>
                <div class="flex items-center justify-between mb-1.5">
                  <label class="text-xs font-semibold text-gray-500 uppercase tracking-wider">English</label>
                  <button type="button" @click="translateDescription('en-to-bn')"
                    :disabled="!form.description.trim() || isTranslating('desc-en-to-bn')"
                    class="flex items-center gap-1 text-xs text-indigo-500 hover:text-indigo-700 disabled:opacity-40 disabled:cursor-not-allowed transition">
                    <span v-if="isTranslating('desc-en-to-bn')" class="flex items-center gap-1">
                      <svg class="animate-spin w-3 h-3" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                      </svg>
                      Translating...
                    </span>
                    <span v-else class="flex items-center gap-1">
                      <ArrowLeftRight class="w-3 h-3" />
                      → বাংলা
                    </span>
                  </button>
                </div>
                <textarea v-model="form.description" rows="4" placeholder="Enter English description..."
                  class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 transition placeholder:text-gray-300 resize-none" />
              </div>

              <!-- Bangla description -->
              <div>
                <div class="flex items-center justify-between mb-1.5">
                  <label class="text-xs font-semibold text-gray-500 uppercase tracking-wider">বাংলা</label>
                  <button type="button" @click="translateDescription('bn-to-en')"
                    :disabled="!form.descriptionBn.trim() || isTranslating('desc-bn-to-en')"
                    class="flex items-center gap-1 text-xs text-indigo-500 hover:text-indigo-700 disabled:opacity-40 disabled:cursor-not-allowed transition">
                    <span v-if="isTranslating('desc-bn-to-en')" class="flex items-center gap-1">
                      <svg class="animate-spin w-3 h-3" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                      </svg>
                      Translating...
                    </span>
                    <span v-else class="flex items-center gap-1">
                      <ArrowLeftRight class="w-3 h-3" />
                      → English
                    </span>
                  </button>
                </div>
                <textarea v-model="form.descriptionBn" rows="4" placeholder="বাংলা বিবরণ লিখুন..."
                  class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 transition placeholder:text-gray-300 resize-none"
                  style="font-family: 'Noto Sans Bengali', sans-serif;" />
              </div>
            </div>
          </div>

          <!-- Action bar -->
          <div class="flex flex-col-reverse sm:flex-row items-stretch sm:items-center justify-between gap-3
                      bg-white rounded-2xl shadow-sm border border-gray-100 px-4 sm:px-6 py-4">
            <button
              @click="router.push({ name: 'home-slides' })"
              class="flex items-center justify-center gap-2 px-4 py-2.5 text-sm font-medium text-gray-500 hover:text-gray-700 rounded-xl hover:bg-gray-100 transition"
            >
              <ArrowLeft class="w-4 h-4" />
              Back to List
            </button>

            <button
              @click="handleSubmit"
              :disabled="submitting"
              :class="['flex items-center justify-center gap-2 px-6 py-2.5 text-white text-sm font-medium rounded-xl transition shadow-sm disabled:opacity-60 disabled:cursor-not-allowed',
                isEditing ? 'bg-blue-600 hover:bg-blue-700' : 'bg-green-600 hover:bg-green-700']"
            >
              <svg v-if="submitting" class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
              </svg>
              <Save v-else class="w-4 h-4" />
              {{ submitting ? 'Saving...' : (isEditing ? 'Update Slide' : 'Create Slide') }}
            </button>
          </div>

        </div>
      </div>
    </main>
  </div>
</template>