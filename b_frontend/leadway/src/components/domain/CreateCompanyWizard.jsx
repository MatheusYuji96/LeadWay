import { useEffect, useState } from 'react'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { Field, SelectField } from '@/components/common/Field'
import { Logo } from '@/components/common/Logo'
import { SuccessContent } from '@/components/common/Modal'
import { Title } from '@/components/common/Title'
import { isMockMode } from '@/services/http'
import { representativesService } from '@/services/representatives.service'
import { lookupService } from '@/services/lookup.service'
import { isEmail, isCNPJ, isCEP, isPhone11 } from '@/viewmodels/validators'
import styles from './Wizard.module.css'

const STEPS = ['Empresa', 'Endereço', 'Responsável e qualificação', 'Revisão']
const initial = {
  cnpj: '', razaoSocial: '', nomeFantasia: '', setorId: '', email: '', telefone: '',
  etapaId: '', potencial: 'ALTO', farol: '', vendedorId: '',
  endereco: { rua: '', cep: '', numero: '', complemento: '', bairro: '', cidade: '', estado: '', pais: 'Brasil' },
}
const digits = (v) => String(v ?? '').replace(/\D/g, '')
const blank = (v) => !String(v ?? '').trim()
const sections = [
  ['cnpj', 'razaoSocial', 'nomeFantasia', 'setorId', 'email', 'telefone'],
  ['endereco.rua', 'endereco.cep', 'endereco.numero', 'endereco.bairro', 'endereco.cidade', 'endereco.estado', 'endereco.pais'],
  ['etapaId', 'potencial', 'vendedorId'],
]
const fieldNames = {
  cnpj: 'CNPJ', razaoSocial: 'Razão social', nomeFantasia: 'Nome fantasia', setorId: 'Setor',
  email: 'E-mail corporativo', telefone: 'Telefone', etapaId: 'Etapa', potencial: 'Potencial', farol: 'Farol',
  vendedorId: 'Vendedor', 'endereco.rua': 'Rua', 'endereco.cep': 'CEP',
  'endereco.numero': 'Número', 'endereco.bairro': 'Bairro', 'endereco.cidade': 'Cidade',
  'endereco.estado': 'Estado', 'endereco.pais': 'País',
}
const valueAt = (v, path) => path.startsWith('endereco.') ? v.endereco[path.slice(9)] : v[path]
const stepFor = (path) => Math.max(0, sections.findIndex((group) => group.includes(path)))

function validate(v, setores, etapas, reps) {
  const errors = {}
  for (const path of ['razaoSocial', 'nomeFantasia', 'setorId', 'email', 'telefone', 'potencial',
    'endereco.rua', 'endereco.cep', 'endereco.numero', 'endereco.bairro', 'endereco.cidade', 'endereco.estado', 'endereco.pais']) {
    if (blank(valueAt(v, path))) errors[path] = `${fieldNames[path]} é obrigatório.`
  }
  if (!isCNPJ(v.cnpj)) errors.cnpj = 'Informe um CNPJ válido, com 14 dígitos verificadores corretos.'
  if (!blank(v.email) && !isEmail(v.email)) errors.email = 'Informe um e-mail válido.'
  if (!blank(v.telefone) && !isPhone11(v.telefone)) errors.telefone = 'O telefone precisa ter 11 números (DDD + número).'
  if (!blank(v.endereco.cep) && !isCEP(v.endereco.cep)) errors['endereco.cep'] = 'Informe um CEP com 8 números.'
  if (!blank(v.setorId) && !setores.some((s) => String(s.id) === String(v.setorId))) errors.setorId = 'Selecione um setor disponível.'
  if (!blank(v.etapaId) && !etapas.some((e) => String(e.id) === String(v.etapaId))) errors.etapaId = 'Selecione uma etapa disponível.'
  if (!blank(v.vendedorId) && !reps.some((r) => String(r.id) === String(v.vendedorId))) errors.vendedorId = 'Selecione um vendedor disponível.'
  return errors
}

function payloadOf(v) {
  return {
    cnpj: digits(v.cnpj), razaoSocial: v.razaoSocial.trim(), nomeFantasia: v.nomeFantasia.trim(),
    setorId: Number(v.setorId), email: v.email.trim(),
    ...(v.etapaId ? { etapaId: Number(v.etapaId) } : {}),
    endereco: {
      rua: v.endereco.rua.trim(), cep: digits(v.endereco.cep), numero: v.endereco.numero.trim(),
      complemento: v.endereco.complemento.trim(), bairro: v.endereco.bairro.trim(),
      cidade: v.endereco.cidade.trim(), estado: v.endereco.estado.trim(), pais: v.endereco.pais.trim(),
    },
    telefone: digits(v.telefone), potencial: v.potencial, farol: v.farol,
    ...(v.vendedorId ? { vendedorId: Number(v.vendedorId) } : {}),
  }
}


// PATCH aceita apenas estes campos no AtualizarLeadRequestDTO do backend.
function editPayloadOf(v, original) {
  const payload = {}
  if (v.email.trim() !== String(original.email ?? '').trim()) payload.email = v.email.trim()
  if (digits(v.telefone) !== digits(original.telefone ?? original.phone)) payload.telefone = digits(v.telefone)
  if (v.potencial !== original.potencial) payload.potencial = v.potencial
  const oldAddress = original.endereco ?? {}
  const endereco = {}
  for (const key of ['rua', 'cep', 'numero', 'complemento', 'bairro', 'cidade', 'estado']) {
    if (String(v.endereco[key] ?? '') !== String(oldAddress[key] ?? '')) {
      endereco[key] = key === 'cep' ? digits(v.endereco[key]) : String(v.endereco[key] ?? '').trim()
    }
  }
  if (Object.keys(endereco).length) payload.endereco = endereco
  if (v.etapaId && String(v.etapaId) !== String(original.etapa?.id ?? original.etapaId ?? '')) payload.etapaId = Number(v.etapaId)
  // Backend tem erro conhecido na atualização de vendedor; não enviar vendedorId.
  return payload
}
function validateEdit(v, setores, etapas, reps) {
  const errors = validate(v, setores, etapas, reps)
  // Campos não editáveis não precisam ser revalidados na edição.
  for (const key of ['cnpj', 'razaoSocial', 'nomeFantasia', 'setorId', 'farol', 'endereco.pais']) delete errors[key]
  return errors
}

export function CreateCompanyWizard({ lead = null, saving = false, error = null, onClose, onSubmit }) {
  const isEdit = Boolean(lead)
  const [step, setStep] = useState(0)
  const [values, setValues] = useState(() => lead ? {
    ...initial, ...lead,
    cnpj: lead.cnpj ?? '', razaoSocial: lead.razaoSocial ?? '',
    nomeFantasia: lead.nomeFantasia ?? lead.name ?? '',
    telefone: lead.telefone ?? lead.phone ?? '',
    setorId: lead.setor?.id ?? lead.setorId ?? '', etapaId: lead.etapa?.id ?? lead.etapaId ?? '',
    vendedorId: lead.vendedor?.id ?? lead.vendedorId ?? '', farol: lead.farol ?? '',
    endereco: { ...initial.endereco, ...(lead.endereco ?? {}) },
  } : structuredClone(initial))
  const [errors, setErrors] = useState({})
  const [notice, setNotice] = useState('')
  const [done, setDone] = useState(false)
  const [setores, setSetores] = useState([])
  const [etapas, setEtapas] = useState([])
  const [reps, setReps] = useState([])
  const [lookupError, setLookupError] = useState('')

  useEffect(() => {
    let alive = true
    Promise.allSettled([lookupService.getSetores(), lookupService.getEtapas(), representativesService.list()])
      .then(([s, e, r]) => {
        if (!alive) return
        if (s.status === 'fulfilled') setSetores(s.value?.items ?? s.value ?? [])
        if (e.status === 'fulfilled') setEtapas(e.value?.items ?? e.value ?? [])
        if (r.status === 'fulfilled') setReps(r.value?.items ?? r.value ?? [])
        if (s.status === 'rejected') setLookupError('Não foi possível carregar os setores. Atualize a página e tente novamente.')
      })
    return () => { alive = false }
  }, [])

  const setField = (path, value) => {
    setValues((prev) => path.startsWith('endereco.')
      ? { ...prev, endereco: { ...prev.endereco, [path.slice(9)]: value } }
      : { ...prev, [path]: value })
    setErrors((prev) => { const next = { ...prev }; delete next[path]; return next })
    setNotice('')
  }
  const validateAndShow = (currentStep = null) => {
    const all = isEdit ? validateEdit(values, setores, etapas, reps) : validate(values, setores, etapas, reps)
    const visible = currentStep === null ? all : Object.fromEntries(Object.entries(all).filter(([k]) => sections[currentStep]?.includes(k)))
    setErrors(all)
    if (Object.keys(visible).length) {
      setNotice('Revise os campos indicados antes de continuar.')
      return false
    }
    setNotice('')
    return true
  }
  const next = () => { if (validateAndShow(step)) setStep((s) => Math.min(s + 1, STEPS.length - 1)) }
  const submit = async () => {
    const all = isEdit ? validateEdit(values, setores, etapas, reps) : validate(values, setores, etapas, reps)
    setErrors(all)
    if (Object.keys(all).length) {
      setNotice('Há campos que precisam de correção. Selecione “Corrigir” ao lado do erro.')
      return
    }
    if (isMockMode && !isEdit) { setNotice('Desative VITE_USE_MOCK para cadastrar no banco de dados.'); return }
    setNotice('')
    try {
      const ok = await onSubmit(isEdit ? editPayloadOf(values, lead) : payloadOf(values), { isEdit, lead })
      if (ok) setDone(true)
    } catch (err) {
      const detail = err?.details ?? err?.response?.data ?? {}
      const mapped = {}
      if (Array.isArray(detail.errors)) detail.errors.forEach((item) => { if (item.field) mapped[item.field] = item.message })
      setErrors((prev) => ({ ...prev, ...mapped }))
      setNotice(err?.status === 500
        ? 'O servidor não conseguiu salvar a empresa. Os dados foram mantidos para uma nova tentativa. Verifique o log do backend para identificar a coluna ou validação que impediu a operação.'
        : (detail.message && detail.message !== 'Bad Request' ? detail.message : err?.message || 'Não foi possível salvar a empresa.'))
    }
  }
  const input = (path, label, placeholder = '', type = 'text') => (
    <Field key={path} label={`${label} *`} placeholder={placeholder} type={type} value={valueAt(values, path)}
      onChange={(v) => setField(path, v)} error={errors[path]} required />
  )
  const select = (path, label, options, required = false) => (
    <div key={path}>
      <SelectField label={`${label}${required ? ' *' : ' (opcional)'}`} value={valueAt(values, path)} onChange={(v) => setField(path, v)}>
        <option value="">Selecione</option>
        {options.map((o) => <option key={o.id} value={o.id}>{o.nome}</option>)}
      </SelectField>
      {errors[path] && <div className={styles.fieldError} role="alert">{errors[path]}</div>}
    </div>
  )
  const review = [
    ['cnpj', 'CNPJ'], ['razaoSocial', 'Razão social'], ['nomeFantasia', 'Nome fantasia'],
    ['setorId', 'Setor', setores.find((s) => String(s.id) === String(values.setorId))?.nome],
    ['email', 'E-mail'], ['telefone', 'Telefone'],
    ...['rua', 'cep', 'numero', 'complemento', 'bairro', 'cidade', 'estado', 'pais'].map((k) => [`endereco.${k}`, fieldNames[`endereco.${k}`] ?? 'Complemento']),
    ['vendedorId', 'Vendedor', reps.find((r) => String(r.id) === String(values.vendedorId))?.nome],
    ['etapaId', 'Etapa', etapas.find((e) => String(e.id) === String(values.etapaId))?.nome ?? 'Etapa inicial (definida pelo backend)'],
    ['potencial', 'Potencial'], ['farol', 'Farol'],
  ]
  if (done) return <div className={styles.backdrop}><div className={styles.modalPane}>
    <SuccessContent title={isEdit ? 'Empresa atualizada' : 'Empresa criada'} description="Dados salvos. A listagem será atualizada." actionLabel="Fechar" onAction={onClose} />
  </div></div>
  return <div className={styles.backdrop}><div className={styles.shell}>
    <aside className={styles.aside}><Logo /><div className={styles.steps}>
      {STEPS.map((label, i) => <span key={label} className={[styles.stepItem, step === i ? styles.current : '', i < step ? styles.done : ''].filter(Boolean).join(' ')}>
        <i>{i < step ? <Icon name="check" size={12} /> : i + 1}</i>{label}
      </span>)}
    </div><div className={styles.help}><strong>Campos obrigatórios</strong><p>Os campos com * seguem as regras de cadastro do backend.</p></div></aside>
    <main className={styles.main}><header className={styles.head}><div><small>Passo {step + 1} de {STEPS.length}</small>
      <Title level={1}>{isEdit ? 'Editar empresa' : 'Criar nova empresa'}</Title><p>{STEPS[step]}</p></div>
      <Button kind="ghost" aria-label="Fechar" onClick={onClose}><Icon name="close" size={16} /></Button>
    </header><div className={styles.body}>
      {step === 0 && <div className={styles.form}>
        <div className={styles.grid}>{input('cnpj', 'CNPJ', '00.000.000/0000-00')}{select('setorId', 'Setor', setores, true)}</div>
        <div className={styles.grid}>{input('razaoSocial', 'Razão social')}{input('nomeFantasia', 'Nome fantasia')}</div>
        <div className={styles.grid}>{input('email', 'E-mail corporativo', 'empresa@exemplo.com', 'email')}{input('telefone', 'Telefone', '(11) 99999-9999')}</div>
        {lookupError && <p className={styles.fieldError} role="alert">{lookupError}</p>}
      </div>}
      {step === 1 && <div className={styles.form}>
        <div className={styles.grid}>{input('endereco.rua', 'Rua')}{input('endereco.numero', 'Número')}</div>
        <div className={styles.grid}>{input('endereco.cep', 'CEP', '00000-000')}{input('endereco.bairro', 'Bairro')}</div>
        <div className={styles.grid}>{input('endereco.cidade', 'Cidade')}
          {input('endereco.estado', 'Estado', 'Ex.: SP ou São Paulo')}
        </div><div className={styles.grid}>{input('endereco.pais', 'País')}
          <Field label="Complemento (opcional)" value={values.endereco.complemento} onChange={(v) => setField('endereco.complemento', v)} /></div>
      </div>}
      {step === 2 && <div className={styles.form}>
        {select('vendedorId', 'Vendedor responsável', reps.map((r) => ({ id: r.id, nome: r.nome ?? r.name ?? r.email })))}
        {select('etapaId', 'Etapa do funil', etapas)}
        <div><SelectField label="Potencial *" value={values.potencial} onChange={(v) => setField('potencial', v)}>
          <option value="ALTO">Alto</option><option value="MEDIO">Médio</option><option value="BAIXO">Baixo</option>
        </SelectField>{errors.potencial && <div className={styles.fieldError} role="alert">{errors.potencial}</div>}</div>
        <div><SelectField label="Farol (opcional)" value={values.farol} onChange={(v) => setField('farol', v)}>
          <option value="">Selecione</option>
          <option value="VERDE">Verde</option>
          <option value="AMARELO">Amarelo</option>
          <option value="VERMELHO">Vermelho</option>
        </SelectField>{errors.farol && <div className={styles.fieldError} role="alert">{errors.farol}</div>}</div>
        <p>Farol é opcional no cadastro. A API atual não permite atualizar Farol, razão social, nome fantasia, setor ou CNPJ pela rota de edição.</p>
      </div>}
      {step === 3 && <div className={styles.review}>
        <p>Confira as informações. Para corrigir um campo, clique em “Corrigir”.</p>
        {review.map(([path, label, display]) => <div key={path} className={styles.reviewRow}>
          <span>{label}</span><strong>{display || valueAt(values, path) || '—'}</strong>
          <button type="button" onClick={() => { setStep(stepFor(path)); setNotice('') }} style={{ cursor: 'pointer', color: 'var(--brand-dark)', background: 'transparent', border: 0 }}>Corrigir</button>
        </div>)}
        {Object.keys(errors).length > 0 && <div role="alert" className={styles.fieldError}>
          <strong>Corrija antes de salvar:</strong>
          {Object.entries(errors).map(([path, message]) => <p key={path}><button type="button" onClick={() => setStep(stepFor(path))} style={{ color: 'inherit', textDecoration: 'underline', cursor: 'pointer', border: 0, background: 'none' }}>{fieldNames[path] ?? path}: {message} — Corrigir</button></p>)}
        </div>}
      </div>}
    </div>
    {(notice || (error && !notice)) && <p className={styles.error} role="alert">{notice || error}</p>}
    <footer className={styles.footer}><Button onClick={step === 0 ? onClose : () => { setStep(step - 1); setNotice('') }}>{step === 0 ? 'Cancelar' : 'Voltar'}</Button>
      {step < 3 ? <Button kind="primary" onClick={next}>Continuar</Button> : <Button kind="primary" onClick={submit} loading={saving}>{isEdit ? 'Atualizar empresa' : 'Criar empresa'}</Button>}
    </footer></main>
  </div></div>
}
export default CreateCompanyWizard
