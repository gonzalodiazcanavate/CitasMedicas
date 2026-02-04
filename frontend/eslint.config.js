import {defineConfig, globalIgnores} from 'eslint/config';
import nextVitals from 'eslint-config-next/core-web-vitals';
import nextTs from 'eslint-config-next/typescript';
import reactPlugin from 'eslint-plugin-react';
import reactHooksPlugin from 'eslint-plugin-react-hooks';

export default defineConfig([
  // Configuración Next.js
  ...nextVitals,
  ...nextTs,

  // Reglas personalizadas
  {
    plugins: {
      react: reactPlugin,
      'react-hooks': reactHooksPlugin,
    },

    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
    },

    rules: {
      // === Reglas base ===
      quotes: ['error', 'single'],
      indent: ['error', 2],
      'max-len': ['error', {code: 150}],
      'no-var': 'error',
      'prefer-const': 'error',
      'object-curly-spacing': ['error', 'never'],
      'array-bracket-spacing': ['error', 'never'],

      // === React ===
      'react/react-in-jsx-scope': 'off', // React 17+
      'react/prop-types': 'off',
      'react/jsx-uses-react': 'off',
      'react/jsx-uses-vars': 'error',
      'react/self-closing-comp': 'warn',
      'react/jsx-curly-brace-presence': ['warn', 'never'],

      // === Hooks (importante en Next) ===
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'warn',
    },

    settings: {
      react: {
        version: 'detect',
      },
    },
  },

  // Ignorados
  globalIgnores([
    '.next/**',
    'out/**',
    'build/**',
    'next-env.d.ts',
  ]),
]);
