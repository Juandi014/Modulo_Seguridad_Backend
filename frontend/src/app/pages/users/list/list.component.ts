import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AUTH_PROVIDER_LABELS, User } from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user.service';
import { getErrorMessage } from 'src/app/utils/http-error.util';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss']
})
export class ListComponent implements OnInit {
  users: User[] = [];
  providerLabels = AUTH_PROVIDER_LABELS;
  constructor(private usersService:UserService,
    private router:Router
  ) { }

  ngOnInit(): void {
    this.list();
  }

  list(){
    this.usersService.list().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        Swal.fire('Error', getErrorMessage(error), 'error');
      }
    });
  }
  create(){
    this.router.navigate(['/users/create']);
  }
  view(id:number){
    this.router.navigate(['/users/view/'+id]);
  }
  edit(id:number){
    this.router.navigate(['/users/update/'+id]);
  }
  profile(id:number){
    this.router.navigate(['/profiles/view/'+id]);
  }
  roles(id:number){
    this.router.navigate(['/user-roles/user/'+id]);
  }
  deactivate(user:User){
    Swal.fire({
      title: 'Desactivar usuario',
      text: `¿Está seguro de desactivar a ${user.name}? No podrá iniciar sesión, pero sus roles y datos se conservan.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, desactivar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.usersService.deactivate(user.id).subscribe({
          next: () => {
            Swal.fire('Desactivado', 'Usuario desactivado correctamente.', 'success');
            this.ngOnInit();
          },
          error: (error) => {
            Swal.fire('Error', getErrorMessage(error), 'error');
          }
        });
      }
    })
  }
  activate(user:User){
    Swal.fire({
      title: 'Activar usuario',
      text: `¿Está seguro de activar nuevamente a ${user.name}?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, activar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.usersService.activate(user.id).subscribe({
          next: () => {
            Swal.fire('Activado', 'Usuario activado correctamente.', 'success');
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
