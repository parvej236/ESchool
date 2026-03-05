<script setup>
import { computed } from 'vue'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'

const props = defineProps({
    currentPage: { type: Number, required: true },
    totalPages: { type: Number, required: true },
    totalCount: { type: Number, required: true },
    itemsPerPage: { type: Number, required: true },
    itemsPerPageOptions: { type: Array, default: () => [5, 10, 20, 50, 100] },
})

const emit = defineEmits(['update:currentPage', 'update:itemsPerPage'])

const visiblePages = computed(() => {
    const pages = []
    const total = props.totalPages
    const current = props.currentPage
    if (total <= 7) {
        for (let i = 1; i <= total; i++) pages.push(i)
    } else {
        pages.push(1)
        if (current > 3) pages.push('...')
        for (let i = Math.max(2, current - 1); i <= Math.min(total - 1, current + 1); i++) pages.push(i)
        if (current < total - 2) pages.push('...')
        pages.push(total)
    }
    return pages
})
</script>

<template>
    <div class="flex flex-col sm:flex-row items-center justify-between gap-3 mt-5">
        <div class="flex items-center gap-2 text-sm text-gray-500">
            <span class="hidden sm:inline">Show</span>
            <select :value="itemsPerPage" @change="$emit('update:itemsPerPage', Number($event.target.value))"
                class="border border-gray-200 rounded-lg px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400 bg-white">
                <option v-for="opt in itemsPerPageOptions" :key="opt" :value="opt">{{ opt }}</option>
            </select>
            <span>per page &nbsp;·&nbsp; {{ totalCount }} total</span>
        </div>

        <div class="flex items-center gap-1">
            <button @click="$emit('update:currentPage', currentPage - 1)" :disabled="currentPage === 1"
                class="p-1.5 rounded-lg border border-gray-200 text-gray-400 hover:bg-gray-50 disabled:opacity-30 disabled:cursor-not-allowed transition">
                <ChevronLeft class="w-4 h-4" />
            </button>
            <template v-for="page in visiblePages" :key="page">
                <span v-if="page === '...'" class="px-2 text-gray-400 text-sm">…</span>
                <button v-else @click="$emit('update:currentPage', page)" :class="['min-w-8 h-8 rounded-lg text-sm font-medium transition border',
                    currentPage === page
                        ? 'bg-blue-600 text-white border-blue-600 shadow-sm'
                        : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50']">
                    {{ page }}
                </button>
            </template>
            <button @click="$emit('update:currentPage', currentPage + 1)" :disabled="currentPage === totalPages"
                class="p-1.5 rounded-lg border border-gray-200 text-gray-400 hover:bg-gray-50 disabled:opacity-30 disabled:cursor-not-allowed transition">
                <ChevronRight class="w-4 h-4" />
            </button>
        </div>

        <p class="text-sm text-gray-400 hidden sm:block">Page {{ currentPage }} of {{ totalPages }}</p>
    </div>
</template>