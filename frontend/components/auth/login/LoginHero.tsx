'use client';

import LoginHeroBackground from './LoginHeroBackground';
import LoginHeroContent from './LoginHeroContent';
import LoginHeroFooter from './LoginHeroFooter';


export default function LoginHero() {
  return (
    <div className="relative hidden lg:flex lg:w-1/2 items-center justify-center overflow-hidden bg-primary">
      {/* Background image */}
      <LoginHeroBackground />

      {/* Content */}
      <LoginHeroContent />

      {/* Footer */}
      <LoginHeroFooter />
    </div>
  );
}