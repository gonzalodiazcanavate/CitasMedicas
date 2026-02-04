export interface LoginRequest {
  emailOrUsername: string;
  password: string;
  rememberMe: boolean;
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface LoginResponse {
  user: User | null;
  message?: string;
}

export interface GoogleLoginRequest {
  idToken: string;
}

export interface AppleLoginRequest {
  idToken: string;
  firstName?: string;
  lastName?: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export interface MessageResponse {
  message?: string;
  error?: string;
}

export interface ValidateTokenResponse {
  valid: boolean;
}

export interface AuthMeResponse {
  auth: boolean;
  user?: User;
}

export interface ApiError {
  error: string;
}