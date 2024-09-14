package com.wiam.archivesnetwork.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArchiveTransactionHistoryRepository extends JpaRepository<ArchiveTransactionHistory, Integer> {
    @Query("""
            SELECT history
            FROM ArchiveTransactionHistory history
            WHERE history.user.id = :userId
            """)
    Page<ArchiveTransactionHistory> findAllBorrowedArchives(Pageable pageable, Integer userId);



    @Query("""
            SELECT history
            FROM ArchiveTransactionHistory history
            WHERE history.archive.owner.id = :userId
            """)
    Page<ArchiveTransactionHistory> findAllReturnedArchives(Pageable pageable, Integer userId);



    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM ArchiveTransactionHistory archiveTransactionHistory
            WHERE archiveTransactionHistory.user.id = :userId
            AND archiveTransactionHistory.archive.id = :archiveId
            AND archiveTransactionHistory.returnApproved = false
            """)

    boolean isAlreadyBorrowedByUser(Integer archiveId, Integer userId);


    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM ArchiveTransactionHistory archiveTransactionHistory
            WHERE archiveTransactionHistory.archive.id = :archiveId
            AND archiveTransactionHistory.returnApproved = false
            """)

    boolean isAlreadyBorrowed(Integer archiveId);


    @Query("""
            SELECT transaction
            FROM ArchiveTransactionHistory  transaction
            WHERE transaction.user.id = :userId
            AND transaction.archive.id = :archiveId
            AND transaction.returned = false
            AND transaction.returnApproved = false
            """)
    Optional<ArchiveTransactionHistory> findByArchiveIdAndUserId(Integer archiveId, Integer userId);



    @Query("""
            SELECT transaction
            FROM ArchiveTransactionHistory  transaction
            WHERE transaction.archive.owner.id = :userId
            AND transaction.archive.id = :archiveId
            AND transaction.returned = true
            AND transaction.returnApproved = false
            """)

    Optional<ArchiveTransactionHistory> findByArchiveIdAndOwnerId(Integer archiveId, Integer userId);
}
