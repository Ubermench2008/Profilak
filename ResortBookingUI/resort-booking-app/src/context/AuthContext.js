import React, { createContext, useState } from 'react'
import { login as apiLogin, register as apiRegister, logout as apiLogout } from '../api/auth'

const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)

  async function login(email, password, role) {
    const res = await apiLogin(email, password)
    setUser({ email: res.data.email, role: res.data.role })
    return res
  }

  async function register(email, password, role) {
    return apiRegister(email, password, role)
  }

  async function logout() {
    await apiLogout()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export default AuthContext