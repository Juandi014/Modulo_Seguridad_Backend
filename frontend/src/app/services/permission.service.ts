import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Permission } from '../models/permission.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  constructor(private http: HttpClient) { }
  list(): Observable<Permission[]> {
    return this.http.get<Permission[]>(`${environment.url_ms_security}/permissions`);
  }
  view(id: number): Observable<Permission> {
    return this.http.get<Permission>(`${environment.url_ms_security}/permissions/${id}`);
  }
  create(newPermission: Permission): Observable<Permission> {
    delete newPermission.id;
    return this.http.post<Permission>(`${environment.url_ms_security}/permissions`, newPermission);
  }
  update(thePermission: Permission): Observable<Permission> {
    return this.http.put<Permission>(`${environment.url_ms_security}/permissions/${thePermission.id}`, thePermission);
  }
  delete(id: number) {
    return this.http.delete<Permission>(`${environment.url_ms_security}/permissions/${id}`);
  }

}
