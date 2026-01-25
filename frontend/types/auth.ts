export interface LoginRequest {
  emailOrUsername: string;
  password: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface LoginResponse {
  user: User;
}

export interface AuthMeResponse {
  auth: boolean;
  user?: User;
}

export interface ApiError {
  error: string;
}