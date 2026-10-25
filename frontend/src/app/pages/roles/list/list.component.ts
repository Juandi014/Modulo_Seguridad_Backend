import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Role } from 'src/app/models/role.model';
import { RoleService } from 'src/app/services/role.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  roles: Role[] = [];
  constructor(private rolesService:RoleService,
    private router:Router
  ) { }

  ngOnInit(): void {
    this.list();
  }

  list(){
    this.rolesService.list().subscribe({
      next: (roles) => {
        this.roles = roles;
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }
  create(){
    this.router.navigate(['/roles/create']);
  }
  assignPermissions(){
    this.router.navigate(['/role-permissions']);
  }
  view(id:number){
    this.router.navigate(['/roles/view/'+id]);
  }
  edit(id:number){
    this.router.navigate(['/roles/update/'+id]);
  }
  delete(role:Role){
    Swal.fire({
      title: 'Eliminar rol',
      text: `¿Está seguro de eliminar el rol ${role.name}? Los usuarios y permisos del sistema no se eliminan.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.rolesService.delete(role.id).subscribe({
          next: () => {
            Swal.fire('Eliminado', 'Rol eliminado correctamente.', 'success');
            this.ngOnInit();
          },
          error: (error) => {
            Swal.fire('Error', getErrorMessage(error), 'error');
          }
        });
      }
    })
  }
  deactivate(role:Role){
    Swal.fire({
      title: 'Desactivar rol',
      text: `¿Está seguro de desactivar el rol ${role.name}? Los usuarios que lo tienen dejarán de recibir sus permisos.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, desactivar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.rolesService.deactivate(role.id).subscribe({
          next: () => {
            Swal.fire('Desactivado', 'Rol desactivado correctamente.', 'success');
            this.ngOnInit();
          },
          error: (error) => {
            Swal.fire('Error', getErrorMessage(error), 'error');
          }
        });
      }
    })
  }
  activate(role:Role){
    Swal.fire({
      title: 'Activar rol',
      text: `¿Está seguro de activar nuevamente el rol ${role.name}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, activar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.rolesService.activate(role.id).subscribe({
          next: () => {
            Swal.fire('Activado', 'Rol activado correctamente.', 'success');
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
