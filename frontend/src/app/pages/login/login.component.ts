import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/models/user.model';

import { SecurityService } from 'src/app/services/security.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

// Botones de proveedores externos (solo se muestran los habilitados en el backend)
const PROVIDER_BUTTONS: { [key: string]: { label: string, icon: string } } = {
  google: { label: 'Google', icon: 'assets/img/icons/common/google.svg' },
  github: { label: 'GitHub', icon: 'assets/img/icons/common/github.svg' },
  facebook: { label: 'Facebook', icon: '' }
};

// Reto 2FA devuelto por el backend cuando la contraseña es correcta
interface TwoFactorChallenge {
  challengeId: string;
  maskedEmail: string;
  codeExpiresAt: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  user: User
  providers: { id: string, label: string, icon: string }[] = [];
  sending = false;

  // Paso 2: segundo factor
  challenge: TwoFactorChallenge = null;
  code = '';
  secondsLeft = 0;
  private countdown: any;

  constructor(private securityService: SecurityService,private router:Router,
    private activatedRoute: ActivatedRoute) {
    this.user = { email: "", password: "" }
  }
  login() {
    if (!this.user.email || !this.user.password) {
      Swal.fire("Datos incompletos", "Ingrese su correo electrónico y su contraseña", "warning")
      return;
    }
    this.sending = true;
    this.securityService.login(this.user).subscribe({
      next: (data) => {
        this.sending = false;
        if (data.twoFactorRequired) {
          // Aún no hay sesión: falta validar el código enviado al correo
          this.startTwoFactor(data.twoFactor);
          return;
        }
        this.securityService.saveSession(data.session)
        this.router.navigate(["dashboard"])
      },
      error: (error) => {
        this.sending = false;
        Swal.fire("Autenticación Inválida", getErrorMessage(error), "error")
      }
    })
  }

  private startTwoFactor(challenge: TwoFactorChallenge) {
    this.challenge = challenge;
    this.code = '';
    this.user.password = '';
    this.updateCountdown();
    clearInterval(this.countdown);
    this.countdown = setInterval(() => this.updateCountdown(), 1000);
  }

  private updateCountdown() {
    if (!this.challenge) {
      return;
    }
    const remaining = Math.floor((new Date(this.challenge.codeExpiresAt).getTime() - Date.now()) / 1000);
    this.secondsLeft = Math.max(0, remaining);
    if (this.secondsLeft === 0) {
      clearInterval(this.countdown);
    }
  }

  get countdownText(): string {
    const minutes = Math.floor(this.secondsLeft / 60);
    const seconds = this.secondsLeft % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }

  verifyCode() {
    if (!/^\d{6}$/.test(this.code)) {
      Swal.fire("Código inválido", "El código debe tener 6 dígitos", "warning");
      return;
    }
    this.sending = true;
    this.securityService.verifyTwoFactor(this.challenge.challengeId, this.code).subscribe({
      next: (session) => {
        this.sending = false;
        clearInterval(this.countdown);
        this.securityService.saveSession(session);
        this.router.navigate(["dashboard"]);
      },
      error: (error) => {
        this.sending = false;
        const message = getErrorMessage(error);
        Swal.fire("Verificación fallida", message, "error");
        // Si el intento quedó anulado (expiró o demasiados intentos) se vuelve al paso 1
        if (!message.startsWith('Código incorrecto')) {
          this.resetToCredentials();
        }
      }
    });
  }

  cancelTwoFactor() {
    const challengeId = this.challenge.challengeId;
    this.resetToCredentials();
    this.securityService.cancelTwoFactor(challengeId).subscribe();
  }

  private resetToCredentials() {
    clearInterval(this.countdown);
    this.challenge = null;
    this.code = '';
  }

  loginWithProvider(provider: string) {
    this.securityService.loginWithProvider(provider);
  }

  ngOnInit() {
    const params = this.activatedRoute.snapshot.queryParams;
    // Viene del registro: se precarga el correo recién creado
    if (params.email) {
      this.user.email = params.email;
    }
    // Viene de un login OAuth rechazado
    if (params.oauthError) {
      Swal.fire("No fue posible iniciar sesión", params.oauthError, "error");
    }
    this.securityService.oauthProviders().subscribe(providers => {
      this.providers = providers
        .filter(id => PROVIDER_BUTTONS[id])
        .map(id => ({ id, ...PROVIDER_BUTTONS[id] }));
    });
  }
  ngOnDestroy() {
    clearInterval(this.countdown);
  }

}
