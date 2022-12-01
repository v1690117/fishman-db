package com.v1690117.fdb.core.util;

public final class Helper {
    private static final String TYPE_TABLE_PREFIX = "type_table_";
    private static final String DELIMETER = "_";

    public static String prepareAttr(String attribute) {
        return "\"" + attribute + "\"";
    }

    public static String tableName(String type) {
        return "\"" + TYPE_TABLE_PREFIX + type + "\"";
    }

    public static String replacements(String input) {
        return input.replaceAll(" ", DELIMETER)
                .replaceAll("(.+[A-Z])", "_$1")
                .replaceAll("\\(", "_")
                .replaceAll("\\)", "_")
                .replaceAll("\"", "_")
                .replaceAll("/", "_")
                .replaceAll("-", "_");
    }

    public static String goodName(String name) {
        return name.replaceAll(" ", "_")
                .replaceAll("\\(", "_")
                .replaceAll("\\)", "_")
                .replaceAll("\"", "_")
                .replaceAll("/", "_")
                .replaceAll("-", "_");
    }

    private Helper() {
    }
}
