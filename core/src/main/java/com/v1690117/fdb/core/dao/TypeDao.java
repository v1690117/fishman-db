package com.v1690117.fdb.core.dao;

import java.util.Map;

public interface TypeDao {
    void addType(String type);

    void defineAttributes(String type, Map<String, String> attributes);
}
