import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AdminService } from '../../../../core/services/admin.service';
import { AdminUserResponse, Post, Report } from '../../../../core/models';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  users: AdminUserResponse[] = [];
  posts: Post[] = [];
  reports: Report[] = [];

  isLoadingUsers = true;
  isLoadingPosts = true;
  isLoadingReports = true;

  userDisplayedColumns = ['username', 'email', 'displayName', 'role', 'banned', 'postCount', 'createdAt', 'actions'];
  postDisplayedColumns = ['content', 'username', 'likeCount', 'commentCount', 'createdAt', 'actions'];
  reportDisplayedColumns = ['reporterUsername', 'reportedUsername', 'reason', 'createdAt', 'actions'];

  constructor(
    private adminService: AdminService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadPosts();
    this.loadReports();
  }

  // Users
  loadUsers(): void {
    this.isLoadingUsers = true;
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.isLoadingUsers = false;
      },
      error: () => {
        this.isLoadingUsers = false;
        this.snackBar.open('Failed to load users', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  banUser(user: AdminUserResponse): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Ban User',
        message: `Are you sure you want to ban @${user.username}? They will not be able to access their account.`,
        confirmText: 'Ban',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.adminService.banUser(user.id).subscribe({
          next: () => {
            user.banned = true;
            this.snackBar.open(`User @${user.username} has been banned`, 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
          },
          error: () => {
            this.snackBar.open('Failed to ban user', 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }
    });
  }

  unbanUser(user: AdminUserResponse): void {
    this.adminService.unbanUser(user.id).subscribe({
      next: () => {
        user.banned = false;
        this.snackBar.open(`User @${user.username} has been unbanned`, 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      },
      error: () => {
        this.snackBar.open('Failed to unban user', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  deleteUser(user: AdminUserResponse): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete User',
        message: `Are you sure you want to permanently delete @${user.username}? This action cannot be undone.`,
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.adminService.deleteUser(user.id).subscribe({
          next: () => {
            this.users = this.users.filter(u => u.id !== user.id);
            this.snackBar.open(`User @${user.username} has been deleted`, 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
          },
          error: () => {
            this.snackBar.open('Failed to delete user', 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }
    });
  }

  // Posts
  loadPosts(): void {
    this.isLoadingPosts = true;
    this.adminService.getAllPosts().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoadingPosts = false;
      },
      error: () => {
        this.isLoadingPosts = false;
        this.snackBar.open('Failed to load posts', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  deletePost(post: Post): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete Post',
        message: 'Are you sure you want to delete this post? This action cannot be undone.',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.adminService.deletePost(post.id).subscribe({
          next: () => {
            this.posts = this.posts.filter(p => p.id !== post.id);
            this.snackBar.open('Post has been deleted', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
          },
          error: () => {
            this.snackBar.open('Failed to delete post', 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }
    });
  }

  // Reports
  loadReports(): void {
    this.isLoadingReports = true;
    this.adminService.getAllReports().subscribe({
      next: (reports) => {
        this.reports = reports;
        this.isLoadingReports = false;
      },
      error: () => {
        this.isLoadingReports = false;
        this.snackBar.open('Failed to load reports', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  dismissReport(report: Report): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Dismiss Report',
        message: 'Are you sure you want to dismiss this report?',
        confirmText: 'Dismiss',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.adminService.dismissReport(report.id).subscribe({
          next: () => {
            this.reports = this.reports.filter(r => r.id !== report.id);
            this.snackBar.open('Report has been dismissed', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
          },
          error: () => {
            this.snackBar.open('Failed to dismiss report', 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }
    });
  }

  truncateContent(content: string, maxLength: number = 100): string {
    if (content.length <= maxLength) return content;
    return content.substring(0, maxLength) + '...';
  }
}
