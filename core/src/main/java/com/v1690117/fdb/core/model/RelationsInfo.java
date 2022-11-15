package com.v1690117.fdb.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class RelationsInfo {
    private static final String NODE_DELIMETER = "\u0007";
    private final String key;
    private final boolean from;
    private final List<Integer> id = new ArrayList<>();
    private final List<String> type = new ArrayList<>();
    private final List<Integer> connectionIds = new ArrayList<>();
    private final List<String> connectionTypes = new ArrayList<>();
    private final Map<String, List<String>> objectAttributes = new HashMap<>();

    public void add(RelatedNode node, Map<String, Object> object) {
        id.add(node.getId());
        type.add(node.getType());
        connectionIds.add(node.getConnectionId());
        connectionTypes.add(node.getConnectionType());
        object.entrySet().forEach(e -> {
            if (!e.getKey().equals("id")) {
                if (objectAttributes.containsKey(e.getKey())) {
                    objectAttributes.get(e.getKey()).add((String) e.getValue());
                } else {
                    ArrayList<String> value = new ArrayList<>();
                    value.add((String) e.getValue());
                    objectAttributes.put(e.getKey(), value);
                }
            }
        });
    }

    public Map<String, String> toMap() {
        var baseInfo = Map.of(
                key + ".id",
                connectionIds.stream().map(l -> "" + l).collect(Collectors.joining(NODE_DELIMETER)),
                key + ".type",
                String.join(NODE_DELIMETER, connectionTypes),
                key + "." + (isFrom() ? "to" : "from") + ".id",
                id.stream().map(l -> "" + l).collect(Collectors.joining(NODE_DELIMETER)),
                key + "." + (isFrom() ? "to" : "from") + ".type",
                String.join(NODE_DELIMETER, type)
        );
        var res = new HashMap<>(baseInfo);
        objectAttributes.forEach((k, v) -> res.put(
                key + "." + (isFrom() ? "to" : "from") + "."
                        + (k.startsWith("_") && k.endsWith("_") ? k.replaceAll("_", "") : "attribute[" + k + "]"),
                String.join(NODE_DELIMETER, v)
        ));
        return res;
    }
}
