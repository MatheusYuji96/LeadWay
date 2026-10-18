import { Navigate, Route, Routes } from 'react-router-dom'
import { AppLayout } from '@/components/layout/AppLayout'
import { SessionProvider } from '@/viewmodels/useSession'
import { paths } from '@/routes/paths'
import { Agenda } from '@/views/Agenda'
import { Contacts } from '@/views/Contacts'
import { Dashboard } from '@/views/Dashboard'
import { Landing } from '@/views/Landing'
import { LeadDetail } from '@/views/LeadDetail'
import { Leads } from '@/views/Leads'
import { Login } from '@/views/Login'
import { RepDetail } from '@/views/RepDetail'
import { Settings } from '@/views/Settings'
import { Signup } from '@/views/Signup'
import { States } from '@/views/States'
import { Team } from '@/views/Team'

function App() {
  return (
    <SessionProvider>
      <Routes>
        <Route path={paths.landing} element={<Landing />} />
        <Route path={paths.login} element={<Login />} />
        <Route path={paths.signup} element={<Signup />} />
        <Route path={paths.app} element={<AppLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="leads" element={<Leads />} />
          <Route path="leads/:leadId" element={<LeadDetail />} />
          <Route path="contatos" element={<Contacts />} />
          <Route path="equipe" element={<Team />} />
          <Route path="equipe/:repId" element={<RepDetail />} />
          <Route path="agenda" element={<Agenda />} />
          <Route path="configuracoes" element={<Settings />} />
          <Route path="estados" element={<States />} />
        </Route>
        <Route path="*" element={<Navigate to={paths.landing} replace />} />
      </Routes>
    </SessionProvider>
  )
}

export default App
