import GoogleButton from './GoogleButton';
import AppleIcon from '../icons/AppleIcon';

interface SocialButtonsProps {
  onGoogleSuccess?: () => void;
  onGoogleError?: (error: string) => void;
}

function SocialButtons({onGoogleSuccess, onGoogleError}: SocialButtonsProps) {
  return (
    <div className="grid grid-cols-2 gap-4">
      {/* Google */}
      <GoogleButton onSuccess={onGoogleSuccess} onError={onGoogleError} />

      {/* Apple */}
      <button
        type="button"
        className="flex items-center justify-center gap-3 rounded-lg border border-slate-200 px-4 py-3 
        font-semibold text-slate-700 transition-colors hover:bg-slate-50 dark:border-slate-700
       dark:text-slate-200 dark:hover:bg-slate-800"
      >
        <AppleIcon className="size-5" />
        <span>Apple</span>
      </button>
    </div>
  );
}

export default SocialButtons;