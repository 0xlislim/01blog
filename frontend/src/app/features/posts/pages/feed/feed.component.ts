import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Post } from '../../../../core/models';
import { PostService } from '../../../../core/services/post.service';
import { CreatePostDialogComponent } from '../../components/create-post-dialog/create-post-dialog.component';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss']
})
export class FeedComponent implements OnInit {
  posts: Post[] = [];
  isLoading = true;

  constructor(
    private postService: PostService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadFeed();
  }

  loadFeed(): void {
    this.isLoading = true;
    this.postService.getFeed().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Failed to load feed', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
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

  refreshFeed(): void {
    this.loadFeed();
  }
}
