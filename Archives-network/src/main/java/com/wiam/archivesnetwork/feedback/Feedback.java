package com.wiam.archivesnetwork.feedback;

import com.wiam.archivesnetwork.archive.Archive;
import com.wiam.archivesnetwork.common.BaseEntity;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Feedback  extends BaseEntity {


    private Double note;
    private String comment;

    @ManyToOne
    @JoinColumn(name="archive_id")
    private Archive archive;


}
