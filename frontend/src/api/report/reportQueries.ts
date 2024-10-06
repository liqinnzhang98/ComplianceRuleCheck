import { z } from 'zod';
import api, { HTTPMethod } from '../api';
import { Report } from '../../types/Report';

export function getAllReports() {
    const getLatestReportRequest = z.void();
    const getLatestReportResponse = z.array(Report);

    return api<z.infer<typeof getLatestReportRequest>, z.infer<typeof getLatestReportResponse>>({
        method: HTTPMethod.GET,
        path: '/reports',
        requestSchema: getLatestReportRequest,
        responseSchema: getLatestReportResponse,
    });
}

export function getReport(processId: number, reportId: number) {
    const getLatestReportRequest = z.void();
    const getLatestReportResponse = Report;

    return api<z.infer<typeof getLatestReportRequest>, z.infer<typeof getLatestReportResponse>>({
        method: HTTPMethod.GET,
        path: `/processes/${processId}/reports/${reportId}`,
        requestSchema: getLatestReportRequest,
        responseSchema: getLatestReportResponse,
    });
}

export function getLatestReport(processId: number) {
    const getLatestReportRequest = z.void();
    const getLatestReportResponse = Report;

    return api<z.infer<typeof getLatestReportRequest>, z.infer<typeof getLatestReportResponse>>({
        method: HTTPMethod.GET,
        path: `/processes/${processId}/report`,
        requestSchema: getLatestReportRequest,
        responseSchema: getLatestReportResponse,
    });
}
