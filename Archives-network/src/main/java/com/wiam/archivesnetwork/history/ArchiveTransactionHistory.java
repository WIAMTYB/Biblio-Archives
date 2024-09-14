package com.wiam.archivesnetwork.history;

import com.wiam.archivesnetwork.archive.Archive;
import com.wiam.archivesnetwork.common.BaseEntity;
import com.wiam.archivesnetwork.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ArchiveTransactionHistory extends BaseEntity {
   //user relationship
   @ManyToOne
   @JoinColumn(name = "user_id")
   private User user;

    // archive relationship
    @ManyToOne
    @JoinColumn(name = "archive_id")
    private Archive archive;





    private boolean returned;
    private boolean returnApproved;

}
