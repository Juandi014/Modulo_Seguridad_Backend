package com.uc.ms_security.service;

/**
 * Envío de correos. Es un servicio externo, por eso los servicios de
 * negocio dependen de esta interfaz y no de un proveedor concreto.
 */
public interface IEmailService {

    void send(String to, String subject, String body);
}
