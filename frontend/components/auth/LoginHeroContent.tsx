'use client';

import {HeartPulse, Lock, ShieldCheck} from 'lucide-react';
import Badge from '../ui/Badge';

const LoginHeroContent = () => {
  return (
    <div className="relative z-10 max-w-xl p-16 text-white">
      {/* Brand */}
      <div className="mb-8 flex items-center gap-3">
        <div className="flex size-10 items-center justify-center rounded-xl border border-white/30 bg-white/20 backdrop-blur-md">
          <HeartPulse className="size-5" />
        </div>
        <h2 className="text-2xl font-bold tracking-tight">
                MedicalApp Professional
        </h2>
      </div>

      {/* Heading */}
      <h1 className="mb-6 text-5xl font-extrabold leading-tight">
              Professional Healthcare at Your Fingertips
      </h1>

      <p className="mb-8 text-lg font-medium text-white/90 leading-relaxed">
              Access your medical records, schedule appointments with world-class
              specialists, and manage your health journey securely.
      </p>

      {/* Badges */}
      <div className="flex gap-4">
        <Badge icon={ShieldCheck} label="HIPAA Compliant" />
        <Badge icon={Lock} label="End-to-End Encrypted" />
      </div>
    </div>
  );
};

export default LoginHeroContent;