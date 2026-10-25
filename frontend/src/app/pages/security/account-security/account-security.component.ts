import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AUTH_PROVIDER_LABELS, User } from 'src/app/models/user.model';
import { AccountService, AccountSession, TwoFactorStatus } from 'src/app/services/account.service';
import { SecurityService } from 'src/app/services/security.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import { PASSWORD_PATTERN, PASSWORD_RULE_MESSAGE, passwordsMatch } from 'src/app/validators/password.validator';
import Swal from 'sweetalert2';

// Pantalla "Seguridad": la seguridad de la cuenta del usuario autenticado
@Component({
  selector: 'app-account-security',
  templateUrl: './account-security.component.html',
  styleUrls: ['./account-security.component.scss']
})
export class AccountSecurityComponent implements OnInit {
  user: User;
  providerLabels = AUTH_PROVIDER_LABELS;
  passwordForm: FormGroup;
  trySend = false;
  sending = false;
  passwordRuleMessage = PASSWORD_RULE_MESSAGE;
  twoFactor: TwoFactorStatus = null;
  sessions: AccountSession[] = [];

  constructor(private securityService: SecurityService,
    private accountService: AccountService,
    private theFormBuilder: FormBuilder,
    private router: Router) {
    this.passwordForm = this.theFormBuilder.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: passwordsMatch('newPassword', 'confirmPassword') });
  }

  ngOnInit(): void {
    this.user = this.securityService.activeUserSession;
    this.loadTwoFactor();
    this.loadSessions();
  }

  // ---- Autenticación de dos factores ----

  loadTwoFactor() {
    this.accountService.twoFactorStatus().subscribe({
      next: (status) => this.twoFactor = status,
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
  }

  toggleTwoFactor() {
    const enable = !this.twoFactor.enabled;
    Swal.fire({
      title: enable ? 'Habilitar 2FA' : 'Deshabilitar 2FA',
      text: enable
        ? 'Cada vez que inicie sesión le enviaremos un código a su correo. Confirme su contraseña actual.'
        : 'Su cuenta quedará protegida solo con la contraseña. Confirme su contraseña actual.',
      input: 'password',
      inputPlaceholder: 'Contraseña actual',
      showCancelButton: true,
      confirmButtonText: enable ? 'Habilitar' : 'Deshabilitar',
      cancelButtonText: 'Cancelar',
      inputValidator: (value) => value ? null : 'Ingrese su contraseña actual'
    }).then((result) => {
      if (!result.isConfirmed) {
        return;
      }
      this.accountService.setTwoFactor(enable, result.value).subscribe({
        next: (status) => {
          this.twoFactor = status;
          Swal.fire('Listo', enable
            ? 'Autenticación de dos factores habilitada.'
            : 'Autenticación de dos factores deshabilitada.', 'success');
        },
        error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
      });
    });
  }

  // ---- Sesiones ----

  loadSessions() {
    this.accountService.mySessions().subscribe({
      next: (sessions) => this.sessions = sessions,
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
  }

  sessionState(session: AccountSession): string {
    if (session.revokedAt) {
      return 'Cerrada';
    }
    return session.active ? 'Activa' : 'Expirada';
  }

  // Cuentas OAuth: no tienen contraseña ni 2FA en la plataforma
  get isExternalAccount(): boolean {
    return !!this.user?.authProvider && this.user.authProvider !== 'LOCAL';
  }

  get passwordControls() {
    return this.passwordForm.controls;
  }

  showErrors(field: string): boolean {
    const control = this.passwordForm.get(field);
    return !!control.errors && (control.dirty || control.touched || this.trySend);
  }

  changePassword() {
    this.trySend = true;
    if (this.passwordForm.invalid) {
      return;
    }
    const { currentPassword, newPassword } = this.passwordForm.value;
    this.sending = true;
    this.accountService.changePassword(currentPassword, newPassword).subscribe({
      next: (response) => {
        this.sending = false;
        // El backend cerró todas las sesiones (incluida esta)
        this.securityService.clearSession();
        Swal.fire('Contraseña actualizada', response.message, 'success');
        this.router.navigate(['/login'], { queryParams: { email: this.user.email } });
      },
      error: (error) => {
        this.sending = false;
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }

}
