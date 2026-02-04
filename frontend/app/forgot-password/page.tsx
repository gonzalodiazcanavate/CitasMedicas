'use client';

import {useState} from 'react';
import {HeartPulse, Mail, ArrowLeft, CheckCircle} from 'lucide-react';
import Link from 'next/link';
import Field from '@/components/forms/Field';
import {forgotPassword} from '@/services/authApi';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!email) {
      setError('Por favor ingresa tu email.');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      await forgotPassword(email);
      setSuccess(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error desconocido');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 dark:bg-background-dark">
        <div className="w-full max-w-md">
          <div className="rounded-2xl bg-white p-8 shadow-lg dark:bg-slate-800">
            {/* Success Icon */}
            <div className="mb-6 flex justify-center">
              <div className="flex size-16 items-center justify-center rounded-full bg-green-100 dark:bg-green-900/30">
                <CheckCircle className="size-8 text-green-600 dark:text-green-400" />
              </div>
            </div>

            {/* Success Message */}
            <h1 className="mb-2 text-center text-2xl font-bold text-slate-900 dark:text-white">
              ¡Revisa tu correo!
            </h1>
            <p className="mb-6 text-center text-slate-600 dark:text-slate-400">
              Si el email <strong>{email}</strong> está registrado, recibirás un
              enlace para restablecer tu contraseña.
            </p>

            {/* Info */}
            <div className="mb-6 rounded-lg bg-blue-50 p-4 dark:bg-blue-900/20">
              <p className="text-sm text-blue-700 dark:text-blue-300">
                El enlace expirará en 30 minutos. Si no recibes el correo,
                revisa tu carpeta de spam.
              </p>
            </div>

            {/* Back to Login */}
            <Link
              href="/login"
              className="flex w-full items-center justify-center gap-2 rounded-lg bg-primary py-3 font-semibold text-white transition-colors hover:bg-primary/90"
            >
              <ArrowLeft className="size-4" />
              Volver al inicio de sesión
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 dark:bg-background-dark">
      <div className="w-full max-w-md">
        <div className="rounded-2xl bg-white p-8 shadow-lg dark:bg-slate-800">
          {/* Logo */}
          <div className="mb-8 flex items-center justify-center gap-3">
            <div className="flex size-10 items-center justify-center rounded-lg bg-primary">
              <HeartPulse className="size-5 text-white" />
            </div>
            <span className="text-xl font-bold text-slate-900 dark:text-white">
              MedicalApp
            </span>
          </div>

          {/* Heading */}
          <h1 className="mb-2 text-center text-2xl font-bold text-slate-900 dark:text-white">
            ¿Olvidaste tu contraseña?
          </h1>
          <p className="mb-8 text-center text-slate-600 dark:text-slate-400">
            Ingresa tu email y te enviaremos instrucciones para restablecerla.
          </p>

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-6">
            <Field
              id="email"
              label="Email"
              type="email"
              placeholder="tu@email.com"
              icon={Mail}
              value={email}
              onChange={setEmail}
            />

            {error && (
              <p className="rounded-lg bg-red-50 p-3 text-sm text-red-600 dark:bg-red-900/20 dark:text-red-400">
                {error}
              </p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-lg bg-primary py-3.5 font-semibold text-white transition-colors hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {loading ? 'Enviando...' : 'Enviar instrucciones'}
            </button>
          </form>

          {/* Back Link */}
          <div className="mt-6 text-center">
            <Link
              href="/login"
              className="inline-flex items-center gap-2 text-sm font-medium text-primary hover:text-primary/80"
            >
              <ArrowLeft className="size-4" />
              Volver al inicio de sesión
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
