import { Routes } from '@angular/router';

import { DashboardComponent } from '../../pages/dashboard/dashboard.component';
import { AuthGuard } from '../../guards/auth.guard';

// "data" lleva el título y subtítulo que muestra la barra superior (navbar)
export const AdminLayoutRoutes: Routes = [
    {
        path: 'dashboard',
        component: DashboardComponent,
        canActivate: [AuthGuard],
        data: {
            title: 'Dashboard',
            subtitle: 'Bienvenido a la plataforma administrativa de SportManager.'
        }
    },
    {
        path: 'users',
        canActivate: [AuthGuard],
        data: {
            title: 'Gestión de Usuarios',
            subtitle: 'Administra las personas que pueden acceder a la plataforma.'
        },
        children: [
            {
                path: '',
                loadChildren: () => import('src/app/pages/users/users.module').then(m => m.UsersModule)
            }
        ]
    },
    {
        path: 'profiles',
        canActivate: [AuthGuard],
        data: {
            title: 'Perfil de Usuario',
            subtitle: 'Consulta y actualiza el teléfono y la fotografía del perfil.'
        },
        children: [
            {
                path: '',
                loadChildren: () => import('src/app/pages/profiles/profiles.module').then(m => m.ProfilesModule)
            }
        ]
    },
    {
        path: 'roles',
        canActivate: [AuthGuard],
        data: {
            title: 'Gestión de Roles',
            subtitle: 'Define los niveles de acceso que se asignan a los usuarios.'
        },
        children: [
            {
                path: '',
                loadChildren: () => import('src/app/pages/roles/roles.module').then(m => m.RolesModule)
            }
        ]
    },
    {
        path: 'user-roles',
        canActivate: [AuthGuard],
        data: {
            title: 'Asignar Roles a Usuarios',
            subtitle: 'Agrega o retira roles de los usuarios de la plataforma.'
        },
        children: [
            {
                path: '',
                loadChildren: () => import('src/app/pages/user-roles/user-roles.module').then(m => m.UserRolesModule)
            }
        ]
    },
    {
        path: 'permissions',
        canActivate: [AuthGuard],
        data: {
            title: 'Gestión de Permisos',
            subtitle: 'Operaciones que pueden autorizarse a los roles del sistema.'
        },
        children: [
            {
                path: '',
                loadChildren: () => import('src/app/pages/permissions/permissions.module').then(m => m.PermissionsModule)
            }
        ]
    },
    {
        path: 'role-permissions',
        canActivate: [AuthGuard],
        data: {
            title: 'Asignar Permisos a Roles',
            subtitle: 'Administra de manera centralizada los permisos que puede ejecutar cada rol del sistema.'
        },
        children: [
            {
                path: '',
                loadChildren: () => import('src/app/pages/role-permissions/role-permissions.module').then(m => m.RolePermissionsModule)
            }
        ]
    },
    {
        path: 'security',
        canActivate: [AuthGuard],
        data: {
            title: 'Seguridad',
            subtitle: 'Gestiona la autenticación de dos factores, tu contraseña y tus sesiones.'
        },
        children: [
            {
                path: '',
                loadChildren: () => import('src/app/pages/security/security.module').then(m => m.SecurityModule)
            }
        ]
    },
    {
        path: 'audit',
        canActivate: [AuthGuard],
        data: {
            title: 'Auditoría',
            subtitle: 'Registro de solo lectura de los intentos de inicio de sesión.'
        },
        children: [
            {
                path: '',
                loadChildren: () => import('src/app/pages/audit/audit.module').then(m => m.AuditModule)
            }
        ]
    }
];
