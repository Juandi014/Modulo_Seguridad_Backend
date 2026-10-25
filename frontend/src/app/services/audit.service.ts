import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginAttempt, Page } from '../models/login-attempt.model';
import { environment } from 'src/environments/environment';

// Auditoría de inicios de sesión (solo lectura)
@Injectable({
  providedIn: 'root'
})
export class AuditService {
  constructor(private http: HttpClient) { }
  loginAttempts(page: number, size: number, email?: string, result?: string): Observable<Page<LoginAttempt>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (email) {
      params = params.set('email', email);
    }
    if (result) {
      params = params.set('result', result);
    }
    return this.http.get<Page<LoginAttempt>>(`${environment.url_ms_security}/audit/login-attempts`, { params });
  }

}
