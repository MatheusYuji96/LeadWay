import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { Field, SelectField, Checkbox } from '@/components/common/Field'
import { Logo } from '@/components/common/Logo'
import { SuccessContent } from '@/components/common/Modal'
import { Title } from '@/components/common/Title'
import { REGIONS } from '@/models/enums'
import styles from './Wizard.module.css'

const STEPS = ['Dados pessoais', 'Código e região', 'Acesso', 'Revisão']

const emptyValues = {
  name: '',
  email: '',
  phone: '',
  code: '',
  region: '',
  active: true,
  sendInvite: true,
}

export function CreateRepresentativeWizard({ saving = false, error = null, onClose, onSubmit }) {
  const [step, setStep] = useState(0)
  const [values, setValues] = useState(emptyValues)
  const [done, setDone] = useState(false)

  const setField = (field, value) => setValues((prev) => ({ ...prev, [field]: value }))

  const next = () => setStep((prev) => Math.min(prev + 1, STEPS.length - 1))
  const back = () => setStep((prev) => Math.max(prev - 1, 0))

  const submit = async () => {
    const ok = await onSubmit(values)
    if (ok) setDone(true)
  }

  if (done) {
    return (
      <div className={styles.backdrop}>
        <div className={styles.modalPane}>
          <SuccessContent
            title="Representante adicionado!"
            description="O novo representante já pode ser vinculado a empresas da sua carteira."
            actionLabel="Fechar"
            onAction={onClose}
          />
        </div>
      </div>
    )
  }

  return (
    <div className={styles.backdrop}>
      <div className={styles.shell}>
        <aside className={styles.aside}>
          <Logo />
          <div className={styles.steps}>
            {STEPS.map((label, index) => (
              <span
                key={label}
                className={[styles.stepItem, index === step ? styles.current : '', index < step ? styles.done : '']
                  .filter(Boolean)
                  .join(' ')}
              >
                <i>{index < step ? <Icon name="check" size={12} /> : index + 1}</i>
                {label}
              </span>
            ))}
          </div>
          <div className={styles.help}>
            <strong>Convite por e-mail</strong>
            <p>Se marcado, o representante recebe um link para definir a própria senha.</p>
          </div>
        </aside>

        <main className={styles.main}>
          <header className={styles.head}>
            <div>
              <small>
                Passo {step + 1} de {STEPS.length}
              </small>
              <Title level={1}>Novo representante</Title>
              <p>{STEPS[step]}</p>
            </div>
            <Button kind="ghost" aria-label="Fechar" onClick={onClose}>
              <Icon name="close" size={16} />
            </Button>
          </header>

          <div className={styles.body}>
            {step === 0 && (
              <div className={styles.form}>
                <Field label="Nome completo" placeholder="Ex.: Carlos Mendes" value={values.name} onChange={(v) => setField('name', v)} />
                <div className={styles.grid}>
                  <Field label="E-mail" type="email" placeholder="carlos@empresa.com" value={values.email} onChange={(v) => setField('email', v)} />
                  <Field label="Telefone" placeholder="(11) 99999-0000" value={values.phone} onChange={(v) => setField('phone', v)} />
                </div>
              </div>
            )}

            {step === 1 && (
              <div className={styles.form}>
                <div className={styles.grid}>
                  <Field label="Código do representante" placeholder="Ex.: AL-005" value={values.code} onChange={(v) => setField('code', v)} />
                  <SelectField label="Região" value={values.region} onChange={(v) => setField('region', v)}>
                    <option value="">Selecione</option>
                    {REGIONS.map((region) => (
                      <option key={region} value={region}>
                        {region}
                      </option>
                    ))}
                  </SelectField>
                </div>
              </div>
            )}

            {step === 2 && (
              <div className={styles.form}>
                <Checkbox
                  checked={values.active}
                  onChange={(checked) => setField('active', checked)}
                  label="Cadastro ativo"
                  hint="Representantes inativos não recebem novas empresas."
                />
                <Checkbox
                  checked={values.sendInvite}
                  onChange={(checked) => setField('sendInvite', checked)}
                  label="Enviar convite de acesso por e-mail"
                  hint="O convite usa o e-mail informado no primeiro passo."
                />
              </div>
            )}

            {step === 3 && (
              <div className={styles.review}>
                <div className={styles.reviewRow}>
                  <span>Nome</span>
                  <strong>{values.name || '—'}</strong>
                </div>
                <div className={styles.reviewRow}>
                  <span>E-mail</span>
                  <strong>{values.email || '—'}</strong>
                </div>
                <div className={styles.reviewRow}>
                  <span>Telefone</span>
                  <strong>{values.phone || '—'}</strong>
                </div>
                <div className={styles.reviewRow}>
                  <span>Código</span>
                  <strong>{values.code || '—'}</strong>
                </div>
                <div className={styles.reviewRow}>
                  <span>Região</span>
                  <strong>{values.region || '—'}</strong>
                </div>
                <div className={styles.reviewRow}>
                  <span>Status</span>
                  <strong>{values.active ? 'Ativo' : 'Inativo'}</strong>
                </div>
                <div className={styles.reviewRow}>
                  <span>Convite</span>
                  <strong>{values.sendInvite ? 'Enviar por e-mail' : 'Sem convite'}</strong>
                </div>
              </div>
            )}
          </div>

          {error && <p className={styles.error}>{error}</p>}

          <footer className={styles.footer}>
            <Button onClick={step === 0 ? onClose : back}>{step === 0 ? 'Cancelar' : 'Voltar'}</Button>
            {step < STEPS.length - 1 ? (
              <Button kind="primary" onClick={next}>
                Continuar
              </Button>
            ) : (
              <Button kind="primary" onClick={submit} loading={saving}>
                Adicionar representante
              </Button>
            )}
          </footer>
        </main>
      </div>
    </div>
  )
}

export default CreateRepresentativeWizard
