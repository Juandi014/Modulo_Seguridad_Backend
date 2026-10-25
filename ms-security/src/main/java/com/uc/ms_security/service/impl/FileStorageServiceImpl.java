package com.uc.ms_security.service.impl;

import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.service.IFileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements IFileStorageService {

    private final Path rootDirectory;

    public FileStorageServiceImpl(
            @Value("${storage.profile-photos.dir}") String directory) {
        this.rootDirectory = Paths.get(directory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootDirectory);
        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "No se pudo crear la carpeta de archivos: " + rootDirectory,
                    exception
            );
        }
    }

    @Override
    public String store(byte[] content, String extension) {
        // Nunca se usa el nombre original: se genera uno aleatorio
        String filename = UUID.randomUUID() + "." + extension;
        try {
            Files.write(resolve(filename), content);
        } catch (IOException exception) {
            throw new UncheckedIOException("No se pudo guardar el archivo", exception);
        }
        return filename;
    }

    @Override
    public byte[] load(String filename) {
        try {
            return Files.readAllBytes(resolve(filename));
        } catch (IOException exception) {
            throw new ApplicationException(
                    ErrorCase.NOT_FOUND,
                    "No se encontró el archivo solicitado"
            );
        }
    }

    @Override
    public void delete(String filename) {
        try {
            Files.deleteIfExists(resolve(filename));
        } catch (IOException exception) {
            throw new UncheckedIOException("No se pudo eliminar el archivo", exception);
        }
    }

    // Evita salir de la carpeta raíz (por ejemplo con "../")
    private Path resolve(String filename) {
        Path file = rootDirectory.resolve(filename).normalize();
        if (!file.getParent().equals(rootDirectory)) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "Nombre de archivo inválido"
            );
        }
        return file;
    }
}
