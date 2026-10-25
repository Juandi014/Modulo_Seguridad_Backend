import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Role } from '../models/role.model';
import { User } from '../models/user.model';
import { RoleUser, UserRole } from '../models/user-role.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserRoleService {
  constructor(private http: HttpClient) { }
  listByRole(roleId: number): Observable<RoleUser[]> {
    return this.http.get<RoleUser[]>(`${environment.url_ms_security}/user-roles/roles/${roleId}`);
  }
  availableUsers(roleId: number): Observable<User[]> {
    return this.http.get<User[]>(`${environment.url_ms_security}/user-roles/roles/${roleId}/available-users`);
  }
  listByUser(userId: number): Observable<UserRole[]> {
    return this.http.get<UserRole[]>(`${environment.url_ms_security}/user-roles/users/${userId}`);
  }
  availableRoles(userId: number): Observable<Role[]> {
    return this.http.get<Role[]>(`${environment.url_ms_security}/user-roles/users/${userId}/available-roles`);
  }
  assign(userId: number, roleId: number): Observable<UserRole> {
    return this.http.post<UserRole>(`${environment.url_ms_security}/user-roles`, { userId, roleId });
  }
  remove(userId: number, roleId: number) {
    return this.http.delete(`${environment.url_ms_security}/user-roles/${userId}/${roleId}`);
  }

}
