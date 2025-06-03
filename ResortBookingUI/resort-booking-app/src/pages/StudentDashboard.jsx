import React, { useEffect, useState } from 'react'
import {
  getStudentSessions,
  getMyRequests,
  createRequest,
  payRequest
} from '../api/requests'
import SessionCard from '../components/Student/SessionCard'
import BookingForm from '../components/Student/BookingForm'
import RequestList from '../components/Student/RequestList'
import '../styles/Dashboard.css'

export default function StudentDashboard() {
  const [sessions, setSessions] = useState([])
  const [requests, setRequests] = useState([])
  const [bookingSession, setBookingSession] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    refreshAll()
  }, [])

  function refreshAll() {
    getStudentSessions()
      .then(r => setSessions(r.data))
      .catch(() => setError('Не удалось загрузить сеансы'))
    getMyRequests()
      .then(r => setRequests(r.data))
      .catch(() => setError('Не удалось загрузить заявки'))
  }

  function handleBook(id) {
    setError('')
    setBookingSession(id)
  }

  function handleSubmit(sessionId, files) {
    createRequest(sessionId, files)
      .then(() => {
        setBookingSession(null)
        refreshAll()
      })
      .catch(err => {
        const msg = err.response?.data?.error || 'Не удалось отправить заявку'
        setError(msg)
      })
  }

  function handlePay(id) {
    payRequest(id)
      .then(() => refreshAll())
      .catch(err => {
        const msg = err.response?.data?.error || 'Не удалось оплатить заявку'
        setError(msg)
      })
  }

  const requestedIds = new Set(requests.map(r => r.session.id))

  return (
    <div className="dashboard-page">
      <div className="dashboard-container">
        <h1>Добро пожаловать</h1>
        {error && <p className="error">{error}</p>}

        <section className="sessions-section">
          <h2>Доступные заезды</h2>
          {sessions.length === 0 ? (
            <p>Нет доступных сеансов</p>
          ) : (
            <div className="sessions-list">
              {sessions.map(s => (
                <SessionCard
                  key={s.id}
                  session={s}
                  requested={requestedIds.has(s.id)}
                  onBook={handleBook}
                />
              ))}
            </div>
          )}
        </section>

        <section className="requests-section">
          <h2>Мои заявки</h2>
          {requests.length === 0 ? (
            <p>У вас ещё нет заявок</p>
          ) : (
            <RequestList requests={requests} onPay={handlePay} />
          )}
        </section>

        {bookingSession && (
          <BookingForm
            sessionId={bookingSession}
            onSubmit={handleSubmit}
            onClose={() => setBookingSession(null)}
          />
        )}
      </div>
    </div>
  )
}