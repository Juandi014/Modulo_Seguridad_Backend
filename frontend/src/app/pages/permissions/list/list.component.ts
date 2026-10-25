import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { moduleLabel, operationLabel, Permission, PERMISSION_MODULES } from 'src/app/models/permission.model';
import { PermissionService } from 'src/app/services/permission.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  permissions: Permission[] = [];
  modules = PERMISSION_MODULES;
  moduleFilter = '';
  moduleLabel = moduleLabel;
  operationLabel = operationLabel;
  constructor(private permissionsService:PermissionService,
    private router:Router
  ) { }

  ngOnInit(): void {
    this.list();
  }

  list(){
    this.permissionsService.list().subscribe({
      next: (permissions) => {
        this.permissions = permissions;
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }

  get filteredPermissions(): Permission[] {
    if (!this.moduleFilter) {
      return this.permissions;
    }
    return this.permissions.filter(p => p.module === this.moduleFilter);
  }

  create(){
    this.router.navigate(['/permissions/create']);
  }
  view(id:number){
    this.router.navigate(['/permissions/view/'+id]);
  }
  edit(id:number){
    this.router.navigate(['/permissions/update/'+id]);
  }
  delete(permission:Permission){
    Swal.fire({
      title: 'Eliminar permiso',
      text: `¿Está seguro de eliminar el permiso ${permission.method} ${permission.url}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.permissionsService.delete(permission.id).subscribe({
          next: () => {
            Swal.fire('Eliminado', 'Permiso eliminado correctamente.', 'success');
            this.ngOnInit();
          },
          error: (error) => {
            Swal.fire('Error', getErrorMessage(error), 'error');
          }
        });
      }
    })
  }

}
