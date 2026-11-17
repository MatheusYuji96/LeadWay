import { http, unwrap, isMockMode } from './http'
import { repInitials } from '@/models/representative'
import { leadsService } from './leads.service'
import { contactsService } from './contacts.service'

function normalizeRepresentative(rep) {
  const name = rep.name ?? rep.nome ?? ''
  return {
    ...rep, id: rep.id, name, code: rep.code ?? rep.codVendedor ?? '',
    region: rep.region ?? rep.regiao ?? '—', email: rep.email ?? '',
    phone: rep.phone ?? rep.telefone ?? '',
    initials: rep.initials ?? repInitials(name),
    leads: rep.leads ?? 0, contacts: rep.contacts ?? 0, pending: rep.pending ?? 0,
    active: rep.active ?? rep.ativo ?? false,
  }
}


export function belongsToRepresentative(lead, rep) {
  const owner = lead.vendedor ?? {}
  if (owner.id != null && rep.id != null) return String(owner.id) === String(rep.id)
  return Boolean((owner.codVendedor && rep.code && owner.codVendedor === rep.code) || (owner.email && rep.email && owner.email.toLowerCase() === rep.email.toLowerCase()))
}

export function isProposalStage(lead) {
  const stage = lead.etapa ?? {}
  return Number(stage.ordem) === 3 || /apresenta[cç][aã]o\s*\/\s*proposta/i.test(String(stage.nome ?? lead.stage ?? ''))
}

// Considera pendente o Lead cuja próxima ação esteja vencida (antes de hoje).
export function isOverdue(lead) {
  const raw = lead.proximoContato ?? lead.proximaAcao ?? lead.nextActionDate ?? lead.dataProximoContato
  if (!raw) return false
  const date = new Date(raw)
  return !Number.isNaN(date.getTime()) && date.getTime() < Date.now()
}

export const representativesService = {
  /**
   * GET /representatives
   * @returns {Promise<{ items: import('@/models/representative').Representative[], stats: object[] }>}
   */
  async list() {
    // Use real endpoint in integration mode. In mock mode the local adapter
    // will handle '/vendedores' or we may use '/representatives' depending on setup.
    const result = unwrap(await http.get(isMockMode ? '/representatives' : '/vendedores'))
    const rows = Array.isArray(result) ? result : (result?.items ?? [])
    const items = rows.map(normalizeRepresentative)
    if (!isMockMode && items.length) {
      const leads = (await leadsService.list()).items
      const groups = await Promise.allSettled(leads.map(async (lead) => {
        const response = await contactsService.listByLead(lead.id)
        return { leadId: lead.id, count: (Array.isArray(response) ? response : response?.items ?? []).length }
      }))
      const contactsByLead = new Map(groups.filter((r) => r.status === 'fulfilled').map((r) => [String(r.value.leadId), r.value.count]))
      for (const rep of items) {
        const portfolio = leads.filter((lead) => belongsToRepresentative(lead, rep))
        rep.leads = portfolio.length
        rep.contacts = portfolio.reduce((sum, lead) => sum + (contactsByLead.get(String(lead.id)) ?? 0), 0)
        rep.completed = portfolio.filter(isProposalStage).length
        rep.pending = portfolio.filter(isOverdue).length
      }
    }
    return { items, stats: Array.isArray(result?.stats) ? result.stats : [] }
  },

  /**
   * GET /representatives/:id
   * @param {string} id
   */
  async getById(id) {
    if (!isMockMode) {
      // O backend disponibilizado não implementa GET /vendedores/{id}.
      const rows = (await this.list()).items
      const found = rows.find((rep) => String(rep.id) === String(id))
      if (!found) throw new Error('Representante não encontrado.')
      return found
    }
    return normalizeRepresentative(unwrap(await http.get(`/representatives/${id}`)))
  },

  /**
   * POST /representatives
   * @param {object} payload
   */
  async create(payload) {
    // backend endpoint: /vendedores
    // Build a payload that matches the backend DTO exactly. Only include the
    // fields the API expects to avoid accidental type/shape mismatches.
    const telefone = String(payload.phone || payload.telefone || '').replace(/\D/g, '')
    const liderIdRaw = payload.liderId ?? payload.lider
    const body = {
      nome: payload.name || payload.nome || '',
      telefone,
      email: payload.email || '',
      senha: payload.password || payload.senha || '',
      codVendedor: payload.code || payload.codVendedor || '',
      // ativo is expected as a boolean in the backend DTO
      ativo: payload.active === undefined ? true : !!payload.active,
      regiao: payload.region ?? payload.regiao ?? null,
    }
    // include liderId only when provided and a valid integer
    if (liderIdRaw !== undefined && liderIdRaw !== null && String(liderIdRaw).trim() !== '') {
      const parsed = Number(liderIdRaw)
      if (!Number.isNaN(parsed)) body.liderId = parsed
    }
    return normalizeRepresentative(unwrap(await http.post(isMockMode ? '/representatives' : '/vendedores', body)))
  },

  /**
   * DELETE /representatives/:id
   * @param {string} id
   */
  async remove(id) {
    return unwrap(await http.delete(isMockMode ? `/representatives/${id}` : `/vendedores/${id}`))
  },
}
