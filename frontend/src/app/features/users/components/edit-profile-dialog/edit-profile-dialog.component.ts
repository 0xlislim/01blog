import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FileService, UploadProgress } from '../../../../core/services/file.service';
import { UploadedFile, FileValidationConfig } from '../../../../shared/components/file-upload/file-upload.component';

export interface EditProfileDialogData {
  displayName: string;
  bio?: string;
  avatarUrl?: string;
}

@Component({
  selector: 'app-edit-profile-dialog',
  templateUrl: './edit-profile-dialog.component.html',
  styleUrls: ['./edit-profile-dialog.component.scss']
})
export class EditProfileDialogComponent implements OnInit {
  profileForm!: FormGroup;
  avatarUrl?: string;
  uploadedFile: UploadedFile | null = null;
  isUploading = false;
  uploadProgress = 0;
  isSubmitting = false;

  avatarConfig: FileValidationConfig = {
    maxSizeMB: 5,
    allowedImageTypes: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
    allowedVideoTypes: []
  };

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditProfileDialogComponent>,
    private fileService: FileService,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: EditProfileDialogData
  ) {
    this.avatarUrl = data.avatarUrl;
  }

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      displayName: [this.data.displayName, [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50)
      ]],
      bio: [this.data.bio || '', [Validators.maxLength(500)]]
    });
  }

  getAvatarUrl(): string {
    if (!this.avatarUrl) return '';
    return this.fileService.getFullMediaUrl(this.avatarUrl);
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

  removeAvatar(): void {
    this.avatarUrl = undefined;
    this.uploadedFile = null;
  }

  async onSubmit(): Promise<void> {
    if (this.profileForm.invalid || this.isSubmitting) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    try {
      // Upload new avatar if selected
      if (this.uploadedFile) {
        this.isUploading = true;
        this.uploadProgress = 0;

        const progressCallback = (progress: UploadProgress) => {
          this.uploadProgress = progress.progress;
        };

        const uploadResult = await this.fileService
          .uploadFileWithProgress(this.uploadedFile.file, progressCallback)
          .toPromise();

        this.avatarUrl = uploadResult?.fileUrl || undefined;
        this.isUploading = false;
      }

      this.dialogRef.close({
        ...this.profileForm.value,
        avatarUrl: this.avatarUrl || null
      });
    } catch {
      this.isSubmitting = false;
      this.isUploading = false;
      this.uploadProgress = 0;
      this.snackBar.open('Failed to upload avatar', 'Close', {
        duration: 5000,
        panelClass: ['error-snackbar']
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getErrorMessage(field: string): string {
    const control = this.profileForm.get(field);

    if (control?.hasError('required')) {
      return 'Display name is required';
    }
    if (control?.hasError('minlength')) {
      return 'Display name must be at least 2 characters';
    }
    if (control?.hasError('maxlength')) {
      const maxLength = field === 'displayName' ? 50 : 500;
      return `Maximum ${maxLength} characters allowed`;
    }
    return '';
  }
}
