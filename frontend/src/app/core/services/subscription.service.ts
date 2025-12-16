import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SubscriptionStatus {
  subscribed: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = `${environment.apiUrl}/subscriptions`;

  constructor(private http: HttpClient) {}

  subscribe(userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${userId}`, {});
  }

  unsubscribe(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${userId}`);
  }

  getSubscriptionStatus(userId: number): Observable<SubscriptionStatus> {
    return this.http.get<SubscriptionStatus>(`${this.apiUrl}/${userId}/status`);
  }
}
