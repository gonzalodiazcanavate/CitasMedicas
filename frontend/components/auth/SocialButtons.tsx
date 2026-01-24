// src/components/login/socialButtons.tsx
import GoogleIcon from '../icons/GoogleIcon';
import AppleIcon from '../icons/AppleIcon';

function SocialButtons() {
  return (
    <div className="grid grid-cols-2 gap-4">
      {/* Google */}
      <button className="flex items-center justify-center gap-3 rounded-lg border border-slate-200 px-4 py-3 
        font-semibold text-slate-700 transition-colors hover:bg-slate-50 dark:border-slate-700 
      dark:text-slate-200 dark:hover:bg-slate-800">
        <GoogleIcon className="w-5 h-5" />
        <span>Google</span>
      </button>

      {/* Apple */}
      <button className="flex items-center justify-center gap-3 rounded-lg border border-slate-200 px-4 py-3 
        font-semibold text-slate-700 transition-colors hover:bg-slate-50 dark:border-slate-700
       dark:text-slate-200 dark:hover:bg-slate-800">
        <AppleIcon className="w-5 h-5" />
        <span>Apple</span>
      </button>
    </div>
  );
}

export default SocialButtons;