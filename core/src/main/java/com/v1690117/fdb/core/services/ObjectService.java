package com.v1690117.fdb.core.services;

import com.v1690117.fdb.core.model.SimpleExpansionParameters;

import java.util.List;
import java.util.Map;

public interface ObjectService {
    Map<String, Object> read(Integer id);

    List<Map<String, Object>> readAll();

    List<Map<String, Object>> readAll(String type);

    Integer count();

    Integer count(String type);

    Integer create(String type, Map<String, String> attributes);

    void update(Integer id, Map<String, String> attributes);

    List<Map<String, Object>> expand(Integer id, SimpleExpansionParameters parameters);

    Integer connect(Integer from, Integer to, String relationship);

    void disconnect(Integer connectionId);
}
