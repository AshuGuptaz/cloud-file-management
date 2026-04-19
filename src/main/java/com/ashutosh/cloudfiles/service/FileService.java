package com.ashutosh.cloudfiles.service;

import com.ashutosh.cloudfiles.model.FileMetadata;
import com.ashutosh.cloudfiles.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload.dir}")
    private String uploadDir;

    private final FileMetadataRepository fileMetadataRepository;

    public FileService(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public FileMetadata uploadFile(MultipartFile file, String username) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String storedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalFilename(file.getOriginalFilename());
        metadata.setStoredFilename(storedFilename);
        metadata.setFileType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setUploadedBy(username);

        return fileMetadataRepository.save(metadata);
    }

    public List<FileMetadata> getUserFiles(String username) {
        return fileMetadataRepository.findByUploadedBy(username);
    }

    public Resource downloadFile(Long fileId, String username) throws MalformedURLException {
        FileMetadata metadata = fileMetadataRepository.findByIdAndUploadedBy(fileId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize()
                .resolve(metadata.getStoredFilename());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found on disk");
        }
        return resource;
    }

    public void deleteFile(Long fileId, String username) throws IOException {
        FileMetadata metadata = fileMetadataRepository.findByIdAndUploadedBy(fileId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize()
                .resolve(metadata.getStoredFilename());
        Files.deleteIfExists(filePath);
        fileMetadataRepository.delete(metadata);
    }

    public List<FileMetadata> getAllFiles() {
        return fileMetadataRepository.findAll();
    }
}
