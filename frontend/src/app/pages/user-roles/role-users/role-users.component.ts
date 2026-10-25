import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from 'src/app/models/role.model';
import { User } from 'src/app/models/user.model';
import { RoleUser } from 'src/app/models/user-role.model';
import { RoleService } from 'src/app/services/role.service';
import { UserRoleService } from 'src/app/services/user-role.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

// Usuarios de un rol: consultar los asignados, agregar disponibles y retirar.
@Component({
  selector: 'app-role-users',
  templateUrl: './role-users.component.html',
  styleUrls: ['./role-users.component.scss']
})
export class RoleUsersComponent implements OnInit {
  roleId: number;
  role: Role = {};
  assigned: RoleUser[] = [];
  available: User[] = [];
  filter = '';
  constructor(private activatedRoute: ActivatedRoute,
    private router: Router,
    private roleService: RoleService,
    private userRoleService: UserRoleService
  ) { }

  ngOnInit(): void {
    this.roleId = Number(this.activatedRoute.snapshot.params.id);
    this.roleService.view(this.roleId).subscribe({
      next: (role) => this.role = role,
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
        this.back();
      }
    });
    this.load();
  }

  load() {
    this.userRoleService.listByRole(this.roleId).subscribe({
      next: (assigned) => this.assigned = assigned,
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
    this.userRoleService.availableUsers(this.roleId).subscribe({
      next: (available) => this.available = available,
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
  }

  get filteredAvailable(): User[] {
    const text = this.filter.trim().toLowerCase();
    if (!text) {
      return this.available;
    }
    return this.available.filter(user =>
      user.name.toLowerCase().includes(text) || user.email.toLowerCase().includes(text));
  }

  assign(user: User) {
    this.userRoleService.assign(user.id, this.roleId).subscribe({
      next: () => {
        Swal.fire('Asignado', `${user.name} ahora tiene el rol ${this.role.name}.`, 'success');
        this.load();
      },
      error: (error) => Swal.fire('Error', getErrorMessage(error), 'error')
    });
  }

  remove(item: RoleUser) {
    Swal.fire({
      title: 'Retirar rol',
      text: `¿Está seguro de retirar el rol ${this.role.name} a ${item.user.name}? El usuario y el rol no se eliminan.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, retirar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userRoleService.remove(item.user.id, this.roleId).subscribe({
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
    this.router.navigate(['/roles/view/' + this.roleId]);
  }

}
