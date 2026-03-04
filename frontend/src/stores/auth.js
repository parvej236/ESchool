import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('jwt_token') || null)
  const user = ref(null)
  /** Becomes true after first validateAuth(); prevents showing Dashboard from stale token before validation */
  const authReady = ref(false)

  const isAuthenticated = computed(() => !!token.value)
  /** Only true when we have a token AND we've validated it (so no "logged in" from stale token on load) */
  const isAuthenticatedAfterCheck = computed(() => authReady.value && !!token.value)

  function setToken(newToken) {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('jwt_token', newToken)
    } else {
      localStorage.removeItem('jwt_token')
    }
  }

  function setUser(userData) {
    user.value = userData
  }

  async function fetchUserInfo() {
    if (!token.value) return
    
    try {
      const response = await fetch('http://localhost:8080/api/dashboard/profile', {
        headers: {
          'Authorization': getAuthHeader(),
        },
      })
      
      if (response.ok) {
        const profile = await response.json()
        setUser(profile)
      } else if (response.status === 401 || response.status === 403) {
        // Token invalid or user not in DB – clear so user must log in again
        logout()
      } else {
        // Other error (e.g. 500) – don't treat as logged in
        logout()
      }
    } catch (err) {
      // Network error or other – can't verify token, so clear it
      console.error('Failed to fetch user info:', err)
      logout()
    }
  }

  /** Call on app load to validate token; clears it if backend rejects (e.g. no user in DB) */
  async function validateAuth() {
    if (!token.value) {
      authReady.value = true
      return
    }
    await fetchUserInfo()
    authReady.value = true
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('jwt_token')
  }

  function getAuthHeader() {
    return token.value ? `Bearer ${token.value}` : null
  }

  return {
    token,
    user,
    authReady,
    isAuthenticated,
    isAuthenticatedAfterCheck,
    setToken,
    setUser,
    logout,
    getAuthHeader,
    fetchUserInfo,
    validateAuth
  }
})

