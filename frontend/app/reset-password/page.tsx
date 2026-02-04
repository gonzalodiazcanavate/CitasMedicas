'use client';

import {useState, useEffect, Suspense} from 'react';
import {useSearchParams} from 'next/navigation';
import {HeartPulse, CheckCircle, XCircle, ArrowLeft, Loader2} from 'lucide-react';
import Link from 'next/link';
import PasswordField from '@/components/forms/PasswordField';
import {resetPassword, validateResetToken} from '@/services/authApi';

function ResetPasswordContent() {
  const searchParams = useSearchParams();
  const token = searchParams.get('token');

  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [validating, setValidating] = useState(true);
  const [tokenValid, setTokenValid] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  // Validar token al cargar
  useEffect(() => {
    async function validateToken() {
      if (!token) {
        setTokenValid(false);
        setValidating(false);
        return;
      }

      try {
        const result = await validateResetToken(token);
        setTokenValid(result.valid);
      } catch {
        setTokenValid(false);
      } finally {
        setValidating(false);
      }
    }

    validateToken();
  }, [token]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!password || !confirmPassword) {
      setError('Por favor completa todos los campos.');
      return;
    }

    if (password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres.');
      return;
    }

    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden.');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      await resetPassword(token!, password);
      setSuccess(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error desconocido');
    } finally {
      setLoading(false);
    }
  };

  // Estado: Validando token
  if (validating) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50 dark:bg-background-dark">
        <div className="text-center">
          <Loader2 className="mx-auto size-8 animate-spin text-primary" />
          <p className="mt-4 text-slate-600 dark:text-slate-400">
            Validando enlace...
          </p>
        </div>
      </div>
    );
  }

  // Estado: Token inválido
  if (!tokenValid) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 dark:bg-background-dark">
        <div className="w-full max-w-md">
          <div className="rounded-2xl bg-white p-8 shadow-lg dark:bg-slate-800">
            <div className="mb-6 flex justify-center">
              <div className="flex size-16 items-center justify-center rounded-full bg-red-100 dark:bg-red-900/30">
                <XCircle className="size-8 text-red-600 dark:text-red-400" />
              </div>
            </div>

            <h1 className="mb-2 text-center text-2xl font-bold text-slate-900 dark:text-white">
              Enlace inválido
            </h1>
            <p className="mb-6 text-center text-slate-600 dark:text-slate-400">
              Este enlace de recuperación no es válido o ha expirado.
            </p>

            <Link
              href="/forgot-password"
              className="flex w-full items-center justify-center gap-2 rounded-lg bg-primary py-3 font-semibold text-white transition-colors hover:bg-primary/90"
            >
              Solicitar nuevo enlace
            </Link>

            <div className="mt-4 text-center">
              <Link
                href="/login"
                className="inline-flex items-center gap-2 text-sm font-medium text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white"
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

  // Estado: Éxito
  if (success) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 dark:bg-background-dark">
        <div className="w-full max-w-md">
          <div className="rounded-2xl bg-white p-8 shadow-lg dark:bg-slate-800">
            <div className="mb-6 flex justify-center">
              <div className="flex size-16 items-center justify-center rounded-full bg-green-100 dark:bg-green-900/30">
                <CheckCircle className="size-8 text-green-600 dark:text-green-400" />
              </div>
            </div>

            <h1 className="mb-2 text-center text-2xl font-bold text-slate-900 dark:text-white">
              ¡Contraseña actualizada!
            </h1>
            <p className="mb-6 text-center text-slate-600 dark:text-slate-400">
              Tu contraseña ha sido restablecida exitosamente. Ya puedes iniciar
              sesión con tu nueva contraseña.
            </p>

            <Link
              href="/login"
              className="flex w-full items-center justify-center rounded-lg bg-primary py-3 font-semibold text-white transition-colors hover:bg-primary/90"
            >
              Iniciar sesión
            </Link>
          </div>
        </div>
      </div>
    );
  }

  // Estado: Formulario
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
            Nueva contraseña
          </h1>
          <p className="mb-8 text-center text-slate-600 dark:text-slate-400">
            Ingresa tu nueva contraseña. Debe tener al menos 8 caracteres.
          </p>

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-6">
            <PasswordField value={password} onChange={setPassword} />

            <div className="space-y-2">
              <label
                htmlFor="confirmPassword"
                className="text-sm font-semibold text-slate-900 dark:text-slate-200"
              >
                Confirmar contraseña
              </label>
              <input
                id="confirmPassword"
                type="password"
                placeholder="••••••••"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full rounded-lg border border-slate-200 bg-slate-50 py-3.5 px-4 text-slate-900 placeholder:text-slate-400 focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20 dark:border-slate-700 dark:bg-slate-800/50 dark:text-white"
              />
            </div>

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
              {loading ? 'Actualizando...' : 'Actualizar contraseña'}
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

export default function ResetPasswordPage() {
  return (
    <Suspense
      fallback={
        <div className="flex min-h-screen items-center justify-center bg-slate-50 dark:bg-background-dark">
          <Loader2 className="size-8 animate-spin text-primary" />
        </div>
      }
    >
      <ResetPasswordContent />
    </Suspense>
  );
}
