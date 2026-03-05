import { ref } from 'vue'

const BASE_URL = 'http://localhost:8080/api'

export function useTranslate() {
  const translating = ref({})  // tracks which field is being translated

  async function translate(text, from, to) {
    if (!text?.trim()) return null
    try {
      const res  = await fetch(`${BASE_URL}/translate`, {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify({ text, from, to }),
      })
      const data = await res.json()
      return data.success ? data.translatedText : null
    } catch {
      return null
    }
  }

  // Translate a specific form field
  // fieldKey = unique key to track loading state (e.g. 'title', 'description')
  async function translateField(text, from, to, fieldKey) {
    if (!text?.trim()) return null
    translating.value[fieldKey] = true
    try {
      return await translate(text, from, to)
    } finally {
      translating.value[fieldKey] = false
    }
  }

  const isTranslating = (fieldKey) => !!translating.value[fieldKey]

  return { translate, translateField, isTranslating, translating }
}