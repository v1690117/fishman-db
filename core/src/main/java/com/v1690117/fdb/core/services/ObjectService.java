package com.v1690117.fdb.core.services;

import com.v1690117.fdb.core.model.ExpansionParameters;
import com.v1690117.fdb.core.model.Selector;

import java.util.List;
import java.util.Map;

public interface ObjectService {
    Map<String, Object> read(Integer id, Selector selector);

    List<Map<String, Object>> readAll(String type, Selector selector);

    Integer count();

    Integer count(String type);

    /**
     * Here key is attr name, value - its value
     */
    Integer create(String type, Map<String, String> attributes);

    void update(Integer id, Map<String, String> attributes);

    List<Map<String, Object>> expand(Integer id, ExpansionParameters parameters);

    Integer connect(Integer from, Integer to, String relationship);

    void disconnect(Integer connectionId);
}
