<script setup>
import { ref, computed, onMounted } from 'vue'
import { UserPlus } from 'lucide-vue-next'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import DataFilters from '@/components/shared/DataFilters.vue'
import DataTable from '@/components/shared/DataTable.vue'
import DataGrid from '@/components/shared/DataGrid.vue'
import DataPagination from '@/components/shared/DataPagination.vue'
import { useDataList } from '@/composables/useDataList'

// Column definitions — only this file knows about user fields
const columns = [
    { key: 'id', label: 'ID', sortable: true, filterable: true, class: 'w-20' },
    { key: 'username', label: 'User', sortable: true, filterable: true },
    { key: 'email', label: 'Email', sortable: true, filterable: true, hideOnMobile: true },
    { key: 'actions', label: 'Actions', sortable: false, filterable: false, class: 'text-center' },
]

const searchFields = ['username', 'email']

const {
    items, filteredItems, paginatedItems,
    searchQuery, viewMode, showFilterRow, filters,
    sortKey, sortOrder, currentPage, itemsPerPage, totalPages,
    hasActiveFilters, handleSort, clearFilters,
} = useDataList('http://localhost:8080/api/users/all', searchFields)

const COLORS = ['#7C3AED', '#059669', '#DC2626', '#D97706', '#2563EB', '#DB2777', '#0891B2', '#65A30D']
const getColor = (id) => COLORS[id % COLORS.length]
const getInitials = (name) => name?.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) ?? '??'

onMounted(() => items.fetch())
</script>

<template>
    <DashboardSidebar />
    <div class="ml-0 md:ml-64 transition-all">
        <DashboardHeader />
        <main class="pt-3 px-3 sm:px-6 pb-6">

            <DataFilters title="Users" :totalCount="filteredItems.length" v-model:searchQuery="searchQuery"
                v-model:viewMode="viewMode" v-model:showFilterRow="showFilterRow" :hasActiveFilters="hasActiveFilters"
                @clearFilters="clearFilters">
                <template #actions>
                    <button
                        class="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-3 sm:px-4 py-2 rounded-lg text-sm font-medium transition shadow-sm">
                        <UserPlus class="w-4 h-4" />
                        <span class="hidden sm:inline">New User</span>
                    </button>
                </template>
            </DataFilters>

            <!-- Grid -->
            <DataGrid v-if="viewMode === 'grid'" :items="paginatedItems">
                <template #card="{ item }">
                    <div
                        class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 sm:p-5 flex flex-col items-center text-center hover:shadow-md transition-all hover:-translate-y-0.5">
                        <div class="relative mb-3">
                            <div class="w-16 h-16 rounded-xl flex items-center justify-center text-white text-lg font-bold shadow-md"
                                :style="{ backgroundColor: getColor(item.id) }">
                                {{ getInitials(item.username) }}
                            </div>
                            <div class="absolute -bottom-1 -right-1 w-6 h-6 rounded-md flex items-center justify-center text-white text-xs font-bold shadow"
                                :style="{ backgroundColor: getColor(item.id + 3) }">
                                {{ getInitials(item.username).slice(0, 1) }}
                            </div>
                        </div>
                        <h3 class="font-semibold text-gray-800 text-sm">{{ item.username }}</h3>
                        <p class="text-xs text-blue-600 mt-1">Platform Member</p>
                        <div class="w-full border-t border-gray-100 my-3"></div>
                        <p class="text-xs text-gray-500 truncate w-full text-center">{{ item.email }}</p>
                    </div>
                </template>
            </DataGrid>

            <!-- Table -->
            <DataTable v-else :rows="paginatedItems" :columns="columns" :sortKey="sortKey" :sortOrder="sortOrder"
                :showFilterRow="showFilterRow" :filters="filters" @sort="handleSort"
                @update:filters="(v) => { filters = v; currentPage = 1 }" @clearFilters="clearFilters">
                <!-- Custom cell: ID -->
                <template #cell-id="{ value }">
                    <span class="text-gray-400 text-xs">#{{ value }}</span>
                </template>

                <!-- Custom cell: Username with avatar -->
                <template #cell-username="{ row }">
                    <div class="flex items-center gap-3">
                        <div class="w-8 h-8 rounded-lg flex items-center justify-center text-white text-xs font-bold"
                            :style="{ backgroundColor: getColor(row.id) }">
                            {{ getInitials(row.username) }}
                        </div>
                        <div>
                            <p class="font-medium text-gray-800 text-sm">{{ row.username }}</p>
                            <p class="text-xs text-gray-400 sm:hidden truncate max-w-35">{{ row.email }}</p>
                        </div>
                    </div>
                </template>

                <!-- Custom cell: Actions -->
                <template #cell-actions>
                    <span
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-700">
                        Pending
                    </span>
                </template>
            </DataTable>

            <!-- Empty -->
            <div v-if="filteredItems.length === 0"
                class="text-center py-16 text-gray-400 bg-white rounded-2xl border border-gray-100">
                <p class="font-medium">No users found</p>
                <button @click="clearFilters" class="mt-2 text-sm text-blue-500 hover:underline">Clear filters</button>
            </div>

            <DataPagination v-if="filteredItems.length > 0" :currentPage="currentPage" :totalPages="totalPages"
                :totalCount="filteredItems.length" :itemsPerPage="itemsPerPage"
                @update:currentPage="currentPage = $event"
                @update:itemsPerPage="itemsPerPage = $event; currentPage = 1" />
        </main>
    </div>
</template>