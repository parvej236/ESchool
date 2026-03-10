import { computed } from 'vue'
import { useI18n }  from 'vue-i18n'

export function useLang() {
  const { t, locale } = useI18n()

  const isBn = computed(() => locale.value === 'bn')

  // For backend objects with titleEn / titleBn fields
  function tField(obj, field) {
    if (!obj) return ''
    return isBn.value
      ? (obj[`${field}Bn`] || obj[`${field}En`] || '')
      : (obj[`${field}En`] || obj[field]         || '')
  }

  function toggleLang() {
    locale.value = locale.value === 'en' ? 'bn' : 'en'
    localStorage.setItem('lang', locale.value)
  }

  return { t, locale, isBn, tField, toggleLang }
}