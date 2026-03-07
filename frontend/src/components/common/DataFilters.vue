<script setup>
import { Search, LayoutGrid, TableOfContents } from 'lucide-vue-next'

defineProps({
    title: { type: String, default: 'Data' },
    totalCount: { type: Number, default: 0 },
    searchQuery: { type: String, default: '' },
    searchPlaceholder: { type: String, default: 'Search here' },
    viewMode: { type: String, default: 'grid' },
    showViewToggle: { type: Boolean, default: true },
})

const emit = defineEmits([
    'update:searchQuery',
    'update:viewMode',
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
                    class="pl-9 pr-4 py-2 rounded-full border border-gray-200 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 w-full sm:w-70 shadow-sm" />
            </div>

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