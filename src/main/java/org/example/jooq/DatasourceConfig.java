package org.example.jooq;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatasourceConfig {

    public static DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3307/budget?useSSL=false&amp;serverTimezone=UTC");
        config.setUsername("root");
        config.setPassword("");
        config.setAutoCommit(true);
        config.setMaximumPoolSize(32);
        config.setDriverClassName("com.mysql.jdbc.Driver");
        return new HikariDataSource(config);
    }
}
