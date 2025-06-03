import React, { useState } from 'react'
import '../../styles/BookingForm.css'

export default function BookingForm({ sessionId, onSubmit, onClose }) {
  const [files, setFiles] = useState([])
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')

  const handleChange = e => setFiles(Array.from(e.target.files))

  const handleSubmit = async e => {
    e.preventDefault()
    setMessage('')
    setLoading(true)
    try {
      await onSubmit(sessionId, files)
      setMessage('Заявка отправлена успешно')
    } catch (err) {
      const msg = err.response?.data?.error || 'Ошибка при отправке'
      setMessage(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-backdrop">
      <div className="modal booking-modal">
        <h3>Загрузка справок</h3>
        {message && <p className="status">{message}</p>}
        <form onSubmit={handleSubmit}>
          <input type="file" multiple onChange={handleChange} />
          <div className="modal-buttons">
            <button type="submit" disabled={loading}>
              {loading ? 'Отправка...' : 'Отправить'}
            </button>
            <button type="button" onClick={onClose} disabled={loading}>
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}