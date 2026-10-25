import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SecurityRoutingModule } from './security-routing.module';
import { AccountSecurityComponent } from './account-security/account-security.component';


@NgModule({
  declarations: [
    AccountSecurityComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SecurityRoutingModule
  ]
})
export class SecurityModule { }
