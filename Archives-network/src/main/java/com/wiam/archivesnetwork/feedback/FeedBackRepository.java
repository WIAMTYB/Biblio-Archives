package com.wiam.archivesnetwork.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedBackRepository extends JpaRepository<Feedback, Integer> {
    @Query("""
            SELECT feedback
            FROM Feedback  feedback
            WHERE feedback.archive.id = :archiveId
""")
    Page<Feedback> findAllByArchiveId(@Param("archiveId") Integer archiveId, Pageable pageable);
}
