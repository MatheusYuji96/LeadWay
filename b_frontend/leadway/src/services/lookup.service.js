// Setores cadastrados no banco de dados MySQL
const setores = [
  { id: 1, nome: 'Tecnologia' },
  { id: 2, nome: 'Saúde e Farmacêutica' },
  { id: 3, nome: 'Educação' },
  { id: 4, nome: 'Varejo e E-commerce' },
  { id: 5, nome: 'Indústria e Manufatura' },
]

// Etapas cadastradas no banco de dados MySQL
const etapas = [
  { id: 1, nome: 'Prospecção / Novo Lead' },
  { id: 2, nome: 'Qualificação' },
  { id: 3, nome: 'Apresentação / Proposta' },
  { id: 4, nome: 'Negociação' },
  { id: 5, nome: 'Fechado / Ganho' },
]

export const lookupService = {
  async getSetores() {
    return setores
  },

  async getEtapas() {
    return etapas
  },
}

export default lookupService