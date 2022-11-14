package com.v1690117.fdb.core.dao;

import java.util.Map;

public interface TypeRepository {
    void addType(String type);

    void deleteType(String type);

    /**
     * TODO use attribute list instead of map
     * It is assumed here that key is name of the attribute and the value is attribute type.
     */
    void defineAttributes(String type, Map<String, String> attributes);
}
