import { useCallback, useEffect, useMemo, useState } from 'react'
import { settingsService } from '@/services/settings.service'
import { useAsync } from './useAsync'
import { isEmail, passwordRules } from './validators'

const PROFILE_FIELDS = ['name', 'email', 'whatsapp', 'role', 'address', 'cityState']

const DEFAULT_PREFERENCES = {
  newLeads: true,
  appointmentReminders: true,
  lateCadenceAlerts: true,
  weeklySummaryEmail: false,
}

/**
 * ViewModel da tela de configurações (conta, segurança e preferências).
 *
 * O formulário começa como rascunho nulo e passa a valer o dado carregado do
 * servidor; edições sobrescrevem o rascunho a partir do dado mais recente.
 */
export function useSettingsViewModel() {
  const [tab, setTab] = useState('account')
  const profile = useAsync()
  const execProfile = profile.execute
  const [draft, setDraft] = useState(null)
  const [preferences, setPreferences] = useState(null)
  const [passwords, setPasswords] = useState({ current: '', next: '', confirm: '' })
  const [feedback, setFeedback] = useState(null)
  const [saving, setSaving] = useState(false)

  const loadProfile = useCallback(() => execProfile(() => settingsService.getProfile()), [execProfile])

  useEffect(() => {
    loadProfile()
    settingsService.getPreferences().then((saved) => { if (saved) setPreferences(saved) }).catch(() => {})
  }, [loadProfile])

  const values = useMemo(() => draft ?? profile.data ?? {}, [draft, profile.data])
  const prefs = preferences ?? DEFAULT_PREFERENCES
  const profileData = profile.data

  const setField = useCallback(
    (field, value) => {
      setDraft((prev) => ({ ...(prev ?? profileData ?? {}), [field]: value }))
      setFeedback(null)
    },
    [profileData],
  )

  const togglePreference = useCallback((key) => {
    setPreferences((prev) => {
      const base = prev ?? DEFAULT_PREFERENCES
      return { ...base, [key]: !base[key] }
    })
    setFeedback(null)
  }, [])

  const setPassword = useCallback((key, value) => {
    setPasswords((prev) => ({ ...prev, [key]: value }))
    setFeedback(null)
  }, [])

  const saveAccount = useCallback(async () => {
    setSaving(true)
    setFeedback(null)
    if (!isEmail(values.email ?? '')) {
      setFeedback({ type: 'error', message: 'Informe um e-mail válido.' })
      setSaving(false)
      return
    }
    try {
      const updated = await settingsService.updateProfile(values)
      setDraft(updated)
      setFeedback({ type: 'success', message: 'Dados salvos neste navegador. O backend ainda não oferece atualização de perfil.' })
    } catch (err) {
      setFeedback({ type: 'error', message: err?.message || 'Não foi possível salvar.' })
    } finally {
      setSaving(false)
    }
  }, [values])

  const savePreferences = useCallback(async () => {
    setSaving(true)
    setFeedback(null)
    try {
      await settingsService.updatePreferences(prefs)
      setFeedback({ type: 'success', message: 'Preferências salvas no banco de dados. O envio de notificações automáticas ainda depende do backend.' })
    } catch (err) {
      setFeedback({ type: 'error', message: err?.message || 'Não foi possível salvar as preferências.' })
    } finally {
      setSaving(false)
    }
  }, [prefs])

  const savePassword = useCallback(async () => {
    setSaving(true)
    setFeedback(null)
    if (!passwordRules(passwords.next).valid) {
      setFeedback({ type: 'error', message: 'A nova senha precisa de 8+ caracteres, uma maiúscula e um número.' })
      setSaving(false)
      return
    }
    if (passwords.next !== passwords.confirm) {
      setFeedback({ type: 'error', message: 'As senhas não coincidem.' })
      setSaving(false)
      return
    }
    try {
      await settingsService.updatePassword({
        currentPassword: passwords.current,
        newPassword: passwords.next,
      })
      setPasswords({ current: '', next: '', confirm: '' })
      setFeedback({ type: 'success', message: 'Senha alterada com sucesso.' })
    } catch (err) {
      setFeedback({ type: 'error', message: err?.message || 'Não foi possível alterar a senha.' })
    } finally {
      setSaving(false)
    }
  }, [passwords])

  const save = useCallback(() => {
    if (tab === 'account') return saveAccount()
    if (tab === 'security') return savePassword()
    return savePreferences()
  }, [saveAccount, savePassword, savePreferences, tab])

  return {
    tab,
    setTab,
    values,
    setField,
    preferences: prefs,
    togglePreference,
    passwords,
    setPassword,
    feedback,
    saving,
    loading: profile.loading,
    profileError: profile.error,
    profileFields: PROFILE_FIELDS,
    save,
    reload: loadProfile,
  }
}
