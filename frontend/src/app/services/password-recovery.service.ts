import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

// Endpoints públicos de recuperación de contraseña
@Injectable({
  providedIn: 'root'
})
export class PasswordRecoveryService {
  private baseUrl = `${environment.url_ms_security}/auth/password-recovery`;

  constructor(private http: HttpClient) { }
  request(email: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/request`, { email });
  }
  validate(token: string): Observable<{ message: string }> {
    return this.http.get<{ message: string }>(`${this.baseUrl}/validate`, { params: { token } });
  }
  reset(token: string, newPassword: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/reset`, { token, newPassword });
  }

}
