import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field } from '@/components/common/Field'
import { InlineMessage } from '@/components/common/Feedback'
import { Panel } from '@/components/common/Panel'
import styles from './CalConnectCard.module.css'

const API_KEYS_URL = 'https://app.cal.com/settings/developer/api-keys'

/**
 * Convite para o vendedor conectar o Cal.com. É exibido no lugar da agenda enquanto não houver
 * conexão. A API key só trafega do formulário para o backend, que a valida e a guarda.
 */
export function CalConnectCard({ onConnect }) {
  const [apiKey, setApiKey] = useState('')
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)

  const submit = async () => {
    setSaving(true)
    setError(null)
    try {
      await onConnect(apiKey.trim())
    } catch (err) {
      setError(err?.message || 'Não foi possível conectar o Cal.com.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <Panel>
      <div className={styles.card}>
        <h2>Conecte seu Cal.com para usar a agenda</h2>
        <p className={styles.lead}>
          Seus compromissos ficam guardados no Cal.com, sincronizados com o seu calendário (Google, Outlook...).
          O LeadWay mostra os horários livres, cria a reunião e o Cal.com envia o convite ao contato por e-mail.
        </p>
        <ol className={styles.steps}>
          <li>Entre (ou crie sua conta) em cal.com e conecte seu calendário.</li>
          <li>
            Gere uma API key em{' '}
            <a href={API_KEYS_URL} target="_blank" rel="noreferrer">
              Settings &gt; Developer &gt; API Keys
            </a>
            .
          </li>
          <li>Cole a chave abaixo. O LeadWay a valida no Cal.com e a guarda no servidor; ela nunca volta para a tela.</li>
        </ol>
        <div className={styles.form}>
          <Field
            label="API key do Cal.com"
            type="password"
            icon="lock"
            placeholder="cal_live_..."
            value={apiKey}
            onChange={setApiKey}
            autoComplete="off"
          />
          {error && <InlineMessage title="Não foi possível conectar" description={error} />}
          <div>
            <Button kind="primary" onClick={submit} loading={saving} disabled={!apiKey.trim()}>
              Conectar Cal.com
            </Button>
          </div>
        </div>
      </div>
    </Panel>
  )
}

export default CalConnectCard
