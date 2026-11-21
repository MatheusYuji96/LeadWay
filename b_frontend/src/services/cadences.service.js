import { http, unwrap } from './http'

export const cadencesService = {
  list: async () => unwrap(await http.get('/cadencias')),
  create: async (payload) => unwrap(await http.post('/cadencias', payload)),
  update: async (id, payload) => unwrap(await http.patch(`/cadencias/${id}`, payload)),
}
