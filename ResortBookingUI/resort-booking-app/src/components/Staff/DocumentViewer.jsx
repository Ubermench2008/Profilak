import React, { useEffect, useState } from 'react'
import { getRequestDocuments } from '../../api/requests'
import '../../styles/DocumentViewer.css'

export default function DocumentViewer({ requestId, onClose }) {
  const [docs, setDocs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getRequestDocuments(requestId)
      .then(res => setDocs(res.data))
      .catch(() => setError('Не удалось загрузить документы'))
      .finally(() => setLoading(false))
  }, [requestId])

  return (
    <div className="modal-backdrop">
      <div className="modal doc-modal">
        <h3>Документы заявки #{requestId}</h3>

        {loading && <p>Загрузка...</p>}
        {error && <p className="error">{error}</p>}

        {!loading && !error && docs.length === 0 && (
          <p>Документы не найдены</p>
        )}

        {!loading && docs.length > 0 && (
          <ul className="download-list">
            {docs.map(doc => (
              <li key={doc.id} className="download-item">
                <span className="file-name">{doc.fileName}</span>
                <a
                  href={`/files/${doc.filePath}`}
                  download
                  className="download-btn"
                >
                  Скачать
                </a>
              </li>
            ))}
          </ul>
        )}

        <button onClick={onClose} className="close-btn">Закрыть</button>
      </div>
    </div>
  )
}