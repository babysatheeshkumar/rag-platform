package com.poc.rag.repository;

import com.poc.rag.entity.RagAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RagAuditRepository extends JpaRepository<RagAudit, Long> {
}
