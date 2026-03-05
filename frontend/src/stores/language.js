import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useLanguageStore = defineStore('language', () => {
  const lang = ref(localStorage.getItem('app_lang') || 'en')

  const isEnglish = computed(() => lang.value === 'en')
  const isBangla  = computed(() => lang.value === 'bn')

  function setLanguage(newLang) {
    lang.value = newLang
    localStorage.setItem('app_lang', newLang)
  }

  function toggle() {
    setLanguage(lang.value === 'en' ? 'bn' : 'en')
  }

  // Helper: pick the right field based on current language
  // Usage: t(slide, 'title') → returns titleBn or title
  function t(obj, field) {
    if (!obj) return ''
    if (lang.value === 'bn') {
      const bnField = field + 'Bn'
      return obj[bnField] || obj[field] || ''  // fallback to English if Bangla missing
    }
    return obj[field] || ''
  }

  return { lang, isEnglish, isBangla, setLanguage, toggle, t }
})