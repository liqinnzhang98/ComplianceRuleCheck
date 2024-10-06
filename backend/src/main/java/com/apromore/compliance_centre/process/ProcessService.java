package com.apromore.compliance_centre.process;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.apromore.compliance_centre.process.attributes.AttributeDto;

public interface ProcessService {
    /**
     * Return a list of all processes
     *
     * @return
     */
    List<ProcessDto> getAllProcesses();

    /**
     * Get an existing process by it's id
     *
     * @param processId
     * @return
     */
    ProcessDto getProcessById(long processId);

    /**
     * Create a new empty process
     *
     * @param processDto
     * @return
     */
    ProcessDto createNewProcess(ProcessDto processDto);

    /**
     * Get all attributes for a process
     *
     * @param processDto
     * @return
     */
    List<AttributeDto> getAttributes(long processId);

    /**
     * Save attributes for a process
     *
     * @param processId
     * @param attributeDtos
     * @return
     */
    List<AttributeDto> saveAttributes(long processId, List<AttributeDto> attributeDtos);

    /**
     * Get all metadata for a process
     * 
     * @param processDto
     * @return
     */
    Map<String, Set<String>> getMetadata(long processId);

    /**
     * Get all the case IDs for a process
     * 
     * @param processDto
     * @return
     */
    List<String> getAllCases(long processId);
}
