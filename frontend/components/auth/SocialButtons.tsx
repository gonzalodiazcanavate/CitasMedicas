import GoogleButton from './GoogleButton';
import AppleButton from './AppleButton';

interface SocialButtonsProps {
  onGoogleSuccess?: () => void;
  onGoogleError?: (error: string) => void;
  onAppleSuccess?: () => void;
  onAppleError?: (error: string) => void;
}

function SocialButtons({
  onGoogleSuccess,
  onGoogleError,
  onAppleSuccess,
  onAppleError,
}: SocialButtonsProps) {
  return (
    <div className="grid grid-cols-2 gap-4">
      {/* Google */}
      <GoogleButton onSuccess={onGoogleSuccess} onError={onGoogleError} />

      {/* Apple */}
      <AppleButton onSuccess={onAppleSuccess} onError={onAppleError} />
    </div>
  );
}

export default SocialButtons;