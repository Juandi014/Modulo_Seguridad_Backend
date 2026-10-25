import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RolePermissionMatrix } from '../models/role-permission.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RolePermissionService {
  constructor(private http: HttpClient) { }
  matrix(roleId: number): Observable<RolePermissionMatrix> {
    return this.http.get<RolePermissionMatrix>(
      `${environment.url_ms_security}/role-permissions/roles/${roleId}/matrix`);
  }
  // Guarda el estado final: el rol queda con exactamente estos permisos
  save(roleId: number, permissionIds: number[]): Observable<RolePermissionMatrix> {
    return this.http.put<RolePermissionMatrix>(
      `${environment.url_ms_security}/role-permissions/roles/${roleId}`, { permissionIds });
  }

}
