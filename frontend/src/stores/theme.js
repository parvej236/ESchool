import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(false)

  function applyTheme(dark) {
    document.documentElement.classList.toggle('dark', dark)
    isDark.value = dark
  }

  function toggleTheme() {
    const next = !isDark.value
    localStorage.setItem('theme', next ? 'dark' : 'light')
    applyTheme(next)
  }

  function initTheme() {
    const stored     = localStorage.getItem('theme')
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    applyTheme(stored ? stored === 'dark' : prefersDark)

    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      if (!localStorage.getItem('theme')) applyTheme(e.matches)
    })
  }

  return { isDark, toggleTheme, initTheme }
})