package com.v1690117.fdb.core.dao.impl;

import com.v1690117.fdb.core.dao.GraphRepository;
import com.v1690117.fdb.core.model.ExpansionParameters;
import com.v1690117.fdb.core.model.RelatedNode;
import com.v1690117.fdb.core.util.Helper;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.AccessMode;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class Neo4JRepository implements GraphRepository {
    private final Driver driver;

    @Override
    public String getType(Integer id) {
        try (var session = driver.session()) {
            return session.run(
                    String.format(
                            "MATCH (n) WHERE ID(n) = %s RETURN n.type",
                            id
                    )
            ).peek().get("n.type").asString();
        }
    }

    @Override
    public void registerType(String type) {
    }

    @Override
    public Integer addObject(String type) {
        try (var session = driver.session()) {
            var newRecord = session.run(
                    String.format(
                            "CREATE (ee:%s {type: '%s'}) return ee",
                            Helper.goodName(type),
                            type
                    )
            ).peek();
            return (int) newRecord.get("ee").asNode().id();
        }
    }

    @Override
    public Integer connect(Integer from, Integer to, String relationship) {
        try (Session session = driver.session(
                SessionConfig.builder().withDefaultAccessMode(AccessMode.WRITE).build()
        )) {
            session.run(
                    String.format(
                            "MATCH (from) WHERE ID(from) = %s "
                                    + "MATCH (to) WHERE ID(to) = %s "
                                    + "CREATE (from) -[:%s {type: '%s'}]-> (to)",
                            from,
                            to,
                            Helper.goodName(relationship),
                            relationship
                    )
            );
        }
        return 0; // todo return
    }

    @Override
    public void disconnect(Integer id) {
        // todo
    }

    @Override
    public List<RelatedNode> expand(Integer id) {
        try (var session = driver.session()) {
            return session.run(
                    String.format(
                            "MATCH (current) -[rel]- (other)  WHERE ID(current) = %s RETURN other,rel",
                            id
                    )
            ).stream().map(r -> {
                var object = r.get("other").asNode();
                var rel = r.get("rel").asRelationship();
                return new RelatedNode(
                        (int) object.id(),
                        object.get("type").asString(),
                        (int) rel.id(),
                        rel.get("type").asString(),
                        r.get("other").asNode().id() == r.get("rel").asRelationship().endNodeId(),
                        1
                );
            }).collect(Collectors.toList());
        }
    }

    @Override
    public List<RelatedNode> expand(Integer id, ExpansionParameters parameters) {
        throw new UnsupportedOperationException(
                "Not ready yet!"
        );
//        try (var session = driver.session()) {
//            var res = session.run(
//                    //match path=(rsp) -[rel *] ->(req)
//                    //  where ID(rsp) = 19045
//                    //  AND (type(rel[-1]) = 'Specification_Structure' or type(rel[-1]) = 'Sub_Requirement')
//                    // return distinct ID(req) as id,req.type as type,ID(rel[-1]), rel[-1].type
//                    // as connectionType,length(path) as level
//                    String.format(
//                            // todo distinct??
//                            "MATCH path=(expanding) %s[rel *] %s (child) "
//                                    + "where (ID(expanding) = %s %s) return distinct "
//                                    + "ID(child) as id, "
//                                    + "child.type as type, "
//                                    + "length(path) as level,"
//                                    + "rel[-1].type as connectionType,"
//                                    + "ID(rel[-1]) as connectionId",
//                            parameters.getFrom(),
//                            parameters.getTo(),
//                            id,
//                            parameters.getRelationships() == null || parameters.getRelationships().isEmpty()
//                                    ? ""
//                                    : "AND (" + parameters.getRelationships().stream()
//                                    .map(r -> String.format("type(rel[-1]) = '%s'", Helper.goodName(r)))
//                                    .collect(Collectors.joining(" OR ")) + ")"
//                    )
//            ).stream().map(r -> {
//                return new RelatedNode(
//                        r.get("id").asLong(),
//                        r.get("type").asString(),
//                        r.get("connectionId").asLong(),
//                        r.get("connectionType").asString(),
//                        true, // todo
//                        r.get("level").asInt()
//                );
//            }).collect(Collectors.toList());
//            return res;
//        }
    }

    @Override
    public Map<Integer, List<RelatedNode>> readAll(String type) {
        Map<Integer, List<RelatedNode>> map = new HashMap<>();
        try (var session = driver.session()) {
            session.run(
                    String.format(
                            "MATCH (current:%s) -[rel]- (other) RETURN current,other,rel",
                            type
                    )
            ).stream().forEach(r -> {
                var current = r.get("current").asNode();
                var object = r.get("other").asNode();
                var rel = r.get("rel").asRelationship();
                var node = new RelatedNode(
                        (int) object.id(),
                        object.get("type").asString(),
                        (int) rel.id(),
                        rel.get("type").asString(),
                        r.get("other").asNode().id() == r.get("rel").asRelationship().endNodeId(),
                        1
                );
                if (map.containsKey(current.id())) {
                    map.get(current.id()).add(node);
                } else {
                    ArrayList<RelatedNode> value = new ArrayList<>();
                    value.add(node);
                    map.put((int) current.id(), value);
                }
            });
            return map;
        }
    }
}
