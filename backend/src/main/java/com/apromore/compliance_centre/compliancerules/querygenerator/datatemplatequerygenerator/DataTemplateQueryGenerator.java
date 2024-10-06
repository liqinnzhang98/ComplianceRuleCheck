package com.apromore.compliance_centre.compliancerules.querygenerator.datatemplatequerygenerator;

import com.apromore.compliance_centre.compliancerules.querygenerator.QueryGenerator;
import com.apromore.compliance_centre.compliancerules.querygenerator.QueryOperatorEnum;
import com.apromore.compliance_centre.eventlogs.EventLogModel;
import java.util.Map;

public class DataTemplateQueryGenerator extends QueryGenerator {

    QueryOperatorEnum operator;
    String attributeX;
    String attributeY;
    String valueY;

    DataTemplateQueryGenerator(
        QueryOperatorEnum operator,
        EventLogModel eventLog,
        Map<String, String> templateValues
    ) {
        super(eventLog, templateValues);
        this.operator = operator;
    }

    @Override
    public void processQueryParameters(Map<String, String> templateValues) {
        attributeX = templateValues.get("attribute_x");
        attributeY = templateValues.get("attribute_y");
        valueY = templateValues.get("value_y");

        if (attributeX == null) {
            throw new IllegalArgumentException("Missing attribute_x");
        }

        if (attributeY != null && valueY != null) {
            throw new IllegalArgumentException("Cannot have both attribute_y and value_y");
        }

        if (attributeY == null && valueY == null) {
            throw new IllegalArgumentException("Missing attribute_y or value_y");
        }
    }

    @Override
    public String getQuery() {
        return String.format(
            "SELECT %s FROM %s WHERE `%s` %s %s GROUP BY %s",
            caseIdColumnName,
            tableName,
            attributeX,
            operator.getOperator(),
            attributeY != null ? "`" + attributeY + "`" : "'" + valueY + "'",
            caseIdColumnName
        );
    }
}
