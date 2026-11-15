import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Avatar } from '@/components/common/Avatar'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { paths } from '@/routes/paths'
import { useSession } from '@/viewmodels/sessionContext'
import { NotificationsPanel } from './NotificationsPanel'
import styles from './AppHeader.module.css'

export function AppHeader({ onToggleMenu, notifications }) {
  const [userMenuOpen, setUserMenuOpen] = useState(false)
  const navigate = useNavigate()
  const { user, logout } = useSession()
  const menuRef = useRef(null)

  useEffect(() => {
    if (!userMenuOpen) return undefined
    const onDocClick = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) setUserMenuOpen(false)
    }
    document.addEventListener('mousedown', onDocClick)
    return () => document.removeEventListener('mousedown', onDocClick)
  }, [userMenuOpen])

  const closeAll = () => {
    setUserMenuOpen(false)
    notifications.close()
  }

  const goTo = (to) => {
    closeAll()
    navigate(to)
  }

  const handleLogout = () => {
    closeAll()
    logout()
    navigate(paths.landing)
  }

  return (
    <header className={styles.header}>
      <Button kind="ghost" className={styles.mobileMenu} onClick={onToggleMenu} aria-label="Abrir menu">
        <Icon name="menu" />
      </Button>

      <div className={styles.actions}>
        <Button
          kind="ghost"
          className={styles.iconButton}
          onClick={() => {
            setUserMenuOpen(false)
            notifications.toggle()
          }}
          aria-label="Notificações"
        >
          <Icon name="bell" />
          {notifications.unread > 0 && !notifications.open && <i className={styles.dot} />}
        </Button>

        <span className={styles.separator} />

        <div className={styles.userMenu} ref={menuRef}>
          <Button
            kind="ghost"
            className={styles.userTrigger}
            onClick={() => {
              notifications.close()
              setUserMenuOpen((prev) => !prev)
            }}
          >
            <Avatar initials={user?.initials ?? '—'} />
            <span className={styles.headerUser}>
              <strong>{user?.name ?? 'Visitante'}</strong>
              <small>{user?.role ?? 'Sem sessão'}</small>
            </span>
            <span className={userMenuOpen ? styles.chevronOpen : styles.chevron}>
              <Icon name="chevron" />
            </span>
          </Button>

          {userMenuOpen && (
            <div className={styles.dropdown}>
              <div className={styles.dropdownHead}>
                <strong>Minha conta</strong>
                <small>{user?.email ?? '—'}</small>
              </div>
              <Button kind="ghost" className={styles.menuItem} onClick={() => goTo(paths.settings)}>
                <Icon name="settings" /> Configurações
              </Button>
              <Button kind="danger" className={styles.menuItemLogout} onClick={handleLogout}>
                Sair
              </Button>
            </div>
          )}
        </div>
      </div>

      {notifications.open && (
        <NotificationsPanel
          items={notifications.items}
          loading={notifications.loading}
          error={notifications.error}
          onClose={notifications.close}
          reload={notifications.reload}
        />
      )}
    </header>
  )
}

export default AppHeader
