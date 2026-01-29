import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface ReportDialogData {
  userId?: number;
  username?: string;
  postId?: number;
  postContent?: string;
}

@Component({
  selector: 'app-report-dialog',
  templateUrl: './report-dialog.component.html',
  styleUrls: ['./report-dialog.component.scss']
})
export class ReportDialogComponent implements OnInit {
  reportForm!: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<ReportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ReportDialogData
  ) {}

  ngOnInit(): void {
    this.reportForm = this.fb.group({
      reason: ['', [
        Validators.required,
        Validators.minLength(10),
        Validators.maxLength(500)
      ]]
    });
  }

  get isPostReport(): boolean {
    return !!this.data.postId;
  }

  get truncatedPostContent(): string {
    if (!this.data.postContent) return '';
    return this.data.postContent.length > 100
      ? this.data.postContent.substring(0, 100) + '...'
      : this.data.postContent;
  }

  onSubmit(): void {
    if (this.reportForm.invalid) {
      this.reportForm.markAllAsTouched();
      return;
    }

    const result: any = {
      reason: this.reportForm.get('reason')?.value.trim()
    };

    if (this.data.postId) {
      result.reportedPostId = this.data.postId;
    } else if (this.data.userId) {
      result.reportedUserId = this.data.userId;
    }

    this.dialogRef.close(result);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getErrorMessage(): string {
    const control = this.reportForm.get('reason');

    if (control?.hasError('required')) {
      return 'Please provide a reason for the report';
    }
    if (control?.hasError('minlength')) {
      return 'Reason must be at least 10 characters';
    }
    if (control?.hasError('maxlength')) {
      return 'Reason cannot exceed 500 characters';
    }
    return '';
  }
}
