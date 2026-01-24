'use client';

const LoginHeroBackground = () => {
  return (
    <div
      className="absolute inset-0 bg-cover bg-center"
      style={{backgroundImage: 'url(\'/login-background.png\')'}}
    >
      <div className="absolute inset-0 bg-primary/40 mix-blend-multiply" />
      <div className="absolute inset-0 bg-gradient-to-t from-primary/80 via-transparent to-transparent" />
    </div>
  );
};

export default LoginHeroBackground;