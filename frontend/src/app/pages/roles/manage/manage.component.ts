import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from 'src/app/models/role.model';
import { RoleService } from 'src/app/services/role.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-manage',
  templateUrl: './manage.component.html',
  styleUrls: ['./manage.component.scss']
})
export class ManageComponent implements OnInit {
  mode: number; // 1: view, 2: create, 3: update
  role: Role;
  theFormGroup: FormGroup; // Policía de formulario
  trySend: boolean;
  constructor(private activatedRoute: ActivatedRoute,
    private rolesService: RoleService,
    private router: Router,
    private theFormBuilder: FormBuilder //Definir las reglas
  ) {
    this.trySend = false;
    this.role = { id: 0 };
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
    if (this.activatedRoute.snapshot.params.id) {
      this.role.id = this.activatedRoute.snapshot.params.id
      this.getRole(this.role.id)
    }

  }
  configFormGroup() {
    this.theFormGroup = this.theFormBuilder.group({
      // primer elemento del vector, valor por defecto
      // lista, serán las reglas
      id: [0,[]],
      name: ['', [Validators.required, Validators.maxLength(50)]],
      description: ['', [Validators.required, Validators.maxLength(255)]]
    })
  }


  get getTheFormGroup() {
    return this.theFormGroup.controls
  }

  getRole(id: number) {
    this.rolesService.view(id).subscribe({
      next: (response) => {
        this.role = response;

        this.theFormGroup.patchValue({
          id: this.role.id,
          name: this.role.name,
          description: this.role.description
        });
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
        this.back();
      }
    });
  }
  back() {
    this.router.navigate(['/roles/list']);
  }

  users() {
    this.router.navigate(['/user-roles/role/' + this.role.id]);
  }

  permissions() {
    this.router.navigate(['/role-permissions/role/' + this.role.id]);
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
    this.rolesService.create(this.theFormGroup.value).subscribe({
      next: () => {
        Swal.fire({
          title: 'Creado!',
          text: 'Rol creado correctamente.',
          icon: 'success',
        })
        this.router.navigate(['/roles/list']);
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
    this.rolesService.update(this.theFormGroup.value).subscribe({
      next: () => {
        Swal.fire({
          title: 'Actualizado!',
          text: 'Rol actualizado correctamente.',
          icon: 'success',
        })
        this.router.navigate(['/roles/list']);
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }

}
