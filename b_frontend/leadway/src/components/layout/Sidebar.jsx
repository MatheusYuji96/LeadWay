import { NavLink, useLocation } from 'react-router-dom'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { Logo } from '@/components/common/Logo'
import { paths } from '@/routes/paths'
import { useSession } from '@/viewmodels/sessionContext'
import styles from './Sidebar.module.css'

const navItems = [
  { to: paths.app, icon: 'home', label: 'Inicial', end: true },
  { to: paths.leads, icon: 'briefcase', label: 'Leads', badge: 12 },
  { to: paths.team, icon: 'users', label: 'Funcionários' },
  { to: paths.agenda, icon: 'calendar', label: 'Agenda' },
  { to: paths.settings, icon: 'settings', label: 'Configurações' },
]

export function Sidebar({ open, onClose, onNavigate }) {
  const location = useLocation()
  const { logout } = useSession()

  const handleLogout = () => {
    logout()
    onNavigate?.(paths.landing)
  }

  return (
    <aside className={[styles.sidebar, open ? styles.open : ''].filter(Boolean).join(' ')}>
      <div className={styles.top}>
        <Logo light />
        <Button kind="ghost" className={styles.mobileClose} onClick={onClose} aria-label="Fechar menu">
          <Icon name="close" />
        </Button>
      </div>

      <nav className={styles.nav}>
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            onClick={onClose}
            className={({ isActive }) =>
              [styles.navItem, isActive || (item.to !== paths.app && location.pathname.startsWith(item.to)) ? styles.active : '']
                .filter(Boolean)
                .join(' ')
            }
          >
            <Icon name={item.icon} />
            <span>{item.label}</span>
            {item.badge && <small>{item.badge}</small>}
          </NavLink>
        ))}
      </nav>

      <div className={styles.help}>
        <span>
          <Icon name="trend" />
        </span>
        <strong>Acelere seus resultados</strong>
        <p>Complete seu perfil e aproveite todos os recursos.</p>
        <div className={styles.progress}>
          <i />
        </div>
        <small>75% concluído</small>
      </div>

      <Button kind="danger" className={styles.logout} onClick={handleLogout}>
        Sair
      </Button>
    </aside>
  )
}

export default Sidebar
