package com.v1690117.fdb.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@AllArgsConstructor
@Builder
@Accessors(fluent = true)
@Getter
public class SimpleExpansionParameters implements ExpansionParameters {
    private List<String> relationships;
    private int level;
    private boolean from;
    private boolean to;

    public String getFrom() {
        return from && to
                ? "-"
                : from ? "-" : "<-";
    }

    public String getTo() {
        return from && to
                ? "-"
                : from ? "->" : "-";
    }
}
