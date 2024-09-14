package com.wiam.archivesnetwork.archive;

import org.springframework.data.jpa.domain.Specification;

public class ArchiveSpecification {
    public static Specification<Archive> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }


}
