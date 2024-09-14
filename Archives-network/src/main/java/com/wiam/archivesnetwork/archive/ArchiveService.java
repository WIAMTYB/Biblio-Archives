package com.wiam.archivesnetwork.archive;

import com.wiam.archivesnetwork.common.PageResponse;
import com.wiam.archivesnetwork.exception.OperationNotPermittedException;
import com.wiam.archivesnetwork.file.FileStorageService;
import com.wiam.archivesnetwork.history.ArchiveTransactionHistory;
import com.wiam.archivesnetwork.history.ArchiveTransactionHistoryRepository;
import com.wiam.archivesnetwork.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArchiveService {
    private final ArchiveRepository archiveRepository;
    private final ArchiveMapper archiveMapper;
    private final ArchiveTransactionHistoryRepository archiveTransactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Integer save(ArchiveRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Archive archive = archiveMapper.toArchive(request);
        archive.setOwner(user);
        return archiveRepository.save(archive).getId();
    }

    public ArchiveResponse findById(Integer archiveId) {
        return archiveRepository.findById(archiveId)
                .map(archiveMapper::toArchiveResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + archiveId));
    }


    public PageResponse<ArchiveResponse> findAllArchives(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Archive> archives = archiveRepository.findAllDisplayableArchives(pageable, user.getId());
        List<ArchiveResponse> archivesResponse = archives.stream()
                .map(archiveMapper::toArchiveResponse)
                .toList();
        return new PageResponse<>(
                archivesResponse,
               archives.getNumber(),
                archives.getSize(),
                archives.getTotalElements(),
                archives.getTotalPages(),
                archives.isFirst(),
                archives.isLast()
        );
    }


    public PageResponse<ArchiveResponse> findAllArchivesByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Archive> archives = archiveRepository.findAll( ArchiveSpecification.withOwnerId(user.getId()), pageable);
        List<ArchiveResponse> archivesResponse = archives.stream()
                .map(archiveMapper::toArchiveResponse)
                .toList();
        return new PageResponse<>(
                archivesResponse,
                archives.getNumber(),
                archives.getSize(),
                archives.getTotalElements(),
                archives.getTotalPages(),
                archives.isFirst(),
                archives.isLast()
        );


    }

    public PageResponse<BorrowedArchiveResponse> findAllBorrowedArchives(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<ArchiveTransactionHistory> allBorrowedArchives = archiveTransactionHistoryRepository.findAllBorrowedArchives(pageable, user.getId());
        List<BorrowedArchiveResponse> archivesResponse = allBorrowedArchives.stream()
                .map(archiveMapper::toBorrowedArchiveResponse)
                .toList();
        return new PageResponse<>(
                archivesResponse,
                allBorrowedArchives.getNumber(),
                allBorrowedArchives.getSize(),
                allBorrowedArchives.getTotalElements(),
                allBorrowedArchives.getTotalPages(),
                allBorrowedArchives.isFirst(),
                allBorrowedArchives.isLast()
        );
    }


    public PageResponse<BorrowedArchiveResponse> findAllReturnedArchives(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<ArchiveTransactionHistory> allBorrowedArchives = archiveTransactionHistoryRepository.findAllReturnedArchives(pageable, user.getId());
        List<BorrowedArchiveResponse> archivesResponse = allBorrowedArchives.stream()
                .map(archiveMapper::toBorrowedArchiveResponse)
                .toList();
        return new PageResponse<>(
                archivesResponse,
                allBorrowedArchives.getNumber(),
                allBorrowedArchives.getSize(),
                allBorrowedArchives.getTotalElements(),
                allBorrowedArchives.getTotalPages(),
                allBorrowedArchives.isFirst(),
                allBorrowedArchives.isLast()
        );


    }

    public Integer updateShareableStatus(Integer archiveId, Authentication connectedUser) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new EntityNotFoundException("No archive found with ID:: " + archiveId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(archive.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others archives shareable status");
        }
        archive.setShareable(!archive.isShareable());
        archiveRepository.save(archive);
        return archiveId;
    }


    public Integer updateArchivedStatus(Integer archiveId, Authentication connectedUser) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new EntityNotFoundException("No archive found with ID:: " + archiveId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(archive.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others archives archived status");
        }
        archive.setArchived(!archive.isArchived());
        archiveRepository.save(archive);
        return archiveId;



    }

    public Integer borrowArchive(Integer archiveId, Authentication connectedUser) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + archiveId));
        if (archive.isArchived() || !archive.isShareable()) {
            throw new OperationNotPermittedException("The requested archive cannot be borrowed since it is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(archive.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own archive");
        }
        final boolean isAlreadyBorrowedByUser = archiveTransactionHistoryRepository.isAlreadyBorrowedByUser(archiveId, user.getId());
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("You already borrowed this archive and it is still not returned or the return is not approved by the owner");
        }

        final boolean isAlreadyBorrowedByOtherUser = archiveTransactionHistoryRepository.isAlreadyBorrowed(archiveId);
        if (isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("Te requested archive is already borrowed");
        }

        ArchiveTransactionHistory archiveTransactionHistory = ArchiveTransactionHistory.builder()
                .user(user)
                .archive(archive)
                .returned(false)
                .returnApproved(false)
                .build();
        return archiveTransactionHistoryRepository.save(archiveTransactionHistory).getId();




    }

    public Integer returnBorrowedArchive(Integer archiveId, Authentication connectedUser) {

        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new EntityNotFoundException("No archive found with ID:: " + archiveId));
        if (archive.isArchived() || !archive.isShareable()) {
            throw new OperationNotPermittedException("The requested archive is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(archive.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own archive");
        }

        ArchiveTransactionHistory archiveTransactionHistory = archiveTransactionHistoryRepository.findByArchiveIdAndUserId(archiveId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this archive"));


        archiveTransactionHistory.setReturned(true);
        return archiveTransactionHistoryRepository.save(archiveTransactionHistory).getId();
    }


    public Integer approveReturnBorrowedArchive(Integer archiveId, Authentication connectedUser) {
        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new EntityNotFoundException("No archive found with ID:: " + archiveId));
        if (archive.isArchived() || !archive.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(archive.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a archive you do not own");
        }

        ArchiveTransactionHistory archiveTransactionHistory = archiveTransactionHistoryRepository.findByArchiveIdAndOwnerId(archiveId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The archive is not returned yet. You cannot approve its return"));

        archiveTransactionHistory.setReturnApproved(true);
        return archiveTransactionHistoryRepository.save(archiveTransactionHistory).getId();
    }


    public void uploadArchiveCoverPicture(MultipartFile file, Authentication connectedUser, Integer archiveId) {

        Archive archive = archiveRepository.findById(archiveId)
                .orElseThrow(() -> new EntityNotFoundException("No archive found with ID:: " + archiveId));
        User user = ((User) connectedUser.getPrincipal());
        var profilePicture = fileStorageService.saveFile(file, user.getId());
        archive.setArchiveCover(profilePicture);
        archiveRepository.save(archive);




    }
}

