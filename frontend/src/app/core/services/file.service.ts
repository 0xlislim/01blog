import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FileUploadResponse {
  url: string;
  filename: string;
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

  getFileUrl(filename: string): string {
    return `${this.apiUrl}/${filename}`;
  }

  deleteFile(filename: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${filename}`);
  }
}
