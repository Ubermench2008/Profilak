import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import useAuth from '../hooks/useAuth'
import '../styles/Auth.css'

export default function StaffLogin() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const { login, logout } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      const res = await login(email, password)
      if (res.data.role !== 'ROLE_STAFF') {
        await logout()
        setError('Неверные данные')
        return
      }
      navigate('/staff/dashboard')
    } catch {
      setError('Неверные данные')
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-container">
        <h2>Вход для персонала</h2>
        {error && <p className="error">{error}</p>}
        <form onSubmit={handleSubmit}>
          <input
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            placeholder="Email"
            required
          />
          <input
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            placeholder="Пароль"
            required
          />
          <button type="submit">Войти</button>
        </form>
      </div>
    </div>
  )
}