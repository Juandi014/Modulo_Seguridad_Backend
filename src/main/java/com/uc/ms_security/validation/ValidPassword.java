package com.uc.ms_security.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Regla única de contraseña segura del sistema.
 * Se reutiliza en creación de usuarios, registro y recuperación.
 * Un valor null es válido: la obligatoriedad se controla con @NotBlank
 * en cada DTO que lo requiera.
 */
@Size(
        min = 8,
        max = 72,
        message = "La contraseña debe tener entre 8 y 72 caracteres"
)
@Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])\\S{8,72}$",
        message = "La contraseña debe contener al menos una mayúscula, "
                + "una minúscula, un número y un carácter especial (@$!%*?&), "
                + "sin espacios"
)
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "La contraseña no cumple las reglas de seguridad";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
