import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  HTTP_METHODS, Permission, PERMISSION_MODULES, PERMISSION_OPERATIONS
} from 'src/app/models/permission.model';
import { PermissionService } from 'src/app/services/permission.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-manage',
  templateUrl: './manage.component.html',
  styleUrls: ['./manage.component.scss']
})
export class ManageComponent implements OnInit {
  mode: number; // 1: view, 2: create, 3: update
  permission: Permission;
  theFormGroup: FormGroup; // Policía de formulario
  trySend: boolean;
  modules = PERMISSION_MODULES;
  operations = PERMISSION_OPERATIONS;
  methods = HTTP_METHODS;
  existingResources: string[] = []; // sugerencias para escribir el recurso igual
  constructor(private activatedRoute: ActivatedRoute,
    private permissionsService: PermissionService,
    private router: Router,
    private theFormBuilder: FormBuilder //Definir las reglas
  ) {
    this.trySend = false;
    this.permission = { id: 0 };
    this.configFormGroup()
  }

  ngOnInit(): void {
    const currentUrl = this.activatedRoute.snapshot.url.join('/');
    if (currentUrl.includes('view')) {
      this.mode = 1;
      this.theFormGroup.disable();
    } else if (currentUrl.includes('create')) {
      this.mode = 2;
    } else if (currentUrl.includes('update')) {
      this.mode = 3;
    }
    if (this.activatedRoute.snapshot.params.id) {
      this.permission.id = this.activatedRoute.snapshot.params.id
      this.getPermission(this.permission.id)
    }
    this.loadExistingResources();
  }
  configFormGroup() {
    this.theFormGroup = this.theFormBuilder.group({
      // primer elemento del vector, valor por defecto
      // lista, serán las reglas
      id: [0,[]],
      module: ['', [Validators.required]],
      resource: ['', [Validators.required, Validators.maxLength(50)]],
      operation: ['', [Validators.required]],
      method: ['', [Validators.required]],
      url: ['', [Validators.required, Validators.maxLength(255), Validators.pattern(/^\/\S*$/)]]
    })
  }


  get getTheFormGroup() {
    return this.theFormGroup.controls
  }

  loadExistingResources() {
    this.permissionsService.list().subscribe({
      next: (permissions) => {
        this.existingResources = Array.from(new Set(
          permissions.map(p => p.resource).filter(resource => !!resource)
        )).sort();
      }
    });
  }

  getPermission(id: number) {
    this.permissionsService.view(id).subscribe({
      next: (response) => {
        this.permission = response;

        this.theFormGroup.patchValue({
          id: this.permission.id,
          module: this.permission.module || '',
          resource: this.permission.resource || '',
          operation: this.permission.operation || '',
          method: this.permission.method,
          url: this.permission.url
        });
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
        this.back();
      }
    });
  }
  back() {
    this.router.navigate(['/permissions/list']);
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
    this.permissionsService.create(this.theFormGroup.value).subscribe({
      next: () => {
        Swal.fire({
          title: 'Creado!',
          text: 'Permiso creado correctamente.',
          icon: 'success',
        })
        this.router.navigate(['/permissions/list']);
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
    this.permissionsService.update(this.theFormGroup.value).subscribe({
      next: () => {
        Swal.fire({
          title: 'Actualizado!',
          text: 'Permiso actualizado correctamente.',
          icon: 'success',
        })
        this.router.navigate(['/permissions/list']);
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }

}
