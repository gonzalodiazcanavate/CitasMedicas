import {ArrowRight, Loader2} from 'lucide-react';
import Button from '@/components/ui/Button';

interface SubmitButtonProps {
  disabled?: boolean;
}

const SubmitButton = ({disabled = false}: SubmitButtonProps) => {
  return (
    <Button type="submit" className="w-full py-4" disabled={disabled}>
      {disabled ? (
        <>
          <Loader2 className="size-5 animate-spin" />
          <span>Signing in...</span>
        </>
      ) : (
        <>
          <span>Sign In</span>
          <ArrowRight className="size-5" />
        </>
      )}
    </Button>
  );
};

export default SubmitButton;