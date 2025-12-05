export interface Post {
  id: number;
  content: string;
  mediaUrl?: string;
  mediaType?: 'IMAGE' | 'VIDEO';
  createdAt: string;
  updatedAt: string;
  userId: number;
  username: string;
  userDisplayName: string;
  likeCount: number;
  commentCount: number;
  likedByCurrentUser: boolean;
}

export interface PostRequest {
  content: string;
  mediaUrl?: string;
  mediaType?: 'IMAGE' | 'VIDEO';
}

export interface Comment {
  id: number;
  content: string;
  createdAt: string;
  userId: number;
  username: string;
  userDisplayName: string;
}

export interface CommentRequest {
  content: string;
}
