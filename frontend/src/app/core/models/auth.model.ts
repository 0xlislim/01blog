export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  displayName: string;
}

export interface JwtResponse {
  accessToken: string;
  tokenType: string;
  userId: number;
  username: string;
  role: 'USER' | 'ADMIN';
}

export interface MessageResponse {
  message: string;
}
