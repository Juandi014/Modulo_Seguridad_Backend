import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { User } from 'src/app/models/user.model';
import { SecurityService } from 'src/app/services/security.service';

declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    matches: string[];  // rutas que dejan marcada la opción (ej. la matriz pertenece a Roles)
}
export const ROUTES: RouteInfo[] = [
    { path: '/dashboard',        title: 'Dashboard',  icon: 'ni-tv-2',             matches: ['/dashboard'] },
    { path: '/users/list',       title: 'Usuarios',   icon: 'ni-circle-08',        matches: ['/users', '/profiles/view', '/profiles/update', '/user-roles/user'] },
    { path: '/role-permissions', title: 'Roles',     icon: 'ni-badge',            matches: ['/roles', '/role-permissions', '/user-roles/role'] },
    { path: '/permissions/list', title: 'Permisos',   icon: 'ni-key-25',           matches: ['/permissions'] },
    { path: '/profiles/me/view', title: 'Mi perfil',  icon: 'ni-single-02',        matches: ['/profiles/me'] },
    { path: '/security',         title: 'Seguridad',  icon: 'ni-lock-circle-open', matches: ['/security'] },
    { path: '/audit/list',       title: 'Auditoría',  icon: 'ni-archive-2',        matches: ['/audit'] }
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit, OnDestroy {

  public menuItems: RouteInfo[];
  public isCollapsed = true;
  user: User;
  private subscription: Subscription;

  constructor(private router: Router, private securityService: SecurityService) { }

  ngOnInit() {
    this.menuItems = ROUTES;
    this.subscription = this.securityService.getUser().subscribe(data => this.user = data);
    this.router.events.subscribe(() => {
      this.isCollapsed = true;
    });
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  isActive(item: RouteInfo): boolean {
    return item.matches.some(path => this.router.url.startsWith(path));
  }

  // Cierra la sesión en el backend (el token deja de ser válido) y vuelve al login
  logout() {
    this.securityService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}
