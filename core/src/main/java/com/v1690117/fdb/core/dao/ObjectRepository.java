package com.v1690117.fdb.core.dao;

import com.v1690117.fdb.core.model.ObjectInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ObjectRepository {
    Long count(String type);

    Long addObject(Long id, String type, Map<String, String> attributes);

    Map<String, Object> read(Long id, String type);

    List<Map<String, Object>> readAll(String type);

    List<Map<String, Object>> readAll(Collection<ObjectInfo> objects);

    List<Map<String, Object>> findByProperty(String type, String propertyName, String propertyValue);
}
