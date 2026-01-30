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

export interface AuthMeResponse {
  auth: boolean;
  user?: User;
}

export interface ApiError {
  error: string;
}