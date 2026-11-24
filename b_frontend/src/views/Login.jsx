import { useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Button } from '@/components/common/Button'
import { Field } from '@/components/common/Field'
import { InlineMessage } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import { Logo } from '@/components/common/Logo'
import { Title } from '@/components/common/Title'
import { useAuthViewModel } from '@/viewmodels/useAuthViewModel'
import { paths } from '@/routes/paths'
import styles from './Auth.module.css'

export function Login() {
  const navigate = useNavigate()
  const { values, errors, formError, submitting, finished, setValue, submit } = useAuthViewModel('login')

  useEffect(() => {
    if (finished) navigate(paths.dashboard, { replace: true })
  }, [finished, navigate])

  const handleSubmit = (event) => {
    event.preventDefault()
    submit()
  }

  return (
    <div className={styles.page}>
      <aside className={styles.pitch}>
        <Logo light />
        <div className={styles.pitchBody}>
          <Title level={1}>Bem-vindo de volta</Title>
          <p>Acesse sua carteira de leads, sua agenda e o acompanhamento da sua equipe.</p>
          <ul>
            <li>
              <Icon name="check" size={14} /> Funil e cadência sempre atualizados
            </li>
            <li>
              <Icon name="check" size={14} /> Alertas de leads parados
            </li>
            <li>
              <Icon name="check" size={14} /> Visão individual e da equipe
            </li>
          </ul>
        </div>
      </aside>

      <main className={styles.formSide}>
        <div className={styles.formBox}>
          <div className={styles.formHead}>
            <Logo />
            <Title level={2}>Entrar</Title>
            <p>Use seu e-mail e senha para acessar o sistema.</p>
          </div>

          {formError && <InlineMessage title="Não foi possível entrar" description={formError} />}

          <form className={styles.form} onSubmit={handleSubmit} noValidate>
            <Field
              label="E-mail"
              type="email"
              icon="mail"
              placeholder="voce@empresa.com"
              autoComplete="email"
              value={values.email}
              onChange={(v) => setValue('email', v)}
              error={errors.email}
            />
            <Field
              label="Senha"
              type="password"
              icon="lock"
              placeholder="••••••••"
              autoComplete="current-password"
              value={values.password}
              onChange={(v) => setValue('password', v)}
              error={errors.password}
            />
            <Button type="submit" kind="primary" full loading={submitting}>
              Entrar
            </Button>
          </form>

          <p className={styles.switch}>
            Ainda não tem conta? <Link to={paths.signup}>Criar cadastro</Link>
          </p>
          <Link className={styles.back} to={paths.landing}>
            <Icon name="chevron" size={13} /> Voltar para a página inicial
          </Link>
        </div>
      </main>
    </div>
  )
}

export default Login
