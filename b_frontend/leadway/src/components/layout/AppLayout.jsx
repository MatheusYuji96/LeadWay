import { useState } from 'react'
import { Outlet, useNavigate } from 'react-router-dom'
import { isMockMode } from '@/services'
import { AppHeader } from './AppHeader'
import { Sidebar } from './Sidebar'
import { useNotificationsViewModel } from '@/viewmodels/useNotificationsViewModel'
import styles from './AppLayout.module.css'

export function DemoBanner() {
  if (!isMockMode) return null
  return (
    <div className={styles.demoBanner} role="note">
      Modo demonstração ativo — os dados exibidos vêm de fixtures do protótipo, não de backend.
      Configure <code>VITE_API_BASE_URL</code> e <code>VITE_USE_MOCK=false</code> para consumir a API real.
    </div>
  )
}

export function AppLayout() {
  const [menuOpen, setMenuOpen] = useState(false)
  const notifications = useNotificationsViewModel()
  const navigate = useNavigate()

  return (
    <div className={styles.shell}>
      <Sidebar open={menuOpen} onClose={() => setMenuOpen(false)} onNavigate={navigate} />
      {menuOpen && <div className={styles.overlay} onClick={() => setMenuOpen(false)} role="presentation" />}
      <div className={styles.main}>
        <AppHeader onToggleMenu={() => setMenuOpen((prev) => !prev)} notifications={notifications} />
        <DemoBanner />
        <Outlet />
      </div>
    </div>
  )
}

export default AppLayout
