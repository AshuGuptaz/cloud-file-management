package com.ashutosh.cloudfiles.repository;

import com.ashutosh.cloudfiles.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByUploadedBy(String username);
    Optional<FileMetadata> findByIdAndUploadedBy(Long id, String username);
}
