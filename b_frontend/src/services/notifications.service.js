import { http, unwrap } from './http'

export const notificationsService = {
  /**
   * GET /notifications
   * @returns {Promise<{ items: import('@/models/notification').AppNotification[], unread: number }>}
   */
  async list() {
    return unwrap(await http.get('/notifications'))
  },
}
