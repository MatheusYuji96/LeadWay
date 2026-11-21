import axios from 'axios'
import { createLocalApiAdapter } from './localApiAdapter'

const envBase = (import.meta.env.VITE_API_BASE_URL ?? '').trim()
const isDev = import.meta.env.DEV === true
// In development force use of the Vite proxy path '/api' to avoid CORS.
// In production (non-DEV) use VITE_API_BASE_URL when provided, otherwise default to the local backend URL.
const baseURL = envBase || (isDev ? '/api' : 'http://localhost:8080')

// Toggle mock/local adapter via env var VITE_USE_MOCK (string 'true')
export const isMockMode = String(import.meta.env.VITE_USE_MOCK || '').toLowerCase() === 'true'

export const http = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
})

// Use the local adapter only when explicitly requested by env
if (isMockMode) {
  http.defaults.adapter = createLocalApiAdapter()
}

// Envia o token quando o backend disponibiliza autenticação por JWT.
http.interceptors.request.use((config) => {
  if (isMockMode) return config
  try {
    const session = JSON.parse(localStorage.getItem('leadway_session_v1') || 'null')
    const token = session?.token ?? session?.accessToken ?? session?.jwt
    if (token) config.headers.Authorization = `Bearer ${token}`
  } catch { /* sessão ausente ou inválida */ }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    // Distinguish network/timeout errors from HTTP errors and build a
    // user-friendly message while preserving details for diagnostics.
    const resp = error.response
    let friendly
    if (resp) {
      // Try to extract a meaningful message from the server response body
      const data = resp.data || {}
      const serverMessage = data?.message || data?.error ||
        (Array.isArray(data?.errors) ? data.errors.map((e) => e.message || e).join('; ') : null)
      if (serverMessage) {
        friendly = new Error(`HTTP ${resp.status} — ${String(serverMessage)}`)
      } else {
        // fallback to a generic message including HTTP status
        friendly = new Error(`HTTP ${resp.status}`)
      }
      friendly.status = resp.status
      friendly.httpStatus = resp.status
      friendly.details = data
      friendly.cause = error
    } else {
      // Network or timeout (no HTTP response)
      const msg = error.code === 'ECONNABORTED'
        ? 'O servidor demorou para responder. Tente novamente.'
        : 'Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.'
      friendly = new Error(msg)
      friendly.status = undefined
      friendly.details = undefined
      friendly.cause = error
    }
    return Promise.reject(friendly)
  },
)

/**
 * Extrai o payload de uma resposta Axios.
 * @template T
 * @param {import('axios').AxiosResponse<T>} response
 * @returns {T}
 */
export function unwrap(response) {
  return response.data
}
