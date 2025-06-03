import React from 'react'
import { Link } from 'react-router-dom'
import '../styles/LandingPage.css'

export default function LandingPage() {
  return (
    <div className="landing-container">
      <h1 className="landing-title">Сайт записи в профилакторий</h1>
      <p className="landing-subtitle">Записаться на сеанс санатория просто и удобно</p>

      <div className="landing-panels">
        <div className="landing-panel">
          <h2 className="panel-title">Студент НГУ</h2>
          <Link to="/student/register" className="panel-button">
            Зарегистрироваться
          </Link>
          <Link to="/student/login" className="panel-button">
            Войти
          </Link>
        </div>

        <div className="landing-panel">
          <h2 className="panel-title">Персонал</h2>
          <Link to="/staff/register" className="panel-button">
            Зарегистрироваться
          </Link>
          <Link to="/staff/login" className="panel-button">
            Войти
          </Link>
        </div>
      </div>
    </div>
  )
}