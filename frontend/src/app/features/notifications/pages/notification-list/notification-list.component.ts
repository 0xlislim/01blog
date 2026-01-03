import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationService } from '../../../../core/services/notification.service';
import { Notification } from '../../../../core/models';

@Component({
  selector: 'app-notification-list',
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.scss']
})
export class NotificationListComponent implements OnInit {
  notifications: Notification[] = [];
  isLoading = true;
  selectedTab = 0;

  constructor(
    private notificationService: NotificationService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.isLoading = true;
    this.notificationService.getNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Failed to load notifications', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  get allNotifications(): Notification[] {
    return this.notifications;
  }

  get unreadNotifications(): Notification[] {
    return this.notifications.filter(n => !n.read);
  }

  get hasUnread(): boolean {
    return this.unreadNotifications.length > 0;
  }

  onMarkAsRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: (updated) => {
        const index = this.notifications.findIndex(n => n.id === id);
        if (index !== -1) {
          this.notifications[index] = updated;
        }
      },
      error: () => {
        this.snackBar.open('Failed to mark notification as read', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onDelete(id: number): void {
    this.notificationService.deleteNotification(id).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== id);
        this.snackBar.open('Notification deleted', 'Close', {
          duration: 2000
        });
      },
      error: () => {
        this.snackBar.open('Failed to delete notification', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications = this.notifications.map(n => ({ ...n, read: true }));
        this.snackBar.open('All notifications marked as read', 'Close', {
          duration: 2000
        });
      },
      error: () => {
        this.snackBar.open('Failed to mark all as read', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}
