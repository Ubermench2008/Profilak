import React, { useState } from 'react'
import DocumentViewer from './DocumentViewer'
import '../../styles/StaffDashboard.css'

const statusLabels = {
  PENDING: 'На рассмотрении',
  APPROVED: 'Одобрена',
  REJECTED: 'Отклонена'
}

export default function RequestTable({ requests, onApprove, onReject }) {
  const [selectedId, setSelectedId] = useState(null)

  return (
    <>
      <table className="request-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Студент</th>
            <th>Заезд</th>
            <th>Выезд</th>
            <th>Вместимость</th>
            <th>Статус</th>
            <th>Опл.</th>
            <th>Документы</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {requests.map(r => (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.user.email}</td>
              <td>{r.session.startDate}</td>
              <td>{r.session.endDate}</td>
              <td>{r.session.capacity}</td>
              <td>{statusLabels[r.status]}</td>
              <td>{r.isPaid ? 'Да' : 'Нет'}</td>
              <td>
                <button onClick={() => setSelectedId(r.id)}>Просмотр</button>
              </td>
              <td>
                {r.status === 'PENDING' && (
                  <>
                    <button onClick={() => onApprove(r.id)}>Утвердить</button>
                    <button onClick={() => onReject(r.id)}>Отклонить</button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {selectedId && (
        <DocumentViewer
          requestId={selectedId}
          onClose={() => setSelectedId(null)}
        />
      )}
    </>
  )
}
