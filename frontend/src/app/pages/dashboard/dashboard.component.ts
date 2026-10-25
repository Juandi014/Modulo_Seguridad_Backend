import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AUTH_PROVIDER_LABELS, User } from 'src/app/models/user.model';
import { SecurityService } from 'src/app/services/security.service';

declare interface ModuleShortcut {
  path: string;
  title: string;
  description: string;
  icon: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {

  user: User;
  providerLabels = AUTH_PROVIDER_LABELS;
  private subscription: Subscription;

  // Accesos directos a las pantallas del módulo de seguridad.
  // Si el usuario no tiene el permiso, el backend responde 403 al entrar.
  shortcuts: ModuleShortcut[] = [
    { path: '/users/list',       title: 'Usuarios',               icon: 'ni-circle-08',
      description: 'Registrar, consultar, actualizar y desactivar usuarios.' },
    { path: '/role-permissions', title: 'Roles',                  icon: 'ni-badge',
      description: 'Marcar en la matriz los permisos de cada rol y gestionar los roles.' },
    { path: '/permissions/list', title: 'Permisos',               icon: 'ni-key-25',
      description: 'Registrar las operaciones que se pueden autorizar.' },
    { path: '/profiles/me/view', title: 'Mi perfil',              icon: 'ni-single-02',
      description: 'Actualizar su teléfono y fotografía.' },
    { path: '/security',         title: 'Seguridad',              icon: 'ni-lock-circle-open',
      description: 'Autenticación de dos factores, contraseña y sesiones.' },
    { path: '/audit/list',       title: 'Auditoría',              icon: 'ni-archive-2',
      description: 'Consultar los intentos de inicio de sesión.' }
  ];

  constructor(private securityService: SecurityService) { }

  ngOnInit() {
    this.subscription = this.securityService.getUser().subscribe(data => this.user = data);
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }
}
