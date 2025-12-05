export interface Notification {
  id: number;
  type: 'NEW_SUBSCRIBER' | 'NEW_LIKE' | 'NEW_COMMENT' | 'NEW_POST';
  message: string;
  read: boolean;
  createdAt: string;
  actorId?: number;
  actorUsername?: string;
  postId?: number;
}

export interface NotificationCount {
  count: number;
}
