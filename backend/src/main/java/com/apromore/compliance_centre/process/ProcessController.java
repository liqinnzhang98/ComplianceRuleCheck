package com.apromore.compliance_centre.process;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.apromore.compliance_centre.eventlogs.EventLogDto;
import com.apromore.compliance_centre.eventlogs.EventLogService;
import com.apromore.compliance_centre.process.attributes.AttributeDto;
import com.apromore.compliance_centre.response.Response;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/processes")
public class ProcessController {
    @Autowired
    private ProcessService processService;

    @Autowired
    private EventLogService eventLogService;

    @GetMapping
    public Response<List<ProcessDto>> getAllProcesses() {
        return Response.ok(processService.getAllProcesses());
    }

    @GetMapping("{id}/event-logs")
    public Response<List<EventLogDto>> getEventLogsForProcess(
            @PathVariable(value = "id") Long processId) {
        return Response.ok(eventLogService.findEventLogsByProcessId(processId));
    }

    @PostMapping
    public Response<ProcessDto> createNewProcess(@RequestBody @Valid ProcessDto processDto) {
        return Response.ok(processService.createNewProcess(processDto));
    }

    @GetMapping("/{id}")
    public Response<ProcessDto> getProcessById(@PathVariable Long id) {
        ProcessDto processDto = processService.getProcessById(id);

        if (processDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }

        return Response.ok(processDto);
    }

    @GetMapping("/{id}/schema")
    public Response<List<AttributeDto>> getProcessSchema(@PathVariable Long id) {
        return Response.ok(processService.getAttributes(id));
    }

    @PostMapping("/{id}/schema")
    public Response<List<AttributeDto>> createProcessSchema(@PathVariable Long id,
            @RequestBody List<AttributeDto> attributes) {

        return Response.ok(processService.saveAttributes(id, attributes));
    }

    @GetMapping("/{id}/metadata")
    public Response<Map<String, Set<String>>> getEventlog(@PathVariable(value = "id") Long id) {
        return Response.ok(processService.getMetadata(id));
    }
}