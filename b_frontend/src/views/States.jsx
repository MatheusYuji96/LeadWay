import { useState } from 'react'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { EmptyState, ErrorState, InlineMessage, Skeleton, Spinner } from '@/components/common/Feedback'
import { PageHeading } from '@/components/common/PagePrimitives'
import { Panel, PanelHead } from '@/components/common/Panel'
import styles from './States.module.css'

export function States() {
  const [loading, setLoading] = useState(false)

  const simulate = () => {
    setLoading(true)
    setTimeout(() => setLoading(false), 1800)
  }

  return (
    <div className={styles.page}>
      <PageHeading
        eyebrow="Biblioteca"
        title="Estados de tela"
        description="Componentes de loading, vazio, erro e feedback usados no sistema."
        actions={
          <Button kind="primary" icon="arrow" onClick={simulate} loading={loading}>
            Simular carregamento
          </Button>
        }
      />

      <div className={styles.grid}>
        <Panel>
          <PanelHead title="Loading" subtitle="Skeleton e spinner de espera" />
          <div className={styles.body}>
            {loading ? <Spinner label="Carregando dados..." /> : <Skeleton lines={3} cards={2} />}
          </div>
        </Panel>

        <Panel>
          <PanelHead title="Vazio" subtitle="Quando não há resultados" />
          <div className={styles.body}>
            <EmptyState
              icon="search"
              title="Nenhum resultado encontrado"
              description="Ajuste os filtros para encontrar o que procura."
              actionLabel="Limpar filtros"
              onAction={() => {}}
            />
          </div>
        </Panel>

        <Panel>
          <PanelHead title="Erro" subtitle="Falha de carregamento com retry" />
          <div className={styles.body}>
            <ErrorState message="Não foi possível conectar ao servidor." onRetry={simulate} />
          </div>
        </Panel>

        <Panel>
          <PanelHead title="Mensagens" subtitle="Feedbacks inline de formulário" />
          <div className={[styles.body, styles.messages].join(' ')}>
            <InlineMessage tone="success" title="Dados salvos com sucesso." />
            <InlineMessage tone="warning" title="Este lead está há 10 dias sem contato." />
            <InlineMessage tone="error" title="Preencha os campos obrigatórios." description="Nome e e-mail são obrigatórios." />
          </div>
        </Panel>

        <Panel>
          <PanelHead title="Badges" subtitle="Status de leads e cadências" />
          <div className={[styles.body, styles.badges].join(' ')}>
            <Badge tone="success">Em dia</Badge>
            <Badge tone="info">Próximo contato</Badge>
            <Badge tone="warning">Atenção</Badge>
            <Badge tone="danger">Atrasado</Badge>
            <Badge tone="brand">Qualificação</Badge>
            <Badge tone="neutral">Inativo</Badge>
          </div>
        </Panel>

        <Panel>
          <PanelHead title="Botões" subtitle="Variações de ação" />
          <div className={[styles.body, styles.buttons].join(' ')}>
            <Button kind="primary">Primário</Button>
            <Button>Secundário</Button>
            <Button kind="ghost">Fantasma</Button>
            <Button icon="plus">Com ícone</Button>
            <Button kind="primary" disabled>
              Desabilitado
            </Button>
          </div>
        </Panel>
      </div>
    </div>
  )
}

export default States
