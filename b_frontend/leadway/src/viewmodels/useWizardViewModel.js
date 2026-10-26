import { useCallback, useState } from 'react'

/**
 * ViewModel genérico dos cadastros em etapas (wizard de 4 ou 5 passos).
 * @param {number} totalSteps
 * @param {number} [initialStep]
 */
export function useWizardViewModel(totalSteps, initialStep = 1) {
  const [step, setStep] = useState(initialStep)
  const [finished, setFinished] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  const next = useCallback(() => {
    setError(null)
    setStep((prev) => Math.min(totalSteps, prev + 1))
  }, [totalSteps])

  const back = useCallback(() => {
    setError(null)
    setStep((prev) => Math.max(1, prev - 1))
  }, [])

  const goTo = useCallback((target) => {
    setError(null)
    setStep(target)
  }, [])

  /**
   * Executa a ação final do wizard (submissão ao service).
   * @param {() => Promise<unknown>} submit
   */
  const complete = useCallback(
    async (submit) => {
      setSubmitting(true)
      setError(null)
      try {
        await submit()
        setFinished(true)
        return true
      } catch (err) {
        setError(err?.message || 'Não foi possível concluir o cadastro.')
        return false
      } finally {
        setSubmitting(false)
      }
    },
    [],
  )

  return { step, finished, submitting, error, next, back, goTo, complete, setFinished }
}
