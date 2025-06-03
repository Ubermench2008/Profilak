import React from 'react'
import '../../styles/Dashboard.css'

const statusLabels = {
  PENDING: 'На рассмотрении',
  APPROVED: 'Одобрена',
  REJECTED: 'Отклонена'
}

export default function RequestList({ requests, onPay }) {
  return (
    <div className="requests">
      <h2>Мои заявки</h2>
      {requests.map(r => (
        <div key={r.id} className="request-item">
          <div>
            <p>С {r.session.startDate} по {r.session.endDate}</p>
            <p>Статус: {statusLabels[r.status]}</p>
            <p>{r.isPaid ? 'Оплачено' : 'Не оплачено'}</p>
          </div>
          {!r.isPaid && r.status === 'APPROVED' && (
            <button onClick={() => onPay(r.id)}>Оплатить</button>
          )}
        </div>
      ))}
    </div>
  )
}