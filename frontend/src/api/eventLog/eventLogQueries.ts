import { z } from 'zod';
import api, { HTTPMethod } from '../api';
import { EventLogData } from '../../types/EventLogData';

export function getEventLogDataForCaseId(processId: number, caseId: string) {
    const GetEventLogDataRequest = z.void();
    const GetEventLogDataResponse = EventLogData;

    return api<z.infer<typeof GetEventLogDataRequest>, z.infer<typeof GetEventLogDataResponse>>({
        method: HTTPMethod.GET,
        path: `/processes/${processId}/data/${caseId}`,
        requestSchema: GetEventLogDataRequest,
        responseSchema: GetEventLogDataResponse,
    });
}
