package com.v1690117.fdb.core.dao;

import com.v1690117.fdb.core.model.ObjectInfo;
import com.v1690117.fdb.core.model.Selector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ObjectRepository {
    Map<String, Object> read(Integer id, String type, Selector selector);

    List<Map<String, Object>> readAll(String type, Selector selector);

    List<Map<String, Object>> readAll(Collection<ObjectInfo> objects, Selector selector);

    Integer count(String type);

    Integer addObject(Integer id, String type, Map<String, String> attributes);

    List<Map<String, Object>> findByProperty(String type, String propertyName, String propertyValue, Selector selector);
}
