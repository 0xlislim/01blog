import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FileService, UploadProgress } from '../../../../core/services/file.service';
import { UploadedFile } from '../../../../shared/components/file-upload/file-upload.component';

export interface EditPostDialogData {
  content: string;
  mediaUrl?: string;
  mediaType?: 'IMAGE' | 'VIDEO';
}

@Component({
  selector: 'app-edit-post-dialog',
  templateUrl: './edit-post-dialog.component.html',
  styleUrls: ['./edit-post-dialog.component.scss']
})
export class EditPostDialogComponent implements OnInit {
  postForm!: FormGroup;
  mediaUrl?: string;
  mediaType?: 'IMAGE' | 'VIDEO';

  // New file upload state
  uploadedFile: UploadedFile | null = null;
  isUploading = false;
  uploadProgress = 0;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditPostDialogComponent>,
    private fileService: FileService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: EditPostDialogData
  ) {
    this.mediaUrl = data.mediaUrl;
    this.mediaType = data.mediaType;
  }

  getMediaUrl(): string {
    if (!this.mediaUrl) return '';
    return this.fileService.getFullMediaUrl(this.mediaUrl);
  }

  ngOnInit(): void {
    this.postForm = this.fb.group({
      content: [this.data.content, [Validators.required, Validators.maxLength(5000)]]
    });
  }

  onFileSelected(file: UploadedFile): void {
    this.uploadedFile = file;
  }

  onFileRemoved(): void {
    this.uploadedFile = null;
    this.uploadProgress = 0;
  }

  onValidationError(error: string): void {
    this.snackBar.open(error, 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }

  removeMedia(): void {
    this.mediaUrl = undefined;
    this.mediaType = undefined;
    this.uploadedFile = null;
  }

  async onSubmit(): Promise<void> {
    if (this.postForm.invalid || this.isSubmitting) return;

    this.isSubmitting = true;

    try {
      // Upload new file if selected
      if (this.uploadedFile) {
        this.isUploading = true;
        this.uploadProgress = 0;

        const progressCallback = (progress: UploadProgress) => {
          this.uploadProgress = progress.progress;
        };

        const uploadResult = await this.fileService
          .uploadFileWithProgress(this.uploadedFile.file, progressCallback)
          .toPromise();

        this.mediaUrl = uploadResult?.fileUrl || undefined;
        this.mediaType = this.uploadedFile.type;
        this.isUploading = false;
      }

      this.dialogRef.close({
        content: this.postForm.value.content,
        mediaUrl: this.mediaUrl || null,
        mediaType: this.mediaType || null
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
