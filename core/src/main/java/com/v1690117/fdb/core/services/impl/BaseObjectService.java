package com.v1690117.fdb.core.services.impl;

import com.v1690117.fdb.core.dao.GraphRepository;
import com.v1690117.fdb.core.dao.ObjectRepository;
import com.v1690117.fdb.core.model.ExpansionParameters;
import com.v1690117.fdb.core.model.ObjectInfo;
import com.v1690117.fdb.core.model.RelatedNode;
import com.v1690117.fdb.core.model.RelationsInfo;
import com.v1690117.fdb.core.model.Selector;
import com.v1690117.fdb.core.services.ObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BaseObjectService implements ObjectService {
    private final ObjectRepository objectRepository;
    private final GraphRepository graphRepository;

    @Override
    public List<Map<String, Object>> readAll(String type, Selector selector) {
        var graphData = graphRepository.readAll(type);
        var objectData = objectRepository.readAll(type, null);
        var relatedObjects = objectRepository.readAll(
                graphData.values().stream().flatMap(Collection::stream).map(ObjectInfo::new).collect(Collectors.toList()),
                null
        ).stream().collect(
                Collectors.toMap(
                        e -> Long.parseLong("" + e.get("id")),
                        Function.identity()
                )
        );

        return objectData.stream().map(o -> {
            var id = (Integer) o.get("id");
            return merge(
                    o,
                    graphData.get(Long.valueOf(id)),
                    relatedObjects
            );
        }).collect(Collectors.toList());
    }

    @Override
    public Integer count() {
        throw new UnsupportedOperationException(
                "Not implemented yet!"
        );
    }

    @Override
    public Integer count(String type) {
        return objectRepository.count(type);
    }

    @Override
    public Map<String, Object> read(Integer id, Selector selector) {
        Map<String, Object> sqlData = objectRepository.read(
                id,
                graphRepository.getType(id),
                null
        );
        var graphData = (List<RelatedNode>) graphRepository.expand(id);
        var relatedObjects = objectRepository.readAll(
                graphData.stream().map(ObjectInfo::new).collect(Collectors.toList()),
                null
        ).stream().collect(
                Collectors.toMap(
                        e -> Long.parseLong("" + e.get("id")),
                        Function.identity()
                )
        );
        return merge(
                sqlData,
                graphData,
                relatedObjects
        );
    }

    @Override
    public Integer create(String type, Map<String, String> attributes) {
        return objectRepository.addObject(
                graphRepository.addObject(type),
                type,
                attributes
        );
    }

    @Override
    public void update(Integer id, Map<String, String> attributes) {
        throw new UnsupportedOperationException(
                "Not implemented yet!"
        );
    }

    @Override
    public List<Map<String, Object>> expand(Integer id, ExpansionParameters parameters) {
        var graphData = (List<RelatedNode>) graphRepository.expand(id, parameters);
        var relatedObjects = objectRepository.readAll(
                graphData.stream().map(ObjectInfo::new).collect(Collectors.toList()),
                null
        ).stream().collect(
                Collectors.toMap(
                        e -> Long.parseLong("" + e.get("id")),
                        Function.identity()
                )
        );
        return graphData.stream().map(node -> {
            var map = new HashMap<>(relatedObjects.get(node.getId()));
            map.put("id[connection]", node.getConnectionId());
            map.put("type[connection]", node.getConnectionType());
            map.put("level", node.getLevel());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer connect(Integer from, Integer to, String relationship) {
        return graphRepository.connect(
                from,
                to,
                relationship
        );
    }

    @Override
    public void disconnect(Integer connectionId) {
        throw new UnsupportedOperationException(
                "Not implemented yet!"
        );
    }

    // move out
    private Map<String, Object> merge(
            Map<String, Object> sqlData,
            List<RelatedNode> graphData,
            Map<Long, Map<String, Object>> relatedObjects
    ) {
        var res = new HashMap<>(sqlData);
        if (graphData == null) {
            return res;
        }
        res.putAll(
                nodesToMap(
                        graphData,
                        relatedObjects
                )
        );
        return res;
    }

    // move out
    private Map<String, String> nodesToMap(List<RelatedNode> nodes, Map<Long, Map<String, Object>> relatedObjects) {
        var rels = new HashMap<String, RelationsInfo>();
        for (var node : nodes) {
            var key = String.format(
                    "%s[%s]",
                    node.isFrom() ? "from" : "to",
                    node.getConnectionType()
            );
            if (!rels.containsKey(key)) {
                var info = new RelationsInfo(key, node.isFrom());
                rels.put(key, info);
            }
            var object = relatedObjects.get(node.getId()); //sqlDao.read(node.getId(), node.getType());
            rels.get(key).add(node, object == null ? new HashMap<>() : object);
        }
        var res = new HashMap<String, String>();
        for (RelationsInfo value : rels.values()) {
            Map<String, String> m = value.toMap();
            res.putAll(m);
        }
        return res;
    }
}
