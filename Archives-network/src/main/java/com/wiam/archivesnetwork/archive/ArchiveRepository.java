package com.wiam.archivesnetwork.archive;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ArchiveRepository  extends JpaRepository<Archive,Integer>, JpaSpecificationExecutor<Archive> {
    @Query("""
            SELECT archive
            FROM Archive archive
            WHERE archive.archived = false
            AND archive.shareable = true
            AND archive.owner.id != :userId
            """)
    Page<Archive> findAllDisplayableArchives(Pageable pageable, Integer userId);
}
