import { useMutation, useQuery } from 'react-query';
import { getReport, getAllReports } from '../api/report/reportQueries';
import { generateReport } from '../api/report/reportMutations';

export function useReports() {
    return useQuery(['reports'], () => getAllReports()());
}

export function useFullReport(processId?: number, reportId?: number) {
    if (!processId || !reportId) return useQuery([], () => null);
    return useQuery(['processes', processId, 'report', reportId], () => getReport(processId, reportId)());
}

export function useGenerateReport(processId: number) {
    return useMutation(generateReport(processId));
}
