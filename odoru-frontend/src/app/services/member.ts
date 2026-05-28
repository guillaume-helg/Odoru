import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class MemberService {
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

  getMembers(): Observable<any[]> {
    return this.http.get<any[]>('/api/members', this.getOptions());
  }

  getMemberById(id: string): Observable<any> {
    return this.http.get<any>(`/api/members/${id}`, this.getOptions());
  }

  signup(memberData: any): Observable<any> {
    return this.http.post<any>('/api/members/signup', memberData);
  }

  updateMember(id: string, memberData: any): Observable<any> {
    return this.http.put<any>(`/api/members/${id}`, memberData, this.getOptions());
  }

  deleteMember(id: string): Observable<any> {
    return this.http.delete<any>(`/api/members/${id}`, this.getOptions());
  }

  patchMemberRole(id: string, role: string): Observable<any> {
    const url = `/api/members/${id}/role?role=${encodeURIComponent(role)}`;
    return this.http.patch<any>(url, {}, this.getOptions());
  }

  patchMemberExpertise(id: string, level: number): Observable<any> {
    const url = `/api/members/${id}/expertise?expertiseLevel=${level}`;
    return this.http.patch<any>(url, {}, this.getOptions());
  }

  patchMemberRegistrationStatus(id: string, statusData: {
    registrationValidated?: boolean;
    feePaid?: boolean;
    medicalCertificateProvided?: boolean;
  }): Observable<any> {
    return this.http.patch<any>(`/api/members/${id}/registration-status`, statusData, this.getOptions());
  }
}
