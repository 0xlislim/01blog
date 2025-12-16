import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserProfile, UpdateProfileRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getUserProfile(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${userId}`);
  }

  updateProfile(userId: number, data: UpdateProfileRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/${userId}`, data);
  }

  searchUsers(query: string): Observable<UserProfile[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<UserProfile[]>(`${this.apiUrl}/search`, { params });
  }
}
