import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { SecurityService } from '../services/security.service';
import { environment } from 'src/environments/environment';
import Swal from 'sweetalert2';

// Endpoints públicos: no llevan token (uno vencido haría fallar la petición)
const PUBLIC_ENDPOINTS = ['/auth/login', '/auth/register', '/auth/oauth/', '/auth/password-recovery/', '/auth/2fa/'];

/**
 * Agrega el token JWT a las peticiones hacia ms-security
 * y cierra la sesión si el backend responde 401 (token inválido, sesión
 * cerrada o expirada).
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private securityService: SecurityService, private router: Router) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.securityService.activeUserSession?.token;
    const isSecurityApi = request.url.startsWith(environment.url_ms_security);
    const isPublic = PUBLIC_ENDPOINTS.some(endpoint => request.url.includes(endpoint));

    if (token && isSecurityApi && !isPublic) {
      request = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !isPublic && !request.url.endsWith('/auth/logout')) {
          // Solo se avisa una vez, aunque fallen varias peticiones a la vez
          const hadSession = !!this.securityService.activeUserSession?.token;
          this.securityService.clearSession();
          if (hadSession) {
            Swal.fire('Sesión finalizada', 'Su sesión expiró o fue cerrada. Inicie sesión nuevamente.', 'info');
          }
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
