<script setup>
import { onMounted } from 'vue'
import { PlusCircle } from 'lucide-vue-next'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import DataFilters from '@/components/shared/DataFilters.vue'
import DataTable from '@/components/shared/DataTable.vue'
import DataPagination from '@/components/shared/DataPagination.vue'
import { useDataList } from '@/composables/useDataList'

const columns = [
    { key: 'id', label: 'ID', sortable: true, filterable: true, class: 'w-20' },
    { key: 'name', label: 'Class Name', sortable: true, filterable: true },
    { key: 'teacher', label: 'Teacher', sortable: true, filterable: true, hideOnMobile: true },
    { key: 'students', label: 'Students', sortable: true, filterable: false, class: 'text-center' },
    { key: 'actions', label: 'Actions', sortable: false, filterable: false, class: 'text-center' },
]

const {
    items, filteredItems, paginatedItems,
    searchQuery, viewMode, showFilterRow, filters,
    sortKey, sortOrder, currentPage, itemsPerPage, totalPages,
    hasActiveFilters, handleSort, clearFilters,
} = useDataList('http://localhost:8080/api/classes', ['name', 'teacher'])

onMounted(() => items.fetch())
</script>

<template>
    <DashboardSidebar />
    <div class="ml-0 md:ml-64 transition-all">
        <DashboardHeader />
        <main class="pt-3 px-3 sm:px-6 pb-6">

            <DataFilters title="Classes" :totalCount="filteredItems.length" v-model:searchQuery="searchQuery"
                v-model:viewMode="viewMode" v-model:showFilterRow="showFilterRow" :hasActiveFilters="hasActiveFilters"
                :showViewToggle="false" @clearFilters="clearFilters">
                <template #actions>
                    <button
                        class="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition shadow-sm">
                        <PlusCircle class="w-4 h-4" />
                        <span class="hidden sm:inline">New Class</span>
                    </button>
                </template>
            </DataFilters>

            <DataTable :rows="paginatedItems" :columns="columns" :sortKey="sortKey" :sortOrder="sortOrder"
                :showFilterRow="showFilterRow" :filters="filters" @sort="handleSort"
                @update:filters="(v) => { filters = v; currentPage = 1 }" @clearFilters="clearFilters">
                <template #cell-id="{ value }">
                    <span class="text-gray-400 text-xs">#{{ value }}</span>
                </template>
                <template #cell-students="{ value }">
                    <span
                        class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-700">
                        {{ value }} students
                    </span>
                </template>
                <template #cell-actions>
                    <div class="flex items-center justify-center gap-2">
                        <button class="text-xs text-blue-500 hover:underline">Edit</button>
                        <button class="text-xs text-red-400 hover:underline">Delete</button>
                    </div>
                </template>
            </DataTable>

            <div v-if="filteredItems.length === 0"
                class="text-center py-16 text-gray-400 bg-white rounded-2xl border border-gray-100">
                <p class="font-medium">No classes found</p>
                <button @click="clearFilters" class="mt-2 text-sm text-blue-500 hover:underline">Clear filters</button>
            </div>

            <DataPagination v-if="filteredItems.length > 0" :currentPage="currentPage" :totalPages="totalPages"
                :totalCount="filteredItems.length" :itemsPerPage="itemsPerPage"
                @update:currentPage="currentPage = $event"
                @update:itemsPerPage="itemsPerPage = $event; currentPage = 1" />
        </main>
    </div>
</template>