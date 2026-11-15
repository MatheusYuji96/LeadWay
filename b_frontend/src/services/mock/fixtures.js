/**
 * Fixtures de demonstração derivadas do protótipo Figma Make.
 *
 * IMPORTANT: estes dados NÃO vêm de backend. Elos existem apenas para
 * viabilizar o desenvolvimento da UI enquanto a API (Spring/MySQL) não
 * está disponível. Em produção, defina VITE_API_BASE_URL e
 * VITE_USE_MOCK=false e os services passarão a consumir a API real.
 */

export const mockUser = {
  id: 'user-1',
  name: 'Lucas Martins',
  email: 'lucas@leadway.com.br',
  role: 'Supervisor',
  initials: 'LM',
}

export const mockProfile = {
  name: 'Lucas Martins',
  email: 'lucas@leadway.com.br',
  whatsapp: '(11) 99999-9999',
  role: 'Supervisor comercial',
  address: 'Av. Paulista, 1000',
  cityState: 'São Paulo, SP',
  initials: 'LM',
}

export const mockPreferences = {
  newLeads: true,
  appointmentReminders: true,
  lateCadenceAlerts: true,
  weeklySummaryEmail: false,
}

export const mockRepresentatives = [
  {
    id: 'rep-1',
    name: 'João Silva',
    code: 'VEN-0001',
    region: 'Grande São Paulo',
    email: 'joao.silva@leadway.com',
    phone: '(11) 99876-1200',
    initials: 'JS',
    leads: 32,
    pending: 3,
    contacts: 21,
    address: 'Rua das Flores, 210 — São Paulo, SP',
    since: '2023-08-15',
    active: true,
  },
  {
    id: 'rep-2',
    name: 'Maria Souza',
    code: 'VEN-0002',
    region: 'ABC',
    email: 'maria.souza@leadway.com',
    phone: '(11) 99123-4567',
    initials: 'MS',
    leads: 27,
    pending: 2,
    contacts: 19,
    address: 'Rua das Acácias, 88 — Santo André, SP',
    since: '2024-02-01',
    active: true,
  },
  {
    id: 'rep-3',
    name: 'Carlos Oliveira',
    code: 'VEN-0003',
    region: 'Grande São Paulo',
    email: 'carlos.oliveira@leadway.com',
    phone: '(11) 99456-7890',
    initials: 'CO',
    leads: 29,
    pending: 6,
    contacts: 18,
    address: 'Av. Rebouças, 900 — São Paulo, SP',
    since: '2023-11-20',
    active: true,
  },
  {
    id: 'rep-4',
    name: 'Ana Santos',
    code: 'VEN-0004',
    region: 'Outras regiões',
    email: 'ana.santos@leadway.com',
    phone: '(11) 99789-0123',
    initials: 'AS',
    leads: 24,
    pending: 1,
    contacts: 17,
    address: 'Rua Vieira de Morais, 450 — São Paulo, SP',
    since: '2024-06-10',
    active: true,
  },
]

export const mockCompanies = [
  {
    id: 'lead-1',
    name: 'TechNova Sistemas',
    cnpj: '42.987.654/0001-10',
    sector: 'Tecnologia e software',
    score: 5,
    stage: 'Follow-up',
    representativeId: 'rep-1',
    representativeName: 'João Silva',
    lastContact: 'Hoje, 09:30',
    nextAction: 'Reunião • amanhã',
    cadenceStatus: 'ON_TRACK',
    phone: '(11) 3456-7890',
    whatsapp: '(11) 99876-4321',
    email: 'contato@technova.com.br',
    address: {
      street: 'Av. Paulista',
      number: '1374',
      complement: '8º andar',
      neighborhood: 'Bela Vista',
      city: 'São Paulo',
      state: 'SP',
      zipCode: '01310-100',
    },
    enteredStageAt: '2025-05-11',
    daysInStage: 4,
  },
  {
    id: 'lead-2',
    name: 'Alpha Solutions',
    cnpj: '18.224.901/0001-55',
    sector: 'Serviços',
    score: 4,
    stage: 'Portifólio',
    representativeId: 'rep-2',
    representativeName: 'Maria Souza',
    lastContact: 'Ontem, 15:42',
    nextAction: 'Ligação • 16 mai',
    cadenceStatus: 'NEXT',
    phone: '(11) 3210-4455',
    whatsapp: '(11) 99221-7788',
    email: 'contato@alphasolutions.com.br',
    address: {
      street: 'Av. Brigadeiro Faria Lima',
      number: '2927',
      complement: 'Sala 110',
      neighborhood: 'Itaim Bibi',
      city: 'São Paulo',
      state: 'SP',
      zipCode: '04538-133',
    },
    enteredStageAt: '2025-05-06',
    daysInStage: 9,
  },
  {
    id: 'lead-3',
    name: 'PrimeLog Transportes',
    cnpj: '55.103.772/0001-08',
    sector: 'Serviços',
    score: 3,
    stage: 'Qualificação',
    representativeId: 'rep-3',
    representativeName: 'Carlos Oliveira',
    lastContact: 'Há 8 dias',
    nextAction: 'Contatar hoje',
    cadenceStatus: 'LATE',
    phone: '(11) 4002-8922',
    whatsapp: '(11) 99880-1234',
    email: 'comercial@primelog.com.br',
    address: {
      street: 'Rod. Anhanguera, km 21',
      number: 'S/N',
      complement: 'Galpão 3',
      neighborhood: 'Distrito Industrial',
      city: 'Guarulhos',
      state: 'SP',
      zipCode: '07000-000',
    },
    enteredStageAt: '2025-04-28',
    daysInStage: 17,
  },
  {
    id: 'lead-4',
    name: 'Nova Saúde Empresarial',
    cnpj: '30.887.441/0001-72',
    sector: 'Serviços',
    score: 5,
    stage: 'Visita/Orçamento',
    representativeId: 'rep-4',
    representativeName: 'Ana Santos',
    lastContact: '12 mai, 11:20',
    nextAction: 'Orçamento • 17 mai',
    cadenceStatus: 'ON_TRACK',
    phone: '(11) 3888-1200',
    whatsapp: '(11) 99010-2233',
    email: 'contato@novasaude.com.br',
    address: {
      street: 'Av. Eng. Luís Carlos Berrini',
      number: '1681',
      complement: '12º andar',
      neighborhood: 'Cidade Monções',
      city: 'São Paulo',
      state: 'SP',
      zipCode: '04571-010',
    },
    enteredStageAt: '2025-05-09',
    daysInStage: 6,
  },
  {
    id: 'lead-5',
    name: 'BlueTech Consultoria',
    cnpj: '77.554.310/0001-19',
    sector: 'Tecnologia e software',
    score: 4,
    stage: 'Primeiro Contato',
    representativeId: 'rep-1',
    representativeName: 'João Silva',
    lastContact: '10 mai, 14:10',
    nextAction: 'WhatsApp • hoje',
    cadenceStatus: 'ATTENTION',
    phone: '(11) 3777-9090',
    whatsapp: '(11) 99333-5566',
    email: 'contato@bluetechconsultoria.com.br',
    address: {
      street: 'Rua Fidêncio Ramos',
      number: '195',
      complement: '5º andar',
      neighborhood: 'Vila Olímpia',
      city: 'São Paulo',
      state: 'SP',
      zipCode: '04551-010',
    },
    enteredStageAt: '2025-05-10',
    daysInStage: 5,
  },
]

export const mockContacts = [
  {
    id: 'contact-1',
    name: 'Mariana Costa',
    role: 'Diretora de Operações',
    email: 'mariana@technova.com.br',
    whatsapp: '(11) 99742-1180',
    companyId: 'lead-1',
    companyName: 'TechNova Sistemas',
    initials: 'MC',
    status: 'active',
  },
  {
    id: 'contact-2',
    name: 'Roberto Lima',
    role: 'Coordenador de Compras',
    email: 'roberto@technova.com.br',
    whatsapp: '(11) 99618-3042',
    companyId: 'lead-1',
    companyName: 'TechNova Sistemas',
    initials: 'RL',
    status: 'active',
  },
  {
    id: 'contact-3',
    name: 'Fernanda Alves',
    role: 'Head Comercial',
    email: 'fernanda@alphasolutions.com.br',
    whatsapp: '(11) 99453-8820',
    companyId: 'lead-2',
    companyName: 'Alpha Solutions',
    initials: 'FA',
    status: 'pending',
  },
  {
    id: 'contact-4',
    name: 'Gustavo Mendes',
    role: 'Gerente de Logística',
    email: 'gustavo@primelog.com.br',
    whatsapp: '(11) 99100-1203',
    companyId: 'lead-3',
    companyName: 'PrimeLog Transportes',
    initials: 'GM',
    status: 'active',
  },
]

/** Contatos extra vinculados a uma empresa (recorte do protótipo). */
export const mockLeadContacts = {
  'lead-1': ['contact-1', 'contact-2', 'contact-3'],
}

export const mockTimeline = {
  'lead-1': [
    {
      id: 'note-1',
      dateLabel: 'Hoje, 09:30',
      title: 'Follow-up realizado via WhatsApp',
      author: 'João Silva conversou com Mariana Costa',
      description: 'Retorno solicitado para sexta-feira.',
      icon: 'phone',
      status: 'pending',
    },
    {
      id: 'note-2',
      dateLabel: '12 mai, 14:20',
      title: 'Catálogo enviado por e-mail',
      author: 'João Silva • E-mail',
      description: 'Apresentação comercial e cases do segmento.',
      icon: 'mail',
      status: 'done',
    },
    {
      id: 'note-3',
      dateLabel: '08 mai, 10:00',
      title: 'Reunião de descoberta',
      author: 'João Silva com Mariana Costa',
      description: 'Necessidade de centralizar a gestão das filiais.',
      icon: 'users',
      status: 'done',
    },
  ],
}

export const mockLeadFunnel = {
  'lead-1': { currentStage: 'Follow-up', daysInCurrentStage: 4, enteredStageAt: '2025-05-11' },
}

export const mockLeadCadence = {
  'lead-1': {
    status: 'ON_TRACK',
    lastContactLabel: 'Hoje',
    nextContactLabel: 'Em 2 dias',
    progress: 58,
    nextAction: {
      title: 'Reunião de proposta',
      whenLabel: 'Sexta, 17 mai • 10:00',
    },
    plan: [
      { title: 'Contato inicial', state: 'complete', whenLabel: 'Concluído' },
      { title: 'Envio de apresentação', state: 'complete', whenLabel: 'Concluído' },
      { title: 'Follow-up', state: 'active', whenLabel: 'Hoje' },
      { title: 'Reunião de proposta', state: 'todo', whenLabel: '17 mai' },
    ],
  },
}

export const mockLeadStats = {
  total: 128,
  prospecting: 46,
  followUp: 24,
  needAttention: 12,
}

export const mockDashboardStats = [
  { key: 'total', icon: 'building', label: 'Total de leads', value: '128', detail: 'desde o mês passado', delta: '↑ 8%', tone: 'brand' },
  { key: 'appointments', icon: 'calendar', label: 'Compromissos', value: '18', detail: 'nesta semana', delta: '↑ 5%', tone: 'blue' },
  { key: 'prospecting', icon: 'trend', label: 'Em prospecção', value: '46', detail: 'em andamento', delta: '↑ 3%', tone: 'purple' },
  { key: 'stale', icon: 'bell', label: 'Sem atualização', value: '7', detail: 'há mais de 7 dias', delta: '↓ 2', tone: 'danger' },
]

export const mockTodayAppointments = [
  { id: 'ap-1', time: '09:30', type: 'Reunião', companyName: 'TechNova Sistemas', contactName: 'Mariana Costa', tone: 'brand' },
  { id: 'ap-2', time: '11:00', type: 'Visita', companyName: 'Nova Saúde Empresarial', contactName: 'Roberto Lima', tone: 'blue' },
  { id: 'ap-3', time: '14:30', type: 'Follow-up', companyName: 'Alpha Solutions', contactName: 'Fernanda Alves', tone: 'warning' },
]

export const mockWeekActivity = [
  { day: 'Dom', contacts: 30, meetings: 35, followUps: 40 },
  { day: 'Seg', contacts: 70, meetings: 35, followUps: 50 },
  { day: 'Ter', contacts: 55, meetings: 60, followUps: 40 },
  { day: 'Qua', contacts: 85, meetings: 48, followUps: 65 },
  { day: 'Qui', contacts: 68, meetings: 75, followUps: 52 },
  { day: 'Sex', contacts: 42, meetings: 30, followUps: 70 },
  { day: 'Sáb', contacts: 35, meetings: 42, followUps: 30 },
]

export const mockTeamStats = [
  { key: 'reps', icon: 'users', label: 'Representantes ativos', value: '12', detail: '100% da equipe', delta: '↑ 1', tone: 'brand' },
  { key: 'wallet', icon: 'briefcase', label: 'Leads em carteira', value: '286', detail: '12 novos este mês', delta: '↑ 4%', tone: 'blue' },
  { key: 'contacts', icon: 'check', label: 'Contatos realizados', value: '164', detail: 'esta semana', delta: '↑ 9%', tone: 'purple' },
  { key: 'pending', icon: 'bell', label: 'Pendências da equipe', value: '22', detail: 'exigem atenção', delta: '↓ 3', tone: 'danger' },
]

export const mockTeamPerformance = [
  { name: 'João', contacts: 84, meetings: 18, visits: 12 },
  { name: 'Maria', contacts: 76, meetings: 16, visits: 10 },
  { name: 'Carlos', contacts: 68, meetings: 12, visits: 8 },
  { name: 'Ana', contacts: 72, meetings: 14, visits: 11 },
]

export const mockStaleLeads = [
  { id: 'lead-3', name: 'PrimeLog Transportes', representativeName: 'Carlos Oliveira', stage: 'Qualificação', days: 8 },
  { id: 'lead-4', name: 'Nova Saúde Empresarial', representativeName: 'Ana Santos', stage: 'Visita/Orçamento', days: 11 },
  { id: 'lead-5', name: 'BlueTech Consultoria', representativeName: 'João Silva', stage: 'Primeiro Contato', days: 14 },
]

export const mockTeamPageStats = [
  { key: 'reps', icon: 'users', label: 'Representantes', value: '12', detail: 'todos ativos', delta: '↑ 1', tone: 'brand' },
  { key: 'distributed', icon: 'briefcase', label: 'Leads distribuídos', value: '286', detail: '23,8 por pessoa', delta: '↑ 4%', tone: 'blue' },
  { key: 'contacts', icon: 'trend', label: 'Contatos no mês', value: '614', detail: '12% de aumento', delta: '↑ 12%', tone: 'purple' },
  { key: 'pending', icon: 'bell', label: 'Pendências', value: '22', detail: 'em 4 carteiras', delta: '↓ 3', tone: 'danger' },
]

export const mockTeamDetail = {
  'rep-1': {
    notice: {
      title: 'Este representante possui 3 leads pendentes.',
      description: 'Existem oportunidades sem atualização há mais de 7 dias.',
    },
    stats: [
      { key: 'wallet', icon: 'briefcase', label: 'Leads em carteira', value: '32', detail: '4 novos este mês', delta: '↑ 4%', tone: 'brand' },
      { key: 'contacts', icon: 'phone', label: 'Contatos realizados', value: '84', detail: 'nos últimos 30 dias', delta: '↑ 10%', tone: 'blue' },
      { key: 'visits', icon: 'calendar', label: 'Visitas', value: '12', detail: '8 concluídas', delta: '↑ 2', tone: 'purple' },
      { key: 'conversions', icon: 'trend', label: 'Conversões', value: '7', detail: '21,8% de conversão', delta: '↑ 1,2%', tone: 'warning' },
    ],
    evolution: [
      { month: 'Dez', value: 35 },
      { month: 'Jan', value: 48 },
      { month: 'Fev', value: 42 },
      { month: 'Mar', value: 65 },
      { month: 'Abr', value: 72 },
      { month: 'Mai', value: 85 },
    ],
  },
}

/**
 * Compromissos de maio/2025 usados pela agenda (mês, semana e dia).
 * data base do protótipo: 15/05/2025 (quinta-feira).
 */
export const mockAppointments = [
  { id: 'evt-1', date: '2025-05-02', startTime: '09:00', endTime: '10:00', title: 'Visita', type: 'Visita', companyId: 'lead-4', companyName: 'Nova Saúde Empresarial', contactName: 'Roberto Lima', tone: 'blue', representativeId: 'rep-1', location: 'São Paulo — SP' },
  { id: 'evt-2', date: '2025-05-05', startTime: '14:00', endTime: '14:30', title: 'Follow-up', type: 'Follow-up', companyId: 'lead-5', companyName: 'BlueTech Consultoria', contactName: 'Fernanda Alves', tone: 'warning', representativeId: 'rep-1' },
  { id: 'evt-3', date: '2025-05-08', startTime: '10:30', endTime: '11:30', title: 'Reunião', type: 'Reunião', companyId: 'lead-2', companyName: 'Alpha Solutions', contactName: 'Fernanda Alves', tone: 'purple', representativeId: 'rep-1' },
  { id: 'evt-4', date: '2025-05-12', startTime: '09:00', endTime: '09:30', title: 'Contato', type: 'Contato', companyId: 'lead-3', companyName: 'PrimeLog Transportes', contactName: 'Gustavo Mendes', tone: 'brand', representativeId: 'rep-1' },
  { id: 'evt-5', date: '2025-05-12', startTime: '15:30', endTime: '16:30', title: 'Orçamento', type: 'Orçamento', companyId: 'lead-4', companyName: 'Nova Saúde Empresarial', contactName: 'Roberto Lima', tone: 'warning', representativeId: 'rep-1' },
  { id: 'evt-6', date: '2025-05-15', startTime: '09:30', endTime: '10:15', title: 'Contato de qualificação', type: 'Contato', companyId: 'lead-1', companyName: 'TechNova Sistemas', contactName: 'Mariana Costa • Diretora de Operações', detail: 'Ligação para validar necessidades e próximos passos.', tone: 'brand', representativeId: 'rep-1', icon: 'phone' },
  { id: 'evt-7', date: '2025-05-15', startTime: '11:00', endTime: '12:00', title: 'Visita comercial', type: 'Visita', companyId: 'lead-4', companyName: 'Nova Saúde Empresarial', contactName: 'Roberto Lima • Coordenador de Compras', detail: 'Av. Paulista, 1374 — São Paulo, SP', tone: 'blue', representativeId: 'rep-1', icon: 'map', location: 'Av. Paulista, 1374 — São Paulo, SP' },
  { id: 'evt-8', date: '2025-05-15', startTime: '14:30', endTime: '15:00', title: 'Follow-up de proposta', type: 'Follow-up', companyId: 'lead-2', companyName: 'Alpha Solutions', contactName: 'Fernanda Alves • Head Comercial', detail: 'Retorno via WhatsApp sobre a proposta enviada.', tone: 'warning', representativeId: 'rep-1', icon: 'phone' },
  { id: 'evt-9', date: '2025-05-16', startTime: '16:00', endTime: '16:30', title: 'Contato', type: 'Contato', companyId: 'lead-5', companyName: 'BlueTech Consultoria', contactName: 'Fernanda Alves', tone: 'brand', representativeId: 'rep-1' },
  { id: 'evt-10', date: '2025-05-17', startTime: '10:00', endTime: '11:00', title: 'Reunião', type: 'Reunião', companyId: 'lead-3', companyName: 'PrimeLog Transportes', contactName: 'Gustavo Mendes', tone: 'purple', representativeId: 'rep-1' },
  { id: 'evt-11', date: '2025-05-21', startTime: '13:30', endTime: '14:30', title: 'Visita', type: 'Visita', companyId: 'lead-4', companyName: 'Nova Saúde Empresarial', contactName: 'Roberto Lima', tone: 'blue', representativeId: 'rep-1' },
  { id: 'evt-12', date: '2025-05-26', startTime: '11:00', endTime: '11:30', title: 'Follow-up', type: 'Follow-up', companyId: 'lead-1', companyName: 'TechNova Sistemas', contactName: 'Mariana Costa', tone: 'brand', representativeId: 'rep-1' },
]

export const mockAgendaOverview = [
  { key: 'today', icon: 'calendar', tone: 'brand', label: 'HOJE', value: '3 compromissos' },
  { key: 'next', icon: 'clock', tone: 'blue', label: 'PRÓXIMO', value: '09:30 • TechNova' },
  { key: 'attention', icon: 'bell', tone: 'warning', label: 'ATENÇÃO', value: '2 follow-ups pendentes' },
]

export const mockFreeSlots = ['09:00', '10:30', '12:30', '13:00', '16:00']

export const mockNotifications = [
  { id: 'ntf-1', icon: 'bell', title: '3 leads sem atualização', description: 'Revise as próximas ações da sua carteira.', tone: 'danger', createdAt: 'Há 20 minutos', read: false },
  { id: 'ntf-2', icon: 'calendar', title: 'Visita amanhã às 10:00', description: 'Nova Saúde Empresarial • Roberto Lima', tone: 'blue', createdAt: 'Há 20 minutos', read: false },
  { id: 'ntf-3', icon: 'clock', title: 'Cadência atrasada', description: 'PrimeLog está há 8 dias sem contato.', tone: 'warning', createdAt: 'Há 20 minutos', read: false },
  { id: 'ntf-4', icon: 'briefcase', title: 'Novo lead atribuído', description: 'BlueTech Consultoria está na sua carteira.', tone: 'brand', createdAt: 'Há 20 minutos', read: false },
]
