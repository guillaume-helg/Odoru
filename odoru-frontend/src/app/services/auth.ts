import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { UserStore } from '../stores/user';

export interface TokenResponse {
  access_token: string;
  expires_in: number;
  refresh_token: string;
  token_type: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly userStore = inject(UserStore);

  constructor() {
    const token = this.userStore.token();
    if (token && this.isTokenExpired(token)) {
      this.logout();
    }
  }

  login(username: string, password: string): Observable<TokenResponse> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    const body = new URLSearchParams();
    body.set('client_id', 'odoru-services');
    body.set('grant_type', 'password');
    body.set('username', username);
    body.set('password', password);

    return this.http.post<TokenResponse>('/realms/odoru/protocol/openid-connect/token', body.toString(), { headers }).pipe(
      tap((res) => {
        this.userStore.setCredentials(res.access_token, username);
      })
    );
  }

  logout(): void {
    this.userStore.clear();
    this.router.navigate(['/login']);
  }

  getAuthorizationHeader(): string {
    const token = this.userStore.token();
    return token ? `Bearer ${token}` : '';
  }

  private parseJwt(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        window
          .atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (e) {
      return null;
    }
  }

  private isTokenExpired(token: string): boolean {
    const decoded = this.parseJwt(token);
    if (!decoded || !decoded.exp) return true;
    return Math.floor(Date.now() / 1000) >= decoded.exp - 10;
  }
}
