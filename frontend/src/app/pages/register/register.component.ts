import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RegistrationService } from 'src/app/services/registration.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import { PASSWORD_PATTERN, PASSWORD_RULE_MESSAGE, passwordsMatch } from 'src/app/validators/password.validator';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  theFormGroup: FormGroup; // Policía de formulario
  trySend = false;
  sending = false;
  passwordRuleMessage = PASSWORD_RULE_MESSAGE;

  constructor(private theFormBuilder: FormBuilder,
    private registrationService: RegistrationService,
    private router: Router) {
    this.configFormGroup();
  }

  ngOnInit() {
  }

  configFormGroup() {
    this.theFormGroup = this.theFormBuilder.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: passwordsMatch('password', 'confirmPassword') });
  }

  get getTheFormGroup() {
    return this.theFormGroup.controls
  }

  showErrors(field: string): boolean {
    const control = this.theFormGroup.get(field);
    return !!control.errors && (control.dirty || control.touched || this.trySend);
  }

  register() {
    this.trySend = true;
    if (this.theFormGroup.invalid) {
      Swal.fire({
        title: 'Error!',
        text: 'Por favor, complete correctamente todos los campos requeridos.',
        icon: 'error',
      })
      return;
    }
    this.sending = true;
    this.registrationService.register(this.theFormGroup.value).subscribe({
      next: (user) => {
        this.sending = false;
        Swal.fire('Cuenta creada', 'Su cuenta fue creada correctamente. Ya puede iniciar sesión.', 'success');
        this.router.navigate(['/login'], { queryParams: { email: user.email } });
      },
      error: (error) => {
        this.sending = false;
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }

}
