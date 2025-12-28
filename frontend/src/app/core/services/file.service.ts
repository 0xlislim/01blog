import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType, HttpRequest } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface FileUploadResponse {
  url: string;
  filename: string;
}

export interface UploadProgress {
  progress: number;
  loaded: number;
  total: number;
}

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = `${environment.apiUrl}/files`;

  constructor(private http: HttpClient) {}

  uploadFile(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<FileUploadResponse>(`${this.apiUrl}/upload`, formData);
  }

  uploadFileWithProgress(file: File, progressCallback?: (progress: UploadProgress) => void): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);

    const request = new HttpRequest('POST', `${this.apiUrl}/upload`, formData, {
      reportProgress: true
    });

    return new Observable<FileUploadResponse>(observer => {
      this.http.request(request).subscribe({
        next: (event) => {
          if (event.type === HttpEventType.UploadProgress) {
            const progress: UploadProgress = {
              progress: event.total ? Math.round((100 * event.loaded) / event.total) : 0,
              loaded: event.loaded,
              total: event.total || 0
            };
            if (progressCallback) {
              progressCallback(progress);
            }
          } else if (event.type === HttpEventType.Response) {
            const body = event.body as FileUploadResponse;
            observer.next(body);
            observer.complete();
          }
        },
        error: (error) => {
          observer.error(error);
        }
      });
    });
  }

  getFileUrl(filename: string): string {
    return `${this.apiUrl}/${filename}`;
  }

  deleteFile(filename: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${filename}`);
  }

  validateFile(file: File, maxSizeMB: number = 10, allowedTypes: string[] = []): { valid: boolean; error?: string } {
    // Check file size
    const maxSizeBytes = maxSizeMB * 1024 * 1024;
    if (file.size > maxSizeBytes) {
      return { valid: false, error: `File size must be less than ${maxSizeMB}MB` };
    }

    // Check file type
    if (allowedTypes.length > 0 && !allowedTypes.includes(file.type)) {
      return { valid: false, error: 'Invalid file type' };
    }

    return { valid: true };
  }
}
