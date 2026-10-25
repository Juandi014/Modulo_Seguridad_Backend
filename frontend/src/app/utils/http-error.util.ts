import { HttpErrorResponse } from '@angular/common/http';

/**
 * Extrae el mensaje de error que envía ms-security:
 * - ApplicationException: { errorCase, message }
 * - Validación (@Valid):   { campo: mensaje, ... }
 */
export function getErrorMessage(error: HttpErrorResponse): string {
  const body = error.error;
  if (error.status === 0) {
    return 'No fue posible conectarse con el servidor.';
  }
  if (error.status === 403) {
    return 'No tiene permiso para realizar esta operación.';
  }
  if (body && typeof body === 'object') {
    if (body.message) {
      return body.message;
    }
    const messages = Object.values(body).filter(value => typeof value === 'string');
    if (messages.length > 0) {
      return messages.join('\n');
    }
  }
  return 'Ocurrió un error inesperado.';
}
