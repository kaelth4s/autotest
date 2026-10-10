package ru.kaelth4s.db.dto;

public record DbEnvironmentData(
        String host,
        String port,
        String schema,
        String user,
        String password,
        String params
) {
    private static final String JDBC_MY_SQL_TEMPLATE = "jdbc:mysql://%s:%s/%s?%s";

    public String jdbcUrl() {
        return String.format(JDBC_MY_SQL_TEMPLATE, host, port, schema, params);
    }
}