import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PostService } from '../../../../core/services/post.service';
import { FileService } from '../../../../core/services/file.service';

@Component({
  selector: 'app-create-post-dialog',
  templateUrl: './create-post-dialog.component.html',
  styleUrls: ['./create-post-dialog.component.scss']
})
export class CreatePostDialogComponent implements OnInit {
  postForm!: FormGroup;
  isSubmitting = false;
  isUploading = false;

  selectedFile: File | null = null;
  mediaPreview: string | null = null;
  mediaType: 'IMAGE' | 'VIDEO' | null = null;
  uploadedMediaUrl: string | null = null;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<CreatePostDialogComponent>,
    private postService: PostService,
    private fileService: FileService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.postForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(5000)]]
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    // Validate file type
    const allowedImageTypes = ['image/jpeg', 'image/png', 'image/gif'];
    const allowedVideoTypes = ['video/mp4', 'video/webm'];

    if (allowedImageTypes.includes(file.type)) {
      this.mediaType = 'IMAGE';
    } else if (allowedVideoTypes.includes(file.type)) {
      this.mediaType = 'VIDEO';
    } else {
      this.snackBar.open('Invalid file type. Allowed: JPEG, PNG, GIF, MP4, WEBM', 'Close', {
        duration: 5000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    // Validate file size (10MB max)
    if (file.size > 10 * 1024 * 1024) {
      this.snackBar.open('File size must be less than 10MB', 'Close', {
        duration: 5000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    this.selectedFile = file;

    // Create preview
    const reader = new FileReader();
    reader.onload = () => {
      this.mediaPreview = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  removeMedia(): void {
    this.selectedFile = null;
    this.mediaPreview = null;
    this.mediaType = null;
    this.uploadedMediaUrl = null;
  }

  async onSubmit(): Promise<void> {
    if (this.postForm.invalid || this.isSubmitting) return;

    this.isSubmitting = true;

    try {
      // Upload file first if selected
      if (this.selectedFile && !this.uploadedMediaUrl) {
        this.isUploading = true;
        const uploadResult = await this.fileService.uploadFile(this.selectedFile).toPromise();
        this.uploadedMediaUrl = uploadResult?.url || null;
        this.isUploading = false;
      }

      // Create post
      const postData = {
        content: this.postForm.value.content,
        mediaUrl: this.uploadedMediaUrl || undefined,
        mediaType: this.uploadedMediaUrl ? this.mediaType || undefined : undefined
      };

      this.postService.createPost(postData).subscribe({
        next: (post) => {
          this.dialogRef.close(post);
          this.snackBar.open('Post created successfully!', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
        },
        error: () => {
          this.isSubmitting = false;
          this.snackBar.open('Failed to create post', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    } catch {
      this.isSubmitting = false;
      this.isUploading = false;
      this.snackBar.open('Failed to upload file', 'Close', {
        duration: 5000,
        panelClass: ['error-snackbar']
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
