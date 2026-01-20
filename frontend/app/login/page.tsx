'use client';

import LoginForm from '../../components/auth/LoginForm';
import LoginHero from '../../components/auth/LoginHero';

export default function LoginPage() {
  return (
    <div className="min-h-screen bg-background-light dark:bg-background-dark text-[#0d141b] dark:text-slate-100 antialiased">
      <div className="flex min-h-screen">
        {/* Left: Hero / Background (desktop only) */}
        <LoginHero />
        {/* Right: Login form */}
        <LoginForm />
      </div>
    </div>
  );
}