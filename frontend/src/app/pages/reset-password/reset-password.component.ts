import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PasswordRecoveryService } from 'src/app/services/password-recovery.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import { PASSWORD_PATTERN, PASSWORD_RULE_MESSAGE, passwordsMatch } from 'src/app/validators/password.validator';
import Swal from 'sweetalert2';

// Destino del enlace del correo: establecer la nueva contraseña
@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  token = '';
  validLink: boolean = null;   // null = verificando
  invalidMessage = '';
  theFormGroup: FormGroup;
  trySend = false;
  sending = false;
  passwordRuleMessage = PASSWORD_RULE_MESSAGE;

  constructor(private activatedRoute: ActivatedRoute,
    private router: Router,
    private theFormBuilder: FormBuilder,
    private passwordRecoveryService: PasswordRecoveryService) {
    this.theFormGroup = this.theFormBuilder.group({
      newPassword: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: passwordsMatch('newPassword', 'confirmPassword') });
  }

  ngOnInit(): void {
    this.token = this.activatedRoute.snapshot.queryParams.token || '';
    if (!this.token) {
      this.validLink = false;
      this.invalidMessage = 'El enlace de recuperación no es válido o ya fue utilizado';
      return;
    }
    this.passwordRecoveryService.validate(this.token).subscribe({
      next: () => this.validLink = true,
      error: (error) => {
        this.validLink = false;
        this.invalidMessage = getErrorMessage(error);
      }
    });
  }

  get getTheFormGroup() {
    return this.theFormGroup.controls
  }

  showErrors(field: string): boolean {
    const control = this.theFormGroup.get(field);
    return !!control.errors && (control.dirty || control.touched || this.trySend);
  }

  reset() {
    this.trySend = true;
    if (this.theFormGroup.invalid) {
      return;
    }
    this.sending = true;
    this.passwordRecoveryService.reset(this.token, this.theFormGroup.value.newPassword).subscribe({
      next: (response) => {
        this.sending = false;
        Swal.fire('Contraseña actualizada', response.message, 'success');
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.sending = false;
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }

}
