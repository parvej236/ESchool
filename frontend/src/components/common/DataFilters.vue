<script setup>
import { Search, LayoutGrid, TableOfContents } from 'lucide-vue-next'

defineProps({
    title: { type: String, default: 'Data' },
    totalCount: { type: Number, default: 0 },
    searchQuery: { type: String, default: '' },
    searchPlaceholder: { type: String, default: 'Search here' },
    viewMode: { type: String, default: 'grid' },
    showFilterRow: { type: Boolean, default: false },
    hasActiveFilters: { type: Boolean, default: false },
    // hide grid toggle if you only want table view for certain pages
    showViewToggle: { type: Boolean, default: true },
})

const emit = defineEmits([
    'update:searchQuery',
    'update:viewMode',
    'update:showFilterRow',
    'clearFilters',
])
</script>

<template>
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6">
        <div>
            <h1 class="text-xl sm:text-2xl font-bold text-gray-800">{{ title }}</h1>
            <p class="text-sm text-gray-400">{{ totalCount }} records found</p>
        </div>

        <div class="flex flex-wrap items-center gap-2">
            <!-- Search -->
            <div class="relative flex-1 sm:flex-none">
                <Search class="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
                <input :value="searchQuery" @input="$emit('update:searchQuery', $event.target.value)" type="text"
                    :placeholder="searchPlaceholder"
                    class="pl-9 pr-4 py-2 rounded-full border border-gray-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 w-full sm:w-56 shadow-sm" />
            </div>

            <!-- Filter Toggle (list only) -->
            <button v-if="viewMode === 'list'" @click="$emit('update:showFilterRow', !showFilterRow)" :class="['p-2 rounded-lg transition text-sm flex items-center gap-1 border',
                showFilterRow
                    ? 'bg-blue-50 border-blue-200 text-blue-600'
                    : 'bg-white border-gray-200 text-gray-500 hover:bg-gray-50']">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2a1 1 0 01-.293.707L13 13.414V19a1 1 0 01-.553.894l-4 2A1 1 0 017 21v-7.586L3.293 6.707A1 1 0 013 6V4z" />
                </svg>
                <span class="hidden sm:inline text-xs font-medium">Filter</span>
                <span v-if="hasActiveFilters" class="w-2 h-2 bg-blue-500 rounded-full"></span>
            </button>

            <!-- Clear -->
            <button v-if="hasActiveFilters" @click="$emit('clearFilters')"
                class="text-xs text-red-400 hover:text-red-600 transition px-2 py-1 rounded-lg hover:bg-red-50">
                Clear
            </button>

            <!-- View Toggle -->
            <div v-if="showViewToggle"
                class="flex border border-gray-200 rounded-lg overflow-hidden bg-white shadow-sm">
                <button @click="$emit('update:viewMode', 'list')"
                    :class="['p-2 transition', viewMode === 'list' ? 'bg-blue-50 text-blue-600' : 'text-gray-400 hover:bg-gray-50']">
                    <TableOfContents class="w-4 h-4 scale-x-[-1]" />
                </button>
                <button @click="$emit('update:viewMode', 'grid')"
                    :class="['p-2 transition', viewMode === 'grid' ? 'bg-blue-50 text-blue-600' : 'text-gray-400 hover:bg-gray-50']">
                    <LayoutGrid class="w-4 h-4" />
                </button>
            </div>

            <!-- Action slot — each page puts its own button here (New User / New Class / etc.) -->
            <slot name="actions" />
        </div>
    </div>
</template>