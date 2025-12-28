import { Component, Input, Output, EventEmitter, ElementRef, ViewChild } from '@angular/core';

export interface FileValidationConfig {
  maxSizeMB: number;
  allowedImageTypes: string[];
  allowedVideoTypes: string[];
}

export interface UploadedFile {
  file: File;
  preview: string;
  type: 'IMAGE' | 'VIDEO';
}

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss']
})
export class FileUploadComponent {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  @Input() config: FileValidationConfig = {
    maxSizeMB: 10,
    allowedImageTypes: ['image/jpeg', 'image/png', 'image/gif'],
    allowedVideoTypes: ['video/mp4', 'video/webm']
  };

  @Input() disabled = false;
  @Input() uploadedFile: UploadedFile | null = null;

  @Output() fileSelected = new EventEmitter<UploadedFile>();
  @Output() fileRemoved = new EventEmitter<void>();
  @Output() validationError = new EventEmitter<string>();

  isDragOver = false;

  get acceptedTypes(): string {
    return [...this.config.allowedImageTypes, ...this.config.allowedVideoTypes].join(',');
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    if (!this.disabled) {
      this.isDragOver = true;
    }
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;

    if (this.disabled) return;

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.processFile(files[0]);
    }
  }

  onFileInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.processFile(input.files[0]);
    }
  }

  openFileDialog(): void {
    if (!this.disabled) {
      this.fileInput.nativeElement.click();
    }
  }

  removeFile(): void {
    this.uploadedFile = null;
    this.fileInput.nativeElement.value = '';
    this.fileRemoved.emit();
  }

  private processFile(file: File): void {
    // Validate file type
    const isImage = this.config.allowedImageTypes.includes(file.type);
    const isVideo = this.config.allowedVideoTypes.includes(file.type);

    if (!isImage && !isVideo) {
      const allowedExtensions = this.getAllowedExtensions();
      this.validationError.emit(`Invalid file type. Allowed: ${allowedExtensions}`);
      return;
    }

    // Validate file size
    const maxSizeBytes = this.config.maxSizeMB * 1024 * 1024;
    if (file.size > maxSizeBytes) {
      this.validationError.emit(`File size must be less than ${this.config.maxSizeMB}MB`);
      return;
    }

    // Create preview
    const reader = new FileReader();
    reader.onload = () => {
      const uploadedFile: UploadedFile = {
        file,
        preview: reader.result as string,
        type: isImage ? 'IMAGE' : 'VIDEO'
      };
      this.uploadedFile = uploadedFile;
      this.fileSelected.emit(uploadedFile);
    };
    reader.readAsDataURL(file);
  }

  private getAllowedExtensions(): string {
    const extensions: string[] = [];

    this.config.allowedImageTypes.forEach(type => {
      const ext = type.split('/')[1].toUpperCase();
      extensions.push(ext);
    });

    this.config.allowedVideoTypes.forEach(type => {
      const ext = type.split('/')[1].toUpperCase();
      extensions.push(ext);
    });

    return extensions.join(', ');
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }
}
