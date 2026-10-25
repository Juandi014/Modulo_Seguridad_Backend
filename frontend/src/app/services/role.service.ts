import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Role } from '../models/role.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  constructor(private http: HttpClient) { }
  list(): Observable<Role[]> {
    return this.http.get<Role[]>(`${environment.url_ms_security}/roles`);
  }
  view(id: number): Observable<Role> {
    return this.http.get<Role>(`${environment.url_ms_security}/roles/${id}`);
  }
  create(newRole: Role): Observable<Role> {
    return this.http.post<Role>(`${environment.url_ms_security}/roles`,
      { name: newRole.name, description: newRole.description });
  }
  update(theRole: Role): Observable<Role> {
    return this.http.put<Role>(`${environment.url_ms_security}/roles/${theRole.id}`,
      { name: theRole.name, description: theRole.description });
  }
  delete(id: number) {
    return this.http.delete<Role>(`${environment.url_ms_security}/roles/${id}`);
  }
  deactivate(id: number): Observable<Role> {
    return this.http.patch<Role>(`${environment.url_ms_security}/roles/${id}/deactivate`, {});
  }
  activate(id: number): Observable<Role> {
    return this.http.patch<Role>(`${environment.url_ms_security}/roles/${id}/activate`, {});
  }

}
