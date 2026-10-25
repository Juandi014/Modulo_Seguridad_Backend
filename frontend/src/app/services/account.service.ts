import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface TwoFactorStatus {
  enabled: boolean;
  available: boolean;   // false para cuentas OAuth
}

export interface AccountSession {
  id: number;
  authMethod: string;
  createdAt: string;
  expiration: string;
  revokedAt: string;
  active: boolean;
  current: boolean;
}

// Seguridad de la cuenta del usuario autenticado (pantalla "Seguridad")
@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private baseUrl = `${environment.url_ms_security}/account`;

  constructor(private http: HttpClient) { }
  changePassword(currentPassword: string, newPassword: string): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.baseUrl}/password`, { currentPassword, newPassword });
  }
  twoFactorStatus(): Observable<TwoFactorStatus> {
    return this.http.get<TwoFactorStatus>(`${this.baseUrl}/2fa`);
  }
  setTwoFactor(enabled: boolean, currentPassword: string): Observable<TwoFactorStatus> {
    return this.http.put<TwoFactorStatus>(`${this.baseUrl}/2fa`, { enabled, currentPassword });
  }
  mySessions(): Observable<AccountSession[]> {
    return this.http.get<AccountSession[]>(`${this.baseUrl}/sessions`);
  }

}
