import React, { useState } from 'react'

export default function SessionForm({ onCreate }) {
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [capacity, setCapacity] = useState(1)
  const [error, setError] = useState('')

  function submit(e) {
    e.preventDefault()
    if (!startDate || !endDate || capacity < 1) {
      setError('Заполните все поля корректно')
      return
    }
    onCreate(startDate, endDate, capacity)
      .catch(() => setError('Не удалось создать сеанс'))
  }

  return (
    <form className="session-form" onSubmit={submit}>
      {error && <p className="error">{error}</p>}
      <label>
        Дата заезда
        <input
          type="date"
          value={startDate}
          onChange={e => setStartDate(e.target.value)}
          required
        />
      </label>
      <label>
        Дата выезда
        <input
          type="date"
          value={endDate}
          onChange={e => setEndDate(e.target.value)}
          required
        />
      </label>
      <label className="field-capacity">
        Вместимость
        <input
          type="number"
          value={capacity}
          min="1"
          onChange={e => setCapacity(+e.target.value)}
          required
        />
      </label>
      <button type="submit">Создать</button>
    </form>
  )
}