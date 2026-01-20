import type { Config } from 'tailwindcss'

export default {
  darkMode: 'class',
  content: ['./app/**/*.{ts,tsx}'],
  plugins: [require('@tailwindcss/forms')],
} satisfies Config
