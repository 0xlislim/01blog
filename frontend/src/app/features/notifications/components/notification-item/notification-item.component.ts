import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Notification } from '../../../../core/models';

@Component({
  selector: 'app-notification-item',
  templateUrl: './notification-item.component.html',
  styleUrls: ['./notification-item.component.scss']
})
export class NotificationItemComponent {
  @Input() notification!: Notification;
  @Output() markAsRead = new EventEmitter<number>();
  @Output() delete = new EventEmitter<number>();

  constructor(private router: Router) {}

  getIcon(): string {
    switch (this.notification.type) {
      case 'NEW_SUBSCRIBER':
        return 'person_add';
      case 'NEW_LIKE':
        return 'favorite';
      case 'NEW_COMMENT':
        return 'comment';
      case 'NEW_POST':
        return 'article';
      default:
        return 'notifications';
    }
  }

  getIconColor(): string {
    switch (this.notification.type) {
      case 'NEW_SUBSCRIBER':
        return 'primary';
      case 'NEW_LIKE':
        return 'warn';
      case 'NEW_COMMENT':
        return 'accent';
      case 'NEW_POST':
        return 'primary';
      default:
        return '';
    }
  }

  onClick(): void {
    if (!this.notification.read) {
      this.markAsRead.emit(this.notification.id);
    }

    // Navigate based on notification type
    if (this.notification.postId) {
      this.router.navigate(['/feed']);
    } else if (this.notification.actorId) {
      this.router.navigate(['/users', this.notification.actorId]);
    }
  }

  onMarkAsRead(event: Event): void {
    event.stopPropagation();
    this.markAsRead.emit(this.notification.id);
  }

  onDelete(event: Event): void {
    event.stopPropagation();
    this.delete.emit(this.notification.id);
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) {
      return 'Just now';
    } else if (diffMins < 60) {
      return `${diffMins}m ago`;
    } else if (diffHours < 24) {
      return `${diffHours}h ago`;
    } else if (diffDays < 7) {
      return `${diffDays}d ago`;
    } else {
      return date.toLocaleDateString();
    }
  }
}
