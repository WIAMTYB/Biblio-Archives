package com.wiam.archivesnetwork.archive;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedArchiveResponse {
    private Integer id;
    private String title;
    private String isbn;
    private double rate;
    private boolean returned;
    private boolean returnApproved;


}
