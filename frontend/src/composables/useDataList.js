import { ref, computed } from "vue";

export function useDataList(apiUrl, searchFields = []) {
  const rawItems = ref([]);
  const searchQuery = ref("");
  const viewMode = ref("grid");
  const showFilterRow = ref(false);
  const filters = ref({});
  const sortKey = ref("");
  const sortOrder = ref("asc");
  const currentPage = ref(1);
  const itemsPerPage = ref(10);

  // Fetch
  const items = {
    fetch: async () => {
      try {
        const res = await fetch(apiUrl);
        const result = await res.json();
        if (result.success) rawItems.value = result.data;
      } catch (e) {
        console.error(`Failed to fetch from ${apiUrl}:`, e);
      }
    },
  };

  const filteredItems = computed(() => {
    let result = rawItems.value;

    // Global search across specified fields
    if (searchQuery.value) {
      const q = searchQuery.value.toLowerCase();
      result = result.filter((item) =>
        searchFields.some((field) => item[field]?.toLowerCase().includes(q)),
      );
    }

    // Per-column filters
    Object.entries(filters.value).forEach(([key, val]) => {
      if (val)
        result = result.filter((item) =>
          String(item[key] ?? "")
            .toLowerCase()
            .includes(val.toLowerCase()),
        );
    });

    // Sorting
    if (sortKey.value) {
      result = [...result].sort((a, b) => {
        const aVal = String(a[sortKey.value] ?? "").toLowerCase();
        const bVal = String(b[sortKey.value] ?? "").toLowerCase();
        return sortOrder.value === "asc"
          ? aVal.localeCompare(bVal)
          : bVal.localeCompare(aVal);
      });
    }

    return result;
  });

  const totalPages = computed(() =>
    Math.max(1, Math.ceil(filteredItems.value.length / itemsPerPage.value)),
  );

  const paginatedItems = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value;
    return filteredItems.value.slice(start, start + itemsPerPage.value);
  });

  const hasActiveFilters = computed(
    () => !!searchQuery.value || Object.values(filters.value).some((v) => !!v),
  );

  const handleSort = (key) => {
    sortOrder.value =
      sortKey.value === key && sortOrder.value === "asc" ? "desc" : "asc";
    sortKey.value = key;
    currentPage.value = 1;
  };

  const clearFilters = () => {
    filters.value = {};
    searchQuery.value = "";
    currentPage.value = 1;
  };

  return {
    items,
    filteredItems,
    paginatedItems,
    searchQuery,
    viewMode,
    showFilterRow,
    filters,
    sortKey,
    sortOrder,
    currentPage,
    itemsPerPage,
    totalPages,
    hasActiveFilters,
    handleSort,
    clearFilters,
  };
}
