<script setup>
defineProps({
    items: { type: Array, required: true },
    // e.g. 'grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'
    gridClass: {
        type: String,
        default: 'grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'
    },
})
</script>

<template>
    <div :class="['grid gap-3 sm:gap-4', gridClass]">
        <slot v-for="item in items" :key="item.id ?? item" name="card" :item="item">
            <!-- fallback if no slot provided -->
            <div class="bg-white rounded-2xl border border-gray-100 p-4 text-sm text-gray-600">
                {{ JSON.stringify(item) }}
            </div>
        </slot>
    </div>
</template>