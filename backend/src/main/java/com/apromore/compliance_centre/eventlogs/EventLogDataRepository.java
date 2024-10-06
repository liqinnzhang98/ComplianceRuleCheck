package com.apromore.compliance_centre.eventlogs;

import com.apromore.compliance_centre.process.attributes.AttributeModel;
import java.util.List;
import java.util.Map;

public interface EventLogDataRepository {
    // this method is used to create a new log_<eventLogName> table in the database
    // passed in attributes map contains the name and the data type
    // the header of the new table would be:
    // ----------------------------------------
    // attr name 1 | attr name 2 | ... |
    // -----------------------------------------
    void createEventLogTable(EventLogModel eventLog, List<AttributeModel> attributes);

    // this method takes a map of attributes_name and the value both as string type
    // then transfer the string and insert the data into the log_<eventLogName> table
    void saveEventLogData(EventLogModel eventLog, List<AttributeModel> attributes, List<List<String>> data);

    // this method fetches all the unique values for a given eventlog attribute
    List<String> getEventLogDataAttributeValues(EventLogModel eventLog, AttributeModel attribute);

    List<Map<String, Object>> findAllDataRows(EventLogModel eventLog);

    List<Map<String, Object>> findDataRowsByCaseId(EventLogModel eventLog, String caseId);
}
