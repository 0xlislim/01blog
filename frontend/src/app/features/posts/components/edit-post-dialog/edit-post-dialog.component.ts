import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

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

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditPostDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EditPostDialogData
  ) {}

  ngOnInit(): void {
    this.postForm = this.fb.group({
      content: [this.data.content, [Validators.required, Validators.maxLength(5000)]]
    });
  }

  onSubmit(): void {
    if (this.postForm.invalid) {
      this.postForm.markAllAsTouched();
      return;
    }

    this.dialogRef.close({
      content: this.postForm.value.content,
      mediaUrl: this.data.mediaUrl,
      mediaType: this.data.mediaType
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
