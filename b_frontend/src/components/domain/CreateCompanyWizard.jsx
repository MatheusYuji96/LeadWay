import { useEffect, useState } from 'react'
import { useSession } from '@/viewmodels/sessionContext'
import { maskCnpj, maskCep, maskPhone } from '@/viewmodels/inputMasks'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { Field, SelectField } from '@/components/common/Field'
import { Logo } from '@/components/common/Logo'
import { SuccessContent } from '@/components/common/Modal'
import { Title } from '@/components/common/Title'
import { isMockMode } from '@/services/http'
import { representativesService } from '@/services/representatives.service'
import { lookupService } from '@/services/lookup.service'
import { contactsService } from '@/services/contacts.service'
import { isEmail, isCNPJ, isCEP, isPhone11 } from '@/viewmodels/validators'
import styles from './Wizard.module.css'

const STEPS = ['Empresa', 'Endereço', 'Responsável e qualificação', 'Contatos', 'Revisão']
const initial = {
  cnpj: '', razaoSocial: '', nomeFantasia: '', setorId: '', email: '', telefone: '', prioridade: '',
  etapaId: '', potencial: '5', vendedorId: '', ativo: true, motivoPerda: '',
  endereco: { rua: '', cep: '', numero: '', complemento: '', bairro: '', cidade: '', estado: '', pais: 'Brasil' },
}
const digits = (v) => String(v ?? '').replace(/\D/g, '')
const blank = (v) => !String(v ?? '').trim()
const sections = [
  ['cnpj', 'razaoSocial', 'nomeFantasia', 'setorId', 'email', 'telefone'],
  ['endereco.rua', 'endereco.cep', 'endereco.numero', 'endereco.bairro', 'endereco.cidade', 'endereco.estado', 'endereco.pais'],
  ['etapaId', 'potencial', 'vendedorId', 'prioridade'],
]
const fieldNames = {
  cnpj: 'CNPJ', razaoSocial: 'Razão social', nomeFantasia: 'Nome fantasia', setorId: 'Setor',
  email: 'E-mail corporativo', telefone: 'Telefone', etapaId: 'Etapa', potencial: 'Potencial',
  vendedorId: 'Vendedor', prioridade: 'Prioridade', 'endereco.rua': 'Rua', 'endereco.cep': 'CEP',
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
  if (!isCNPJ(v.cnpj)) errors.cnpj = digits(v.cnpj).length !== 14
    ? 'O CNPJ precisa conter 14 números.'
    : 'Os dígitos verificadores do CNPJ estão incorretos. Confira o número informado.'
  if (!blank(v.email) && !isEmail(v.email)) errors.email = 'Informe um e-mail válido.'
  if (!blank(v.telefone) && !isPhone11(v.telefone)) errors.telefone = 'O telefone precisa ter 11 números (DDD + número).'
  if (!blank(v.endereco.cep) && !isCEP(v.endereco.cep)) errors['endereco.cep'] = 'Informe um CEP com 8 números.'
  if (!blank(v.setorId) && !setores.some((s) => String(s.id) === String(v.setorId))) errors.setorId = 'Selecione um setor disponível.'
  if (v.prioridade && !['ALTA', 'MEDIA', 'BAIXA'].includes(v.prioridade)) errors.prioridade = 'Selecione uma prioridade válida.'
  if (!blank(v.etapaId) && !etapas.some((e) => String(e.id) === String(v.etapaId))) errors.etapaId = 'Selecione uma etapa disponível.'
  if (!blank(v.vendedorId) && !reps.some((r) => String(r.id) === String(v.vendedorId))) errors.vendedorId = 'Selecione um responsável disponível.'
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
    telefone: digits(v.telefone), potencial: Number(v.potencial),
    ...(v.prioridade ? { prioridade: v.prioridade } : {}),
    ...(v.vendedorId ? { vendedorId: Number(v.vendedorId) } : {}),
  }
}


// PATCH aceita apenas estes campos no AtualizarLeadRequestDTO do backend.
function editPayloadOf(v, original) {
  const payload = {}
  if (v.email.trim() !== String(original.email ?? '').trim()) payload.email = v.email.trim()
  if (digits(v.telefone) !== digits(original.telefone ?? original.phone)) payload.telefone = digits(v.telefone)
  if (Number(v.potencial) !== Number(original.potencial)) payload.potencial = Number(v.potencial)
  if (v.prioridade !== (original.prioridade ?? '')) payload.prioridade = v.prioridade || null
  if (Boolean(v.ativo) !== (original.ativo !== false)) payload.ativo = Boolean(v.ativo)
  if (!v.ativo && v.motivoPerda?.trim() !== String(original.motivoPerda ?? '').trim()) payload.motivoPerda = v.motivoPerda.trim()
  const oldAddress = original.endereco ?? {}
  const endereco = {}
  for (const key of ['rua', 'cep', 'numero', 'complemento', 'bairro', 'cidade', 'estado']) {
    if (String(key === 'cep' ? digits(v.endereco[key]) : v.endereco[key] ?? '') !== String(key === 'cep' ? digits(oldAddress[key]) : oldAddress[key] ?? '')) {
      endereco[key] = key === 'cep' ? digits(v.endereco[key]) : String(v.endereco[key] ?? '').trim()
    }
  }
  if (Object.keys(endereco).length) payload.endereco = endereco
  if (v.etapaId && String(v.etapaId) !== String(original.etapa?.id ?? original.etapaId ?? '')) payload.etapaId = Number(v.etapaId)
  if (
    v.vendedorId &&
    String(v.vendedorId) !==
    String(original.responsavelId ?? original.responsavel?.id ?? original.vendedorId ?? original.vendedor?.id ?? '')
  ) {
    payload.vendedorId = Number(v.vendedorId)
  }
  return payload
}
function validateEdit(v, setores, etapas, reps) {
  const errors = validate(v, setores, etapas, reps)
  // Campos não editáveis não precisam ser revalidados na edição.
  for (const key of ['cnpj', 'razaoSocial', 'nomeFantasia', 'setorId', 'endereco.pais', 'vendedorId']) delete errors[key]
  if (!v.ativo && !String(v.motivoPerda ?? '').trim()) errors.motivoPerda = 'Informe o motivo da perda para desativar o Lead.'
  return errors
}

export function CreateCompanyWizard({ lead = null, initialStep = 0, saving = false, error = null, onClose, onSubmit }) {
  const isEdit = Boolean(lead)
  const { user } = useSession()
  const userId = user?.id ?? user?.usuario?.id ?? user?.user?.id
  const userName = user?.name ?? user?.nome ?? user?.usuario?.nome ?? user?.user?.nome ?? 'Usuário atual'
  const role = String(user?.role ?? user?.cargo?.nome ?? '').toUpperCase()
  const canAssignOthers = role.includes('ADMINISTRADOR') || role.includes('GERENTE')
  const [step, setStep] = useState(initialStep)
  const [contacts, setContacts] = useState([])
  const [contactError, setContactError] = useState('')
  const [contactErrors, setContactErrors] = useState({})
  const [savedLeadId, setSavedLeadId] = useState(null)
  const [values, setValues] = useState(() => lead ? {
    ...initial, ...lead,
    cnpj: maskCnpj(lead.cnpj ?? ''), razaoSocial: lead.razaoSocial ?? '',
    nomeFantasia: lead.nomeFantasia ?? lead.name ?? '',
    telefone: maskPhone(lead.telefone ?? lead.phone ?? ''),
    setorId: lead.setor?.id ?? lead.setorId ?? '', etapaId: lead.etapa?.id ?? lead.etapaId ?? '',
    vendedorId: lead.responsavelId ?? lead.responsavel?.id ?? lead.vendedor?.id ?? lead.vendedorId ?? '', ativo: lead.ativo !== false, motivoPerda: lead.motivoPerda ?? '', potencial: String(lead.potencial ?? 5), prioridade: lead.prioridade ?? '',
    endereco: { ...initial.endereco, ...(lead.endereco ?? {}), cep: maskCep(lead.endereco?.cep ?? '') },
  } : structuredClone(initial))
  const [errors, setErrors] = useState({})
  const [notice, setNotice] = useState('')
  const [httpStatus, setHttpStatus] = useState(null)
  const [done, setDone] = useState(false)
  const [setores, setSetores] = useState([])
  const [etapas, setEtapas] = useState([])
  const [reps, setReps] = useState([])
  const [repsError, setRepsError] = useState('')
  const [lookupError, setLookupError] = useState('')

  useEffect(() => {
    if (!isEdit || !lead?.id || isMockMode) return
    contactsService.listByLead(lead.id).then((items) => setContacts(items.map((item) => ({ ...item, contato: item.contato ?? 'PADRAO' })))).catch((err) => setContactError(err.message))
  }, [isEdit, lead?.id])

  useEffect(() => {
    let alive = true
    Promise.allSettled([lookupService.getSetores(), lookupService.getEtapas(), canAssignOthers ? representativesService.list() : Promise.resolve({ items: [] })])
      .then(([s, e, r]) => {
        if (!alive) return
        if (s.status === 'fulfilled') setSetores(s.value?.items ?? s.value ?? [])
        if (e.status === 'fulfilled') setEtapas(e.value?.items ?? e.value ?? [])
        if (r.status === 'fulfilled') {
          const rows = r.value?.items ?? r.value ?? []
          // O backend deve filtrar os usuários permitidos ao gerente; o frontend não amplia esse escopo.
          setReps(rows)
        } else if (canAssignOthers) setRepsError('Não foi possível consultar os responsáveis disponíveis.')
        if (s.status === 'rejected') setLookupError('Não foi possível carregar os setores. Atualize a página e tente novamente.')
      })
    return () => { alive = false }
  }, [canAssignOthers])

  useEffect(() => {
    // Em novo cadastro, o responsável padrão é sempre quem está autenticado.
    if (!isEdit && userId != null) {
      setValues((prev) => prev.vendedorId ? prev : { ...prev, vendedorId: String(userId) })
    }
  }, [isEdit, userId])

  const eligibleReps = [
    ...(userId != null ? [{ id: userId, nome: userName }] : []),
    ...reps.filter((r) => String(r.id) !== String(userId)),
  ]
  const setField = (path, value) => {
    setValues((prev) => path.startsWith('endereco.')
      ? { ...prev, endereco: { ...prev.endereco, [path.slice(9)]: value } }
      : { ...prev, [path]: value })
    setErrors((prev) => { const next = { ...prev }; delete next[path]; return next })
    setNotice('')
    setHttpStatus(null)
  }
  const validateAndShow = (currentStep = null) => {
    const all = isEdit ? validateEdit(values, setores, etapas, eligibleReps) : validate(values, setores, etapas, eligibleReps)
    const visible = currentStep === null ? all : Object.fromEntries(Object.entries(all).filter(([k]) => sections[currentStep]?.includes(k)))
    setErrors(all)
    if (Object.keys(visible).length) {
      setNotice('Revise os campos indicados antes de continuar.')
      return false
    }
    setNotice('')
    return true
  }
  const next = () => {
    if (step === 3 && !validateContacts()) { setContactError('Corrija os campos destacados em vermelho.'); return }
    if (validateAndShow(step)) setStep((s) => Math.min(s + 1, STEPS.length - 1))
  }
  const submit = async () => {
    const all = isEdit ? validateEdit(values, setores, etapas, eligibleReps) : validate(values, setores, etapas, eligibleReps)
    setErrors(all)
    if (Object.keys(all).length) {
      setNotice('Há campos que precisam de correção. Selecione “Corrigir” ao lado do erro.')
      return
    }
    if (!validateContacts()) { setContactError('Preencha nome, cargo, e-mail válido e WhatsApp com 11 dígitos de todos os contatos.'); setNotice('Há dados de contato inválidos.'); setStep(3); return }
    if (isMockMode && !isEdit) { setNotice('Desative VITE_USE_MOCK para cadastrar no banco de dados.'); return }
    setNotice('')
    setContactError('')
    setHttpStatus(null)
    try {
      const result = savedLeadId ? { id: savedLeadId } : await onSubmit(isEdit ? editPayloadOf(values, lead) : payloadOf(values), { isEdit, lead })
      if (!result) { setNotice('Não foi possível salvar a empresa. Verifique os campos e tente novamente.'); setStep(4); return }
      const leadId = isEdit ? lead?.id : result?.id
      if (leadId) setSavedLeadId(leadId)
      if (contacts.length && !leadId) throw new Error('O backend não retornou o ID da empresa. Não foi possível cadastrar os contatos.')
      for (const item of contacts) {
        if (item._removed && item.id) { await contactsService.remove(leadId, item.id); continue }
        if (item._removed) continue
        const data = { nome: item.nome.trim(), cargo: item.cargo.trim(), email: item.email.trim(), whatsapp: digits(item.whatsapp), contato: item.contato || 'PADRAO', ativo: true }
        if (item.id) await contactsService.update(leadId, item.id, data)
        else { const saved = await contactsService.create(leadId, data); item.id = saved.id }
      }
      setDone(true)
    } catch (err) {
      const detail = err?.details ?? err?.response?.data ?? {}
      const mapped = {}
      const serverErrors = Array.isArray(detail.errors) ? detail.errors : Array.isArray(detail.fieldErrors) ? detail.fieldErrors : []
      serverErrors.forEach((item) => {
        const path = item.field ?? item.propertyPath ?? item.path
        if (path) mapped[String(path).replace(/^request\./, '')] = item.defaultMessage ?? item.message ?? 'Valor inválido.'
      })
      // Alguns backends retornam erros de campo como um objeto { campo: mensagem }.
      if (detail.errors && !Array.isArray(detail.errors) && typeof detail.errors === 'object') {
        Object.entries(detail.errors).forEach(([path, message]) => { mapped[path] = String(message) })
      }
      setErrors((prev) => ({ ...prev, ...mapped }))
      setHttpStatus(err?.status ?? err?.httpStatus ?? null)
      // Erros de contatos são exibidos na própria etapa de contatos.
      if (Object.keys(mapped).some((key) => key.startsWith('contat'))) setContactError('O backend rejeitou um ou mais contatos. Verifique os dados informados.');
      setStep(4)
      setNotice(err?.status === 500
        ? 'O servidor não conseguiu salvar a empresa. Os dados foram mantidos para uma nova tentativa. Verifique o log do backend para identificar a coluna ou validação que impediu a operação.'
        : (detail.message && detail.message !== 'Bad Request' ? detail.message : err?.message || 'Não foi possível salvar a empresa.'))
    }
  }
  const validateContacts = () => {
    const found = {}
    contacts.forEach((c, index) => {
      if (c._removed) return
      if (!String(c.nome ?? '').trim()) found[`${index}.nome`] = 'Nome obrigatório.'
      if (!String(c.cargo ?? '').trim()) found[`${index}.cargo`] = 'Cargo obrigatório.'
      if (!isEmail(c.email)) found[`${index}.email`] = 'Informe um e-mail válido.'
      if (!isPhone11(c.whatsapp)) found[`${index}.whatsapp`] = 'WhatsApp precisa ter 11 números.'
    })
    setContactErrors(found)
    return Object.keys(found).length === 0
  }
  const addContact = () => setContacts((prev) => [...prev, { nome: '', cargo: '', email: '', whatsapp: '', contato: 'PADRAO' }])
  const changeContact = (index, key, value) => {
    setContacts((prev) => prev.map((c, i) => i === index ? { ...c, [key]: key === 'whatsapp' ? maskPhone(value) : value } : c))
    setContactErrors((prev) => { const next = { ...prev }; delete next[`${index}.${key}`]; return next })
    setContactError('')
  }
  const removeContact = (index) => setContacts((prev) => prev.map((c, i) => i === index ? { ...c, _removed: true } : c))
  const input = (path, label, placeholder = '', type = 'text') => (
    <Field key={path} label={`${label} *`} placeholder={placeholder} type={type} value={valueAt(values, path)}
      onChange={(v) => setField(path, path === 'cnpj' ? maskCnpj(v) : path === 'telefone' ? maskPhone(v) : path === 'endereco.cep' ? maskCep(v) : v)} error={errors[path]} required disabled={isEdit && ['cnpj', 'razaoSocial', 'nomeFantasia'].includes(path)} />
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
    ['vendedorId', 'Vendedor', eligibleReps.find((r) => String(r.id) === String(values.vendedorId))?.nome ?? userName],
    ['etapaId', 'Etapa', etapas.find((e) => String(e.id) === String(values.etapaId))?.nome ?? 'Etapa inicial (definida pelo backend)'],
    ['potencial', 'Potencial'], ['prioridade', 'Prioridade'], ...(isEdit ? [['ativo', 'Situação'], ['motivoPerda', 'Motivo da perda']] : []),
  ]
  if (done) return <div className={styles.backdrop}><div className={styles.modalPane}>
    <SuccessContent title={isEdit ? 'Empresa atualizada com sucesso!' : 'Empresa cadastrada com sucesso!'} description="Dados salvos. A listagem será atualizada." actionLabel="Fechar" onAction={onClose} />
  </div></div>
  return <div className={styles.backdrop}><div className={styles.shell}>
    <aside className={styles.aside}><Logo /><div className={styles.steps}>
      {STEPS.map((label, i) => <span key={label} className={[styles.stepItem, step === i ? styles.current : '', i < step ? styles.done : ''].filter(Boolean).join(' ')}>
        <i>{i < step ? <Icon name="check" size={12} /> : i + 1}</i>{label}
      </span>)}
    </div></aside>
    <main className={styles.main}><header className={styles.head}><div><small>Passo {step + 1} de {STEPS.length}</small>
      <Title level={1}>{isEdit ? 'Editar empresa' : 'Criar nova empresa'}</Title><p>{STEPS[step]}</p></div>
      <Button kind="ghost" aria-label="Fechar" onClick={onClose}><Icon name="close" size={16} /></Button>
    </header><div className={styles.body}>
        {step === 0 && <div className={styles.form}>
          <div className={styles.grid}>{input('cnpj', 'CNPJ', '00.000.000/0000-00')}{isEdit ? <Field label="Setor (não editável pela API)" value={lead?.setor?.nome ?? lead?.sector ?? '—'} disabled /> : select('setorId', 'Setor', setores, true)}</div>
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
          <div>
            {canAssignOthers ? (
              <SelectField label="Vendedor responsável *" value={values.vendedorId || String(userId ?? '')} onChange={(v) => setField('vendedorId', v)}>
                {eligibleReps.map((r) => <option key={r.id} value={r.id}>{r.nome ?? r.name ?? r.email}</option>)}
              </SelectField>
            ) : (
              <Field label="Vendedor responsável *" value={isEdit ? (eligibleReps.find((r) => String(r.id) === String(values.vendedorId))?.nome ?? userName) : userName} />
            )}
            {repsError && <p className={styles.fieldError} role="alert">{repsError}</p>}
            {errors.vendedorId && <div className={styles.fieldError} role="alert">{errors.vendedorId}</div>}
          </div>
          {select('etapaId', 'Etapa do funil', etapas)}
          {isEdit && <div><SelectField label="Situação do Lead *" value={values.ativo ? 'true' : 'false'} onChange={(v) => setField('ativo', v === 'true')}><option value="true">Ativo</option><option value="false">Inativo</option></SelectField></div>}
          {isEdit && !values.ativo && <Field label="Motivo da perda *" value={values.motivoPerda ?? ''} onChange={(v) => setField('motivoPerda', v)} error={errors.motivoPerda} />}
          <div><SelectField label="Potencial *" value={values.potencial} onChange={(v) => setField('potencial', v)}>
            <option value="5">5 estrelas</option><option value="4">4 estrelas</option><option value="3">3 estrelas</option><option value="2">2 estrelas</option><option value="1">1 estrela</option>
          </SelectField>{errors.potencial && <div className={styles.fieldError} role="alert">{errors.potencial}</div>}</div>
          <div><SelectField label="Prioridade" value={values.prioridade ?? ''} onChange={(v) => setField('prioridade', v)}>
            <option value="">Não definida</option><option value="ALTA">Alta</option><option value="MEDIA">Média</option><option value="BAIXA">Baixa</option>
          </SelectField>{errors.prioridade && <div className={styles.fieldError} role="alert">{errors.prioridade}</div>}</div>
        </div>}
        {step === 3 && <div className={styles.form}>
          <p>Contatos vinculados a esta empresa. Você pode cadastrar mais de um contato.</p>
          {contactError && <p role="alert" className={styles.fieldError}>{contactError}</p>}
          {contacts.map((contact, index) => contact._removed ? null : <div key={contact.id ?? `novo-${index}`} style={{ border: '1px solid #d9e3e8', borderRadius: 10, padding: 16, marginBottom: 12 }}>
            <div className={styles.grid}><Field label="Nome do contato *" value={contact.nome} error={contactErrors[`${index}.nome`]} onChange={(v) => changeContact(index, 'nome', v)} /><Field label="Cargo *" value={contact.cargo} error={contactErrors[`${index}.cargo`]} onChange={(v) => changeContact(index, 'cargo', v)} /></div>
            <div className={styles.grid}><Field label="E-mail *" type="email" value={contact.email} error={contactErrors[`${index}.email`]} onChange={(v) => changeContact(index, 'email', v)} /><Field label="WhatsApp *" placeholder="(11) 99999-9999" value={maskPhone(contact.whatsapp)} error={contactErrors[`${index}.whatsapp`]} onChange={(v) => changeContact(index, 'whatsapp', v)} /></div>
            <SelectField label="Classificação" value={contact.contato} onChange={(v) => changeContact(index, 'contato', v)}><option value="PADRAO">Padrão</option><option value="SECUNDARIO">Secundário</option><option value="PRINCIPAL">Principal</option></SelectField>
            <Button onClick={() => removeContact(index)}>Remover contato</Button>
          </div>)}
          <Button onClick={addContact}>Adicionar contato</Button>
        </div>}
        {step === 4 && <div className={styles.review}>
          <p>Confira as informações. Para corrigir um campo, clique em “Corrigir”.</p>
          {review.map(([path, label, display]) => <div key={path} className={`${styles.reviewRow} ${errors[path] ? styles.reviewRowInvalid : ''}`}>
            <span>{label}{errors[path] && <small className={styles.reviewError}>{errors[path]}</small>}</span><strong>{display || valueAt(values, path) || '—'}</strong>
            <button type="button" onClick={() => { setStep(stepFor(path)); setNotice('') }} style={{ cursor: 'pointer', color: 'var(--brand-dark)', background: 'transparent', border: 0 }}>Corrigir</button>
          </div>)}
          {contactError && <p className={styles.fieldError} role="alert">Contatos: {contactError} <button type="button" onClick={() => setStep(3)}>Corrigir</button></p>}
          {Object.keys(errors).length > 0 && <div role="alert" className={styles.fieldError}>
            <strong>Corrija antes de salvar:</strong>
            {Object.entries(errors).map(([path, message]) => <p key={path}><button type="button" onClick={() => setStep(stepFor(path))} style={{ color: 'inherit', textDecoration: 'underline', cursor: 'pointer', border: 0, background: 'none' }}>{fieldNames[path] ?? path}: {message} — Corrigir</button></p>)}
          </div>}
        </div>}
      </div>
      {(notice || (error && !notice) || httpStatus) && <p className={styles.error} role="alert">{httpStatus && <strong>HTTP {httpStatus} — </strong>}{notice || error}</p>}
      <footer className={styles.footer}><Button onClick={step === 0 ? onClose : () => { setStep(step - 1); setNotice('') }}>{step === 0 ? 'Cancelar' : 'Voltar'}</Button>
        {step < STEPS.length - 1 ? <Button kind="primary" onClick={next}>Continuar</Button> : <Button kind="primary" onClick={submit} loading={saving}>{isEdit ? 'Atualizar empresa' : 'Criar empresa'}</Button>}
      </footer></main>
  </div></div>
}
export default CreateCompanyWizard
