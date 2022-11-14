package com.v1690117.fdb.core.dao;

import com.v1690117.fdb.core.model.ExpansionParameters;
import com.v1690117.fdb.core.model.RelatedNode;

import java.util.List;
import java.util.Map;

public interface GraphRepository {

    String getType(Integer id);

    void registerType(String type);

    Integer addObject(String type);

    Integer connect(Integer from, Integer to, String relationship);

    void disconnect(Integer id);

    List<RelatedNode> expand(Integer id);

    List<RelatedNode> expand(Integer id, ExpansionParameters expansionParams);

    Map<Integer, List<RelatedNode>> readAll(String type);
}
