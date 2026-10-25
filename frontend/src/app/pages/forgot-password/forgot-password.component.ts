import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PasswordRecoveryService } from 'src/app/services/password-recovery.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

// Solicitar la recuperación de contraseña indicando el correo
@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  theFormGroup: FormGroup;
  trySend = false;
  sending = false;
  sentMessage = '';

  constructor(private theFormBuilder: FormBuilder,
    private passwordRecoveryService: PasswordRecoveryService) {
    this.theFormGroup = this.theFormBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  get getTheFormGroup() {
    return this.theFormGroup.controls
  }

  send() {
    this.trySend = true;
    if (this.theFormGroup.invalid) {
      return;
    }
    this.sending = true;
    this.passwordRecoveryService.request(this.theFormGroup.value.email).subscribe({
      next: (response) => {
        this.sending = false;
        this.sentMessage = response.message;
      },
      error: (error) => {
        this.sending = false;
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }

}
