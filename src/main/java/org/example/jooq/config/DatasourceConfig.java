package org.example.jooq.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatasourceConfig {

    private static final Logger logger = LoggerFactory.getLogger("DatasourceConfig");

    public static DataSource createDataSource() throws IOException {
        InputStream is = DatasourceConfig.class.getClassLoader()
                .getResourceAsStream("config.properties");
        Properties properties = new Properties();
        properties.load(is);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("db.url"));
        config.setUsername(properties.getProperty("db.user"));
        config.setPassword(properties.getProperty("db.password"));
        config.setAutoCommit(true);
        config.setMaximumPoolSize(32);
        config.setDriverClassName(properties.getProperty("db.driver"));
        return new HikariDataSource(config);
    }
}