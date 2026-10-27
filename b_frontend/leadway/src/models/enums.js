/** Etapas do funil de vendas, na ordem do pipeline. */
export const FUNNEL_STAGES = [
  'Prospecção / Novo Lead',
  'Qualificação',
  'Apresentação / Proposta',
  'Negociação',
  'Fechado / Ganho',
]

/** Status de cadência exibidos como badge. */
export const CADENCE_STATUS = {
  ON_TRACK: { label: 'Em dia', tone: 'success' },
  NEXT: { label: 'Próximo contato', tone: 'info' },
  ATTENTION: { label: 'Atenção', tone: 'warning' },
  LATE: { label: 'Atrasado', tone: 'danger' },
}

/** Tipos de compromisso da agenda. */
export const APPOINTMENT_TYPES = ['Contato', 'Visita', 'Reunião', 'Follow-up', 'Orçamento']

/** tons de cor usados em badges, ícones e eventos. */
export const TONES = ['brand', 'success', 'info', 'warning', 'danger', 'neutral', 'blue', 'purple']

/** Regiões comerciais cadastradas. */
export const REGIONS = ['Grande São Paulo', 'ABC', 'Interior de São Paulo', 'Outras regiões']

/** Setores aceitos no cadastro de empresa. */
export const COMPANY_SECTORS = ['Tecnologia e software', 'Serviços']

/** UFs aceitas no formulário (recorte do protótipo). */
export const STATES = ['SP', 'RJ', 'MG']

/** Preferências de notificação do usuário. */
export const PREFERENCE_KEYS = [
  'newLeads',
  'appointmentReminders',
  'lateCadenceAlerts',
  'weeklySummaryEmail',
]
