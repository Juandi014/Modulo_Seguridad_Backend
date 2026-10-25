import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoleUsersComponent } from './role-users/role-users.component';
import { UserRolesComponent } from './user-roles/user-roles.component';

const routes: Routes = [
  {path: 'role/:id', component:RoleUsersComponent},   // usuarios de un rol
  {path: 'user/:id', component:UserRolesComponent}    // roles de un usuario
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRolesRoutingModule { }
