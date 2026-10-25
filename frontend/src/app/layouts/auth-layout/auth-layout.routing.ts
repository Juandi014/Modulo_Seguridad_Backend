import { Routes } from '@angular/router';

import { LoginComponent } from '../../pages/login/login.component';
import { RegisterComponent } from '../../pages/register/register.component';
import { OAuthCallbackComponent } from '../../pages/oauth-callback/oauth-callback.component';
import { ForgotPasswordComponent } from '../../pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from '../../pages/reset-password/reset-password.component';

export const AuthLayoutRoutes: Routes = [
    { path: 'login',           component: LoginComponent },
    { path: 'register',        component: RegisterComponent },
    { path: 'oauth-callback',  component: OAuthCallbackComponent },
    { path: 'forgot-password', component: ForgotPasswordComponent },
    { path: 'reset-password',  component: ResetPasswordComponent }
];
