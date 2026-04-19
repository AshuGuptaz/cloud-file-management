package com.ashutosh.cloudfiles.controller;

import com.ashutosh.cloudfiles.model.FileMetadata;
import com.ashutosh.cloudfiles.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final FileService fileService;

    public AdminController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileMetadata>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }
}
