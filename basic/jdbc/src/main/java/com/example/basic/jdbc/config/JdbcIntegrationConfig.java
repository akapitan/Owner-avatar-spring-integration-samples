package com.example.basic.jdbc.config;

import com.example.basic.jdbc.domain.Person;
import com.example.basic.jdbc.mapper.PersonRowMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jdbc.BeanPropertySqlParameterSourceFactory;
import org.springframework.integration.jdbc.SqlParameterSourceFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Configuration
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
    public SqlParameterSourceFactory parameterSourceFactory() {
        return new BeanPropertySqlParameterSourceFactory();
    }

    @Bean
    public IntegrationFlow findPersonFlow(JdbcTemplate jdbcTemplate) {
        return IntegrationFlow.from("findPersonChannel")
                .<String>handle((p, h) -> {
                    String query = "SELECT * FROM person WHERE LOWER(first_name) LIKE LOWER(?)";
                    return jdbcTemplate.query(query, new PersonRowMapper(), "%" + p + "%");
                })
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