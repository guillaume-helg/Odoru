import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class LessonService {
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

  getLessons(): Observable<any[]> {
    return this.http.get<any[]>('/api/lessons', this.getOptions());
  }

  createLesson(lessonData: any): Observable<any> {
    return this.http.post<any>('/api/lessons', lessonData, this.getOptions());
  }

  getLessonById(id: string): Observable<any> {
    return this.http.get<any>(`/api/lessons/${id}`, this.getOptions());
  }

  getLessonsByTeacher(teacherId: string): Observable<any[]> {
    return this.http.get<any[]>(`/api/lessons/teacher/${teacherId}`, this.getOptions());
  }

  getLessonsByStudent(studentId: string): Observable<any[]> {
    return this.http.get<any[]>(`/api/lessons/student/${studentId}`, this.getOptions());
  }
}
