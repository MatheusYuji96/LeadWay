import { Link, useNavigate } from 'react-router-dom'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { Logo } from '@/components/common/Logo'
import { isMockMode } from '@/services'
import { paths } from '@/routes/paths'
import styles from './Landing.module.css'

const FEATURES = [
  {
    icon: 'briefcase',
    title: 'Carteira de leads organizada',
    text: 'Toda a sua prospecção em um só lugar, com funil, cadência e próxima ação visíveis.',
  },
  {
    icon: 'calendar',
    title: 'Agenda comercial integrada',
    text: 'Reuniões, visitas e follow-ups em visões de mês, semana e dia sem sair do sistema.',
  },
  {
    icon: 'users',
    title: 'Gestão da equipe',
    text: 'Acompanhe desempenho, pendências e evolução de cada representante em tempo real.',
  },
  {
    icon: 'bell',
    title: 'Alertas de cadência',
    text: 'Receba avisos quando um lead estiver parado ou uma ação estiver atrasada.',
  },
  {
    icon: 'trend',
    title: 'Indicadores claros',
    text: 'Painéis com métricas de prospecção, conversão e atividades da semana.',
  },
  {
    icon: 'settings',
    title: 'Configurações flexíveis',
    text: 'Conta, segurança e preferências de notificação ajustadas ao seu jeito de trabalhar.',
  },
]

const STEPS = [
  { n: '01', title: 'Cadastre sua equipe', text: 'Adicione representantes e distribua regiões de atuação.' },
  { n: '02', title: 'Organize os leads', text: 'Crie empresas, vincule contatos e defina a etapa do funil.' },
  { n: '03', title: 'Execute a cadência', text: 'Siga as próximas ações sugeridas e registre cada contato.' },
]

export function Landing() {
  const navigate = useNavigate()

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <Logo />
        <nav className={styles.nav}>
          <a href="#recursos">Recursos</a>
          <a href="#como-funciona">Como funciona</a>
          <a href="#estados">Estados de tela</a>
        </nav>
        <div className={styles.headerActions}>
          <Button onClick={() => navigate(paths.login)}>Entrar</Button>
          <Button kind="primary" onClick={() => navigate(paths.signup)}>
            Criar conta
          </Button>
        </div>
      </header>

      <section className={styles.hero}>
        <span className={styles.heroTag}>Gestão de leads para pré-vendas</span>
        <h1>
          Organize sua carteira e <em>conquiste mais negócios</em>
        </h1>
        <p>
          O LeadWay reúne leads, contatos, agenda e acompanhamento da equipe em um fluxo único — do primeiro
          contato ao orçamento.
        </p>
        <div className={styles.heroActions}>
          <Button kind="primary" size="lg" onClick={() => navigate(paths.signup)}>
            Começar agora <Icon name="arrow" />
          </Button>
          <Button size="lg" onClick={() => navigate(paths.login)}>
            Ver demonstração
          </Button>
        </div>
        {isMockMode && <small className={styles.heroNote}>Ambiente em modo demonstração com dados de exemplo.</small>}
      </section>

      <section className={styles.features} id="recursos">
        <div className={styles.sectionHead}>
          <h2>Tudo o que a pré-venda precisa</h2>
          <p>Recursos pensados para o dia a dia de representantes e gestores comerciais.</p>
        </div>
        <div className={styles.grid}>
          {FEATURES.map((feature) => (
            <article className={styles.card} key={feature.title}>
              <span className={styles.cardIcon}>
                <Icon name={feature.icon} />
              </span>
              <h3>{feature.title}</h3>
              <p>{feature.text}</p>
            </article>
          ))}
        </div>
      </section>

      <section className={styles.steps} id="como-funciona">
        <div className={styles.sectionHead}>
          <h2>Como funciona</h2>
          <p>Três passos para colocar sua operação de pré-vendas no ar.</p>
        </div>
        <div className={styles.stepGrid}>
          {STEPS.map((step) => (
            <div className={styles.step} key={step.n}>
              <strong>{step.n}</strong>
              <h3>{step.title}</h3>
              <p>{step.text}</p>
            </div>
          ))}
        </div>
      </section>

      <section className={styles.callout} id="estados">
        <div>
          <h2>Pronto para começar?</h2>
          <p>Crie sua conta e explore todas as telas, inclusive estados de loading, vazio e erro.</p>
        </div>
        <div className={styles.calloutActions}>
          <Button kind="primary" onClick={() => navigate(paths.signup)}>
            Criar conta grátis
          </Button>
          <Button onClick={() => navigate(paths.states)}>Ver estados de tela</Button>
        </div>
      </section>

      <footer className={styles.footer}>
        <Logo compact />
        <span>LeadWay — Sistema de gestão de leads para pré-vendas.</span>
        <Link to={paths.login}>Entrar</Link>
      </footer>
    </div>
  )
}

export default Landing
