import { ArrowRight } from 'lucide-react';
import Button from '@/components/ui/Button';

const SubmitButton = () => {
  return (
    <Button type="submit" className="w-full py-4">
      <span>Sign In</span>
      <ArrowRight className="size-5" />
    </Button>
  );
};

export default SubmitButton;