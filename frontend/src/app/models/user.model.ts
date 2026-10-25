export type AuthProvider = 'LOCAL' | 'GOOGLE' | 'GITHUB' | 'FACEBOOK';

export class User {
    id?:number;
    name?:string;
    email?:string;
    password?:string;
    active?:boolean;
    authProvider?:AuthProvider;
    token?:string;
    expiresAt?:string;
}

export const AUTH_PROVIDER_LABELS: { [key: string]: string } = {
    LOCAL: 'Correo y contraseña',
    GOOGLE: 'Google',
    GITHUB: 'GitHub',
    FACEBOOK: 'Facebook'
};
