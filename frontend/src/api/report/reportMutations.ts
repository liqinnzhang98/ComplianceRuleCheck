import { z } from 'zod';
import api, { HTTPMethod } from '../api';
import { Report } from '../../types/Report';
import { ComplianceRule } from '../../types/ComplianceRule';

export function generateReport(processId: number) {
    const generateReportRequest = z.array(ComplianceRule);
    const generateReportResponse = Report;

    return api<z.infer<typeof generateReportRequest>, z.infer<typeof generateReportResponse>>({
        method: HTTPMethod.POST,
        path: `/processes/${processId}/check-compliance`,
        requestSchema: generateReportRequest,
        responseSchema: generateReportResponse,
    });
}
