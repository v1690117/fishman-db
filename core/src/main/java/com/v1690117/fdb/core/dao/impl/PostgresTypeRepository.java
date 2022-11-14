package com.v1690117.fdb.core.dao.impl;

import com.v1690117.fdb.core.dao.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.v1690117.fdb.core.util.Helper.prepareAttr;
import static com.v1690117.fdb.core.util.Helper.tableName;

@RequiredArgsConstructor
@Repository
public class PostgresTypeRepository implements TypeRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void addType(String type) {
        jdbc.getJdbcOperations().execute(
                "CREATE TABLE IF NOT EXISTS "
                        + tableName(type)
                        + " ("
                        + "id SERIAL PRIMARY KEY, "
                        // todo remove next columns
                        + "_physicalid_ VARCHAR(80),"
                        + "_description_ VARCHAR(320),"
                        + "_name_ VARCHAR(320),"
                        + "_revision_ VARCHAR(36),"
                        + "_current_ VARCHAR(36)"
                        + ")"
        );
    }

    @Override
    public void deleteType(String type) {
        throw new UnsupportedOperationException(
                "Type deletion not implemented yet!"
        );
    }

    public List<String> getTableColumns(String type) {
        List columns = jdbc.getJdbcOperations().queryForList(
                "SELECT column_name "
                        + "FROM information_schema.columns "
                        + "WHERE table_name='"
                        + tableName(type) + "'",
                List.class
        );
        var list = new ArrayList<String>();
        for (Object column : columns) {
            list.add((String) column);
        }
        return list;
    }

    @Override
    public void defineAttributes(String type, Map<String, String> attributes) {
        var columns = getTableColumns(type);
        for (var attribute : attributes.entrySet()) {
            var a = prepareAttr(attribute.getKey());
            if (!columns.contains(a)) {
                jdbc.getJdbcOperations().execute(
                        "ALTER TABLE "
                                + tableName(type)
                                + " ADD COLUMN IF NOT EXISTS "
                                + a
                                + " VARCHAR(240)"
                );
            }
        }
    }
}
