import axios from './index'

axios.defaults.baseURL = process.env.REACT_APP_API_URL || ''

export function register(email, password, role) {
  return axios.post('/auth/register', { email, password, role })
}

export function login(email, password) {
  return axios.post('/auth/login', { email, password })
}

export function logout() {
  return axios.post('/auth/logout')
}