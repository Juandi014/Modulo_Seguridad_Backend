import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { SecurityService } from '../services/security.service';

// Solo deja entrar a las pantallas protegidas si hay una sesión vigente.
// (El backend igual valida el token en cada petición.)
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private securityService: SecurityService, private router: Router) { }

  canActivate(): boolean | UrlTree {
    return this.securityService.existSession() ? true : this.router.parseUrl('/login');
  }
}
