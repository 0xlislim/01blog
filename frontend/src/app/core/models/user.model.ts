export interface User {
  id: number;
  username: string;
  email: string;
  displayName: string;
  bio?: string;
  role: 'USER' | 'ADMIN';
  banned: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserProfile {
  id: number;
  username: string;
  displayName: string;
  bio?: string;
  role: 'USER' | 'ADMIN';
  createdAt: string;
  postCount: number;
  subscriberCount: number;
  subscribedToCount: number;
  isSubscribed?: boolean;
}

export interface UpdateProfileRequest {
  displayName?: string;
  bio?: string;
}

export interface AdminUserResponse {
  id: number;
  username: string;
  email: string;
  displayName: string;
  role: 'USER' | 'ADMIN';
  banned: boolean;
  createdAt: string;
  postCount: number;
  subscriberCount: number;
}
