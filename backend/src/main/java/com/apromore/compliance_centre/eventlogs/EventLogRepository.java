package com.apromore.compliance_centre.eventlogs;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventLogRepository
        extends CrudRepository<EventLogModel, Long>, EventLogDataRepository {

    List<EventLogModel> findByProcessId(Long processId);
}
