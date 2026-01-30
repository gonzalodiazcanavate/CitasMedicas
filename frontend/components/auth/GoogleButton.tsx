'use client';

import {useGoogleLogin} from '@react-oauth/google';
import {useState} from 'react';
import GoogleIcon from '../icons/GoogleIcon';
import {loginWithGoogle} from '@/services/authApi';

interface GoogleButtonProps {
  onSuccess?: () => void;
  onError?: (error: string) => void;
}

function GoogleButton({onSuccess, onError}: GoogleButtonProps) {
  const [loading, setLoading] = useState(false);

  const googleLogin = useGoogleLogin({
    flow: 'implicit',
    onSuccess: async (tokenResponse) => {
      setLoading(true);
      try {
        // Obtener el ID token usando el access token
        const userInfoResponse = await fetch(
          'https://www.googleapis.com/oauth2/v3/userinfo',
          {
            headers: {Authorization: `Bearer ${tokenResponse.access_token}`},
          }
        );

        if (!userInfoResponse.ok) {
          throw new Error('Error al obtener información del usuario de Google');
        }

        // Usar el access_token como credential para el backend
        // Nota: Para mayor seguridad, considera usar el flujo de authorization code
        const result = await loginWithGoogle(tokenResponse.access_token);

        if (result.message && !result.user) {
          // Usuario no registrado
          onError?.(result.message);
        } else {
          onSuccess?.();
        }
      } catch (err) {
        onError?.(err instanceof Error ? err.message : 'Error al iniciar sesión con Google');
      } finally {
        setLoading(false);
      }
    },
    onError: (errorResponse) => {
      console.error('Google Login Error:', errorResponse);
      onError?.('Error al conectar con Google');
    },
  });

  return (
    <button
      type="button"
      onClick={() => googleLogin()}
      disabled={loading}
      className="flex items-center justify-center gap-3 rounded-lg border border-slate-200 px-4 py-3 
        font-semibold text-slate-700 transition-colors hover:bg-slate-50 dark:border-slate-700 
        dark:text-slate-200 dark:hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
    >
      {loading ? (
        <div className="size-5 animate-spin rounded-full border-2 border-slate-300 border-t-primary" />
      ) : (
        <GoogleIcon className="size-5" />
      )}
      <span>{loading ? 'Conectando...' : 'Google'}</span>
    </button>
  );
}

export default GoogleButton;
