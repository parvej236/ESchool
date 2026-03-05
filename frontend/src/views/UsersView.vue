<script setup>
import { ref, computed, onMounted } from 'vue'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import DataFilters from '@/components/shared/DataFilters.vue'
import DataTable from '@/components/shared/DataTable.vue'
import DataGrid from '@/components/shared/DataGrid.vue'
import DataPagination from '@/components/shared/DataPagination.vue'
import { useAuthStore } from '@/stores/auth'                          // ← ADD THIS
import { UserPlus, X, Eye, EyeOff, Pencil, Trash2, AlertTriangle, Mail } from 'lucide-vue-next'

const BASE_URL  = 'http://localhost:8080/api/users'
const authStore = useAuthStore()                                       // ← ADD THIS

// ── State ──────────────────────────────────────────────
const users   = ref([])
const loading = ref(false)
const toast   = ref({ show: false, message: '', type: 'success' })

const searchQuery   = ref('')
const viewMode      = ref('grid')
const showFilterRow = ref(false)
const filters       = ref({})
const sortKey       = ref('')
const sortOrder     = ref('asc')
const currentPage   = ref(1)
const itemsPerPage  = ref(10)

const showModal       = ref(false)
const showDeleteModal = ref(false)
const isEditing       = ref(false)
const selectedUser    = ref(null)
const showPassword    = ref(false)
const formError       = ref('')
const submitting      = ref(false)
const form            = ref({ username: '', email: '', password: '' })

// ── Columns ────────────────────────────────────────────
const columns = [
    { key: 'id',        label: 'ID',      sortable: true,  filterable: true,  class: 'w-20' },
    { key: 'username',  label: 'User',    sortable: true,  filterable: true  },
    { key: 'email',     label: 'Email',   sortable: true,  filterable: true,  hideOnMobile: true },
    { key: 'createdAt', label: 'Joined',  sortable: true,  filterable: false, hideOnMobile: true },
    { key: 'actions',   label: 'Actions', sortable: false, filterable: false, class: 'text-center' },
]

// ── Computed ───────────────────────────────────────────
const filteredUsers = computed(() => {
    let result = users.value
    if (searchQuery.value) {
        const q = searchQuery.value.toLowerCase()
        result = result.filter(u =>
            u.username?.toLowerCase().includes(q) ||
            u.email?.toLowerCase().includes(q)
        )
    }
    Object.entries(filters.value).forEach(([key, val]) => {
        if (val) result = result.filter(u =>
            String(u[key] ?? '').toLowerCase().includes(val.toLowerCase())
        )
    })
    if (sortKey.value) {
        result = [...result].sort((a, b) => {
            const aV = String(a[sortKey.value] ?? '').toLowerCase()
            const bV = String(b[sortKey.value] ?? '').toLowerCase()
            return sortOrder.value === 'asc' ? aV.localeCompare(bV) : bV.localeCompare(aV)
        })
    }
    return result
})

const totalPages     = computed(() => Math.max(1, Math.ceil(filteredUsers.value.length / itemsPerPage.value)))
const paginatedUsers = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value
    return filteredUsers.value.slice(start, start + itemsPerPage.value)
})
const hasActiveFilters = computed(() =>
    !!searchQuery.value || Object.values(filters.value).some(v => !!v)
)

// ── Helpers ────────────────────────────────────────────
const COLORS      = ['#7C3AED','#059669','#DC2626','#D97706','#2563EB','#DB2777','#0891B2','#65A30D']
const getColor    = (id) => COLORS[id % COLORS.length]
const getInitials = (name) => name?.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) ?? '??'
const formatDate  = (date) => date
    ? new Date(date).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
    : '—'

const showToast = (message, type = 'success') => {
    toast.value = { show: true, message, type }
    setTimeout(() => toast.value.show = false, 3500)
}

const handleSort = (key) => {
    sortOrder.value   = sortKey.value === key && sortOrder.value === 'asc' ? 'desc' : 'asc'
    sortKey.value     = key
    currentPage.value = 1
}

const clearFilters = () => {
    filters.value     = {}
    searchQuery.value = ''
    currentPage.value = 1
}

// ── Shared auth headers ────────────────────────────────
// GET /all is public so no auth header needed
// POST/PUT/DELETE all require Bearer token
const authHeaders = () => ({
    'Content-Type':  'application/json',
    'Authorization': authStore.getAuthHeader(),   // ← "Bearer <token>"
})

// ── API ────────────────────────────────────────────────
const fetchUsers = async () => {
    loading.value = true
    try {
        // GET is public — no auth header needed
        const res    = await fetch(`${BASE_URL}/all`)
        const result = await res.json()
        if (result.success) users.value = result.data
        else showToast(result.message || 'Failed to load users', 'error')
    } catch {
        showToast('Failed to connect to server', 'error')
    } finally {
        loading.value = false
    }
}

const createUser = async () => {
    submitting.value = true
    formError.value  = ''
    try {
        const res = await fetch(`${BASE_URL}/create`, {
            method:  'POST',
            headers: authHeaders(),               // ← JWT token sent here
            body:    JSON.stringify(form.value),
        })

        // Handle 401/403 specifically
        if (res.status === 401 || res.status === 403) {
            formError.value = 'Session expired. Please log in again.'
            return
        }

        const result = await res.json()
        if (result.success) {
            users.value.unshift(result.data)
            closeModal()
            showToast('User created successfully!')
        } else {
            formError.value = result.message || 'Failed to create user'
        }
    } catch {
        formError.value = 'Network error. Please try again.'
    } finally {
        submitting.value = false
    }
}

const updateUser = async () => {
    submitting.value = true
    formError.value  = ''
    try {
        const res = await fetch(`${BASE_URL}/update/${selectedUser.value.id}`, {
            method:  'PUT',
            headers: authHeaders(),               // ← JWT token sent here
            body:    JSON.stringify(form.value),
        })

        if (res.status === 401 || res.status === 403) {
            formError.value = 'Session expired. Please log in again.'
            return
        }

        const result = await res.json()
        if (result.success) {
            const idx = users.value.findIndex(u => u.id === selectedUser.value.id)
            if (idx !== -1) users.value[idx] = result.data
            closeModal()
            showToast('User updated successfully!')
        } else {
            formError.value = result.message || 'Failed to update user'
        }
    } catch {
        formError.value = 'Network error. Please try again.'
    } finally {
        submitting.value = false
    }
}

const confirmDelete = async () => {
    submitting.value = true
    try {
        const res = await fetch(`${BASE_URL}/delete/${selectedUser.value.id}`, {
            method:  'DELETE',
            headers: authHeaders(),               // ← JWT token sent here
        })

        if (res.status === 401 || res.status === 403) {
            showToast('Session expired. Please log in again.', 'error')
            return
        }

        const result = await res.json()
        if (result.success) {
            users.value           = users.value.filter(u => u.id !== selectedUser.value.id)
            showDeleteModal.value = false
            selectedUser.value    = null
            showToast('User deleted successfully!')
        } else {
            showToast(result.message || 'Failed to delete user', 'error')
        }
    } catch {
        showToast('Failed to delete user', 'error')
    } finally {
        submitting.value = false
    }
}

// ── Modal helpers ──────────────────────────────────────
const openCreateModal = () => {
    isEditing.value    = false
    selectedUser.value = null
    form.value         = { username: '', email: '', password: '' }
    formError.value    = ''
    showPassword.value = false
    showModal.value    = true
}

const openEditModal = (user) => {
    isEditing.value    = true
    selectedUser.value = user
    form.value         = { username: user.username, email: user.email, password: '' }
    formError.value    = ''
    showPassword.value = false
    showModal.value    = true
}

const openDeleteModal = (user) => {
    selectedUser.value    = user
    showDeleteModal.value = true
}

const closeModal = () => {
    showModal.value    = false
    selectedUser.value = null
    formError.value    = ''
}

const handleSubmit = () => isEditing.value ? updateUser() : createUser()

onMounted(fetchUsers)
</script>

<template>
    <DashboardSidebar />
    <div class="ml-0 md:ml-64 transition-all">
        <DashboardHeader />
        <main class="pt-3 px-3 sm:px-6 pb-6">

            <!-- Toast notification -->
            <Transition
                enter-active-class="transition duration-300 ease-out"
                enter-from-class="opacity-0 translate-y-[-8px]"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition duration-200"
                leave-from-class="opacity-100"
                leave-to-class="opacity-0">
                <div v-if="toast.show"
                    :class="['fixed top-5 right-5 z-100 flex items-center gap-3 px-4 py-3 rounded-xl shadow-xl text-sm font-medium text-white min-w-55',
                        toast.type === 'success' ? 'bg-green-500' : 'bg-red-500']">
                    <svg v-if="toast.type === 'success'" class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"/>
                    </svg>
                    <AlertTriangle v-else class="w-4 h-4 shrink-0" />
                    <span class="flex-1">{{ toast.message }}</span>
                    <button @click="toast.show = false" class="opacity-70 hover:opacity-100 ml-1">
                        <X class="w-3.5 h-3.5" />
                    </button>
                </div>
            </Transition>

            <!-- Toolbar -->
            <DataFilters
                title="Users"
                :totalCount="filteredUsers.length"
                v-model:searchQuery="searchQuery"
                v-model:viewMode="viewMode"
                v-model:showFilterRow="showFilterRow"
                :hasActiveFilters="hasActiveFilters"
                @clearFilters="clearFilters"
            >
                <template #actions>
                    <button @click="openCreateModal"
                        class="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-3 sm:px-4 py-2 rounded-lg text-sm font-medium transition shadow-sm whitespace-nowrap">
                        <UserPlus class="w-4 h-4" />
                        <span class="hidden sm:inline">New User</span>
                    </button>
                </template>
            </DataFilters>

            <!-- Loading skeleton -->
            <div v-if="loading" class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
                <div v-for="i in 10" :key="i"
                    class="bg-white rounded-2xl border border-gray-100 p-5 animate-pulse flex flex-col items-center">
                    <div class="w-16 h-16 bg-gray-200 rounded-xl mb-3"></div>
                    <div class="h-3 bg-gray-200 rounded w-3/4 mb-2"></div>
                    <div class="h-2 bg-gray-100 rounded w-1/2 mb-4"></div>
                    <div class="w-full border-t border-gray-100 pt-3 space-y-2">
                        <div class="h-2 bg-gray-100 rounded w-full"></div>
                        <div class="h-2 bg-gray-100 rounded w-4/5"></div>
                    </div>
                </div>
            </div>

            <template v-else>
                <!-- Grid View -->
                <DataGrid v-if="viewMode === 'grid'" :items="paginatedUsers">
                    <template #card="{ item }">
                        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 sm:p-5 flex flex-col items-center text-center hover:shadow-md transition-all hover:-translate-y-0.5 relative group">

                            <!-- Hover actions -->
                            <div class="absolute top-3 right-3 flex gap-1 opacity-0 group-hover:opacity-100 transition-all">
                                <button @click="openEditModal(item)"
                                    title="Edit"
                                    class="p-1.5 rounded-lg bg-blue-50 text-blue-500 hover:bg-blue-100 transition">
                                    <Pencil class="w-3 h-3" />
                                </button>
                                <button @click="openDeleteModal(item)"
                                    title="Delete"
                                    class="p-1.5 rounded-lg bg-red-50 text-red-400 hover:bg-red-100 transition">
                                    <Trash2 class="w-3 h-3" />
                                </button>
                            </div>

                            <!-- Avatar -->
                            <div class="relative mb-3">
                                <div class="w-14 h-14 sm:w-16 sm:h-16 rounded-xl flex items-center justify-center text-white text-lg font-bold shadow-md"
                                    :style="{ backgroundColor: getColor(item.id) }">
                                    {{ getInitials(item.username) }}
                                </div>
                                <div class="absolute -bottom-1 -right-1 w-5 h-5 sm:w-6 sm:h-6 rounded-md flex items-center justify-center text-white text-xs font-bold shadow"
                                    :style="{ backgroundColor: getColor(item.id + 3) }">
                                    {{ getInitials(item.username).slice(0, 1) }}
                                </div>
                            </div>

                            <h3 class="font-semibold text-gray-800 text-sm leading-tight">{{ item.username }}</h3>
                            <p class="text-xs text-gray-400 mt-0.5 mb-3">Platform Member</p>

                            <div class="w-full border-t border-gray-100 mb-3"></div>

                            <div class="w-full space-y-1.5">
                                <div class="flex items-center gap-2 text-xs text-gray-500">
                                    <div class="w-5 h-5 sm:w-6 sm:h-6 rounded-full bg-blue-100 flex items-center justify-center shrink-0">
                                        <Mail class="w-2.5 h-2.5 text-blue-600" />
                                    </div>
                                    <span class="truncate">{{ item.email }}</span>
                                </div>
                                <div class="flex items-center gap-2 text-xs text-gray-400">
                                    <div class="w-5 h-5 sm:w-6 sm:h-6 rounded-full bg-gray-100 flex items-center justify-center shrink-0">
                                        <svg class="w-2.5 h-2.5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                                        </svg>
                                    </div>
                                    <span>{{ formatDate(item.createdAt) }}</span>
                                </div>
                            </div>
                        </div>
                    </template>
                </DataGrid>

                <!-- Table View -->
                <DataTable
                    v-else
                    :rows="paginatedUsers"
                    :columns="columns"
                    :sortKey="sortKey"
                    :sortOrder="sortOrder"
                    :showFilterRow="showFilterRow"
                    :filters="filters"
                    @sort="handleSort"
                    @update:filters="(v) => { filters = v; currentPage = 1 }"
                    @clearFilters="clearFilters"
                >
                    <template #cell-id="{ value }">
                        <span class="text-gray-400 text-xs font-mono">#{{ value }}</span>
                    </template>

                    <template #cell-username="{ row }">
                        <div class="flex items-center gap-3">
                            <div class="w-8 h-8 rounded-lg flex items-center justify-center text-white text-xs font-bold shrink-0"
                                :style="{ backgroundColor: getColor(row.id) }">
                                {{ getInitials(row.username) }}
                            </div>
                            <div>
                                <p class="font-medium text-gray-800 text-sm">{{ row.username }}</p>
                                <p class="text-xs text-gray-400 sm:hidden truncate max-w-35">{{ row.email }}</p>
                            </div>
                        </div>
                    </template>

                    <template #cell-createdAt="{ value }">
                        <span class="text-gray-400 text-xs">{{ formatDate(value) }}</span>
                    </template>

                    <template #cell-actions="{ row }">
                        <div class="flex items-center justify-center gap-2">
                            <button @click="openEditModal(row)"
                                class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-blue-50 text-blue-600 hover:bg-blue-100 transition">
                                <Pencil class="w-3 h-3" />
                                Edit
                            </button>
                            <button @click="openDeleteModal(row)"
                                class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium bg-red-50 text-red-500 hover:bg-red-100 transition">
                                <Trash2 class="w-3 h-3" />
                                Delete
                            </button>
                        </div>
                    </template>
                </DataTable>

                <!-- Empty state -->
                <div v-if="filteredUsers.length === 0"
                    class="text-center py-16 text-gray-400 bg-white rounded-2xl border border-gray-100">
                    <div class="w-16 h-16 bg-gray-100 rounded-2xl flex items-center justify-center mx-auto mb-4">
                        <svg class="w-8 h-8 opacity-40" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
                        </svg>
                    </div>
                    <p class="font-medium text-gray-500">No users found</p>
                    <p v-if="hasActiveFilters" class="text-sm text-gray-400 mt-1">Try adjusting your search or filters</p>
                    <div class="flex items-center justify-center gap-3 mt-4">
                        <button v-if="hasActiveFilters" @click="clearFilters"
                            class="text-sm text-blue-500 hover:underline">Clear filters</button>
                        <button @click="openCreateModal"
                            class="text-sm bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition">
                            Create first user
                        </button>
                    </div>
                </div>
            </template>

            <!-- Pagination -->
            <DataPagination
                v-if="!loading && filteredUsers.length > 0"
                :currentPage="currentPage"
                :totalPages="totalPages"
                :totalCount="filteredUsers.length"
                :itemsPerPage="itemsPerPage"
                @update:currentPage="currentPage = $event"
                @update:itemsPerPage="itemsPerPage = $event; currentPage = 1"
            />
        </main>
    </div>

    <!-- ── Create / Edit Modal ─────────────────────────── -->
    <Teleport to="body">
        <Transition
            enter-active-class="transition duration-200 ease-out"
            enter-from-class="opacity-0"
            enter-to-class="opacity-100"
            leave-active-class="transition duration-150"
            leave-from-class="opacity-100"
            leave-to-class="opacity-0">
            <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
                <div class="absolute inset-0 bg-black/40 backdrop-blur-sm" @click="closeModal"></div>

                <Transition
                    enter-active-class="transition duration-200 ease-out"
                    enter-from-class="opacity-0 scale-95"
                    enter-to-class="opacity-100 scale-100">
                    <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-md z-10 overflow-hidden">

                        <!-- Modal header -->
                        <div class="flex items-center justify-between px-6 py-5 border-b border-gray-100">
                            <div class="flex items-center gap-3">
                                <div :class="['w-9 h-9 rounded-xl flex items-center justify-center',
                                    isEditing ? 'bg-blue-100' : 'bg-green-100']">
                                    <Pencil v-if="isEditing" class="w-4 h-4 text-blue-600" />
                                    <UserPlus v-else class="w-4 h-4 text-green-600" />
                                </div>
                                <div>
                                    <h2 class="text-base font-bold text-gray-800">
                                        {{ isEditing ? 'Edit User' : 'Create New User' }}
                                    </h2>
                                    <p class="text-xs text-gray-400">
                                        {{ isEditing ? `Editing: ${selectedUser?.username}` : 'Fill in the details below' }}
                                    </p>
                                </div>
                            </div>
                            <button @click="closeModal"
                                class="p-2 rounded-lg hover:bg-gray-100 text-gray-400 hover:text-gray-600 transition">
                                <X class="w-4 h-4" />
                            </button>
                        </div>

                        <!-- Modal body -->
                        <div class="px-6 py-5 space-y-4">

                            <!-- Error banner -->
                            <div v-if="formError"
                                class="flex items-center gap-2 bg-red-50 border border-red-100 text-red-600 text-sm px-4 py-3 rounded-xl">
                                <AlertTriangle class="w-4 h-4 shrink-0" />
                                <span>{{ formError }}</span>
                            </div>

                            <!-- Username -->
                            <div>
                                <label class="block text-xs font-semibold text-gray-600 mb-1.5">
                                    Username <span class="text-red-400">*</span>
                                </label>
                                <input v-model="form.username"
                                    type="text"
                                    placeholder="e.g. john_doe"
                                    class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-transparent transition placeholder:text-gray-300" />
                            </div>

                            <!-- Email -->
                            <div>
                                <label class="block text-xs font-semibold text-gray-600 mb-1.5">
                                    Email <span class="text-red-400">*</span>
                                </label>
                                <input v-model="form.email"
                                    type="email"
                                    placeholder="e.g. john@example.com"
                                    class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-transparent transition placeholder:text-gray-300" />
                            </div>

                            <!-- Password -->
                            <div>
                                <label class="block text-xs font-semibold text-gray-600 mb-1.5">
                                    Password
                                    <span v-if="!isEditing" class="text-red-400">*</span>
                                    <span v-else class="text-gray-400 font-normal ml-1">(leave blank to keep current)</span>
                                </label>
                                <div class="relative">
                                    <input v-model="form.password"
                                        :type="showPassword ? 'text' : 'password'"
                                        placeholder="••••••••"
                                        class="w-full border border-gray-200 rounded-xl px-4 py-2.5 pr-11 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-transparent transition placeholder:text-gray-300" />
                                    <button type="button"
                                        @click="showPassword = !showPassword"
                                        class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition p-1">
                                        <EyeOff v-if="showPassword" class="w-4 h-4" />
                                        <Eye v-else class="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Modal footer -->
                        <div class="flex items-center justify-end gap-3 px-6 py-4 bg-gray-50 border-t border-gray-100">
                            <button @click="closeModal"
                                class="px-4 py-2 text-sm font-medium text-gray-500 hover:text-gray-700 rounded-xl hover:bg-gray-100 transition">
                                Cancel
                            </button>
                            <button @click="handleSubmit"
                                :disabled="submitting"
                                :class="['flex items-center gap-2 px-5 py-2 text-white text-sm font-medium rounded-xl transition shadow-sm disabled:opacity-60 disabled:cursor-not-allowed',
                                    isEditing ? 'bg-blue-600 hover:bg-blue-700' : 'bg-green-600 hover:bg-green-700']">
                                <svg v-if="submitting" class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                                </svg>
                                <Pencil v-else-if="isEditing" class="w-3.5 h-3.5" />
                                <UserPlus v-else class="w-3.5 h-3.5" />
                                {{ submitting ? 'Saving...' : (isEditing ? 'Save Changes' : 'Create User') }}
                            </button>
                        </div>
                    </div>
                </Transition>
            </div>
        </Transition>
    </Teleport>

    <!-- ── Delete Confirm Modal ────────────────────────── -->
    <Teleport to="body">
        <Transition
            enter-active-class="transition duration-200"
            enter-from-class="opacity-0"
            enter-to-class="opacity-100"
            leave-active-class="transition duration-150"
            leave-from-class="opacity-100"
            leave-to-class="opacity-0">
            <div v-if="showDeleteModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
                <div class="absolute inset-0 bg-black/40 backdrop-blur-sm"
                    @click="showDeleteModal = false; selectedUser = null"></div>

                <Transition
                    enter-active-class="transition duration-200 ease-out"
                    enter-from-class="opacity-0 scale-95"
                    enter-to-class="opacity-100 scale-100">
                    <div class="relative bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6 z-10">
                        <div class="flex flex-col items-center text-center">

                            <div class="w-16 h-16 bg-red-50 rounded-2xl flex items-center justify-center mb-4">
                                <Trash2 class="w-7 h-7 text-red-500" />
                            </div>

                            <h2 class="text-lg font-bold text-gray-800 mb-1">Delete User</h2>
                            <p class="text-sm text-gray-500 mb-1">You are about to permanently delete</p>
                            <p class="text-sm font-semibold text-gray-800 bg-gray-100 px-3 py-1 rounded-lg mb-2">
                                {{ selectedUser?.username }}
                            </p>
                            <p class="text-xs text-gray-400 mb-6">This action cannot be undone.</p>

                            <div class="flex items-center gap-3 w-full">
                                <button @click="showDeleteModal = false; selectedUser = null"
                                    class="flex-1 px-4 py-2.5 text-sm font-medium text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-xl transition">
                                    Cancel
                                </button>
                                <button @click="confirmDelete"
                                    :disabled="submitting"
                                    class="flex-1 flex items-center justify-center gap-2 px-4 py-2.5 text-sm font-medium text-white bg-red-500 hover:bg-red-600 disabled:opacity-60 disabled:cursor-not-allowed rounded-xl transition">
                                    <svg v-if="submitting" class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
                                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                                    </svg>
                                    <Trash2 v-else class="w-4 h-4" />
                                    {{ submitting ? 'Deleting...' : 'Yes, Delete' }}
                                </button>
                            </div>
                        </div>
                    </div>
                </Transition>
            </div>
        </Transition>
    </Teleport>
</template>