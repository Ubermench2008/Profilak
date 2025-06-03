import React from 'react'
import { Routes, Route } from 'react-router-dom'
import LandingPage from '../pages/LandingPage'
import StudentLogin from '../pages/StudentLogin'
import StudentRegister from '../pages/StudentRegister'
import StaffLogin from '../pages/StaffLogin'
import StaffRegister from '../pages/StaffRegister'
import StudentDashboard from '../pages/StudentDashboard'
import StaffDashboard from '../pages/StaffDashboard'

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/student/login" element={<StudentLogin />} />
      <Route path="/student/register" element={<StudentRegister />} />
      <Route path="/staff/login" element={<StaffLogin />} />
      <Route path="/staff/register" element={<StaffRegister />} />
      <Route path="/student/dashboard" element={<StudentDashboard />} />
      <Route path="/staff/dashboard" element={<StaffDashboard />} />  
    </Routes>
  )
}