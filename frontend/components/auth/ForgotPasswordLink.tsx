import Link from 'next/link';

const ForgotPasswordLink = () => {
  return (
    <div className="text-right">
      <Link
        href="/forgot-password"
        className="text-sm font-bold text-primary hover:text-primary/80"
      >
        Forgot password?
      </Link>
    </div>
  );
};

export default ForgotPasswordLink;
