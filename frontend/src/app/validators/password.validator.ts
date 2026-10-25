import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Regla única de contraseña segura (la misma de @ValidPassword en el backend):
 * 8 a 72 caracteres, al menos una mayúscula, una minúscula, un número
 * y un carácter especial (@$!%*?&), sin espacios.
 * Se reutiliza en creación de usuarios, registro y recuperación.
 */
export const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])\S{8,72}$/;

export const PASSWORD_RULE_MESSAGE =
  'Entre 8 y 72 caracteres, con mayúscula, minúscula, número y carácter especial (@$!%*?&), sin espacios';

/**
 * Validador de grupo: la confirmación debe coincidir con la contraseña.
 * Marca el error { mismatch: true } en el formulario.
 */
export function passwordsMatch(passwordField: string, confirmField: string): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const password = group.get(passwordField).value;
    const confirm = group.get(confirmField).value;
    return password && confirm && password !== confirm ? { mismatch: true } : null;
  };
}
