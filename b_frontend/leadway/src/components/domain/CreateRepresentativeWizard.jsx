import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { Field, SelectField, Checkbox } from '@/components/common/Field'
import { Logo } from '@/components/common/Logo'
import { SuccessContent } from '@/components/common/Modal'
import { Title } from '@/components/common/Title'
import { REGIONS } from '@/models/enums'
import { isEmail, passwordRules, isPhone11 } from '@/viewmodels/validators'
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
  const [errors, setErrors] = useState({})

  const setField = (field, value) => {
    setValues((prev) => ({ ...prev, [field]: value }))
    // clear validation error for this field as user edits
    setErrors((prev) => {
      if (!prev || !prev[field]) return prev
      const copy = { ...prev }
      delete copy[field]
      return copy
    })
  }

  const validateStep = (s) => {
    const nextErrors = {}
    if (s === 0) {
      if (!values.name || String(values.name).trim().length === 0) nextErrors.name = 'O nome é obrigatório.'
      else if (String(values.name).trim().length < 3) nextErrors.name = 'O nome deve ter ao menos 3 caracteres.'
      if (!values.email || String(values.email).trim().length === 0) nextErrors.email = 'O e-mail é obrigatório.'
      else if (!isEmail(values.email)) nextErrors.email = 'Informe um e-mail válido.'
      if (!values.phone || String(values.phone).replace(/\D/g, '').length === 0) nextErrors.phone = 'O telefone é obrigatório.'
      else if (!isPhone11(values.phone)) nextErrors.phone = 'Informe um telefone com 11 dígitos.'
    }
    if (s === 1) {
      if (!values.code || String(values.code).trim().length === 0) nextErrors.code = 'O código do representante é obrigatório.'
      // region is optional per current rules
    }
    if (s === 2) {
      const pwd = values.password || ''
      const pr = passwordRules(pwd)
      if (!pr.valid) nextErrors.password = 'Senha fraca. Use >=8 chars, maiúscula, número e caractere especial, sem espaços.'
      if ((values.password || '') !== (values.confirmPassword || '')) nextErrors.confirmPassword = 'As senhas não coincidem.'
    }
    return nextErrors
  }

  const next = () => {
    const errs = validateStep(step)
    setErrors((prev) => ({ ...prev, ...errs }))
    if (Object.keys(errs).length === 0) setStep((prev) => Math.min(prev + 1, STEPS.length - 1))
  }
  const back = () => setStep((prev) => Math.max(prev - 1, 0))

  const submit = async () => {
    // basic client-side validation
    const next = {}
    if (!values.name || values.name.trim().length < 3) next.name = 'Informe o nome (mínimo 3 caracteres).'
    if (!isEmail(values.email)) next.email = 'E-mail inválido.'
    if (!isPhone11(values.phone)) next.phone = 'Telefone deve conter 11 dígitos.'
    if (!values.code || values.code.trim().length < 1) next.code = 'Informe o código do representante.'
    // The backend requires a password on Vendedor creation. Do not allow sending without senha.
    const pr = passwordRules(values.password || '')
    if (!pr.valid) next.password = 'Senha fraca. Use >=8 chars, maiúscula, número e caractere especial, sem espaços.'
    if (values.password !== values.confirmPassword) next.confirmPassword = 'As senhas não coincidem.'
    setErrors(next)
    if (Object.keys(next).length) return

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
                <Field
                  label="Nome completo"
                  placeholder="Ex.: Carlos Mendes"
                  value={values.name}
                  onChange={(v) => setField('name', v)}
                  error={errors.name}
                />
                <div className={styles.grid}>
                  <Field
                    label="E-mail"
                    type="email"
                    placeholder="carlos@empresa.com"
                    value={values.email}
                    onChange={(v) => setField('email', v)}
                    error={errors.email}
                  />
                  <Field
                    label="Telefone"
                    placeholder="(11) 99999-0000"
                    value={values.phone}
                    onChange={(v) => setField('phone', v)}
                    error={errors.phone}
                  />
                </div>
              </div>
            )}

            {step === 1 && (
              <div className={styles.form}>
                <div className={styles.grid}>
                  <Field
                    label="Código do representante"
                    placeholder="Ex.: AL-005"
                    value={values.code}
                    onChange={(v) => setField('code', v)}
                    error={errors.code}
                  />
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
                <div>
                  <Field
                    label="Senha"
                    type="password"
                    placeholder="Senha de acesso"
                    value={values.password}
                    onChange={(v) => setField('password', v)}
                    error={errors.password}
                  />
                  <Field
                    label="Confirmar senha"
                    type="password"
                    placeholder="Repita a senha"
                    value={values.confirmPassword}
                    onChange={(v) => setField('confirmPassword', v)}
                    error={errors.confirmPassword}
                  />
                </div>
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
