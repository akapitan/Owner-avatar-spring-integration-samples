package com.example.basic.jdbc.config;

import com.example.basic.jdbc.domain.Person;
import com.example.basic.jdbc.mapper.PersonRowMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jdbc.BeanPropertySqlParameterSourceFactory;
import org.springframework.integration.jdbc.ExpressionEvaluatingSqlParameterSourceFactory;
import org.springframework.integration.jdbc.JdbcOutboundGateway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.messaging.MessageHandler;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class JdbcIntegrationConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public MessageHandler jdbcOutboundGateway(DataSource dataSource) {
        JdbcOutboundGateway gateway = new JdbcOutboundGateway(dataSource,
                null, "SELECT * FROM person WHERE LOWER(first_name) LIKE LOWER(:name)");
        gateway.setReplySqlParameterSourceFactory(new BeanPropertySqlParameterSourceFactory());
        gateway.setRowMapper(new PersonRowMapper());

        ExpressionEvaluatingSqlParameterSourceFactory parameterFactory =
                new ExpressionEvaluatingSqlParameterSourceFactory();
        parameterFactory.setParameterExpressions(Map.of(
                "name", "'%' + payload + '%'"
        ));
        gateway.setReplySqlParameterSourceFactory(parameterFactory);
        gateway.setMaxRows(20);
        return gateway;
    }

    @Bean
    public IntegrationFlow findPersonFlow(MessageHandler jdbcOutboundGateway) {
        return IntegrationFlow.from("findPersonChannel")
                /*.<String>handle((p, h) -> {
                    String query = "SELECT * FROM person WHERE LOWER(first_name) LIKE LOWER(?)";
                    return jdbcTemplate.query(query, new PersonRowMapper(), "%" + p + "%");
                })*/
                .handle(jdbcOutboundGateway)
                .get();
    }

    @Bean
    public IntegrationFlow createPersonFlow(JdbcTemplate jdbcTemplate) {
        return IntegrationFlow.from("createPersonChannel")
                .<Person>handle((p, h) -> {
                    // First insert the person
                    String insertSql = "INSERT INTO person (first_name, last_name, age) VALUES (?, ?, ?)";
                    KeyHolder keyHolder = new GeneratedKeyHolder();

                    jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, p.getFirstName());
                        ps.setString(2, p.getLastName());
                        ps.setInt(3, p.getAge());
                        return ps;
                    }, keyHolder);

                    Number key = keyHolder.getKey();
                    if (key != null) {
                        p.setId(key.intValue());
                    }

                    // Return the person with the generated ID
                    return p;
                })
                .get();
    }
}