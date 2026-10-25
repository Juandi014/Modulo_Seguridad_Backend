import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';

import { RolePermissionsRoutingModule } from './role-permissions-routing.module';
import { MatrixComponent } from './matrix/matrix.component';


@NgModule({
  declarations: [
    MatrixComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbDropdownModule,
    RolePermissionsRoutingModule
  ]
})
export class RolePermissionsModule { }
