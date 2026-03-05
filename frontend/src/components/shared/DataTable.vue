<script setup>
import { ChevronUp, ChevronDown, ChevronsUpDown } from 'lucide-vue-next'

/**
 * columns: Array of column definitions
 * [
 *   { key: 'id',       label: 'ID',       sortable: true,  filterable: true,  class: 'w-20' },
 *   { key: 'username', label: 'User',      sortable: true,  filterable: true },
 *   { key: 'email',    label: 'Email',     sortable: true,  filterable: true,  hideOnMobile: true },
 *   { key: 'actions',  label: 'Actions',   sortable: false, filterable: false, class: 'text-center' },
 * ]
 */
defineProps({
    rows: { type: Array, required: true },
    columns: { type: Array, required: true },
    sortKey: { type: String, default: '' },
    sortOrder: { type: String, default: 'asc' },
    showFilterRow: { type: Boolean, default: false },
    filters: { type: Object, default: () => ({}) },
})

const emit = defineEmits(['sort', 'update:filters', 'clearFilters'])

const toggleSort = (col) => {
    if (col.sortable) emit('sort', col.key)
}

const updateFilter = (key, value) => {
    emit('update:filters', { ...filters, [key]: value })
}
</script>

<template>
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
        <div class="overflow-x-auto">
            <table class="w-full text-sm min-w-125">
                <thead class="bg-gray-50 border-b border-gray-100">
                    <!-- Header Row -->
                    <tr>
                        <th v-for="col in columns" :key="col.key"
                            :class="['px-4 sm:px-6 py-3', col.class, col.hideOnMobile ? 'hidden sm:table-cell' : '']">
                            <button v-if="col.sortable" @click="toggleSort(col)"
                                class="flex items-center gap-1 text-xs font-semibold text-gray-500 uppercase tracking-wider hover:text-gray-700 transition">
                                {{ col.label }}
                                <ChevronsUpDown v-if="sortKey !== col.key" class="w-3 h-3 opacity-40" />
                                <ChevronUp v-else-if="sortOrder === 'asc'" class="w-3 h-3 text-blue-500" />
                                <ChevronDown v-else class="w-3 h-3 text-blue-500" />
                            </button>
                            <span v-else class="text-xs font-semibold text-gray-500 uppercase tracking-wider">
                                {{ col.label }}
                            </span>
                        </th>
                    </tr>

                    <!-- Filter Row -->
                    <tr v-if="showFilterRow" class="bg-blue-50/50 border-t border-blue-100">
                        <td v-for="col in columns" :key="col.key"
                            :class="['px-4 sm:px-6 py-2', col.hideOnMobile ? 'hidden sm:table-cell' : '']">
                            <input v-if="col.filterable" :value="filters[col.key] || ''"
                                @input="updateFilter(col.key, $event.target.value)" :placeholder="`Filter ${col.label}`"
                                class="w-full text-xs border border-gray-200 rounded-lg px-2 py-1.5 focus:outline-none focus:ring-1 focus:ring-blue-400 bg-white" />
                            <button v-else-if="col.key === 'actions'" @click="$emit('clearFilters')"
                                class="text-xs text-red-400 hover:text-red-600 transition mx-auto block">
                                Clear
                            </button>
                        </td>
                    </tr>
                </thead>

                <tbody class="divide-y divide-gray-50">
                    <tr v-for="(row, rowIndex) in rows" :key="row.id ?? rowIndex"
                        class="hover:bg-gray-50/80 transition">
                        <td v-for="col in columns" :key="col.key"
                            :class="['px-4 sm:px-6 py-3', col.hideOnMobile ? 'hidden sm:table-cell' : '', col.tdClass]">
                            <!--
                                Each column renders via a named slot: #cell-[key]
                                Falls back to plain text if no slot provided.
                                Usage in parent:
                                  <template #cell-username="{ row }"> ... </template>
-->
                            <slot :name="`cell-${col.key}`" :row="row" :value="row[col.key]">
                                {{ row[col.key] }}
                            </slot>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>