import React from 'react'

export default function SessionCard({ session, onBook, requested }) {
  const full = session.bookedCount >= session.capacity
  let label, disabled
  if (requested) {
    label = 'Вы уже отправляли заявку'
    disabled = true
  } else if (full) {
    label = 'Мест нет'
    disabled = true
  } else {
    label = 'Записаться'
    disabled = false
  }
  return (
    <div className="session-card">
      <div className="session-dates">
        <span>{session.startDate}</span>
        <span>—</span>
        <span>{session.endDate}</span>
      </div>
      <p>Мест: {session.bookedCount}/{session.capacity}</p>
      <button disabled={disabled} onClick={() => !disabled && onBook(session.id)}>
        {label}
      </button>
    </div>
  )
}
