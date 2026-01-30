'use client';

import {useState, useEffect} from 'react';
import AppleIcon from '../icons/AppleIcon';
import {loginWithApple} from '@/services/authApi';

interface AppleButtonProps {
  onSuccess?: () => void;
  onError?: (error: string) => void;
}

// Declaración de tipos para AppleID JS SDK
declare global {
  interface Window {
    AppleID?: {
      auth: {
        init: (config: {
          clientId: string;
          scope: string;
          redirectURI: string;
          usePopup: boolean;
        }) => void;
        signIn: () => Promise<{
          authorization: {
            id_token: string;
            code: string;
          };
          user?: {
            name?: {
              firstName?: string;
              lastName?: string;
            };
            email?: string;
          };
        }>;
      };
    };
  }
}

function AppleButton({onSuccess, onError}: AppleButtonProps) {
  const [loading, setLoading] = useState(false);
  const [sdkLoaded, setSdkLoaded] = useState(false);

  useEffect(() => {
    // Cargar el SDK de Apple si no está cargado
    if (typeof window !== 'undefined' && !window.AppleID) {
      const script = document.createElement('script');
      script.src =
        'https://appleid.cdn-apple.com/appleauth/static/jsapi/appleid/1/en_US/appleid.auth.js';
      script.async = true;
      script.onload = () => {
        setSdkLoaded(true);
        initAppleAuth();
      };
      script.onerror = () => {
        console.error('Error cargando Apple Sign In SDK');
      };
      document.body.appendChild(script);
    } else if (window.AppleID) {
      setSdkLoaded(true);
      initAppleAuth();
    }
  }, []);

  const initAppleAuth = () => {
    const clientId = process.env.NEXT_PUBLIC_APPLE_CLIENT_ID;
    if (!clientId || !window.AppleID) return;

    window.AppleID.auth.init({
      clientId,
      scope: 'name email',
      redirectURI: window.location.origin,
      usePopup: true,
    });
  };

  const handleAppleLogin = async () => {
    if (!window.AppleID) {
      onError?.('Apple Sign In no está disponible');
      return;
    }

    const clientId = process.env.NEXT_PUBLIC_APPLE_CLIENT_ID;
    if (!clientId) {
      onError?.('Apple Sign In no está configurado porque vale demasiado y este proyecto es de prueba');
      return;
    }

    setLoading(true);
    try {
      const response = await window.AppleID.auth.signIn();

      // Apple solo envía el nombre en el primer login
      const firstName = response.user?.name?.firstName;
      const lastName = response.user?.name?.lastName;
      const idToken = response.authorization.id_token;

      const result = await loginWithApple(idToken, firstName, lastName);

      if (result.message && !result.user) {
        // Usuario no registrado o error
        onError?.(result.message);
      } else {
        onSuccess?.();
      }
    } catch (err) {
      // El usuario canceló o hubo un error
      if (err instanceof Error) {
        if (err.message.includes('popup_closed')) {
          // Usuario cerró el popup, no mostrar error
          return;
        }
        onError?.(err.message);
      } else {
        onError?.('Error al iniciar sesión con Apple');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <button
      type="button"
      onClick={handleAppleLogin}
      disabled={loading || !sdkLoaded}
      className="flex items-center justify-center gap-3 rounded-lg border border-slate-200 px-4 py-3 
        font-semibold text-slate-700 transition-colors hover:bg-slate-50 dark:border-slate-700 
        dark:text-slate-200 dark:hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
    >
      {loading ? (
        <div className="size-5 animate-spin rounded-full border-2 border-slate-300 border-t-primary" />
      ) : (
        <AppleIcon className="size-5" />
      )}
      <span>{loading ? 'Conectando...' : 'Apple'}</span>
    </button>
  );
}

export default AppleButton;
