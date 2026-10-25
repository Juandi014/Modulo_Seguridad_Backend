import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from 'src/app/models/role.model';
import { User } from 'src/app/models/user.model';
import { UserRole } from 'src/app/models/user-role.model';
import { UserService } from 'src/app/services/user.service';
import { UserRoleService } from 'src/app/services/user-role.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

// Roles de un usuario: consultar los asignados, agregar disponibles y retirar.
@Component({
  selector: 'app-user-roles',
  templateUrl: './user-roles.component.html',
  styleUrls: ['./user-roles.component.scss']
})
export class UserRolesComponent implements OnInit {
  userId: number;
  user: User = {};
  assigned: UserRole[] = [];
  available: Role[] = [];
  constructor(private activatedRoute: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private userRoleService: UserRoleService
  ) { }

  ngOnInit(): void {
    this.userId = Number(this.activatedRoute.snapshot.params.id);
    this.userService.view(this.userId).subscribe({
      next: (user) => this.user = user,
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
        this.back();
      }
    });
    this.load();
  }

  load() {
    this.userRoleService.listByUser(this.userId).subscribe({
      next: (assigned) => this.assigned = assigned,
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
    this.userRoleService.availableRoles(this.userId).subscribe({
      next: (available) => this.available = available,
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
  }

  assign(role: Role) {
    this.userRoleService.assign(this.userId, role.id).subscribe({
      next: () => {
        Swal.fire('Asignado', `Rol ${role.name} asignado a ${this.user.name}.`, 'success');
        this.load();
      },
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
  }

  remove(item: UserRole) {
    Swal.fire({
      title: 'Retirar rol',
      text: `¿Está seguro de retirar el rol ${item.role.name} a ${this.user.name}? El usuario y el rol no se eliminan.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, retirar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userRoleService.remove(this.userId, item.role.id).subscribe({
          next: () => {
            Swal.fire('Retirado', 'Rol retirado correctamente.', 'success');
            this.load();
          },
          error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
        });
      }
    })
  }

  back() {
    this.router.navigate(['/users/list']);
  }

}
