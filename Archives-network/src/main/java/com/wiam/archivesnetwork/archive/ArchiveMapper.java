package com.wiam.archivesnetwork.archive;

import com.wiam.archivesnetwork.file.FileUtils;
import com.wiam.archivesnetwork.history.ArchiveTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class ArchiveMapper  {

    public Archive toArchive(ArchiveRequest request) {
        return Archive.builder()
                .id(request.id())
                .title(request.title())
                .isbn(request.isbn())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }


    public ArchiveResponse toArchiveResponse(Archive archive) {
        return ArchiveResponse.builder()
                .id(archive.getId())
                .title(archive.getTitle())
                .isbn(archive.getIsbn())
                .synopsis(archive.getSynopsis())
                .rate(archive.getRate())
                .archived(archive.isArchived())
                .shareable(archive.isShareable())
                .owner(archive.getOwner().fullName())
                .cover(FileUtils.readFileFromLocation(archive.getArchiveCover()))
                .build();

    }

    public BorrowedArchiveResponse toBorrowedArchiveResponse(ArchiveTransactionHistory history) {
        return BorrowedArchiveResponse.builder()
                .id(history.getArchive().getId())
                .title(history.getArchive().getTitle())
                .isbn(history.getArchive().getIsbn())
                .rate(history.getArchive().getRate())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
