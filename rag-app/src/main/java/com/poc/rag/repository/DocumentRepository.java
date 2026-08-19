package com.poc.rag.repository;

import com.poc.rag.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByFileHash(String fileHash);

    boolean existsByFileHash(String fileHash);
}