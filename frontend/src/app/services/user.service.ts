import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) { }
  list(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.url_ms_security}/users`);
  }
  view(id: number): Observable<User> {
    return this.http.get<User>(`${environment.url_ms_security}/users/${id}`);
  }
  create(newUser: User): Observable<User> {
    delete newUser.id;
    delete newUser.active;
    return this.http.post<User>(`${environment.url_ms_security}/users`, newUser);
  }
  update(theUser: User): Observable<User> {
    // Si no se escribe una contraseña nueva, se conserva la actual
    const body: User = { name: theUser.name, email: theUser.email };
    if (theUser.password) {
      body.password = theUser.password;
    }
    return this.http.put<User>(`${environment.url_ms_security}/users/${theUser.id}`, body);
  }
  deactivate(id: number): Observable<User> {
    return this.http.patch<User>(`${environment.url_ms_security}/users/${id}/deactivate`, {});
  }
  activate(id: number): Observable<User> {
    return this.http.patch<User>(`${environment.url_ms_security}/users/${id}/activate`, {});
  }

}
