# Theme & Language System — Developer Guide

A reference for every developer working on this project. Read this before touching any component that needs dark/light theme or EN/BN language support.

---

## Table of Contents

1. [Project Setup](#1-project-setup)
2. [Theme System](#2-theme-system)
3. [Language System — Static Strings](#3-language-system--static-strings)
4. [Language System — Backend Objects](#4-language-system--backend-objects)
5. [Using Both in a Component](#5-using-both-in-a-component)
6. [Adding New Translations](#6-adding-new-translations)
7. [Backend Field Naming Convention](#7-backend-field-naming-convention)
8. [Quick Reference Cheatsheet](#8-quick-reference-cheatsheet)

---

## 1. Project Setup

### Installed packages
```bash
npm install pinia vue-i18n@9
```

### Registration in `main.js`
```js
import { createApp }   from 'vue'
import { createPinia } from 'pinia'
import App             from './App.vue'
import { i18n }        from './i18n'

createApp(App)
  .use(createPinia())
  .use(i18n)
  .mount('#app')
```

### Theme init — `App.vue` (runs once for the whole app)
```vue
<script setup>
import { onMounted }     from 'vue'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()
onMounted(() => themeStore.initTheme())
</script>

<template>
  <RouterView />
</template>
```

---

## 2. Theme System

### File: `src/stores/theme.js`
```js
import { defineStore } from 'pinia'
import { ref }         from 'vue'

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
    const stored      = localStorage.getItem('theme')
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    applyTheme(stored ? stored === 'dark' : prefersDark)

    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      if (!localStorage.getItem('theme')) applyTheme(e.matches)
    })
  }

  return { isDark, toggleTheme, initTheme }
})
```

### How to use theme in any component
```vue
<script setup>
import { useThemeStore } from '@/stores/theme'
const themeStore = useThemeStore()
</script>

<template>
  <!-- Bind classes -->
  <div :class="themeStore.isDark ? 'bg-[#141920] text-white' : 'bg-white text-slate-900'">

  <!-- Bind styles -->
  <button :style="themeStore.isDark
    ? 'background: #1c2430; border: 1px solid #2b3849;'
    : 'background: #f1f5f9; border: 1px solid #e2e8f0;'">

  <!-- Toggle button -->
  <button @click="themeStore.toggleTheme">
    Toggle Theme
  </button>
</template>
```

### ✅ DO — Dark theme color palette
```
Background base:      #0f1318
Surface / cards:      #141920
Elevated surface:     #1c2430
Border subtle:        #232c3a
Border interactive:   #2b3849
Border hover:         #3a4d63

Text primary:         #e2e8f0  (slate-200)
Text secondary:       #94a3b8  (slate-400)
Text muted:           #7a8a9e

Accent blue:          #7eb8f7
Accent blue bg:       #1a2a3d
Accent blue border:   #2a3f5c
Accent emerald:       #34d399
Accent amber:         #c8a84b
```

### ✅ DO — Light theme color palette
```
Background base:      #f8f9fc
Surface / cards:      #ffffff
Border:               #e2e8f0  (slate-200)

Text primary:         #0f172a  (slate-900)
Text secondary:       #64748b  (slate-500)
Text muted:           #94a3b8  (slate-400)

Accent blue:          #3b82f6
Accent blue bg:       #eff6ff
Accent emerald:       #10b981
```

### ❌ DON'T
```
- Never use Tailwind dark: prefix — use themeStore.isDark ternary instead
- Never create a local isDark ref in a component
- Never call applyTheme() or initTheme() from a component — only App.vue does this
- Never hardcode color values outside these palettes
```

---

## 3. Language System — Static Strings

### File: `src/i18n.js`
```js
import { createI18n } from 'vue-i18n'
import en from './locales/en'
import bn from './locales/bn'

export const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('lang') || 'en',
  fallbackLocale: 'en',
  messages: { en, bn }
})
```

### File: `src/composables/useLang.js`
```js
import { computed } from 'vue'
import { useI18n }  from 'vue-i18n'

export function useLang() {
  const { t, locale } = useI18n()

  const isBn = computed(() => locale.value === 'bn')

  // For backend objects: reads obj.titleBn or obj.titleEn
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
```

### How to use language in any component
```vue
<script setup>
import { useLang } from '@/composables/useLang'
const { t, isBn, tField, toggleLang } = useLang()
</script>

<template>
  <!-- Static translation key -->
  <h1 :class="isBn ? 'bn-font' : ''">{{ t('nav.home') }}</h1>

  <!-- Backend object field -->
  <h2>{{ tField(slide, 'title') }}</h2>

  <!-- Toggle button -->
  <button @click="toggleLang">
    {{ isBn ? 'Switch to English' : 'বাংলায় পরিবর্তন করুন' }}
  </button>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+Bengali:wght@400;600;700&display=swap');
.bn-font { font-family: 'Noto Sans Bengali', sans-serif; }
</style>
```

---

## 4. Language System — Backend Objects

When data comes from the API, the backend sends bilingual fields using this naming convention:

### Backend JSON response example
```json
{
  "id": 1,
  "titleEn": "Welcome to Our School",
  "titleBn": "আমাদের স্কুলে স্বাগতম",
  "descriptionEn": "Excellence in education.",
  "descriptionBn": "শিক্ষায় শ্রেষ্ঠত্ব।",
  "imageUrl": "/uploads/slide1.jpg",
  "linkUrl": "/about"
}
```

### Reading backend fields in template
```vue
<!-- tField(object, 'fieldName') -->
<!-- Automatically picks titleBn or titleEn based on active language -->

<h1>{{ tField(slide, 'title') }}</h1>
<p>{{ tField(slide, 'description') }}</p>
<p>{{ tField(article, 'content') }}</p>
<p>{{ tField(teacher, 'name') }}</p>
```

### How tField resolves
```
Active language = EN  →  reads obj.titleEn  →  fallback: obj.title
Active language = BN  →  reads obj.titleBn  →  fallback: obj.titleEn
```

---

## 5. Using Both in a Component

### Full component template
```vue
<script setup>
import { useThemeStore } from '@/stores/theme'
import { useLang }       from '@/composables/useLang'

const themeStore        = useThemeStore()
const { t, isBn, tField } = useLang()
</script>

<template>
  <section
    :class="themeStore.isDark ? 'bg-[#0f1318] text-white' : 'bg-[#f8f9fc] text-slate-900'"
  >
    <!-- Static string from locale file -->
    <h1 :class="isBn ? 'bn-font' : ''">
      {{ t('section.title') }}
    </h1>

    <!-- Backend object field -->
    <p :class="isBn ? 'bn-font' : ''">
      {{ tField(item, 'description') }}
    </p>

    <!-- Theme-aware card -->
    <div
      :class="themeStore.isDark
        ? 'bg-[#141920] border border-[#232c3a]'
        : 'bg-white border border-slate-200 shadow-sm'"
    >
      ...
    </div>
  </section>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+Bengali:wght@400;600;700&display=swap');
.bn-font { font-family: 'Noto Sans Bengali', sans-serif; }
</style>
```

---

## 6. Adding New Translations

### Step 1 — Add keys to both locale files

**`src/locales/en.js`**
```js
export default {
  nav: {
    home:      'Home',
    vision:    'Vision',
    dashboard: 'Dashboard',
    login:     'Login',
  },
  hero: {
    learnMore:    'Learn More',
    loading:      'Loading...',
    welcome:      'Welcome to Our School',
    tagline:      'Excellence in Education, Innovation in Learning',
    heroContent1: 'I Believe',
    heroContent2: 'I Dare',
    heroContent3: 'I Can',
    heroContent4: 'I Will',
    heroContent5: 'My Life',
    heroContent6: 'I Build',
    heroTagline:  'The world is mine.',
  },
  about: {
    aboutTitle: 'Who We Are',
    aboutDesc1: 'We are committed to academic excellence and holistic development.',
    aboutDesc2: 'Our community of educators, students, and families grows together.',
  },
  // ── Add new sections below ──────────────────────────
  // mySection: {
  //   title: 'My Title',
  //   body:  'My body text.',
  // },
}
```

**`src/locales/bn.js`**
```js
export default {
  nav: {
    home:      'হোম',
    vision:    'ভিশন',
    dashboard: 'ড্যাশবোর্ড',
    login:     'লগইন',
  },
  hero: {
    learnMore:    'আরও জানুন',
    loading:      'লোড হচ্ছে...',
    welcome:      'আমাদের স্কুলে স্বাগতম',
    tagline:      'শিক্ষায় শ্রেষ্ঠত্ব, শেখায় উদ্ভাবন',
    heroContent1: 'আমি বিশ্বাস করি',
    heroContent2: 'আমি সাহস করি',
    heroContent3: 'আমি পারি',
    heroContent4: 'আমি করব',
    heroContent5: 'আমার জীবন',
    heroContent6: 'আমি গড়ি',
    heroTagline:  'পৃথিবী আমার।',
  },
  about: {
    aboutTitle: 'আমরা কারা',
    aboutDesc1: 'আমরা শিক্ষার শ্রেষ্ঠত্ব ও সামগ্রিক বিকাশে প্রতিশ্রুতিবদ্ধ।',
    aboutDesc2: 'আমাদের শিক্ষক, শিক্ষার্থী ও পরিবারের সমন্বয়ে গড়া সম্প্রদায় একসাথে এগিয়ে যায়।',
  },
  // ── নতুন সেকশন নিচে যোগ করুন ──────────────────────
  // mySection: {
  //   title: 'আমার শিরোনাম',
  //   body:  'আমার বিষয়বস্তু।',
  // },
}
```

### Step 2 — Use in component
```vue
{{ t('mySection.title') }}
{{ t('mySection.body') }}
```

### Key naming rules
```
✅ nav.home           — dot-separated namespace.key
✅ hero.learnMore     — camelCase keys
✅ about.aboutTitle   — descriptive names
❌ home               — no namespace (will conflict)
❌ nav.home-link      — no hyphens
❌ NAV.HOME           — no uppercase
```

---

## 7. Backend Field Naming Convention

The backend **must** follow this naming pattern for `tField()` to work automatically.

### Pattern: `{fieldName}En` / `{fieldName}Bn`

| Field purpose    | English key      | Bengali key      |
|------------------|------------------|------------------|
| Title            | `titleEn`        | `titleBn`        |
| Description      | `descriptionEn`  | `descriptionBn`  |
| Content / body   | `contentEn`      | `contentBn`      |
| Short summary    | `summaryEn`      | `summaryBn`      |
| Button label     | `labelEn`        | `labelBn`        |
| Teacher name     | `nameEn`         | `nameBn`         |
| Address          | `addressEn`      | `addressBn`      |

### Non-bilingual fields (no suffix needed)
```
id, imageUrl, linkUrl, videoUrl, slug,
createdAt, updatedAt, isActive, order, type
```

### Full example API response
```json
{
  "id": 42,
  "titleEn": "Annual Science Fair",
  "titleBn": "বার্ষিক বিজ্ঞান মেলা",
  "descriptionEn": "Students showcase their projects.",
  "descriptionBn": "শিক্ষার্থীরা তাদের প্রকল্প উপস্থাপন করে।",
  "imageUrl": "/uploads/events/science-fair.jpg",
  "linkUrl": "/events/science-fair",
  "isActive": true,
  "createdAt": "2025-01-15T10:00:00Z"
}
```

### Usage in component
```vue
<h2>{{ tField(event, 'title') }}</h2>
<p>{{ tField(event, 'description') }}</p>
<img :src="imgUrl(event.imageUrl)" />  <!-- imageUrl needs no tField -->
```

---

## 8. Quick Reference Cheatsheet

```
┌─────────────────────────────────────────────────────────────┐
│  IMPORTS                                                      │
├─────────────────────────────────────────────────────────────┤
│  import { useThemeStore } from '@/stores/theme'              │
│  import { useLang }       from '@/composables/useLang'       │
├─────────────────────────────────────────────────────────────┤
│  SETUP                                                        │
├─────────────────────────────────────────────────────────────┤
│  const themeStore          = useThemeStore()                 │
│  const { t, isBn, tField } = useLang()                      │
├─────────────────────────────────────────────────────────────┤
│  THEME                                                        │
├─────────────────────────────────────────────────────────────┤
│  themeStore.isDark          → true / false                   │
│  themeStore.toggleTheme()   → toggle + persist               │
│  themeStore.initTheme()     → call once in App.vue only      │
├─────────────────────────────────────────────────────────────┤
│  LANGUAGE                                                     │
├─────────────────────────────────────────────────────────────┤
│  t('section.key')           → static string from locale      │
│  tField(obj, 'title')       → obj.titleBn or obj.titleEn     │
│  isBn                       → computed true if Bengali        │
│  toggleLang()               → switch EN ↔ BN + persist       │
├─────────────────────────────────────────────────────────────┤
│  BENGALI FONT                                                 │
├─────────────────────────────────────────────────────────────┤
│  :class="isBn ? 'bn-font' : ''"                              │
│  .bn-font { font-family: 'Noto Sans Bengali', sans-serif; }  │
│  Add @import in each component's <style scoped>              │
├─────────────────────────────────────────────────────────────┤
│  BACKEND FIELDS                                               │
├─────────────────────────────────────────────────────────────┤
│  Always send:  titleEn, titleBn, descriptionEn, descriptionBn│
│  Never suffix: id, imageUrl, linkUrl, isActive, createdAt    │
└─────────────────────────────────────────────────────────────┘
```

---

> **Rule of thumb:** Every component needs exactly 2 imports and 2 lines of setup. If you find yourself writing more than that for theme/language, something is wrong — re-read this guide.