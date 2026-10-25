import { Role } from './role.model';
import { User } from './user.model';

// Rol asignado a un usuario (GET /user-roles/users/{userId})
export class UserRole {
    id?:number;
    userId?:number;
    role?:Role;
}

// Usuario que tiene un rol (GET /user-roles/roles/{roleId})
export class RoleUser {
    id?:number;
    roleId?:number;
    user?:User;
}
