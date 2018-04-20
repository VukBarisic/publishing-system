package com.code10.xml.config;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.query.QueryManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarkLogicConfig {

    @Value("${marklogic.host}")
    private String host;

    @Value("${marklogic.port}")
    private int port;

    @Value("${marklogic.database}")
    private String database;

    @Value("${marklogic.user}")
    private String username;

    @Value("${marklogic.pass}")
    private String password;

    @Bean
    public DatabaseClient databaseClient() {
        return DatabaseClientFactory
                .newClient(host, port, database, username, password, DatabaseClientFactory.Authentication.DIGEST);
    }

    @Bean
    public QueryManager getQueryManager() {
        return databaseClient().newQueryManager();
    }

    @Bean
    public XMLDocumentManager getXMLDocumentManager() {
        return databaseClient().newXMLDocumentManager();
    }
}
