import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Post } from '../../../../core/models';
import { PostService } from '../../../../core/services/post.service';
import { AuthService } from '../../../../core/services/auth.service';
import { CreatePostDialogComponent } from '../create-post-dialog/create-post-dialog.component';

@Component({
  selector: 'app-user-posts-list',
  templateUrl: './user-posts-list.component.html',
  styleUrls: ['./user-posts-list.component.scss']
})
export class UserPostsListComponent implements OnInit, OnChanges {
  @Input() userId!: number;
  @Input() showCreateButton = false;

  posts: Post[] = [];
  isLoading = true;

  constructor(
    private postService: PostService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    if (this.userId) {
      this.loadPosts();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userId'] && !changes['userId'].firstChange) {
      this.loadPosts();
    }
  }

  loadPosts(): void {
    this.isLoading = true;
    this.postService.getUserPosts(this.userId).subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Failed to load posts', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  get isOwnProfile(): boolean {
    return this.userId === this.authService.getCurrentUserId();
  }

  openCreatePostDialog(): void {
    const dialogRef = this.dialog.open(CreatePostDialogComponent, {
      width: '500px',
      maxWidth: '90vw'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.posts.unshift(result);
      }
    });
  }

  onPostDeleted(postId: number): void {
    this.posts = this.posts.filter(p => p.id !== postId);
  }

  onPostUpdated(updatedPost: Post): void {
    const index = this.posts.findIndex(p => p.id === updatedPost.id);
    if (index !== -1) {
      this.posts[index] = updatedPost;
    }
  }
}
