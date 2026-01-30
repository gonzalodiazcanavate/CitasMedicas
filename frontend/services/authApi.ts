import {fetcher} from '@/lib/fetcher';
import type {
  LoginResponse,
  AuthMeResponse,
  User,
} from '@/types/auth';

// Login
export function loginUser(
  emailOrUsername: string,
  password: string,
  rememberMe: boolean = false
): Promise<LoginResponse> {
  return fetcher<LoginResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({emailOrUsername, password, rememberMe}),
  });
}

// Registro
export function register(
  username: string,
  email: string,
  password: string
) {
  return fetcher('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({username, email, password}),
  });
}

// Usuario autenticado
export async function getCurrentUser(): Promise<User | null> {
  const data = await fetcher<AuthMeResponse>('/api/auth/me');
  return data.auth ? data.user ?? null : null;
}

// Login con Google
export function loginWithGoogle(idToken: string): Promise<LoginResponse> {
  return fetcher<LoginResponse>('/api/auth/google', {
    method: 'POST',
    body: JSON.stringify({idToken}),
  });
}

// Logout
export function logout() {
  return fetcher('/api/auth/logout', {
    method: 'POST',
  });
}