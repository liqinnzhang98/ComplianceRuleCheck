package com.apromore.compliance_centre.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.apromore.compliance_centre.eventlogs.EventLogModel;
import com.apromore.compliance_centre.eventlogs.EventLogService;
import com.apromore.compliance_centre.process.attributes.AttributeDto;
import com.apromore.compliance_centre.process.attributes.AttributeModel;
import com.apromore.compliance_centre.process.attributes.AttributeType;
import com.apromore.compliance_centre.utils.Utils;

@Component
public class ProcessServiceImpl implements ProcessService {
    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private EventLogService eventLogService;

    @Autowired
    private ModelMapper modelMapper;

    public ProcessServiceImpl(ProcessRepository processRepository, ModelMapper modelMapper){
        this.processRepository = processRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ProcessDto> getAllProcesses() {
        return StreamSupport.stream(processRepository.findAll().spliterator(), false)
                .map(process -> modelMapper.map(process, ProcessDto.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ProcessDto getProcessById(long processId) {
        Optional<ProcessModel> process = processRepository.findById(processId);
        if (process.isPresent())
            return modelMapper.map(process.get(), ProcessDto.class);
        return null;
    }

    @Override
    public ProcessDto createNewProcess(ProcessDto processDto) {
        ProcessModel process = new ProcessModel().setName(processDto.getName());
        return ProcessDto.fromModel(processRepository.save(process));
    }

    @Override
    public List<AttributeDto> getAttributes(long processId) {
        Optional<ProcessModel> process = processRepository.findById(processId);

        if (process.isPresent()) {
            return Utils.mapList(process.get().getAttributes(), AttributeDto::fromModel);
        }

        return new ArrayList<>();
    }

    @Override
    public List<AttributeDto> saveAttributes(long processId, List<AttributeDto> attributeDtos) {
        ProcessModel process = processRepository.findById(processId).get();

        // updating attributes is not allowed at the moment

        // Handle the case where the process already has attributes or event logs
        if (!process.getAttributes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Process schema is already defined");
        }

        if (!process.getEventLogs().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Process already has event logs");
        }

        // Validate the attributes
        List<AttributeModel> attributes = new ArrayList<>();
        Set<String> processedAttributes = new HashSet<String>();

        for (AttributeDto attributeDto : attributeDtos) {
            AttributeModel attribute = AttributeModel.fromDto(attributeDto);
            String attributeName = attribute.getName();
            // handle empty attribute name
            if (attributeName == null || attributeName.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Attribute name cannot be empty");
            }
            // handle duplicate attribute name
            if (processedAttributes.contains(attributeName)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Duplicate attribute name: " + attributeName);
            }
            // if valid attribute, add to list of attributes and processed attributes names
            processedAttributes.add(attributeName);
            attributes.add(attribute);
        }

        // Make sure that the attributes has a reference to the process
        attributes.forEach(attribute -> attribute.setProcess(process));

        // Make sure that the process has a reference to the attributes
        process.setAttributes(attributes);

        // Save the process which will save the attributes as well
        processRepository.save(process);

        return Utils.mapList(attributes, AttributeDto::fromModel);
    }

    @Override
    public Map<String, Set<String>> getMetadata(long processId) {
        // get process and define list of attributes included in the metadata
        Optional<ProcessModel> process = processRepository.findById(processId);
        Map<String, Set<String>> metadata = new HashMap<>();
        List<AttributeType> includedAttributes = new ArrayList<>(
            Arrays.asList(AttributeType.ACTIVITY, AttributeType.RESOURCE, AttributeType.ROLE)
        );

        if (process.isPresent()) {
            // fetch the event logs and attributes for the process
            List<AttributeModel> attributes = process.get().getAttributes();
            List<AttributeModel> filteredAttributes = attributes.stream()
                .filter(attr -> includedAttributes.contains(attr.getType())).toList();
            List<EventLogModel> eventLogs = process.get().getEventLogs();

            // compile list of unique values across all event logs for all required attributes
            if (attributes.size() > 0) {
                metadata.put(AttributeType.ATTRIBUTE.toString(), new HashSet<>(
                    attributes.stream().map(attr -> attr.getName()).toList())
                );
            }
            for(AttributeModel attribute: filteredAttributes) {
                Set<String> attributeValues = new HashSet<>();
                for (EventLogModel eventLog: eventLogs) {
                    List<String> values = eventLogService.getAttributeValues(eventLog, attribute);
                    attributeValues.addAll(values);
                }
                metadata.put(attribute.getType().toString(), attributeValues);
            }
        }

        return metadata;
    }

	@Override
	public List<String> getAllCases(long processId) {
		Optional<ProcessModel> process = processRepository.findById(processId);
        List<String> cases = new ArrayList<>();

        if (process.isPresent()) {
            List<EventLogModel> eventLogs = process.get().getEventLogs();
            List<AttributeModel> caseIDAttribute = process.get().getAttributes().stream()
                .filter(attr -> attr.getType().equals(AttributeType.CASE_ID)).toList();
            if (caseIDAttribute.size() > 0) {
                for (EventLogModel eventLog: eventLogs) {
                    List<String> values = eventLogService.getAttributeValues(eventLog, caseIDAttribute.get(0));
                    cases.addAll(values);
                }
            }
            
        }

        return cases;
	}
}
