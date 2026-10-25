import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountSecurityComponent } from './account-security/account-security.component';

const routes: Routes = [
  {path: '', component:AccountSecurityComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SecurityRoutingModule { }
