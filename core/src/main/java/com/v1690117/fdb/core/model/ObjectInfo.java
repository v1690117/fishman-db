package com.v1690117.fdb.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ObjectInfo {
    private final Integer id;
    private final String type;

    public ObjectInfo(RelatedNode node) {
        this(node.getId(), node.getType());
    }
}
