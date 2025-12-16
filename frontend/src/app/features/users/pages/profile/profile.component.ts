import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../../../core/services/user.service';
import { SubscriptionService } from '../../../../core/services/subscription.service';
import { AuthService } from '../../../../core/services/auth.service';
import { UserProfile } from '../../../../core/models';
import { EditProfileDialogComponent } from '../../components/edit-profile-dialog/edit-profile-dialog.component';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  user: UserProfile | null = null;
  isLoading = true;
  isOwnProfile = false;
  isSubscribing = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private subscriptionService: SubscriptionService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const userId = params['id'];
      if (userId) {
        this.loadUserProfile(+userId);
      } else {
        // Load own profile
        const currentUserId = this.authService.getCurrentUserId();
        if (currentUserId) {
          this.loadUserProfile(currentUserId);
        }
      }
    });
  }

  loadUserProfile(userId: number): void {
    this.isLoading = true;
    const currentUserId = this.authService.getCurrentUserId();
    this.isOwnProfile = currentUserId === userId;

    this.userService.getUserProfile(userId).subscribe({
      next: (user) => {
        this.user = user;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.snackBar.open('Failed to load profile', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        this.router.navigate(['/feed']);
      }
    });
  }

  openEditDialog(): void {
    if (!this.user) return;

    const dialogRef = this.dialog.open(EditProfileDialogComponent, {
      width: '400px',
      data: {
        displayName: this.user.displayName,
        bio: this.user.bio
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.user) {
        this.userService.updateProfile(this.user.id, result).subscribe({
          next: (updatedUser) => {
            this.user = updatedUser;
            this.snackBar.open('Profile updated successfully', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
          },
          error: () => {
            this.snackBar.open('Failed to update profile', 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }
    });
  }

  toggleSubscription(): void {
    if (!this.user || this.isOwnProfile) return;

    this.isSubscribing = true;

    if (this.user.isSubscribed) {
      this.subscriptionService.unsubscribe(this.user.id).subscribe({
        next: () => {
          if (this.user) {
            this.user.isSubscribed = false;
            this.user.subscriberCount--;
          }
          this.isSubscribing = false;
          this.snackBar.open('Unsubscribed successfully', 'Close', {
            duration: 3000
          });
        },
        error: () => {
          this.isSubscribing = false;
          this.snackBar.open('Failed to unsubscribe', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    } else {
      this.subscriptionService.subscribe(this.user.id).subscribe({
        next: () => {
          if (this.user) {
            this.user.isSubscribed = true;
            this.user.subscriberCount++;
          }
          this.isSubscribing = false;
          this.snackBar.open('Subscribed successfully', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
        },
        error: () => {
          this.isSubscribing = false;
          this.snackBar.open('Failed to subscribe', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }
}
