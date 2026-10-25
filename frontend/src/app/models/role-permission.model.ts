import { PermissionModule, PermissionOperation } from './permission.model';
import { Role } from './role.model';

export class MatrixPermission {
    id?:number;
    module?:PermissionModule;
    resource?:string;
    operation?:PermissionOperation;
    method?:string;
    url?:string;
    assigned?:boolean;
}

// Respuesta de GET /role-permissions/roles/{roleId}/matrix
export class RolePermissionMatrix {
    role?:Role;
    userCount?:number;
    permissions?:MatrixPermission[];
}
