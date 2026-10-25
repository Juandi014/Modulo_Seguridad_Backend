import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { UserRolesRoutingModule } from './user-roles-routing.module';
import { RoleUsersComponent } from './role-users/role-users.component';
import { UserRolesComponent } from './user-roles/user-roles.component';


@NgModule({
  declarations: [
    RoleUsersComponent,
    UserRolesComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    UserRolesRoutingModule
  ]
})
export class UserRolesModule { }
