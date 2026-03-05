import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import router from '@/router'

const BASE_URL           = 'http://localhost:8080/api'
const INACTIVITY_TIMEOUT = 10 * 60 * 1000  // 10 minutes in ms
const REFRESH_BEFORE_EXP = 60 * 1000       // refresh 60s before expiry

export const useAuthStore = defineStore('auth', () => {

  const token        = ref(localStorage.getItem('jwt_token')     || null)
  const refreshToken = ref(localStorage.getItem('refresh_token') || null)
  const user         = ref(JSON.parse(localStorage.getItem('auth_user') || 'null'))
  const authReady    = ref(false)

  // ── Timers ─────────────────────────────────────────────
  let inactivityTimer = null
  let refreshTimer    = null

  // ── Computed ───────────────────────────────────────────
  const isAuthenticated           = computed(() => !!token.value)
  const isAuthenticatedAfterCheck = computed(() => authReady.value && !!token.value)

  // ── Token helpers ──────────────────────────────────────
  function setToken(newToken) {
    token.value = newToken
    if (newToken) localStorage.setItem('jwt_token', newToken)
    else          localStorage.removeItem('jwt_token')
  }

  function setRefreshToken(newRefreshToken) {
    refreshToken.value = newRefreshToken
    if (newRefreshToken) localStorage.setItem('refresh_token', newRefreshToken)
    else                 localStorage.removeItem('refresh_token')
  }

  function setUser(userData) {
    user.value = userData
    if (userData) localStorage.setItem('auth_user', JSON.stringify(userData))
    else          localStorage.removeItem('auth_user')
  }

  function getAuthHeader() {
    return token.value ? `Bearer ${token.value}` : null
  }

  function decodeToken(jwt) {
    try {
      return JSON.parse(atob(jwt.split('.')[1]))
    } catch {
      return null
    }
  }

  function getTokenExpiryMs(jwt) {
    const decoded = decodeToken(jwt)
    if (!decoded?.exp) return 0
    return decoded.exp * 1000
  }

  // ── Inactivity timer ───────────────────────────────────
  function resetInactivityTimer() {
    if (!token.value) return
    clearTimeout(inactivityTimer)
    inactivityTimer = setTimeout(() => {
      console.info('[auth] Inactivity timeout — logging out')
      logout(true)
    }, INACTIVITY_TIMEOUT)
  }

  function startActivityListeners() {
    const events = ['mousemove', 'mousedown', 'keydown', 'touchstart', 'scroll', 'click']
    events.forEach(evt =>
      window.addEventListener(evt, resetInactivityTimer, { passive: true })
    )
  }

  function stopActivityListeners() {
    const events = ['mousemove', 'mousedown', 'keydown', 'touchstart', 'scroll', 'click']
    events.forEach(evt =>
      window.removeEventListener(evt, resetInactivityTimer)
    )
  }

  // ── Refresh timer ──────────────────────────────────────
  function scheduleTokenRefresh(accessToken) {
    clearTimeout(refreshTimer)
    const expiryMs      = getTokenExpiryMs(accessToken)
    const nowMs         = Date.now()
    const msUntilExpiry = expiryMs - nowMs
    const refreshInMs   = msUntilExpiry - REFRESH_BEFORE_EXP

    if (refreshInMs <= 0) {
      silentRefresh()
      return
    }

    console.info(`[auth] Token refresh scheduled in ${Math.round(refreshInMs / 1000)}s`)
    refreshTimer = setTimeout(() => silentRefresh(), refreshInMs)
  }

  async function silentRefresh() {
    if (!refreshToken.value) {
      logout(true)
      return
    }

    try {
      const res  = await fetch(`${BASE_URL}/auth/refresh`, {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify({ refreshToken: refreshToken.value }),
      })
      const data = await res.json()

      if (res.ok && data.token) {
        setToken(data.token)
        scheduleTokenRefresh(data.token)
        console.info('[auth] Token silently refreshed')
      } else {
        console.warn('[auth] Refresh token expired — logging out')
        logout(true)
      }
    } catch (err) {
      // Network error — don't logout, user may just be temporarily offline
      console.error('[auth] Silent refresh failed:', err)
    }
  }

  // ── authFetch ──────────────────────────────────────────
  // Use this instead of raw fetch() for ALL authenticated requests.
  // ✅ Automatically adds Authorization header
  // ✅ Skips Content-Type for FormData (file uploads) so browser sets the multipart boundary
  // ✅ Auto-retries once if token expired mid-request

  async function authFetch(url, options = {}) {

    const makeRequest = (tkn) => {
      const headers = { ...options.headers }

      // ✅ Only set JSON content-type if body is NOT FormData
      // For FormData, the browser MUST set Content-Type itself (includes the boundary)
      // Manually setting it breaks multipart uploads
      if (!(options.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json'
      }

      // Always inject the Bearer token
      headers['Authorization'] = `Bearer ${tkn}`

      return fetch(url, { ...options, headers })
    }

    // First attempt
    let res = await makeRequest(token.value)

    // Handle 401 — check if it's a token expiry we can recover from
    if (res.status === 401) {
      let errData = {}
      try {
        // Clone before reading body — body can only be read once
        errData = await res.clone().json()
      } catch {
        // ignore parse error
      }

      if (errData.code === 'TOKEN_EXPIRED' && refreshToken.value) {
        console.info('[auth] Access token expired mid-request — refreshing...')
        await silentRefresh()

        if (token.value) {
          // Retry with new token
          res = await makeRequest(token.value)
        } else {
          // silentRefresh failed / refresh token expired
          return {
            ok:     false,
            status: 401,
            data:   { error: 'Session expired. Please log in again.' }
          }
        }
      }
    }

    // Parse response body
    let data = {}
    try {
      data = await res.json()
    } catch {
      // Response has no body (e.g. 204 No Content)
      data = {}
    }

    return { ok: res.ok, status: res.status, data }
  }

  // ── Auth actions ───────────────────────────────────────

  /**
   * POST /api/auth/login
   * Returns: { token, refreshToken, message } or { error }
   */
  async function login(username, password) {
    try {
      const res  = await fetch(`${BASE_URL}/auth/login`, {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify({ username, password }),
      })
      const data = await res.json()

      if (res.ok && data.token) {
        setToken(data.token)
        setRefreshToken(data.refreshToken)

        const decoded = decodeToken(data.token)
        setUser({ username: decoded?.sub ?? username })

        startActivityListeners()
        resetInactivityTimer()
        scheduleTokenRefresh(data.token)

        return { success: true }
      }

      return {
        success: false,
        message: data.error ?? 'Login failed. Please check your credentials.'
      }
    } catch (err) {
      console.error('[auth] Login error:', err)
      return {
        success: false,
        message: 'Cannot connect to server. Make sure the backend is running on port 8080.'
      }
    }
  }

  /**
   * POST /api/auth/register
   * Returns: { message, user } or { error }
   */
  async function register(username, email, password) {
    try {
      const res  = await fetch(`${BASE_URL}/auth/register`, {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify({ username, email, password }),
      })
      const data = await res.json()

      return res.ok && data.user
        ? { success: true,  message: data.message }
        : { success: false, message: data.error ?? 'Registration failed' }
    } catch {
      return { success: false, message: 'Network error. Please try again.' }
    }
  }

  /**
   * POST /api/auth/forgot-password
   * Returns: { message } or { error }
   */
  async function forgotPassword(email) {
    try {
      const res  = await fetch(`${BASE_URL}/auth/forgot-password`, {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify({ email }),
      })
      const data = await res.json()

      return res.ok
        ? { success: true,  message: data.message }
        : { success: false, message: data.error ?? 'Request failed' }
    } catch {
      return { success: false, message: 'Network error. Please try again.' }
    }
  }

  /**
   * POST /api/auth/reset-password
   * Body: { token, password }
   * Returns: { message } or { error }
   */
  async function resetPassword(resetToken, newPassword) {
    try {
      const res  = await fetch(`${BASE_URL}/auth/reset-password`, {
        method:  'POST',
        headers: { 'Content-Type': 'application/json' },
        body:    JSON.stringify({ token: resetToken, password: newPassword }),
      })
      const data = await res.json()

      return res.ok
        ? { success: true,  message: data.message }
        : { success: false, message: data.error ?? 'Reset failed' }
    } catch {
      return { success: false, message: 'Network error. Please try again.' }
    }
  }

  /**
   * POST /api/auth/change-password
   * Body: { currentPassword, newPassword }
   * Uses authFetch — requires valid session
   */
  async function changePassword(currentPassword, newPassword) {
    const { ok, data } = await authFetch(`${BASE_URL}/auth/change-password`, {
      method: 'POST',
      body:   JSON.stringify({ currentPassword, newPassword }),
    })
    return ok
      ? { success: true,  message: data.message }
      : { success: false, message: data.error ?? 'Change failed' }
  }

  /**
   * Called once in App.vue on every page load / refresh.
   * Validates stored token, restores user, starts timers.
   */
  async function validateAuth() {
    if (!token.value) {
      authReady.value = true
      return
    }

    const expiryMs = getTokenExpiryMs(token.value)

    if (expiryMs && expiryMs < Date.now()) {
      // Access token expired — try to silently refresh before giving up
      if (refreshToken.value) {
        await silentRefresh()
      } else {
        logout()
        authReady.value = true
        return
      }
    }

    // If we still have a valid token after the refresh attempt
    if (token.value) {
      // Restore user from localStorage or decode from token
      if (!user.value) {
        const decoded = decodeToken(token.value)
        if (decoded?.sub) setUser({ username: decoded.sub })
      }

      // Resume activity tracking + schedule next refresh
      startActivityListeners()
      resetInactivityTimer()
      scheduleTokenRefresh(token.value)
    }

    authReady.value = true
  }

  /**
   * Clears all auth state, stops timers.
   * Pass redirect=true to go to login with session_expired message.
   */
  function logout(redirect = false) {
    setToken(null)
    setRefreshToken(null)
    setUser(null)

    clearTimeout(inactivityTimer)
    clearTimeout(refreshTimer)
    stopActivityListeners()

    if (redirect) {
      router.push({ path: '/login', query: { reason: 'session_expired' } })
    }
  }

  return {
    // State
    token,
    refreshToken,
    user,
    authReady,

    // Computed
    isAuthenticated,
    isAuthenticatedAfterCheck,

    // Helpers
    getAuthHeader,
    authFetch,
    decodeToken,

    // Actions
    login,
    register,
    forgotPassword,
    resetPassword,
    changePassword,
    validateAuth,
    logout,
  }
})