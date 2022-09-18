package com.v1690117.fdb.core.services;

import java.util.Set;

public interface TypeService {
    void addType(String type);

    void deleteType(String type);

    void setTypeAttributes(String type, Set<String> split);
}
