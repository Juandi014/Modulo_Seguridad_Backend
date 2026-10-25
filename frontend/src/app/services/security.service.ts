import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { User } from '../models/user.model';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SecurityService {

  theUser = new BehaviorSubject<User>(new User);
  constructor(private http: HttpClient) {
    this.verifyActualSession()
  }

  /**
  * Realiza la petición al backend con el correo y la contraseña
  * para verificar si existe o no en la plataforma
  * @param infoUsuario JSON con la información de correo y contraseña
  * @returns Respuesta HTTP la cual indica si el usuario tiene permiso de acceso
  */
  login(user: User): Observable<any> {
    return this.http.post<any>(`${environment.url_ms_security}/auth/login`, user);
  }

  /**
  * Segundo paso del login (2FA): valida el código enviado al correo.
  * Si es correcto responde la sesión con su token.
  */
  verifyTwoFactor(challengeId: string, code: string): Observable<any> {
    return this.http.post<any>(`${environment.url_ms_security}/auth/2fa/verify`, { challengeId, code });
  }

  /**
  * Abandona el proceso de 2FA: el intento de inicio de sesión queda anulado
  */
  cancelTwoFactor(challengeId: string): Observable<void> {
    return this.http.post<void>(`${environment.url_ms_security}/auth/2fa/cancel`, { challengeId })
      .pipe(catchError(() => of(undefined)));
  }

  /**
  * Proveedores OAuth habilitados en el backend (google, github, facebook)
  */
  oauthProviders(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.url_ms_security}/auth/oauth/providers`)
      .pipe(catchError(() => of([])));
  }

  /**
  * Inicia el login OAuth: el navegador va al backend, que redirige al proveedor
  */
  loginWithProvider(provider: string) {
    window.location.href = `${environment.url_backend}/oauth2/authorization/${provider}`;
  }

  /**
  * Cambia el código de un solo uso (que llega tras el login OAuth) por la sesión
  */
  exchangeOAuthCode(code: string): Observable<any> {
    return this.http.post<any>(`${environment.url_ms_security}/auth/oauth/exchange`, { code });
  }

  /*
  Guardar la información de usuario en el local storage.
  El backend responde { accessToken, tokenType, expiresAt, user }
  */
  saveSession(dataSesion: any) {
    let data: User = {
      id: dataSesion["user"]["id"],
      name: dataSesion["user"]["name"],
      email: dataSesion["user"]["email"],
      authProvider: dataSesion["user"]["authProvider"],
      password: "",
      token: dataSesion["accessToken"],
      expiresAt: dataSesion["expiresAt"]
    };
    localStorage.setItem('sesion', JSON.stringify(data));
    this.setUser(data);
  }
  /**
    * Permite actualizar la información del usuario
    * que acabó de validarse correctamente
    * @param user información del usuario logueado
  */
  setUser(user: User) {
    this.theUser.next(user);
  }
  /**
  * Permite obtener la información del usuario
  * con datos tales como el identificador y el token
  * @returns
  */
  getUser() {
    return this.theUser.asObservable();
  }
  /**
    * Permite obtener la información de usuario
    * que tiene la función activa y servirá
    * para acceder a la información del token
*/
  public get activeUserSession(): User {
    return this.theUser.value;
  }


  /**
  * Cierra la sesión en el backend (el token deja de ser válido)
  * y luego borra los datos locales
  */
  logout(): Observable<void> {
    if (!this.existSession()) {
      this.clearSession();
      return of(undefined);
    }
    return this.http.post<void>(`${environment.url_ms_security}/auth/logout`, {}).pipe(
      catchError(() => of(undefined)), // si ya expiró, igual se limpia localmente
      map(() => this.clearSession())
    );
  }
  /**
  * Borra la sesión local sin llamar al backend
  * (se usa cuando el backend ya respondió 401)
  */
  clearSession() {
    localStorage.removeItem('sesion');
    this.setUser(new User());
  }
  /**
  * Permite verificar si actualmente en el local storage
  * existe información de un usuario previamente logueado
  */
  verifyActualSession() {
    let actualSesion = this.getSessionData();
    if (actualSesion) {
      const user: User = JSON.parse(actualSesion);
      if (this.isExpired(user)) {
        this.clearSession();
      } else {
        this.setUser(user);
      }
    }
  }
  /**
  * Verifica si hay una sesión activa y no expirada
  * @returns
  */
  existSession(): boolean {
    let sesionActual = this.getSessionData();
    if (!sesionActual) {
      return false;
    }
    if (this.isExpired(JSON.parse(sesionActual))) {
      this.clearSession();
      return false;
    }
    return true;
  }
  /**
  * Permite obtener los dato de la sesión activa en el
  * local storage
  * @returns
  */
  getSessionData() {
    let sesionActual = localStorage.getItem('sesion');
    return sesionActual;
  }

  private isExpired(user: User): boolean {
    return !user.token || !user.expiresAt || new Date(user.expiresAt).getTime() <= Date.now();
  }
}
