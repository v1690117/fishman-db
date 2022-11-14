package com.v1690117.fdb.core.services;

import java.util.Map;

public interface TypeService {
    void addType(String type);

    void deleteType(String type);

    void setTypeAttributes(String type, Map<String, String> attributes);
}
