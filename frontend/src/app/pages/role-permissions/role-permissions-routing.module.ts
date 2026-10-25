import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MatrixComponent } from './matrix/matrix.component';

const routes: Routes = [
  {path: '', component:MatrixComponent},          // elegir rol
  {path: 'role/:id', component:MatrixComponent}   // rol ya seleccionado
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RolePermissionsRoutingModule { }
