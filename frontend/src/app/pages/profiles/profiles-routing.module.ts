import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManageComponent } from './manage/manage.component';

// El perfil es único por usuario, por eso no hay listado ni "crear" aparte:
// se registra la primera vez que se guarda en modo actualizar.
const routes: Routes = [
  {path: 'me/view', component:ManageComponent},
  {path: 'me/update', component:ManageComponent},
  {path: 'view/:id', component:ManageComponent},
  {path: 'update/:id', component:ManageComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProfilesRoutingModule { }
