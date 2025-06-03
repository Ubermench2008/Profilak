import axios from './index'

export function getStudentSessions() {
  return axios.get('/student/sessions')
}

export function getMyRequests() {
  return axios.get('/student/requests')
}

export function createRequest(sessionId, files) {
  const form = new FormData()
  form.append('sessionId', sessionId)
  files.forEach(file => form.append('files', file))
  return axios.post('/student/requests', form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function payRequest(id) {
  return axios.post(`/student/requests/${id}/pay`)
}

export function getAllRequests(status) {
  const params = status ? { status } : {}
  return axios.get('/staff/requests', { params })
}

export function approveRequest(id) {
  return axios.post(`/staff/requests/${id}/approve`)
}

export function rejectRequest(id) {
  return axios.post(`/staff/requests/${id}/reject`)
}

export function getAllSessionsAdmin() {
  return axios.get('/staff/sessions')
}

export function createSession(startDate, endDate, capacity) {
  return axios.post('/staff/sessions', { startDate, endDate, capacity })
}

export function getRequestDocuments(requestId) {
  return axios.get(`/staff/requests/${requestId}/documents`)
}