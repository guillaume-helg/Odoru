import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class StatsService {
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

  getCourseStatsSummary(): Observable<any> {
    return this.http.get<any>('/api/stats/courses/summary', this.getOptions());
  }

  getCourseStatsByLesson(lessonId: string): Observable<any> {
    return this.http.get<any>(`/api/stats/courses/${lessonId}/attendance`, this.getOptions());
  }

  getStudentStatsAttendance(studentId: string): Observable<any> {
    return this.http.get<any>(`/api/stats/students/${studentId}/attendance`, this.getOptions());
  }

  getCompetitionStatsSummary(): Observable<any[]> {
    return this.http.get<any[]>('/api/stats/competitions/summary', this.getOptions());
  }

  getStudentStatsCompetitions(studentId: string): Observable<any> {
    return this.http.get<any>(`/api/stats/students/${studentId}/competitions`, this.getOptions());
  }
}
