import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PostService } from '../../../../core/services/post.service';
import { FileService, UploadProgress } from '../../../../core/services/file.service';
import { UploadedFile } from '../../../../shared/components/file-upload/file-upload.component';

@Component({
  selector: 'app-create-post-dialog',
  templateUrl: './create-post-dialog.component.html',
  styleUrls: ['./create-post-dialog.component.scss']
})
export class CreatePostDialogComponent implements OnInit {
  postForm!: FormGroup;
  isSubmitting = false;
  isUploading = false;
  uploadProgress = 0;

  uploadedFile: UploadedFile | null = null;
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

  onFileSelected(file: UploadedFile): void {
    this.uploadedFile = file;
    this.uploadedMediaUrl = null;
  }

  onFileRemoved(): void {
    this.uploadedFile = null;
    this.uploadedMediaUrl = null;
    this.uploadProgress = 0;
  }

  onValidationError(error: string): void {
    this.snackBar.open(error, 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }

  async onSubmit(): Promise<void> {
    if (this.postForm.invalid || this.isSubmitting) return;

    this.isSubmitting = true;

    try {
      // Upload file first if selected
      if (this.uploadedFile && !this.uploadedMediaUrl) {
        this.isUploading = true;
        this.uploadProgress = 0;

        const progressCallback = (progress: UploadProgress) => {
          this.uploadProgress = progress.progress;
        };

        const uploadResult = await this.fileService
          .uploadFileWithProgress(this.uploadedFile.file, progressCallback)
          .toPromise();

        // Store the relative URL - it will be converted to full URL when displaying
        this.uploadedMediaUrl = uploadResult?.fileUrl || null;
        this.isUploading = false;
      }

      // Create post
      const postData = {
        content: this.postForm.value.content,
        mediaUrl: this.uploadedMediaUrl || undefined,
        mediaType: this.uploadedMediaUrl && this.uploadedFile ? this.uploadedFile.type : undefined
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
      this.uploadProgress = 0;
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
