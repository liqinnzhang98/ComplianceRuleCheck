package com.apromore.compliance_centre.eventlogs;

import com.apromore.compliance_centre.process.attributes.AttributeDataType;
import com.apromore.compliance_centre.process.attributes.AttributeModel;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.apromore.compliance_centre.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EventLogDataRepositoryImpl implements EventLogDataRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void createEventLogTable(EventLogModel eventLog, List<AttributeModel> attributes) {
        // ignore the attributes if attributeType = IGNORE_ATTRIBUTE by remvoing them from the list
        attributes.removeIf(attr -> attr.getAttributeType().equals(AttributeType.IGNORE_ATTRIBUTE));

        String columns = attributes
            .stream()
            .map(attr -> {
                String columnDef = attr.getDatabaseColumnName() + " " + attr.getDataType().toString();
                if ("TIMESTAMP".equals(attr.getDataType().toString())) {
                    columnDef += " NULL";
                }
                return columnDef;
            })
            .collect(Collectors.joining(", "));

        String query = String.format("CREATE TABLE IF NOT EXISTS %s (%s)", eventLog.getEventLogTableName(), columns);

        jdbcTemplate.execute(query.toString());
    }

    @Override
    public void saveEventLogData(EventLogModel eventLog, List<AttributeModel> attributes, List<List<String>> data) {
        String tableName = eventLog.getEventLogTableName();

        String columnsQueryString = attributes
            .stream()
            .map(AttributeModel::getDatabaseColumnName)
            .collect(Collectors.joining(", "));

        String placeholders = String.join(", ", Collections.nCopies(attributes.size(), "?"));

        String sql = "INSERT INTO " + tableName + " (" + columnsQueryString + ") VALUES (" + placeholders + ")";

        List<Object[]> batchArgs = new ArrayList<>();

        for (List<String> row : data) {
            Object[] values = new Object[attributes.size()];
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                AttributeModel attribute = attributes.get(columnIndex);
                AttributeDataType datatype = attribute.getDataType();
                String value = !row.get(columnIndex).isEmpty() ? row.get(columnIndex) : null;
                values[columnIndex] = Utils.sanitizeDatabaseValue(value, datatype);
            }
            batchArgs.add(values);
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public List<String> getEventLogDataAttributeValues(EventLogModel eventLog, AttributeModel attribute) {
        return jdbcTemplate.queryForList(
            "SELECT DISTINCT " + attribute.getDatabaseColumnName() + " FROM " + eventLog.getEventLogTableName(),
            String.class
        );
    }

    @Override
    public List<Map<String, Object>> findAllDataRows(EventLogModel eventLog) {
        String query = "SELECT * FROM " + eventLog.getEventLogTableName();
        return jdbcTemplate.queryForList(query);
    }

    @Override
    public List<Map<String, Object>> findDataRowsByCaseId(EventLogModel eventLog, String caseId) {
        var caseIdAttribute = eventLog.getAttributeByType(AttributeType.CASE_ID);
        String query =
            "SELECT * FROM " +
            eventLog.getEventLogTableName() +
            " WHERE " +
            caseIdAttribute.getDatabaseColumnName() +
            " = ?";
        return jdbcTemplate.queryForList(query, caseId);
    }
}
