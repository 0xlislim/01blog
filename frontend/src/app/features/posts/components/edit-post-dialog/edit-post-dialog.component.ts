import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FileService } from '../../../../core/services/file.service';

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

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditPostDialogComponent>,
    private fileService: FileService,
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

  removeMedia(): void {
    this.mediaUrl = undefined;
    this.mediaType = undefined;
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      return;
    }

    this.dialogRef.close({
      content: this.postForm.value.content,
      mediaUrl: this.mediaUrl || null,
      mediaType: this.mediaType || null
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
