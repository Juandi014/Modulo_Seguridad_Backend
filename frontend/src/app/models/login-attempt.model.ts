export class LoginAttempt {
    id?:number;
    email?:string;
    userId?:number;
    userName?:string;
    method?:string;
    result?:string;
    ipAddress?:string;
    createdAt?:string;
}

export class Page<T> {
    content:T[] = [];
    page = 0;
    size = 20;
    totalElements = 0;
    totalPages = 0;
}

// Etiquetas en español de cada resultado; success = se muestra en verde
export const LOGIN_RESULTS: { value: string, label: string, success: boolean }[] = [
    { value: 'SUCCESS', label: 'Exitoso', success: true },
    { value: 'PENDING_2FA', label: 'Contraseña correcta, código 2FA enviado', success: true },
    { value: 'INVALID_CREDENTIALS', label: 'Credenciales incorrectas', success: false },
    { value: 'USER_INACTIVE', label: 'Usuario desactivado', success: false },
    { value: 'EXTERNAL_ACCOUNT', label: 'Cuenta OAuth intentó usar contraseña', success: false },
    { value: 'INVALID_2FA_CODE', label: 'Código 2FA incorrecto', success: false },
    { value: 'EXPIRED_2FA_CODE', label: 'Código 2FA expirado', success: false },
    { value: 'TOO_MANY_2FA_ATTEMPTS', label: 'Demasiados intentos de código 2FA', success: false },
    { value: 'OAUTH_REJECTED', label: 'Acceso OAuth rechazado', success: false }
];
