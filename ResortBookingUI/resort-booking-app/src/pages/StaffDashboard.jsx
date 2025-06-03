import React, { useEffect, useState } from 'react'
import {
  getAllRequests,
  approveRequest,
  rejectRequest,
  getAllSessionsAdmin,
  createSession
} from '../api/requests'
import RequestTable from '../components/Staff/RequestTable'
import SessionForm from '../components/Staff/SessionForm'
import '../styles/Dashboard.css'
import '../styles/StaffDashboard.css'

export default function StaffDashboard() {
  const [requests, setRequests] = useState([])
  const [filter, setFilter] = useState('')
  const [sessions, setSessions] = useState([])
  const [error, setError] = useState('')

  async function fetchData() {
    try {
      const r = await getAllRequests(filter)
      setRequests(r.data)
    } catch {
      setError('Не удалось загрузить заявки')
    }
    try {
      const s = await getAllSessionsAdmin()
      setSessions(s.data)
    } catch {
      setError('Не удалось загрузить сеансы')
    }
  }

  useEffect(() => {
    fetchData()
  }, [filter])

  function onApprove(id) {
    approveRequest(id).then(fetchData)
  }

  function onReject(id) {
    rejectRequest(id).then(fetchData)
  }

  function onCreateSession(startDate, endDate, capacity) {
    return createSession(startDate, endDate, capacity)
      .then(fetchData)
  }

  return (
    <div className="dashboard-page">
      <div className="dashboard-container">
        <h1>Панель персонала</h1>
        {error && <p className="error">{error}</p>}

        <section className="session-admin-section">
          <h2>Добавить новый сеанс</h2>
          <SessionForm onCreate={onCreateSession} />
          <h3>Текущие сеансы</h3>
          {sessions.length === 0 ? (
            <p>Сеансов пока нет</p>
          ) : (
            <ul className="session-list">
              {sessions.map(s => (
                <li key={s.id}>
                  {s.startDate} – {s.endDate}, {s.bookedCount}/{s.capacity}
                </li>
              ))}
            </ul>
          )}
        </section>

        <section className="requests-admin-section">
          <h2>Заявки</h2>
          <div className="filters">
            <button onClick={() => setFilter('')}>Все</button>
            <button onClick={() => setFilter('PENDING')}>Новые</button>
            <button onClick={() => setFilter('APPROVED')}>Одобренные</button>
            <button onClick={() => setFilter('REJECTED')}>Отклонённые</button>
          </div>
          <RequestTable
            requests={requests}
            onApprove={onApprove}
            onReject={onReject}
          />
        </section>
      </div>
    </div>
  )
}