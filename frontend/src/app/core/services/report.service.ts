import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Report, CreateReportRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  createReport(request: CreateReportRequest): Observable<Report> {
    return this.http.post<Report>(this.apiUrl, request);
  }
}
