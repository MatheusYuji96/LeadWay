import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { Field, SelectField, TextArea, Checkbox } from '@/components/common/Field'
import { Logo } from '@/components/common/Logo'
import { SuccessContent } from '@/components/common/Modal'
import { Title } from '@/components/common/Title'
import { FUNNEL_STAGES, STATES } from '@/models/enums'
import { mockRepresentatives } from '@/services/mock/fixtures'
import styles from './Wizard.module.css'

const STEPS = ['Dados da empresa', 'Contatos', 'Responsável', 'Qualificação', 'Revisão']

const emptyValues = {
  name: '',
  segment: '',
  site: '',
  city: '',
  state: '',
  size: '',
  origin: '',
  contactName: '',
  contactRole: '',
  contactEmail: '',
  contactPhone: '',
  representativeId: '',
  stage: FUNNEL_STAGES[0],
  estimatedValue: '',
  interest: '',
  budget: '',
  originNote: '',
  observations: '',
  acceptCadence: true,
}

export function CreateCompanyWizard({ lead = null, saving = false, error = null, onClose, onSubmit }) {
  const isEdit = Boolean(lead)
  const [step, setStep] = useState(isEdit ? 4 : 0)
  const [values, setValues] = useState(() =>
    isEdit
      ? {
          ...emptyValues,
          name: lead.name ?? '',
          segment: lead.segment ?? '',
          site: lead.website ?? '',
          city: lead.city ?? '',
          state: lead.state ?? '',
          size: lead.size ?? '',
          origin: lead.origin ?? '',
          representativeId: lead.representativeId ?? '',
          stage: lead.stage ?? FUNNEL_STAGES[0],
          estimatedValue: lead.estimatedValue ?? '',
          observations: lead.observations ?? '',
        }
      : emptyValues,
  )
  const [done, setDone] = useState(false)

  const setField = (field, value) => setValues((prev) => ({ ...prev, [field]: value }))

  const next = () => setStep((prev) => Math.min(prev + 1, STEPS.length - 1))
  const back = () => setStep((prev) => Math.max(prev - 1, 0))

  const submit = async () => {
    const ok = await onSubmit(values, { isEdit, lead })
    if (ok) setDone(true)
  }

  if (done) {
    return (
      <div className={styles.backdrop}>
        <div className={styles.modalPane}>
          <SuccessContent
            title={isEdit ? 'Empresa atualizada' : 'Empresa criada'}
            description={
              isEdit
                ? 'As alterações foram salvas na sua carteira.'
                : 'A empresa já está disponível na sua carteira de leads.'
            }
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
            <strong>Precisa de ajuda?</strong>
            <p>Dúvidas sobre como preencher os dados? Fale com o administrador da sua equipe.</p>
          </div>
        </aside>

        <main className={styles.main}>
          <header className={styles.head}>
            <div>
              <small>Passo {step + 1} de {STEPS.length}</small>
              <Title level={1}>{isEdit ? 'Editar empresa' : 'Criar nova empresa'}</Title>
              <p>{STEPS[step]}</p>
            </div>
            <Button kind="ghost" aria-label="Fechar" onClick={onClose}>
              <Icon name="close" size={16} />
            </Button>
          </header>

          <div className={styles.body}>{renderStep(step, values, setField)}</div>

          {error && <p className={styles.error}>{error}</p>}

          <footer className={styles.footer}>
            <Button onClick={step === 0 ? onClose : back}>{step === 0 ? 'Cancelar' : 'Voltar'}</Button>
            {step < STEPS.length - 1 ? (
              <Button kind="primary" onClick={next}>
                Continuar
              </Button>
            ) : (
              <Button kind="primary" onClick={submit} loading={saving}>
                {isEdit ? 'Salvar alterações' : 'Criar empresa'}
              </Button>
            )}
          </footer>
        </main>
      </div>
    </div>
  )
}

function renderStep(step, values, setField) {
  switch (step) {
    case 0:
      return (
        <div className={styles.form}>
          <Field label="Nome da empresa" placeholder="Ex.: Acme Corporation" value={values.name} onChange={(v) => setField('name', v)} />
          <div className={styles.grid}>
            <SelectField label="Segmento" value={values.segment} onChange={(v) => setField('segment', v)}>
              <option value="">Selecione</option>
              <option>Tecnologia</option>
              <option>Varejo</option>
              <option>Serviços</option>
              <option>Indústria</option>
              <option>Saúde</option>
              <option>Educação</option>
            </SelectField>
            <Field label="Site" placeholder="https://exemplo.com" value={values.site} onChange={(v) => setField('site', v)} />
            <Field label="Cidade" placeholder="Ex.: São Paulo" value={values.city} onChange={(v) => setField('city', v)} />
            <SelectField label="Estado" value={values.state} onChange={(v) => setField('state', v)}>
              <option value="">Selecione</option>
              {STATES.map((uf) => (
                <option key={uf} value={uf}>
                  {uf}
                </option>
              ))}
            </SelectField>
            <SelectField label="Porte da empresa" value={values.size} onChange={(v) => setField('size', v)}>
              <option value="">Selecione</option>
              <option>Pequena</option>
              <option>Média</option>
              <option>Grande</option>
            </SelectField>
            <SelectField label="Origem do lead" value={values.origin} onChange={(v) => setField('origin', v)}>
              <option value="">Selecione</option>
              <option>Indicação</option>
              <option>Inbound</option>
              <option>Outbound</option>
              <option>Evento</option>
            </SelectField>
          </div>
        </div>
      )
    case 1:
      return (
        <div className={styles.form}>
          <Field label="Nome do contato" placeholder="Ex.: Ana Souza" value={values.contactName} onChange={(v) => setField('contactName', v)} />
          <div className={styles.grid}>
            <Field label="Cargo" placeholder="Ex.: Diretora de Compras" value={values.contactRole} onChange={(v) => setField('contactRole', v)} />
            <Field label="E-mail" type="email" placeholder="ana@empresa.com" value={values.contactEmail} onChange={(v) => setField('contactEmail', v)} />
            <Field label="WhatsApp" placeholder="(11) 99999-0000" value={values.contactPhone} onChange={(v) => setField('contactPhone', v)} />
          </div>
        </div>
      )
    case 2:
      return (
        <div className={styles.form}>
          <SelectField
            label="Representante responsável"
            value={values.representativeId}
            onChange={(v) => setField('representativeId', v)}
          >
            <option value="">Selecione um representante</option>
            {mockRepresentatives.map((rep) => (
              <option key={rep.id} value={rep.id}>
                {rep.name} — {rep.code} ({rep.region})
              </option>
            ))}
          </SelectField>
          <Checkbox
            checked={values.acceptCadence}
            onChange={(checked) => setField('acceptCadence', checked)}
            label="Ativar cadência automática de acompanhamento"
            hint="O representante recebe as próximas ações sugeridas conforme a etapa do funil."
          />
        </div>
      )
    case 3:
      return (
        <div className={styles.form}>
          <div className={styles.grid}>
            <SelectField label="Etapa do funil" value={values.stage} onChange={(v) => setField('stage', v)}>
              {FUNNEL_STAGES.map((stage) => (
                <option key={stage} value={stage}>
                  {stage}
                </option>
              ))}
            </SelectField>
            <Field label="Valor estimado (R$)" placeholder="Ex.: 45000" value={values.estimatedValue} onChange={(v) => setField('estimatedValue', v)} />
            <SelectField label="Interesse principal" value={values.interest} onChange={(v) => setField('interest', v)}>
              <option value="">Selecione</option>
              <option>Plano anual</option>
              <option>Plano mensal</option>
              <option>Projeto sob medida</option>
            </SelectField>
            <SelectField label="Orçamento definido" value={values.budget} onChange={(v) => setField('budget', v)}>
              <option value="">Selecione</option>
              <option>Sim</option>
              <option>Não</option>
              <option>Em definição</option>
            </SelectField>
          </div>
          <TextArea label="Observações" placeholder="Contexto adicional sobre a oportunidade..." value={values.observations} onChange={(v) => setField('observations', v)} />
        </div>
      )
    default:
      return (
        <div className={styles.review}>
          <ReviewRow label="Nome da empresa" value={values.name} />
          <ReviewRow label="Segmento" value={values.segment} />
          <ReviewRow label="Cidade/UF" value={[values.city, values.state].filter(Boolean).join(' / ')} />
          <ReviewRow label="Contato principal" value={[values.contactName, values.contactRole].filter(Boolean).join(' — ')} />
          <ReviewRow label="E-mail" value={values.contactEmail} />
          <ReviewRow label="Responsável" value={mockRepresentatives.find((rep) => rep.id === values.representativeId)?.name ?? '—'} />
          <ReviewRow label="Etapa do funil" value={values.stage} />
          <ReviewRow label="Valor estimado" value={values.estimatedValue ? `R$ ${values.estimatedValue}` : '—'} />
          <ReviewRow label="Cadência" value={values.acceptCadence ? 'Ativada' : 'Desativada'} />
        </div>
      )
  }
}

function ReviewRow({ label, value }) {
  return (
    <div className={styles.reviewRow}>
      <span>{label}</span>
      <strong>{value || '—'}</strong>
    </div>
  )
}

export default CreateCompanyWizard
