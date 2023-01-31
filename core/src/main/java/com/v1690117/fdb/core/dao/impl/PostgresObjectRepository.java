package com.v1690117.fdb.core.dao.impl;

import com.v1690117.fdb.core.dao.ObjectRepository;
import com.v1690117.fdb.core.model.ObjectInfo;
import com.v1690117.fdb.core.model.Selector;
import com.v1690117.fdb.core.util.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.v1690117.fdb.core.util.Helper.prepareAttr;
import static com.v1690117.fdb.core.util.Helper.replacements;
import static com.v1690117.fdb.core.util.Helper.tableName;

@RequiredArgsConstructor
@Repository
// todo rename to DAO
public class PostgresObjectRepository implements ObjectRepository {
    public static final int VARCHAR_SIZE = 239;
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Integer count(String type) {
        return jdbc.queryForObject(
                "select count(id) from " + tableName(type),
                Collections.emptyMap(),
                Integer.class
        );
    }

    @Override
    public Map<String, Object> read(Integer id, String type, Selector selector) {
        var res = jdbc.queryForMap(
                "select * from " + tableName(type) + " where id = :id",
                Collections.singletonMap("id", id)
        );
        res.put("type", type);
        return res;
    }

    @Override
    public List<Map<String, Object>> readAll(String type, Selector selector) {
        var res = jdbc.queryForList(
                "select * from " + tableName(type),
                Collections.emptyMap()
        );
        for (var re : res) {
            re.put("type", type);
        }
        return res;
    }

    @Override
    public List<Map<String, Object>> readAll(Collection<ObjectInfo> objects, Selector selector) {
        var typeToIds = new HashMap<String, List<Integer>>();
        for (var object : objects) {
            if (!typeToIds.containsKey(object.getType())) {
                typeToIds.put(object.getType(), new ArrayList<>());
            }
            typeToIds.get(object.getType()).add(object.getId());
        }
        var result = new ArrayList<Map<String, Object>>();
        typeToIds.forEach((key, value) -> {
            var data = jdbc.queryForList(
                    String.format(
                            "select * from " + tableName(key) + " where id in (%s)",
                            value.stream().map(l -> "" + l).collect(Collectors.joining(", "))
                    ),
                    Collections.emptyMap()
            );
            for (var re : data) {
                re.put("type", key);
                result.add(re);
            }
        });
        return result;
    }

    @Override
    public List<Map<String, Object>> findByProperty(
            String type,
            String propertyName,
            String propertyValue,
            Selector selector
    ) {
        return jdbc.queryForList(
                "select id from " + tableName(type) + "  where " + propertyName + " = '" + propertyValue + "'",
                Collections.emptyMap()
        );
    }

    @Transactional
    @Override
    public Integer addObject(Integer id, String type, Map<String, String> attributes) {
        var data = new HashMap<String, Object>();
        attributes.entrySet().forEach(e -> {
            // todo temp decision for attr value length
            var value = e.getValue();
            if (value.length() > VARCHAR_SIZE) {
                value = value.substring(0, VARCHAR_SIZE);
            }
            data.put(
                    replacements(prepareAttr(e.getKey())),
                    value
            );
        });
        data.put("id", id);
        jdbc.update(
                "insert into " + tableName(type) + " (id "
                        + attributes.keySet().stream()
                        .map(Helper::prepareAttr)
                        .map(a -> ", " + a)
                        .collect(Collectors.joining(""))
                        + ") "
                        + "values  (:id "
                        + attributes.keySet().stream()
                        .map(a -> String.format(", :%s", replacements(prepareAttr(a))))
                        .collect(Collectors.joining(""))
                        + ")",
                data
        );
        return id;
    }
}
