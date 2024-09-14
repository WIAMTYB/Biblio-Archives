package com.wiam.archivesnetwork.feedback;


import com.wiam.archivesnetwork.archive.Archive;
import com.wiam.archivesnetwork.archive.ArchiveRepository;
import com.wiam.archivesnetwork.common.PageResponse;
import com.wiam.archivesnetwork.exception.OperationNotPermittedException;
import com.wiam.archivesnetwork.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedBackRepository feedBackRepository;
    private final FeedbackMapper feedbackMapper;
    private final ArchiveRepository archiveRepository;
    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Archive archive = archiveRepository.findById(request.archiveId())
                .orElseThrow(() -> new EntityNotFoundException("No archive found with ID:: " + request.archiveId()));
        if (archive.isArchived() || !archive.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for and archived or not shareable book");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(archive.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give feedback to your own book");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedBackRepository.save(feedback).getId();
    }


    public PageResponse<FeedbackResponse> findAllFeedbacksByArchive(Integer archiveId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedBackRepository.findAllByArchiveId(archiveId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );

    }
}

