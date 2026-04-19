package com.ashutosh.cloudfiles.controller;

import com.ashutosh.cloudfiles.model.FileMetadata;
import com.ashutosh.cloudfiles.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String username) throws IOException {
        return ResponseEntity.ok(fileService.uploadFile(file, username));
    }

    @GetMapping
    public ResponseEntity<List<FileMetadata>> listFiles(
            @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(fileService.getUserFiles(username));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> download(
            @PathVariable Long fileId,
            @AuthenticationPrincipal String username) throws IOException {
        Resource resource = fileService.downloadFile(fileId, username);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable Long fileId,
            @AuthenticationPrincipal String username) throws IOException {
        fileService.deleteFile(fileId, username);
        return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
    }
}
