import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class CompetitionService {
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

  getCompetitions(): Observable<any[]> {
    return this.http.get<any[]>('/api/competitions', this.getOptions());
  }

  createCompetition(competitionData: any): Observable<any> {
    return this.http.post<any>('/api/competitions', competitionData, this.getOptions());
  }

  getCompetitionById(id: string): Observable<any> {
    return this.http.get<any>(`/api/competitions/${id}`, this.getOptions());
  }

  getCompetitionsByStudent(studentId: string): Observable<any[]> {
    return this.http.get<any[]>(`/api/competitions/student/${studentId}`, this.getOptions());
  }

  submitCompetitionResult(competitionId: string, studentId: string, resultData: { score: number, teacherId: string }): Observable<any> {
    return this.http.post<any>(`/api/competitions/${competitionId}/results/${studentId}`, resultData, this.getOptions());
  }

  getCompetitionResultsByStudent(studentId: string): Observable<any[]> {
    return this.http.get<any[]>(`/api/competitions/results/student/${studentId}`, this.getOptions());
  }
}
