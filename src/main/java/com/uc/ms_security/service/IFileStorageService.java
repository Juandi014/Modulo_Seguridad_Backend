package com.uc.ms_security.service;

/**
 * Almacenamiento físico de archivos. Los servicios de negocio dependen
 * de esta interfaz; hoy se guarda en disco local, mañana podría ser
 * un servicio en la nube sin cambiar a quien la usa.
 */
public interface IFileStorageService {

    /** Guarda el contenido y devuelve el nombre generado del archivo. */
    String store(byte[] content, String extension);

    byte[] load(String filename);

    void delete(String filename);
}
