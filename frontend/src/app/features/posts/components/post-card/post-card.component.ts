import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Post } from '../../../../core/models';
import { PostService } from '../../../../core/services/post.service';
import { AuthService } from '../../../../core/services/auth.service';
import { EditPostDialogComponent } from '../edit-post-dialog/edit-post-dialog.component';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-post-card',
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.scss']
})
export class PostCardComponent {
  @Input() post!: Post;
  @Input() showAuthor = true;
  @Output() postDeleted = new EventEmitter<number>();
  @Output() postUpdated = new EventEmitter<Post>();

  isLiking = false;
  showComments = false;

  constructor(
    private postService: PostService,
    private authService: AuthService,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  get isOwnPost(): boolean {
    return this.post.userId === this.authService.getCurrentUserId();
  }

  toggleLike(): void {
    if (this.isLiking) return;

    this.isLiking = true;
    const wasLiked = this.post.likedByCurrentUser;

    // Optimistic update
    this.post.likedByCurrentUser = !wasLiked;
    this.post.likeCount += wasLiked ? -1 : 1;

    this.postService.toggleLike(this.post.id).subscribe({
      next: (updatedPost) => {
        this.post.likeCount = updatedPost.likeCount;
        this.post.likedByCurrentUser = updatedPost.likedByCurrentUser;
        this.isLiking = false;
      },
      error: () => {
        // Revert on error
        this.post.likedByCurrentUser = wasLiked;
        this.post.likeCount += wasLiked ? 1 : -1;
        this.isLiking = false;
        this.snackBar.open('Failed to update like', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  toggleComments(): void {
    this.showComments = !this.showComments;
  }

  viewAuthorProfile(): void {
    this.router.navigate(['/users', this.post.userId]);
  }

  editPost(): void {
    const dialogRef = this.dialog.open(EditPostDialogComponent, {
      width: '500px',
      data: {
        content: this.post.content,
        mediaUrl: this.post.mediaUrl,
        mediaType: this.post.mediaType
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.postService.updatePost(this.post.id, result).subscribe({
          next: (updatedPost) => {
            this.post = updatedPost;
            this.postUpdated.emit(updatedPost);
            this.snackBar.open('Post updated successfully', 'Close', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
          },
          error: () => {
            this.snackBar.open('Failed to update post', 'Close', {
              duration: 5000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }
    });
  }

  deletePost(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Delete Post',
        message: 'Are you sure you want to delete this post? This action cannot be undone.',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.postService.deletePost(this.post.id).subscribe({
          next: () => {
            this.postDeleted.emit(this.post.id);
            this.snackBar.open('Post deleted successfully', 'Close', {
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
}
