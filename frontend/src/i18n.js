import { createI18n } from 'vue-i18n'
import en from './locales/en'
import bn from './locales/bn'

const savedLang = localStorage.getItem('lang') || 'en'

export const i18n = createI18n({
  legacy: false,
  locale: savedLang,
  fallbackLocale: 'en',
  messages: { en, bn }
})