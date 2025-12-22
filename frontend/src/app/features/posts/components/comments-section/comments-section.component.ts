import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Comment } from '../../../../core/models';
import { PostService } from '../../../../core/services/post.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-comments-section',
  templateUrl: './comments-section.component.html',
  styleUrls: ['./comments-section.component.scss']
})
export class CommentsSectionComponent implements OnInit {
  @Input() postId!: number;
  @Output() commentAdded = new EventEmitter<Comment>();
  @Output() commentDeleted = new EventEmitter<number>();

  comments: Comment[] = [];
  isLoading = true;
  isSubmitting = false;
  commentControl = new FormControl('', [Validators.required, Validators.maxLength(500)]);

  currentUserId: number | null;

  constructor(
    private postService: PostService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.currentUserId = this.authService.getCurrentUserId();
  }

  ngOnInit(): void {
    this.loadComments();
  }

  loadComments(): void {
    this.isLoading = true;
    this.postService.getComments(this.postId).subscribe({
      next: (comments) => {
        this.comments = comments;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Failed to load comments', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  submitComment(): void {
    if (this.commentControl.invalid || this.isSubmitting) return;

    this.isSubmitting = true;
    const content = this.commentControl.value!.trim();

    this.postService.addComment(this.postId, { content }).subscribe({
      next: (comment) => {
        this.comments.unshift(comment);
        this.commentControl.reset();
        this.isSubmitting = false;
        this.commentAdded.emit(comment);
      },
      error: () => {
        this.isSubmitting = false;
        this.snackBar.open('Failed to add comment', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  deleteComment(commentId: number): void {
    this.postService.deleteComment(commentId).subscribe({
      next: () => {
        this.comments = this.comments.filter(c => c.id !== commentId);
        this.commentDeleted.emit(commentId);
        this.snackBar.open('Comment deleted', 'Close', {
          duration: 3000
        });
      },
      error: () => {
        this.snackBar.open('Failed to delete comment', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  viewProfile(userId: number): void {
    this.router.navigate(['/users', userId]);
  }

  isOwnComment(comment: Comment): boolean {
    return comment.userId === this.currentUserId;
  }
}
