package com.v1690117.fdb.core.dao;

import com.v1690117.fdb.core.model.RelatedNode;
import com.v1690117.fdb.core.model.SimpleExpansionParameters;

import java.util.List;
import java.util.Map;

public interface GraphRepository {

    String getType(Long id);

    void registerType(String type);

    Long addObject(String type);

    Long connect(Long from, Long to, String relationship);

    void disconnect(Long id);

    List<RelatedNode> expand(Long id);

    List<RelatedNode> expand(Long id, SimpleExpansionParameters expansionParams);

    Map<Long, List<RelatedNode>> readAll(String type);
}
