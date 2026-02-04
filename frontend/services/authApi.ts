import {fetcher} from '@/lib/fetcher';
import type {
  LoginResponse,
  AuthMeResponse,
  User,
  MessageResponse,
  ValidateTokenResponse,
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

// Login con Apple
export function loginWithApple(
  idToken: string,
  firstName?: string,
  lastName?: string
): Promise<LoginResponse> {
  return fetcher<LoginResponse>('/api/auth/apple', {
    method: 'POST',
    body: JSON.stringify({idToken, firstName, lastName}),
  });
}

// Logout
export function logout() {
  return fetcher('/api/auth/logout', {
    method: 'POST',
  });
}

// Forgot Password
export function forgotPassword(email: string): Promise<MessageResponse> {
  return fetcher<MessageResponse>('/api/auth/forgot-password', {
    method: 'POST',
    body: JSON.stringify({email}),
  });
}

// Reset Password
export function resetPassword(
  token: string,
  newPassword: string
): Promise<MessageResponse> {
  return fetcher<MessageResponse>('/api/auth/reset-password', {
    method: 'POST',
    body: JSON.stringify({token, newPassword}),
  });
}

// Validate Reset Token
export async function validateResetToken(
  token: string
): Promise<ValidateTokenResponse> {
  const res = await fetch(`/api/auth/reset-password?token=${encodeURIComponent(token)}`);
  return res.json();
}