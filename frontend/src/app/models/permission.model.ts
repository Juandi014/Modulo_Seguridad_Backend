export type PermissionModule =
    'ADMINISTRATION' | 'SPORTS_MANAGEMENT' | 'COMMERCIAL' | 'INFRASTRUCTURE' | 'REPORTS';

export type PermissionOperation = 'VIEW' | 'LIST' | 'CREATE' | 'UPDATE' | 'DELETE';

export class Permission {
    id?:number;
    url?:string;
    method?:string;
    module?:PermissionModule;
    resource?:string;
    operation?:PermissionOperation;
}

// Catálogos con las etiquetas en español (el backend usa los valores en inglés).
// El orden es el mismo del mockup de HU-SE-06.
export const PERMISSION_MODULES: { value: PermissionModule, label: string }[] = [
    { value: 'ADMINISTRATION', label: 'Administración' },
    { value: 'SPORTS_MANAGEMENT', label: 'Gestión Deportiva' },
    { value: 'COMMERCIAL', label: 'Comercial' },
    { value: 'INFRASTRUCTURE', label: 'Infraestructura' },
    { value: 'REPORTS', label: 'Reportes' }
];

export const PERMISSION_OPERATIONS: { value: PermissionOperation, label: string }[] = [
    { value: 'VIEW', label: 'Ver' },
    { value: 'LIST', label: 'Listar' },
    { value: 'CREATE', label: 'Crear' },
    { value: 'UPDATE', label: 'Actualizar' },
    { value: 'DELETE', label: 'Eliminar' }
];

// Ícono (Font Awesome) de cada recurso en la matriz de HU-SE-06.
// Un recurso nuevo que no esté aquí usa el ícono genérico.
const RESOURCE_ICONS: { [resource: string]: string } = {
    'Usuarios': 'fa-user',
    'Perfiles': 'fa-id-card',
    'Sesiones': 'fa-desktop',
    'Roles': 'fa-user-tag',
    'Asignación de roles': 'fa-user-friends',
    'Permisos': 'fa-key',
    'Permisos de roles': 'fa-user-shield',
    'Auditoría de accesos': 'fa-clipboard-list',
    'Torneos': 'fa-trophy',
    'Equipos': 'fa-users',
    'Jugadores': 'fa-walking',
    'Partidos': 'fa-futbol',
    'Entradas': 'fa-ticket-alt',
    'Escenarios': 'fa-landmark',
    'Reportes': 'fa-chart-bar'
};

export function resourceIcon(resource: string): string {
    return RESOURCE_ICONS[resource] || 'fa-cube';
}

export const HTTP_METHODS =['GET', 'POST', 'PUT', 'PATCH', 'DELETE'];

export function moduleLabel(value: string): string {
    return PERMISSION_MODULES.find(m => m.value === value)?.label || 'Sin clasificar';
}

export function operationLabel(value: string): string {
    return PERMISSION_OPERATIONS.find(o => o.value === value)?.label || '-';
}
