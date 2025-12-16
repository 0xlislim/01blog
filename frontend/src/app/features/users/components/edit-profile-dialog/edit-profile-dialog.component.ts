import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface EditProfileDialogData {
  displayName: string;
  bio?: string;
}

@Component({
  selector: 'app-edit-profile-dialog',
  templateUrl: './edit-profile-dialog.component.html',
  styleUrls: ['./edit-profile-dialog.component.scss']
})
export class EditProfileDialogComponent implements OnInit {
  profileForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditProfileDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EditProfileDialogData
  ) {}

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

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.dialogRef.close(this.profileForm.value);
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
