'use client';

const LoginFormFooter = () => {
    return (
        <footer className="flex items-center justify-center gap-6 px-8 py-8 text-xs font-medium text-slate-400 md:justify-start sm:px-12 md:px-24 xl:px-32">
            <a href="#" className="transition-colors hover:text-primary">
            Privacy Policy
            </a>
            <a href="#" className="transition-colors hover:text-primary">
            Terms of Service
            </a>
            <a href="#" className="transition-colors hover:text-primary">
            Help Center
            </a>
        </footer>
    );
};

export default LoginFormFooter;