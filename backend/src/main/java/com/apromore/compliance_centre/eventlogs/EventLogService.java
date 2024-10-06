package com.apromore.compliance_centre.eventlogs;

import com.apromore.compliance_centre.process.attributes.AttributeModel;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EventLogService {
    List<EventLogDto> getAllEventLogs();

    EventLogDto getEventLogById(Long eventLogId);

    EventLogDataDto getEventLogData(Long eventLogId);

    EventLogDataDto getEventLogDataByCaseId(Long eventLogId, String caseId);

    EventLogDto uploadEventLogData(Long processId, MultipartFile file);

    List<EventLogDto> findEventLogsByProcessId(Long processId);

    List<String> getAttributeValues(EventLogModel eventLog, AttributeModel attribute);
}
