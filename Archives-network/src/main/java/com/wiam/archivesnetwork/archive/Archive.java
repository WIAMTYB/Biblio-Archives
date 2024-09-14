package com.wiam.archivesnetwork.archive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wiam.archivesnetwork.common.BaseEntity;
import com.wiam.archivesnetwork.feedback.Feedback;
import com.wiam.archivesnetwork.history.ArchiveTransactionHistory;
import com.wiam.archivesnetwork.user.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Archive  extends BaseEntity {

    private String title;
    private String isbn;
    private String synopsis;
    private String archiveCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;

    @OneToMany(mappedBy ="archive")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy ="archive")
    private List<ArchiveTransactionHistory> histories;


    @Transient
    public double getRate(){
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        double roundedRate = Math.round(rate * 10.0) / 10.0;

        // Return 4.0 if roundedRate is less than 4.5, otherwise return 4.5
        return roundedRate;
    }
}
