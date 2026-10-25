import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AUTH_PROVIDER_LABELS, User } from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import { PASSWORD_PATTERN, PASSWORD_RULE_MESSAGE } from 'src/app/validators/password.validator';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-manage',
  templateUrl: './manage.component.html',
  styleUrls: ['./manage.component.scss']
})
export class ManageComponent implements OnInit {
  mode: number; // 1: view, 2: create, 3: update
  user: User;
  theFormGroup: FormGroup; // Policía de formulario
  trySend: boolean;
  passwordRuleMessage = PASSWORD_RULE_MESSAGE;
  providerLabels = AUTH_PROVIDER_LABELS;

  // Cuentas OAuth: no tienen contraseña en la plataforma
  get isExternalAccount(): boolean {
    return !!this.user.authProvider && this.user.authProvider !== 'LOCAL';
  }
  constructor(private activatedRoute: ActivatedRoute,
    private usersService: UserService,
    private router: Router,
    private theFormBuilder: FormBuilder //Definir las reglas
  ) {
    this.trySend = false;
    this.user = { id: 0 };
    this.configFormGroup()
  }

  ngOnInit(): void {
    const currentUrl = this.activatedRoute.snapshot.url.join('/');
    if (currentUrl.includes('view')) {
      this.mode = 1;
    } else if (currentUrl.includes('create')) {
      this.mode = 2;
    } else if (currentUrl.includes('update')) {
      this.mode = 3;
    }
    this.configPasswordRules();
    if (this.activatedRoute.snapshot.params.id) {
      this.user.id = this.activatedRoute.snapshot.params.id
      this.getUser(this.user.id)
    }

  }
  configFormGroup() {
    this.theFormGroup = this.theFormBuilder.group({
      // primer elemento del vector, valor por defecto
      // lista, serán las reglas
      id: [0,[]],
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', []]
    })
  }

  // La contraseña es obligatoria al crear y opcional al actualizar;
  // en ambos casos debe cumplir la regla única de seguridad.
  configPasswordRules() {
    const password = this.theFormGroup.get('password');
    if (this.mode == 2) {
      password.setValidators([Validators.required, Validators.pattern(PASSWORD_PATTERN)]);
    } else {
      password.setValidators([Validators.pattern(PASSWORD_PATTERN)]);
    }
    password.updateValueAndValidity();
  }


  get getTheFormGroup() {
    return this.theFormGroup.controls
  }

  getUser(id: number) {
    this.usersService.view(id).subscribe({
      next: (response) => {
        this.user = response;

        this.theFormGroup.patchValue({
          id: this.user.id,
          name: this.user.name,
          email: this.user.email
        });
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
        this.back();
      }
    });
  }
  back() {
    this.router.navigate(['/users/list']);
  }

  create() {
    this.trySend = true;
    if (this.theFormGroup.invalid) {
      Swal.fire({
        title: 'Error!',
        text: 'Por favor, complete correctamente todos los campos requeridos.',
        icon: 'error',
      })
      return;
    }
    this.usersService.create(this.theFormGroup.value).subscribe({
      next: () => {
        Swal.fire({
          title: 'Creado!',
          text: 'Usuario creado correctamente.',
          icon: 'success',
        })
        this.router.navigate(['/users/list']);
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }
  update() {
    this.trySend = true;
    if (this.theFormGroup.invalid) {
      Swal.fire({
        title: 'Error!',
        text: 'Por favor, complete correctamente todos los campos requeridos.',
        icon: 'error',
      })
      return;
    }
    this.usersService.update(this.theFormGroup.value).subscribe({
      next: () => {
        Swal.fire({
          title: 'Actualizado!',
          text: 'Usuario actualizado correctamente.',
          icon: 'success',
        })
        this.router.navigate(['/users/list']);
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }

}
