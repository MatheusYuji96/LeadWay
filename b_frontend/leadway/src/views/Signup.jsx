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

export function Signup() {
  const navigate = useNavigate()
  const { values, errors, formError, submitting, finished, setValue, submit } = useAuthViewModel('signup')

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
          <Title level={1}>Comece a organizar suas pré-vendas</Title>
          <p>Crie sua conta e explore o sistema completo com dados de demonstração.</p>
          <ul>
            <li>
              <Icon name="check" size={14} /> Cadastro em menos de 1 minuto
            </li>
            <li>
              <Icon name="check" size={14} /> Sem cartão de crédito
            </li>
            <li>
              <Icon name="check" size={14} /> Ambiente de demonstração incluído
            </li>
          </ul>
        </div>
      </aside>

      <main className={styles.formSide}>
        <div className={styles.formBox}>
          <div className={styles.formHead}>
            <Logo />
            <Title level={2}>Criar conta</Title>
            <p>Preencha seus dados para acessar o LeadWay.</p>
          </div>

          {formError && <InlineMessage title="Verifique os dados" description={formError} />}

          <form className={styles.form} onSubmit={handleSubmit} noValidate>
            <Field
              label="Nome completo"
              icon="user"
              placeholder="Ex.: Ana Souza"
              autoComplete="name"
              value={values.name}
              onChange={(v) => setValue('name', v)}
              error={errors.name}
            />
            <Field
              label="WhatsApp"
              icon="phone"
              placeholder="(11) 99999-0000"
              autoComplete="tel"
              value={values.whatsapp}
              onChange={(v) => setValue('whatsapp', v)}
              error={errors.whatsapp}
            />
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
              placeholder="Mínimo de 8 caracteres"
              autoComplete="new-password"
              value={values.password}
              onChange={(v) => setValue('password', v)}
              error={errors.password}
            />
            <Field
              label="Confirmar senha"
              type="password"
              icon="lock"
              placeholder="Repita a senha"
              autoComplete="new-password"
              value={values.confirmPassword}
              onChange={(v) => setValue('confirmPassword', v)}
              error={errors.confirmPassword}
            />
            <Button type="submit" kind="primary" full loading={submitting}>
              Criar conta
            </Button>
          </form>

          <p className={styles.switch}>
            Já tem conta? <Link to={paths.login}>Entrar</Link>
          </p>
          <Link className={styles.back} to={paths.landing}>
            <Icon name="chevron" size={13} /> Voltar para a página inicial
          </Link>
        </div>
      </main>
    </div>
  )
}

export default Signup
