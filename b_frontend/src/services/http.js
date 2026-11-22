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
      const data = resp.data || {}
      const status = resp.status
      const defaults = {
        400: 'Os dados enviados são inválidos. Confira os campos e tente novamente.',
        401: 'Sua sessão expirou ou você precisa entrar novamente.',
        403: 'Você não tem permissão para realizar esta operação.',
        404: 'O recurso solicitado não foi encontrado.',
        405: 'Esta operação não é permitida pelo servidor.',
        409: 'Não foi possível concluir porque existem dados conflitantes ou registros vinculados.',
        422: 'Os dados informados não puderam ser processados.',
        423: 'A conta está temporariamente bloqueada.',
        429: 'Muitas tentativas. Aguarde antes de tentar novamente.',
        500: 'O servidor encontrou um erro interno. Tente novamente ou contate o suporte.',
        502: 'O serviço externo não respondeu corretamente.',
        503: 'O serviço está temporariamente indisponível.',
        504: 'O servidor demorou para responder.'
      }
      const validationErrors = Array.isArray(data?.errors)
        ? data.errors.map((item) => item?.message || item?.defaultMessage || String(item)).join('; ')
        : ''
      // 'Internal Server Error' e 'Forbidden' são rótulos HTTP, não explicações.
      const serverMessage = data?.message || data?.detail || validationErrors || ''
      const generic = /^(forbidden|bad request|internal server error|not found|unauthorized|conflict)$/i.test(String(serverMessage).trim())
      const explanation = !generic && String(serverMessage).trim() ? String(serverMessage).trim() : (defaults[status] || 'Não foi possível concluir a solicitação.')
      friendly = new Error(`HTTP ${status} — ${explanation}`)
      friendly.status = status
      friendly.httpStatus = status
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
