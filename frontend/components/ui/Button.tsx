'use client';

import type { ButtonHTMLAttributes } from 'react';
import { cn } from '@/lib/cn'; // si no lo tienes, lo vemos luego

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'secondary';
};

const Button = ({
  variant = 'primary',
  className,
  ...props
}: ButtonProps) => {
  const base =
    'inline-flex items-center justify-center gap-2 rounded-lg font-bold transition-all focus:outline-none focus:ring-2 focus:ring-primary/30 disabled:opacity-50 disabled:cursor-not-allowed';

  const variants = {
    primary:
      'bg-primary text-white shadow-lg shadow-primary/20 hover:bg-primary/90',
    secondary:
      'border border-slate-200 bg-white text-slate-700 hover:bg-slate-50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200',
  };

  return (
    <button
      className={cn(base, variants[variant], className)}
      {...props}
    />
  );
};

export default Button;