import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// Helper function to decode JWT
function decodeJWT(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch (e) {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('jwt_token') || null)
  const user = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  
  const userRole = computed(() => {
    if (!token.value) return null
    const decoded = decodeJWT(token.value)
    // The role might be in the token or we need to fetch it
    // For now, we'll fetch it from the dashboard endpoint
    return user.value?.role || null
  })

  const isAdmin = computed(() => {
    return userRole.value === 'ADMIN'
  })

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
        // Try to get role from authorities or user data
        if (profile.authorities && profile.authorities.length > 0) {
          const role = profile.authorities[0].authority.replace('ROLE_', '')
          setUser({ ...profile, role })
        } else {
          setUser(profile)
        }
      }
    } catch (err) {
      console.error('Failed to fetch user info:', err)
    }
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
    isAuthenticated,
    userRole,
    isAdmin,
    setToken,
    setUser,
    logout,
    getAuthHeader,
    fetchUserInfo
  }
})

