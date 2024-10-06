package com.apromore.compliance_centre.eventlogs;

import com.apromore.compliance_centre.process.ProcessDto;
import com.apromore.compliance_centre.process.ProcessModel;
import com.apromore.compliance_centre.process.ProcessRepository;
import com.apromore.compliance_centre.process.attributes.AttributeDto;
import com.apromore.compliance_centre.process.attributes.AttributeModel;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.apromore.compliance_centre.utils.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.events.Event;

@Component
public class EventLogServiceImpl implements EventLogService {

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ModelMapper modelMapper;

    public EventLogServiceImpl(
        EventLogRepository eventLogRepository,
        ProcessRepository processRepository,
        ModelMapper modelMapper
    ) {
        this.eventLogRepository = eventLogRepository;
        this.processRepository = processRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<EventLogDto> getAllEventLogs() {
        return StreamSupport
            .stream(eventLogRepository.findAll().spliterator(), false)
            .map(eventLog -> modelMapper.map(eventLog, EventLogDto.class))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public EventLogDto getEventLogById(Long eventLogId) {
        Optional<EventLogModel> eventLog = eventLogRepository.findById(eventLogId);
        return eventLog.map(eventLogModel -> modelMapper.map(eventLogModel, EventLogDto.class)).orElse(null);
    }

    // assuming schema is already defined for the process
    // and also ignore all the attributes that are of type "9"(IGNORE_ATTRIBUTE) in the schema
    @Override
    @Transactional
    public EventLogDto uploadEventLogData(Long processId, MultipartFile file) {
        ProcessModel process = processRepository.findById(processId).get();

        // the schema of the process to which the event log belongs
        List<AttributeModel> attributes = process.getAttributes();

        if (attributes.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No attributes found for process with id " + processId
            );
        }

        // ignore the attribute type of "9" (IGNORE_ATTRIBUTE) by remove them from attributes list
        attributes =
            attributes
                .stream()
                .filter(attribute -> attribute.getType() != AttributeType.IGNORE_ATTRIBUTE)
                .collect(Collectors.toList());

        String fileName = file.getOriginalFilename();

        List<List<String>> tabularData = new ArrayList<>();

        try {
            InputStream inputStream = file.getInputStream();
            Reader reader = new InputStreamReader(inputStream);
            CSVParser csvParser = CSVParser.parse(
                reader,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withAllowMissingColumnNames(true)
            );

            Set<String> missingDataAttributes = new HashSet<>();
            Map<String, String> sanitizedHeadersMap = new HashMap<>();

            // Pre-process the CSV headers and store sanitized mapping
            for (String header : csvParser.getHeaderMap().keySet()) {
                sanitizedHeadersMap.put(Utils.toValidColumnName(header), header);
            }

            for (CSVRecord record : csvParser) {
                List<String> row = new ArrayList<>();
                for (AttributeModel attribute : attributes) {
                    // assuming the CSV column names are already sanitized
                    String column = attribute.getName();

                    // Check if the CSV contains the desired column before trying to fetch it
                    if (sanitizedHeadersMap.containsKey(column)) {
                        // Fetch using the original column name from the mapping
                        row.add(record.get(sanitizedHeadersMap.get(column)));
                    } else {
                        missingDataAttributes.add(column);
                        row.add(null);
                    }
                }
                tabularData.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error reading the CSV file: " + e.getMessage());
        }

        var eventLog = new EventLogModel().setName(fileName).setProcess(process);

        // create the event log entry in the database
        eventLog = eventLogRepository.save(eventLog);

        // create the table for the event log
        eventLogRepository.createEventLogTable(eventLog, process.getAttributes());

        // save the data into the database
        eventLogRepository.saveEventLogData(eventLog, attributes, tabularData);

        return EventLogDto.fromModel(eventLog);
    }

    @Override
    public List<EventLogDto> findEventLogsByProcessId(Long processId) {
        List<EventLogModel> eventLogs = eventLogRepository.findByProcessId(processId);
        return Utils.mapList(eventLogs, EventLogDto::fromModel);
    }

    @Override
    public List<String> getAttributeValues(EventLogModel eventLog, AttributeModel attribute) {
        return eventLogRepository.getEventLogDataAttributeValues(eventLog, attribute);
    }

    @Override
    public EventLogDataDto getEventLogData(Long eventLogId) {
        // TODO: update logic of this method to accept processId

        EventLogModel eventLog = eventLogRepository
            .findById(eventLogId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event log not found"));

        var data = eventLogRepository.findAllDataRows(eventLog);

        return new EventLogDataDto()
            .setAttributes(Utils.mapList(eventLog.getAttributes(), AttributeDto::fromModel))
            // .setEventLog(EventLogDto.fromModel(eventLog))
            .setData(data);
    }

    @Override
    public EventLogDataDto getEventLogDataByCaseId(Long processId, String caseId) {
        ProcessModel process = processRepository
            .findById(processId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event log not found"));

        List<Map<String, Object>> data = new ArrayList<>();
        for (EventLogModel eventLog : process.getEventLogs()) {
            data.addAll(eventLogRepository.findDataRowsByCaseId(eventLog, caseId));
        }

        return new EventLogDataDto()
            .setAttributes(Utils.mapList(process.getAttributes(), AttributeDto::fromModel))
            .setData(data);
    }
}
