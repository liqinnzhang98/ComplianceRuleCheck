import { useQuery } from 'react-query';
import { getEventLogDataForCaseId } from '../api/eventLog/eventLogQueries';

export function useEventLogData(processId: number, caseId: string) {
    return useQuery(['processes', processId, 'data', caseId], () => getEventLogDataForCaseId(processId, caseId)());
}
