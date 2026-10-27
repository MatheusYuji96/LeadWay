import { NavLink, useLocation } from 'react-router-dom'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { Logo } from '@/components/common/Logo'
import { paths } from '@/routes/paths'
import { useSession } from '@/viewmodels/sessionContext'
import styles from './Sidebar.module.css'
import { useEffect, useState } from 'react'
import { leadsService } from '@/services/leads.service'

const navItems = [
  { to: paths.app, icon: 'home', label: 'Inicial', end: true },
  { to: paths.leads, icon: 'briefcase', label: 'Leads' },
  { to: paths.team, icon: 'users', label: 'Funcionários' },
  { to: paths.agenda, icon: 'calendar', label: 'Agenda' },
  { to: paths.settings, icon: 'settings', label: 'Configurações' },
]

export function Sidebar({ open, onClose, onNavigate }) {
  const location = useLocation()
  const { logout } = useSession()
  const [leadCount, setLeadCount] = useState(null)
  useEffect(() => {
    let mounted = true
    const refresh = () => leadsService.list().then((result) => {
      if (mounted) setLeadCount(result.total ?? result.items?.length ?? null)
    }).catch(() => { if (mounted) setLeadCount(null) })
    refresh()
    window.addEventListener('leadway:leads-changed', refresh)
    return () => { mounted = false; window.removeEventListener('leadway:leads-changed', refresh) }
  }, [])

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
            {item.to === paths.leads && leadCount !== null && <small>{leadCount}</small>}
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
