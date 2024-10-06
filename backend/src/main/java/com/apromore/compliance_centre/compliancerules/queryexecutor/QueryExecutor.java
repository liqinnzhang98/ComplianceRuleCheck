package com.apromore.compliance_centre.compliancerules.queryexecutor;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueryExecutor {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public <T> List<T> executeQuery(String sql, Map<String, String> parameters, RowMapper<T> rowMapper) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        return namedParameterJdbcTemplate.query(sql, parameters, rowMapper);
    }
}
