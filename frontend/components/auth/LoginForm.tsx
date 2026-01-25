'use client';

import {useState} from 'react';
import {loginUser} from '@/services/authApi';
import {
  HeartPulse,
  Mail,
} from 'lucide-react';
import Field from '../forms/Field';
import Divider from '../ui/Divider';
import SocialButtons from './SocialButtons';
import LoginFormFooter from './LoginFormFooter';
import PasswordField from '../forms/PasswordField';
import ForgotPasswordLink from './ForgotPasswordLink';
import SubmitButton from './SubmitButton';
import RegisterLink from './RegisterLink';

const LoginForm = () => {
  const [emailOrUsername, setEmailOrUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    // Validación básica
    if (!emailOrUsername || !password) {
      setError('Por favor ingresa email/usuario y contraseña.');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      await loginUser(emailOrUsername, password);
      // guardar token/session y redirigir, p.ej. router.push('/dashboard')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error desconocido');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex w-full flex-col bg-white dark:bg-background-dark lg:w-1/2">
      {/* Mobile header */}
      <div className="flex items-center gap-3 border-b border-slate-100 p-6 dark:border-slate-800 lg:hidden">
        <div className="flex size-8 items-center justify-center rounded-lg bg-primary">
          <HeartPulse className="size-4 text-white" />
        </div>
        <h2 className="text-lg font-bold text-[#0d141b] dark:text-white">
          MedicalApp
        </h2>
      </div>

      {/* Main content */}
      <div className="flex flex-1 flex-col justify-center px-8 py-12 sm:px-12 md:px-24 xl:px-32">
        {/* Heading */}
        <div className="mb-10">
          <h2 className="mb-2 text-3xl font-extrabold text-[#0d141b] dark:text-white">
            Welcome Back
          </h2>
          <p className="text-slate-500 dark:text-slate-400">
            Please enter your credentials to access your secure portal.
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Email or Username */}
          <Field
            id="emailOrUsername"
            label="Email or Username"
            type="text"
            placeholder="e.g. doctor@hospital.com or user455"
            icon={Mail}
            value={emailOrUsername}
            onChange={setEmailOrUsername}
          />

          {/* Password */}
          <PasswordField
            value={password}
            onChange={setPassword}
          />
          <ForgotPasswordLink />

          {/* Remember me (Probablemente se elimine)*/}
          <div className="flex items-center gap-2">
            <input
              id="remember"
              type="checkbox"
              className="size-4 cursor-pointer rounded border-slate-300 text-primary focus:ring-primary"
            />
            <label
              htmlFor="remember"
              className="cursor-pointer text-sm font-medium text-slate-600 dark:text-slate-400"
            >
              Remember me for 30 days
            </label>
          </div>

          {/* Submit */}
          {error && <p className="text-sm text-red-500">{error}</p>}
          <SubmitButton disabled={loading} />
        </form>

        {/* Divider */}
        <Divider />

        {/* Social login */}
        <SocialButtons />

        {/* Register link */}
        <RegisterLink />
      </div>

      {/* Bottom links */}
      <LoginFormFooter />
    </div>
  );
};

export default LoginForm;