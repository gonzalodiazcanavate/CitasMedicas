'use client';

import {GoogleOAuthProvider as GoogleProvider} from '@react-oauth/google';

interface Props {
  children: React.ReactNode;
}

export function GoogleOAuthProvider({children}: Props) {
  const clientId = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;

  if (!clientId) {
    console.warn('NEXT_PUBLIC_GOOGLE_CLIENT_ID no está configurado');
    return <>{children}</>;
  }

  return <GoogleProvider clientId={clientId}>{children}</GoogleProvider>;
}
