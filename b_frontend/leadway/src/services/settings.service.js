import { http, unwrap } from './http'

export const settingsService = {
  /**
   * GET /me/profile
   * @returns {Promise<import('@/models/user').UserProfile>}
   */
  async getProfile() {
    return unwrap(await http.get('/me/profile'))
  },

  /**
   * PUT /me/profile
   * @param {Partial<import('@/models/user').UserProfile>} payload
   */
  async updateProfile(payload) {
    return unwrap(await http.put('/me/profile', payload))
  },

  /**
   * PUT /me/preferences
   * @param {import('@/models/user').UserPreferences} preferences
   */
  async updatePreferences(preferences) {
    return unwrap(await http.put('/me/preferences', preferences))
  },

  /**
   * PUT /me/password
   * @param {{ currentPassword: string, newPassword: string }} payload
   */
  async updatePassword(payload) {
    return unwrap(await http.put('/me/password', payload))
  },
}
