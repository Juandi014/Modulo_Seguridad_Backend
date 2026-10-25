export class Profile {
    id?:number;
    phone?:string;
    hasPhoto?:boolean;
}

// Respuesta de GET /users/{id}/profile y GET /profile/me
export class UserDetail {
    id?:number;
    name?:string;
    email?:string;
    profile?:Profile;
}
