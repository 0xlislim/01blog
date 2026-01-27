export interface Notification {
  id: number;
  type: 'NEW_SUBSCRIBER' | 'NEW_LIKE' | 'NEW_COMMENT' | 'NEW_POST';
  message: string;
  read: boolean;
  createdAt: string;
  relatedPostId?: number;
  relatedPostContent?: string;
  relatedUserId?: number;
  relatedUsername?: string;
  relatedUserDisplayName?: string;
}

export interface NotificationCount {
  count: number;
}
