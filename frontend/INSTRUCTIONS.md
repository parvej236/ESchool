# 📦 Reusable Data List Components — Documentation

A complete guide to using the generic `DataTable`, `DataGrid`, `DataFilters`, and `DataPagination` components with the `useDataList` composable. Build any list/table page (Users, Students, Classes, Subjects, etc.) in minutes with zero logic duplication.

---

## 📁 File Structure

```
src/
├── components/
│   └── shared/
│       ├── DataTable.vue        ← Generic sortable table with filter row
│       ├── DataGrid.vue         ← Generic card grid layout
│       ├── DataFilters.vue      ← Search bar, view toggle, filter toggle
│       └── DataPagination.vue   ← Page navigation + items-per-page
│
├── composables/
│   └── useDataList.js           ← All state & logic (fetch, filter, sort, paginate)
│
└── views/
    ├── UsersView.vue            ← Example: Users page
    ├── ClassesView.vue          ← Example: Classes page
    └── StudentsView.vue         ← Example: Students page (you build this)
```

---

## 🚀 Quick Start — Build a New Page in 5 Steps

### Step 1 — Import the shared components and composable

```vue
<script setup>
import { onMounted } from 'vue'
import DataFilters    from '@/components/shared/DataFilters.vue'
import DataTable      from '@/components/shared/DataTable.vue'
import DataGrid       from '@/components/shared/DataGrid.vue'
import DataPagination from '@/components/shared/DataPagination.vue'
import { useDataList } from '@/composables/useDataList'
</script>
```

---

### Step 2 — Define your columns

Each column is a plain object. Only this page file knows what fields to show.

```js
const columns = [
  { key: 'id',      label: 'ID',      sortable: true,  filterable: true,  class: 'w-20' },
  { key: 'name',    label: 'Name',    sortable: true,  filterable: true },
  { key: 'email',   label: 'Email',   sortable: true,  filterable: true,  hideOnMobile: true },
  { key: 'actions', label: 'Actions', sortable: false, filterable: false, class: 'text-center' },
]
```

**Column properties:**

| Property        | Type      | Required | Description |
|-----------------|-----------|----------|-------------|
| `key`           | `String`  | ✅       | Must match the field name in your API data |
| `label`         | `String`  | ✅       | Text shown in the column header |
| `sortable`      | `Boolean` | ✅       | Enables click-to-sort on this column |
| `filterable`    | `Boolean` | ✅       | Shows a filter input in the filter row |
| `hideOnMobile`  | `Boolean` | ❌       | Hides column on small screens (`hidden sm:table-cell`) |
| `class`         | `String`  | ❌       | Extra Tailwind classes on the `<th>` |
| `tdClass`       | `String`  | ❌       | Extra Tailwind classes on each `<td>` |

> ⚠️ **Important:** Always include an `actions` column last if you need buttons (Edit, Delete, Status badges, etc.).

---

### Step 3 — Call the composable

```js
const {
  items,           // { fetch() } — call items.fetch() in onMounted
  filteredItems,   // computed — all rows after search + filters + sort
  paginatedItems,  // computed — current page slice of filteredItems
  searchQuery,     // ref — bound to the search input
  viewMode,        // ref — 'grid' | 'list'
  showFilterRow,   // ref — toggle the column filter row
  filters,         // ref — object of per-column filter values
  sortKey,         // ref — currently sorted column key
  sortOrder,       // ref — 'asc' | 'desc'
  currentPage,     // ref — current page number
  itemsPerPage,    // ref — rows per page
  totalPages,      // computed — total number of pages
  hasActiveFilters,// computed — true if any filter/search is active
  handleSort,      // fn(key) — toggles sort on a column
  clearFilters,    // fn() — resets all filters, search, and page
} = useDataList(
  'http://localhost:8080/api/your-endpoint', // your API URL
  ['name', 'email']                          // fields to include in global search
)

onMounted(() => items.fetch())
```

---

### Step 4 — Write the template

```vue
<template>
  <DashboardSidebar />
  <div class="ml-0 md:ml-64 transition-all">
    <DashboardHeader />
    <main class="pt-3 px-3 sm:px-6 pb-6">

      <!-- 1. Toolbar: search, filter toggle, view toggle, action button -->
      <DataFilters
        title="Your Page Title"
        :totalCount="filteredItems.length"
        v-model:searchQuery="searchQuery"
        v-model:viewMode="viewMode"
        v-model:showFilterRow="showFilterRow"
        :hasActiveFilters="hasActiveFilters"
        @clearFilters="clearFilters"
      >
        <template #actions>
          <!-- Your custom button goes here -->
          <button class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm">
            + New Item
          </button>
        </template>
      </DataFilters>

      <!-- 2a. Grid view -->
      <DataGrid v-if="viewMode === 'grid'" :items="paginatedItems">
        <template #card="{ item }">
          <!-- Design your card here, item = one row of data -->
          <div class="bg-white rounded-2xl p-4 border">
            <h3>{{ item.name }}</h3>
            <p>{{ item.email }}</p>
          </div>
        </template>
      </DataGrid>

      <!-- 2b. Table view -->
      <DataTable
        v-else
        :rows="paginatedItems"
        :columns="columns"
        :sortKey="sortKey"
        :sortOrder="sortOrder"
        :showFilterRow="showFilterRow"
        :filters="filters"
        @sort="handleSort"
        @update:filters="(v) => { filters = v; currentPage = 1 }"
        @clearFilters="clearFilters"
      >
        <!-- Custom cell slots — see Step 5 below -->
      </DataTable>

      <!-- 3. Empty state -->
      <div v-if="filteredItems.length === 0" class="text-center py-16 text-gray-400 bg-white rounded-2xl border">
        <p class="font-medium">No records found</p>
        <button @click="clearFilters" class="mt-2 text-sm text-blue-500 hover:underline">Clear filters</button>
      </div>

      <!-- 4. Pagination -->
      <DataPagination
        v-if="filteredItems.length > 0"
        :currentPage="currentPage"
        :totalPages="totalPages"
        :totalCount="filteredItems.length"
        :itemsPerPage="itemsPerPage"
        @update:currentPage="currentPage = $event"
        @update:itemsPerPage="itemsPerPage = $event; currentPage = 1"
      />

    </main>
  </div>
</template>
```

---

### Step 5 — Customize table cells with slots

Each column can have a custom cell template using the `#cell-[key]` slot pattern.

```vue
<DataTable ...>

  <!-- Plain ID with # prefix -->
  <template #cell-id="{ value }">
    <span class="text-gray-400 text-xs">#{{ value }}</span>
  </template>

  <!-- Avatar + name (uses full row data) -->
  <template #cell-name="{ row }">
    <div class="flex items-center gap-3">
      <div class="w-8 h-8 rounded-lg bg-blue-500 flex items-center justify-center text-white text-xs font-bold">
        {{ row.name?.charAt(0).toUpperCase() }}
      </div>
      <span class="font-medium text-gray-800">{{ row.name }}</span>
    </div>
  </template>

  <!-- Status badge -->
  <template #cell-status="{ value }">
    <span :class="[
      'inline-flex px-2.5 py-0.5 rounded-full text-xs font-medium',
      value === 'active'   ? 'bg-green-100 text-green-700'  :
      value === 'pending'  ? 'bg-yellow-100 text-yellow-700' :
                             'bg-red-100 text-red-700'
    ]">
      {{ value }}
    </span>
  </template>

  <!-- Action buttons -->
  <template #cell-actions="{ row }">
    <div class="flex items-center justify-center gap-2">
      <button @click="editItem(row)" class="text-xs text-blue-500 hover:underline">Edit</button>
      <button @click="deleteItem(row.id)" class="text-xs text-red-400 hover:underline">Delete</button>
    </div>
  </template>

</DataTable>
```

**Slot variables:**

| Variable | Description |
|----------|-------------|
| `{ value }` | The value of `row[col.key]` — use for simple display |
| `{ row }`   | The entire row object — use when you need other fields |

> 💡 If you don't provide a slot for a column, it falls back to displaying `row[col.key]` as plain text.

---

## 📋 Component API Reference

### `<DataFilters>`

| Prop / Event             | Type      | Description |
|--------------------------|-----------|-------------|
| `title`                  | `String`  | Page heading (e.g. "Users", "Classes") |
| `totalCount`             | `Number`  | Shown as "X records found" |
| `v-model:searchQuery`    | `String`  | Two-way bound search input |
| `v-model:viewMode`       | `String`  | `'grid'` or `'list'` |
| `v-model:showFilterRow`  | `Boolean` | Toggles the column filter row in the table |
| `hasActiveFilters`       | `Boolean` | Shows the red "Clear" button when `true` |
| `showViewToggle`         | `Boolean` | Set `false` to hide the grid/list toggle (default: `true`) |
| `searchPlaceholder`      | `String`  | Placeholder for the search input |
| `@clearFilters`          | Event     | Emitted when "Clear" is clicked |
| `#actions` slot          | Slot      | Put your "New Item" button here |

---

### `<DataTable>`

| Prop / Event         | Type       | Description |
|----------------------|------------|-------------|
| `rows`               | `Array`    | The current page of data to display |
| `columns`            | `Array`    | Column definitions (see column properties above) |
| `sortKey`            | `String`   | Currently sorted column key |
| `sortOrder`          | `String`   | `'asc'` or `'desc'` |
| `showFilterRow`      | `Boolean`  | Shows or hides the filter input row |
| `filters`            | `Object`   | Current per-column filter values |
| `@sort`              | Event      | Emits column key when a header is clicked |
| `@update:filters`    | Event      | Emits updated filters object |
| `@clearFilters`      | Event      | Emits when "Clear" is clicked in the filter row |
| `#cell-[key]` slots  | Slot       | Custom cell renderer per column |

---

### `<DataGrid>`

| Prop          | Type     | Description |
|---------------|----------|-------------|
| `items`       | `Array`  | The current page of data to display |
| `gridClass`   | `String` | Tailwind grid classes (default: `'grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5'`) |
| `#card` slot  | Slot     | Card template — receives `{ item }` |

**Custom grid columns example:**
```vue
<!-- 3 columns max (e.g. for wider cards) -->
<DataGrid :items="paginatedItems" gridClass="grid-cols-1 sm:grid-cols-2 lg:grid-cols-3">
  <template #card="{ item }"> ... </template>
</DataGrid>
```

---

### `<DataPagination>`

| Prop / Event             | Type      | Description |
|--------------------------|-----------|-------------|
| `currentPage`            | `Number`  | Active page number |
| `totalPages`             | `Number`  | Total number of pages |
| `totalCount`             | `Number`  | Total number of filtered records |
| `itemsPerPage`           | `Number`  | Records shown per page |
| `itemsPerPageOptions`    | `Array`   | Dropdown options (default: `[5, 10, 20, 50]`) |
| `@update:currentPage`    | Event     | Emits new page number |
| `@update:itemsPerPage`   | Event     | Emits new items-per-page value |

---

### `useDataList(apiUrl, searchFields)`

| Parameter      | Type       | Description |
|----------------|------------|-------------|
| `apiUrl`       | `String`   | Full URL of your API endpoint |
| `searchFields` | `String[]` | Field names to search across with the global search |

**Expected API response format:**
```json
{
  "success": true,
  "data": [
    { "id": 1, "name": "Alice", "email": "alice@mail.com" },
    { "id": 2, "name": "Bob",   "email": "bob@mail.com" }
  ]
}
```

> ⚠️ The composable expects `result.success === true` and `result.data` to be the array. If your API uses a different shape, edit the `fetch` function inside `useDataList.js`.

---

## 🧩 Real Page Examples

### Users Page
```js
const columns = [
  { key: 'id',       label: 'ID',      sortable: true,  filterable: true,  class: 'w-20' },
  { key: 'username', label: 'User',    sortable: true,  filterable: true },
  { key: 'email',    label: 'Email',   sortable: true,  filterable: true,  hideOnMobile: true },
  { key: 'actions',  label: 'Actions', sortable: false, filterable: false, class: 'text-center' },
]
const { ...all } = useDataList('http://localhost:8080/api/users/all', ['username', 'email'])
```

### Classes Page
```js
const columns = [
  { key: 'id',       label: 'ID',         sortable: true,  filterable: true,  class: 'w-20' },
  { key: 'name',     label: 'Class Name', sortable: true,  filterable: true },
  { key: 'teacher',  label: 'Teacher',    sortable: true,  filterable: true,  hideOnMobile: true },
  { key: 'students', label: 'Students',   sortable: true,  filterable: false, class: 'text-center' },
  { key: 'actions',  label: 'Actions',    sortable: false, filterable: false, class: 'text-center' },
]
const { ...all } = useDataList('http://localhost:8080/api/classes', ['name', 'teacher'])
```

### Students Page
```js
const columns = [
  { key: 'id',        label: 'ID',          sortable: true,  filterable: true,  class: 'w-20' },
  { key: 'name',      label: 'Student',     sortable: true,  filterable: true },
  { key: 'class',     label: 'Class',       sortable: true,  filterable: true,  hideOnMobile: true },
  { key: 'roll',      label: 'Roll No',     sortable: true,  filterable: true },
  { key: 'status',    label: 'Status',      sortable: false, filterable: false, class: 'text-center' },
  { key: 'actions',   label: 'Actions',     sortable: false, filterable: false, class: 'text-center' },
]
const { ...all } = useDataList('http://localhost:8080/api/students', ['name', 'class'])
```

### Subjects Page
```js
const columns = [
  { key: 'id',       label: 'ID',           sortable: true,  filterable: true,  class: 'w-20' },
  { key: 'name',     label: 'Subject',      sortable: true,  filterable: true },
  { key: 'code',     label: 'Code',         sortable: true,  filterable: true },
  { key: 'class',    label: 'Class',        sortable: true,  filterable: true,  hideOnMobile: true },
  { key: 'actions',  label: 'Actions',      sortable: false, filterable: false, class: 'text-center' },
]
const { ...all } = useDataList('http://localhost:8080/api/subjects', ['name', 'code'])
```

---

## 🎨 Common Cell Slot Recipes

Copy and paste these into any page as needed.

### Avatar with initials
```vue
<template #cell-name="{ row }">
  <div class="flex items-center gap-3">
    <div class="w-8 h-8 rounded-lg flex items-center justify-center text-white text-xs font-bold"
      :style="{ backgroundColor: getAvatarColor(row.id) }">
      {{ getInitials(row.name) }}
    </div>
    <div>
      <p class="font-medium text-gray-800 text-sm">{{ row.name }}</p>
      <p class="text-xs text-gray-400 sm:hidden">{{ row.email }}</p>
    </div>
  </div>
</template>
```

### Status badge (active / inactive / pending)
```vue
<template #cell-status="{ value }">
  <span :class="[
    'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium',
    value === 'active'   ? 'bg-green-100 text-green-700'   :
    value === 'inactive' ? 'bg-gray-100 text-gray-500'     :
    value === 'pending'  ? 'bg-yellow-100 text-yellow-700' :
                           'bg-red-100 text-red-700'
  ]">
    {{ value }}
  </span>
</template>
```

### Count badge
```vue
<template #cell-students="{ value }">
  <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-700">
    {{ value }} students
  </span>
</template>
```

### Edit + Delete actions
```vue
<template #cell-actions="{ row }">
  <div class="flex items-center justify-center gap-3">
    <button @click="editItem(row)" class="text-xs text-blue-500 hover:text-blue-700 font-medium transition">
      Edit
    </button>
    <button @click="deleteItem(row.id)" class="text-xs text-red-400 hover:text-red-600 font-medium transition">
      Delete
    </button>
  </div>
</template>
```

### View button
```vue
<template #cell-actions="{ row }">
  <button @click="viewItem(row)" class="text-xs bg-blue-50 text-blue-600 hover:bg-blue-100 px-3 py-1 rounded-lg font-medium transition">
    View
  </button>
</template>
```

### Helper functions (add to any page that needs avatars)
```js
const AVATAR_COLORS = ['#7C3AED','#059669','#DC2626','#D97706','#2563EB','#DB2777','#0891B2','#65A30D']
const getAvatarColor = (id) => AVATAR_COLORS[id % AVATAR_COLORS.length]
const getInitials = (name) => name?.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) ?? '??'
```

---

## ⚙️ Customization Tips

### Hide the grid/list toggle (table-only pages)
```vue
<DataFilters ... :showViewToggle="false">
```

### Change items-per-page options
```vue
<DataPagination ... :itemsPerPageOptions="[10, 25, 100]" />
```

### Start in list view instead of grid
```js
const { viewMode, ...rest } = useDataList(url, fields)
viewMode.value = 'list' // set default before mounting
```

### Change grid column count
```vue
<!-- 3-column max grid -->
<DataGrid gridClass="grid-cols-1 sm:grid-cols-2 lg:grid-cols-3" ...>
```

### Add a custom search placeholder
```vue
<DataFilters searchPlaceholder="Search by name or email..." ...>
```

### Change API response shape
If your API returns `{ status: 'ok', result: [...] }` instead of `{ success: true, data: [...] }`,
edit `useDataList.js`:
```js
// Change this line in useDataList.js:
if (result.success) rawItems.value = result.data
// To match your API:
if (result.status === 'ok') rawItems.value = result.result
```

---

## ❓ Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| Data not loading | Wrong API URL or response shape | Check browser Network tab; verify `result.success` and `result.data` |
| Filter row not appearing | `showFilterRow` not toggled | Click the Filter button in the toolbar (only visible in list view) |
| Sort not working | Column `sortable: false` | Set `sortable: true` on the column |
| Column filter missing | Column `filterable: false` | Set `filterable: true` on the column |
| Grid not showing | `viewMode` defaults to `'list'` | Change `viewMode.value = 'grid'` after calling the composable |
| Mobile column missing | `hideOnMobile: true` is set | Expected behavior — email appears inline under the name on mobile |
| Pagination shows wrong total | `filteredItems` vs `paginatedItems` confusion | Always bind `filteredItems.length` to `totalCount`, never `paginatedItems.length` |

---

## 📌 Summary Checklist for Every New Page

- [ ] Import `DataFilters`, `DataTable`, `DataGrid`, `DataPagination`, `useDataList`
- [ ] Define `columns` array with correct keys, labels, sortable, filterable flags
- [ ] Call `useDataList(url, searchFields)` and destructure what you need
- [ ] Call `items.fetch()` inside `onMounted`
- [ ] Add `<DataFilters>` with your custom `#actions` button
- [ ] Add `<DataGrid>` with a `#card` slot for grid view
- [ ] Add `<DataTable>` with `#cell-[key]` slots for custom cells
- [ ] Add empty state block
- [ ] Add `<DataPagination>`