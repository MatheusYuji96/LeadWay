import styles from '../styleCalendar.module.css'
export function Calendario({ nomeUsuario }) {
  if (!nomeUsuario) {
    return <p className={styles.div}>Informe um usuário para visualizar a agenda.</p>;
  }

  return (
    <div className={styles.calendario}>
      <h2>Minha Agenda - {nomeUsuario}</h2>
      <iframe
        src={`https://cal.com/${nomeUsuario}`}
        title="Agenda do Beneficiário"
        width="100%"
        height="650px"
        style={{ border: "1px solid #ddd", borderRadius: "8px" }}
      />
    </div>
  );
}