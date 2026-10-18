import axios from 'axios'
import { createMockAdapter } from './mock/createMockAdapter'

const baseURL = (import.meta.env.VITE_API_BASE_URL ?? '').trim()
const rawMock = import.meta.env.VITE_USE_MOCK

/**
 * Modo demonstração ativo quando VITE_USE_MOCK=true ou quando nenhuma
 * VITE_API_BASE_URL foi configurada (padrão seguro em desenvolvimento).
 */
export const isMockMode = rawMock === 'true' || (rawMock !== 'false' && !baseURL)

export const http = axios.create({
  baseURL: baseURL || '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
})

if (isMockMode) {
  http.defaults.adapter = createMockAdapter()
}

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const apiMessage = error.response?.data?.message
    const friendly = apiMessage
      ? new Error(apiMessage)
      : new Error(
          error.code === 'ECONNABORTED'
            ? 'O servidor demorou para responder. Tente novamente.'
            : 'Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.',
        )
    friendly.status = error.response?.status
    friendly.cause = error
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
