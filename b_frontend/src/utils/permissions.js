/** A interface esconde recursos; o backend deve validar as permissões novamente. */
export function getUserRole(user) {
  const value = user?.cargo?.nome ?? user?.cargoNome ?? user?.role ?? user?.perfil ?? user?.cargo ?? ''
  return String(value).normalize('NFD').replace(/[\u0300-\u036f]/g, '').trim().toUpperCase()
}

export function canViewTeamDashboard(user) {
  return ['ADMINISTRADOR', 'GERENTE'].includes(getUserRole(user))
}
