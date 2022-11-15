package com.v1690117.fdb.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Node given by an expansion
 */
@Getter
@AllArgsConstructor
public class RelatedNode {
    private final Integer id;
    private final String type;
    private final Integer connectionId;
    private final String connectionType;
    private final boolean from;
    private final Integer level;
}
