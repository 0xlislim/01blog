import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AdminUserResponse, Post, Report } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  // Users management
  getAllUsers(): Observable<AdminUserResponse[]> {
    return this.http.get<AdminUserResponse[]>(`${this.apiUrl}/users`);
  }

  banUser(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/users/${userId}/ban`, {});
  }

  unbanUser(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/users/${userId}/unban`, {});
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${userId}`);
  }

  // Posts management
  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/posts`);
  }

  hidePost(postId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/posts/${postId}/hide`, {});
  }

  unhidePost(postId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/posts/${postId}/unhide`, {});
  }

  deletePost(postId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/posts/${postId}`);
  }

  // Reports management
  getAllReports(): Observable<Report[]> {
    return this.http.get<Report[]>(`${this.apiUrl}/reports`);
  }

  dismissReport(reportId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/reports/${reportId}`);
  }
}
