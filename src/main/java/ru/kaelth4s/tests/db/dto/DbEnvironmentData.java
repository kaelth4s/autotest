package ru.kaelth4s.tests.db.dto;

public record DbEnvironmentData(
        String host,
        String port,
        String schema,
        String user,
        String password,
        String params,
        String driver,
        String showSql,
        String formatSql
) {
    private static final String JDBC_MY_SQL_PARAMS_TEMPLATE = "?%s";
    private static final String JDBC_MY_SQL_TEMPLATE = "jdbc:mysql://%s:%s/%s";

    public String jdbcUrl() {
        String baseUrl = JDBC_MY_SQL_TEMPLATE.formatted(host, port, schema);
        if (!params.isBlank()) {
            return baseUrl + JDBC_MY_SQL_PARAMS_TEMPLATE.formatted(params);
        }
        return baseUrl;
    }
}