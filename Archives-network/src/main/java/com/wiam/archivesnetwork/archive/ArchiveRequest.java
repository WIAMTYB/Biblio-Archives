package com.wiam.archivesnetwork.archive;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record  ArchiveRequest(

        Integer id,
        @NotNull(message = "100")
        @NotEmpty(message = "100")
        String title,

        @NotNull(message = "102")
        @NotEmpty(message = "102")
        String isbn,
        @NotNull(message = "103")
        @NotEmpty(message = "103")
        String synopsis,
        boolean shareable


) {
}
