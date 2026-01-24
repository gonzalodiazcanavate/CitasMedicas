'use client';

import {Eye, EyeOff, Lock} from 'lucide-react';
import {useState} from 'react';
import type {Dispatch, SetStateAction} from 'react';

type PasswordFieldProps = {
  value: string;
  onChange: Dispatch<SetStateAction<string>>;
};

const PasswordField = ({value, onChange}: PasswordFieldProps) => {
  const [showPassword, setShowPassword] = useState(false);

  const toggleShow = () => setShowPassword((prev) => !prev);

  return (
    <div className="space-y-2">
      <label
        htmlFor="password"
        className="text-sm font-semibold text-[#0d141b] dark:text-slate-200"
      >
        Password
      </label>

      <div className="relative">
        {/* Icono de candado */}
        <Lock className="absolute left-3 top-1/2 size-5 -translate-y-1/2 text-slate-400" />

        {/* Input */}
        <input
          id="password"
          type={showPassword ? 'text' : 'password'}
          placeholder="••••••••"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          className="w-full rounded-lg border border-slate-200 bg-slate-50 py-3.5 pl-10 pr-12 text-[#0d141b]
           placeholder:text-slate-400 transition-all focus:border-primary focus:outline-none focus:ring-2 
           focus:ring-primary/20 dark:border-slate-700 dark:bg-slate-800/50 dark:text-white"
        />

        {/* Botón para mostrar/ocultar */}
        <button
          type="button"
          onClick={toggleShow}
          className="absolute inset-y-0 right-0 flex items-center pr-3 text-slate-400 hover:text-slate-600"
        >
          {showPassword ? <EyeOff className="size-5" /> : <Eye className="size-5" />}
        </button>
      </div>
    </div>
  );
};

export default PasswordField;