package com.apromore.compliance_centre.eventlogs;

import com.apromore.compliance_centre.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class EventLogController {

    @Autowired
    private EventLogService eventLogService;

    @GetMapping("event-logs/{id}")
    public Response<EventLogDto> getEventLogsById(@PathVariable(value = "id") Long id) {
        return Response.ok(eventLogService.getEventLogById(id));
    }

    @PostMapping("processes/{processId}/event-logs")
    public Response<EventLogDto> uploadEventLogData(
        @PathVariable Long processId,
        @RequestParam("file") MultipartFile file
    ) {
        var eventLog = eventLogService.uploadEventLogData(processId, file);

        return Response.ok(eventLog);
    }

    @PostMapping("event-logs/{eventLogId}/data")
    public Response<EventLogDataDto> getEventLogData(@PathVariable Long eventLogId) {
        return Response.ok(eventLogService.getEventLogData(eventLogId));
    }

    @GetMapping("processes/{processId}/data/{caseId}")
    public Response<EventLogDataDto> getEventLogDataByCaseId(
        @PathVariable Long processId,
        @PathVariable String caseId
    ) {
        return Response.ok(eventLogService.getEventLogDataByCaseId(processId, caseId));
    }
}
