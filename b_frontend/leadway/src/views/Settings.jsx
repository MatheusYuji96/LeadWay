import { Button } from '@/components/common/Button'
import { Checkbox, Field } from '@/components/common/Field'
import { ErrorState, InlineMessage, Skeleton } from '@/components/common/Feedback'
import { PageHeading, Tabs } from '@/components/common/PagePrimitives'
import { Panel, PanelHead } from '@/components/common/Panel'
import { useSettingsViewModel } from '@/viewmodels/useSettingsViewModel'
import styles from './Settings.module.css'

const TABS = [
  { value: 'account', label: 'Conta' },
  { value: 'security', label: 'Segurança' },
  { value: 'preferences', label: 'Preferências' },
]

const PROFILE_LABELS = {
  name: 'Nome completo',
  email: 'E-mail',
  whatsapp: 'WhatsApp',
  role: 'Cargo',
  address: 'Endereço',
  cityState: 'Cidade / UF',
}

const PREFERENCE_LABELS = {
  newLeads: { label: 'Novos leads atribuídos', hint: 'Avisa quando uma empresa entrar na sua carteira.' },
  appointmentReminders: { label: 'Lembretes de compromissos', hint: 'Notificação antes de reuniões e visitas.' },
  lateCadenceAlerts: { label: 'Alertas de cadência atrasada', hint: 'Avisa quando um lead ficar sem contato.' },
  weeklySummaryEmail: { label: 'Resumo semanal por e-mail', hint: 'Envia um resumo da carteira toda segunda.' },
}

export function Settings() {
  const vm = useSettingsViewModel()

  if (vm.loading) {
    return (
      <div className={styles.page}>
        <PageHeading eyebrow="Sua conta" title="Configurações" />
        <Skeleton lines={5} />
      </div>
    )
  }

  if (vm.profileError) {
    return (
      <div className={styles.page}>
        <PageHeading eyebrow="Sua conta" title="Configurações" />
        <ErrorState message={vm.profileError} onRetry={vm.reload} />
      </div>
    )
  }

  return (
    <div className={styles.page}>
      <PageHeading
        eyebrow="Sua conta"
        title="Configurações"
        description="Dados pessoais, segurança e preferências de notificação."
        actions={
          <Button kind="primary" loading={vm.saving} onClick={vm.save}>
            {vm.tab === 'security' ? 'Alterar senha' : 'Salvar alterações'}
          </Button>
        }
      />

      <Tabs tabs={TABS} active={vm.tab} onChange={vm.setTab} />

      {vm.feedback && (
        <div className={styles.feedback}>
          <InlineMessage tone={vm.feedback.type === 'success' ? 'success' : 'error'} title={vm.feedback.message} />
        </div>
      )}

      <div className={styles.content}>
        {vm.tab === 'account' && (
          <Panel>
            <PanelHead title="Dados pessoais" subtitle="Informações exibidas no seu perfil." />
            <div className={styles.form}>
              {vm.profileFields.map((field) => (
                <Field
                  key={field}
                  label={PROFILE_LABELS[field] ?? field}
                  value={vm.values[field] ?? ''}
                  onChange={(value) => vm.setField(field, value)}
                />
              ))}
            </div>
          </Panel>
        )}

        {vm.tab === 'security' && (
          <Panel>
            <PanelHead title="Alterar senha" subtitle="Use ao menos 8 caracteres, uma letra maiúscula e um número." />
            <div className={styles.form}>
              <Field
                label="Senha atual"
                type="password"
                autoComplete="current-password"
                value={vm.passwords.current}
                onChange={(value) => vm.setPassword('current', value)}
              />
              <Field
                label="Nova senha"
                type="password"
                autoComplete="new-password"
                value={vm.passwords.next}
                onChange={(value) => vm.setPassword('next', value)}
              />
              <Field
                label="Confirmar nova senha"
                type="password"
                autoComplete="new-password"
                value={vm.passwords.confirm}
                onChange={(value) => vm.setPassword('confirm', value)}
              />
            </div>
          </Panel>
        )}

        {vm.tab === 'preferences' && (
          <Panel>
            <PanelHead title="Preferências de notificação" subtitle="Escolha o que você quer receber." />
            <div className={styles.prefs}>
              {Object.entries(PREFERENCE_LABELS).map(([key, item]) => (
                <label className={styles.prefRow} key={key}>
                  <span>
                    <strong>{item.label}</strong>
                    <small>{item.hint}</small>
                  </span>
                  <Checkbox checked={Boolean(vm.preferences?.[key])} onChange={() => vm.togglePreference(key)} label="" />
                </label>
              ))}
            </div>
          </Panel>
        )}
      </div>
    </div>
  )
}

export default Settings
