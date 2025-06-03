import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import useAuth from '../hooks/useAuth'
import '../styles/Auth.css'

export default function StaffRegister() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const { register } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    try {
      await register(email, password, 'STAFF')
      navigate('/staff/login')
    } catch {
      setError('Ошибка регистрации')
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-container">
        <h2>Регистрация персонала</h2>
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
          <button type="submit">Зарегистрироваться</button>
        </form>
      </div>
    </div>
  )
}