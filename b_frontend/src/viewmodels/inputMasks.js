// Máscaras apenas de apresentação: o Service continua enviando números sem pontuação.
export const onlyDigits = (value) => String(value ?? '').replace(/\D/g, '')
export function maskCnpj(value) {
  const v = onlyDigits(value).slice(0, 14)
  return v.replace(/^(\d{2})(\d)/, '$1.$2').replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3').replace(/\.(\d{3})(\d)/, '.$1/$2').replace(/(\d{4})(\d)/, '$1-$2')
}
export function maskCep(value) {
  return onlyDigits(value).slice(0, 8).replace(/^(\d{5})(\d)/, '$1-$2')
}
export function maskPhone(value) {
  const v = onlyDigits(value).slice(0, 11)
  if (v.length <= 2) return v ? `(${v}` : ''
  const area = v.slice(0, 2)
  const number = v.slice(2)
  const split = number.length > 8 ? 5 : 4
  return `(${area}) ${number.slice(0, split)}${number.length > split ? '-' + number.slice(split) : ''}`
}
