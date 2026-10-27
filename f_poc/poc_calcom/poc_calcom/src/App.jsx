import { useState } from 'react'
import heroImg from './assets/hero.png'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import styles from './style.module.css'
import { Agendamento } from './componentes/Agendamento'
import { Calendario } from './componentes/Calendario'
import './App.css'

function App() {
  const [nome, setNome] = useState("seu-nome-teste")
  const [usuario, setUsuario] = useState("seu-usuario-teste")
  const [email, setEmail] = useState("seu-email-teste")
  const [guiaAtiva, setGuiaAtiva] = useState("config")
  const [beneficiario, setBeneficiario] = useState(null);
  const [carregar, setCarregar] = useState(false);

  function salvar(e) {
    e.preventDefault();
    setCarregar(true);

    fetch("http://localhost:8080/api/beneficiarios", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nome, email, usuario })
    }).then(function (response) {
      if (!response.ok) {
        throw new Error("Erro ao salvar beneficiário no servidor.");
      }
      return response.json();
    }).then(function (data) {
      setBeneficiario(data);
      alert("Beneficiário salvo no Backend Java com sucesso! ID: " + data.id);
    }).catch(function (error) {
      console.error("Erro na requisição:", error);
      alert("Não foi possível conectar ao back-end");
    }).finally(function () {
      setCarregar(false);
    });
  }

  return (
    <div className={styles.all}>
      <h1>PoC - Integração Cal.com (Front-end)</h1>

      <div className={styles.aba}>
        <button onClick={() => setGuiaAtiva("config")}>Simular Cadastro</button>
        <button onClick={() => setGuiaAtiva("exibicaoCliente")}>Visão do Agendamento</button>
        <button onClick={() => setGuiaAtiva("exibicaoCalendario")}>Opções de Agendamento</button>
      </div>

      {guiaAtiva === "config" && (
        <div>
          <h2>Cadastro do Beneficiário</h2>
          <form onSubmit={salvar} style={{ display: "flex", flexDirection: "column", gap: "10px", maxWidth: "400px" }}>
            <label>
              <strong>Nome:</strong>
              <input
                type="text"
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                required
                style={{ width: "100%", padding: "8px", marginTop: "4px" }}
              />
            </label>

            <label>
              <strong>E-mail:</strong>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                style={{ width: "100%", padding: "8px", marginTop: "4px" }}
              />
            </label>

            <label>
              <strong>Username do Cal.com:</strong>
              <input
                type="text"
                value={usuario}
                onChange={(e) => setUsuario(e.target.value)}
                placeholder="ex: joaosilva"
                required
                style={{ width: "100%", padding: "8px", marginTop: "4px" }}
              />
            </label>

            <button type="submit" disabled={carregar} style={{ padding: "10px", cursor: "pointer", marginTop: "10px" }}>
              {carregar ? "Salvando..." : "Salvar no Backend Java"}
            </button>
          </form>

          {beneficiario && (
            <div style={{ marginTop: "20px", padding: "10px", background: "#e6ffe6", borderRadius: "5px" }}>
              <p><strong>Dados retornados do Java:</strong></p>
              <ul>
                <li><strong>ID:</strong> {beneficiario.id}</li>
                <li><strong>Nome:</strong> {beneficiario.name}</li>
                <li><strong>Nome Usuário Cal.com:</strong> {beneficiario.usuario}</li>
              </ul>
            </div>
          )}
        </div>
      )}

      {guiaAtiva === "exibicaoCliente" && (
        <div>
          <h2>Agendar Reunião</h2>
          <Agendamento nomeUsuario={usuario} delay="30min" />
        </div>
      )}

      {guiaAtiva === "exibicaoCalendario" && (
        <div>
          <Calendario nomeUsuario={usuario} />
        </div>
      )}
    </div>
  )
}

export default App
