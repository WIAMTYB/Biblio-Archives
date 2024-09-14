package com.wiam.archivesnetwork.archive;

import com.wiam.archivesnetwork.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name="Archive")
public class ArchiveController {

    private final  ArchiveService service;

    @PostMapping
    public ResponseEntity<Integer> saveArchive(
            @Valid @RequestBody ArchiveRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.save(request,connectedUser));

    }

    @GetMapping("/{archive-id}")
    public ResponseEntity<ArchiveResponse> findArchiveById(
            @PathVariable("archive-id") Integer archiveId
    ) {
        return ResponseEntity.ok(service.findById(archiveId));
    }

    @GetMapping
    //paging functionality not return all objects in once
    public ResponseEntity<PageResponse<ArchiveResponse>> findAllArchives(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllArchives(page, size, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<ArchiveResponse>> findAllArchivesByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllArchivesByOwner(page, size, connectedUser));
    }


    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedArchiveResponse>> findAllBorrowedArchives(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllBorrowedArchives(page, size, connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedArchiveResponse>> findAllReturnedArchives(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllReturnedArchives(page, size, connectedUser));
    }


    @PatchMapping("/shareable/{archive-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("archive-id") Integer archiveId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateShareableStatus(archiveId, connectedUser));
    }

    @PatchMapping("/archived/{archive-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("archive-id") Integer archiveId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateArchivedStatus(archiveId, connectedUser));
    }

    @PostMapping("/borrow/{archive-id}")
    public ResponseEntity<Integer> borrowArchive(
            @PathVariable("archive-id") Integer archiveId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.borrowArchive(archiveId, connectedUser));
    }

    @PatchMapping("borrow/return/{archive-id}")
    public ResponseEntity<Integer> returnBorrowArchive(
            @PathVariable("archive-id") Integer archiveId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.returnBorrowedArchive(archiveId, connectedUser));
    }

    @PatchMapping("borrow/return/approve/{archive-id}")
    public ResponseEntity<Integer> approveReturnBorrowArchive(
            @PathVariable("archive-id") Integer archiveId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.approveReturnBorrowedArchive(archiveId, connectedUser));
    }

    //upload folders not in db but somewhere  in server
    @PostMapping(value = "/cover/{archive-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadArchiveCoverPicture(
            @PathVariable("archive-id") Integer archiveId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
    ) {
        service.uploadArchiveCoverPicture(file, connectedUser, archiveId);
        return ResponseEntity.accepted().build();
    }





    

}
