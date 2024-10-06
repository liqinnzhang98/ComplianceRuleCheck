import { z } from 'zod';
import api, { HTTPMethod } from '../api';
import { EventLog } from '../../types/EventLog';
import { Attribute } from '../../types/Attribute';
import { Process, ProcessMetadata } from '../../types/Process';
import { ComplianceRule } from '../../types/ComplianceRule';

export function getEventLogs(processId: number) {
    const GetEventLogRequest = z.void();
    const GetEventLogResponse = z.array(EventLog);

    return api<z.infer<typeof GetEventLogRequest>, z.infer<typeof GetEventLogResponse>>({
        method: HTTPMethod.GET,
        path: `/processes/${processId}/event-logs`,
        requestSchema: GetEventLogRequest,
        responseSchema: GetEventLogResponse,
    });
}

export function getSchema(processId: number) {
    const GetSchemaRequest = z.void();
    const GetSchemaResponse = z.array(Attribute);

    return api<z.infer<typeof GetSchemaRequest>, z.infer<typeof GetSchemaResponse>>({
        method: HTTPMethod.GET,
        path: `/processes/${processId}/schema`,
        requestSchema: GetSchemaRequest,
        responseSchema: GetSchemaResponse,
    });
}

export function getProcess(processId: number) {
    const GetSchemaRequest = z.void();
    const GetSchemaResponse = Process;

    return api<z.infer<typeof GetSchemaRequest>, z.infer<typeof GetSchemaResponse>>({
        method: HTTPMethod.GET,
        path: `/processes/${processId}`,
        requestSchema: GetSchemaRequest,
        responseSchema: GetSchemaResponse,
    });
}


export function getProcesses() {
    const GetProcessesRequest = z.void();
    const GetProcessesResponse = z.array(Process);

    return api<z.infer<typeof GetProcessesRequest>, z.infer<typeof GetProcessesResponse>>({
        method: HTTPMethod.GET,
        path: '/processes',
        requestSchema: GetProcessesRequest,
        responseSchema: GetProcessesResponse,
    });
}

export function getMetadata(processId: number) {
    const GetMetadataRequest = z.void();
    const GetMetadataResponse = ProcessMetadata;
    return api<z.infer<typeof GetMetadataRequest>, z.infer<typeof GetMetadataResponse>>({
        method: HTTPMethod.GET,
        path: `/processes/${processId}/metadata`,
        requestSchema: GetMetadataRequest,
        responseSchema: GetMetadataResponse,
    });
}
