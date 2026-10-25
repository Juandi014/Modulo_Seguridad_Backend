import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  constructor(private http: HttpClient) { }
  // Registro público con correo y contraseña (no requiere sesión)
  register(newUser: User): Observable<User> {
    return this.http.post<User>(`${environment.url_ms_security}/auth/register`, {
      name: newUser.name,
      email: newUser.email,
      password: newUser.password
    });
  }

}
