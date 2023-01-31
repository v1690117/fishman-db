package com.v1690117.fdb.core.model;

import java.util.List;

public interface ExpansionParameters {
    List<String> relationships();

    int level();

    boolean includeFrom();

    boolean includeTo();
}
