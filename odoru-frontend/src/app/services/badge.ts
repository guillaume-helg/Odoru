import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class BadgeService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  private getOptions() {
    return {
      headers: {
        'Authorization': this.auth.getAuthorizationHeader(),
        'Content-Type': 'application/json'
      }
    };
  }

  associateBadge(associationData: { memberId: string, badgeNumber: string }): Observable<any> {
    return this.http.post<any>('/api/badges/associate', associationData, this.getOptions());
  }

  dissociateBadge(memberId: string): Observable<any> {
    return this.http.post<any>(`/api/badges/dissociate/${memberId}`, {}, this.getOptions());
  }

  scanBadge(scanData: { badgeNumber: string, lessonId: string }): Observable<any> {
    return this.http.post<any>('/api/badges/scan', scanData, this.getOptions());
  }

  getBadgeAttendanceByStudent(studentId: string): Observable<any[]> {
    return this.http.get<any[]>(`/api/badges/attendance/student/${studentId}`, this.getOptions());
  }

  getBadgeAttendanceByLesson(lessonId: string): Observable<any[]> {
    return this.http.get<any[]>(`/api/badges/attendance/lesson/${lessonId}`, this.getOptions());
  }
}
