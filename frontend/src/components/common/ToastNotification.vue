<script setup>
import { ref } from 'vue'
import { X, AlertTriangle } from 'lucide-vue-next'

// Expose `show` method so parent can call: toastRef.value.show('message', 'success')
const toast = ref({ show: false, message: '', type: 'success' })

const show = (message, type = 'success', duration = 3500) => {
    toast.value = { show: true, message, type }
    setTimeout(() => toast.value.show = false, duration)
}

const hide = () => {
    toast.value.show = false
}

defineExpose({ show, hide })
</script>

<template>
    <Teleport to="body">
        <Transition enter-active-class="transition duration-300 ease-out"
            enter-from-class="opacity-0 translate-y-[-8px]" enter-to-class="opacity-100 translate-y-0"
            leave-active-class="transition duration-200" leave-from-class="opacity-100" leave-to-class="opacity-0">
            <div v-if="toast.show"
                :class="['fixed top-5 right-5 z-9999 flex items-center gap-3 px-4 py-3 rounded-xl shadow-xl text-sm font-medium text-white min-w-55', toast.type === 'success' ? 'bg-green-500' : 'bg-red-500']">
                <svg v-if="toast.type === 'success'" class="w-4 h-4 shrink-0" fill="none" stroke="currentColor"
                    viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7" />
                </svg>
                <AlertTriangle v-else class="w-4 h-4 shrink-0" />

                <span class="flex-1">{{ toast.message }}</span>

                <button @click="hide" class="opacity-70 hover:opacity-100 ml-1">
                    <X class="w-3.5 h-3.5" />
                </button>
            </div>
        </Transition>
    </Teleport>
</template>