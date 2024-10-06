import { z } from 'zod';
import { Attribute } from '../../types/Attribute';
import api, { HTTPMethod } from '../api';
import { EventLog } from '../../types/EventLog';
import { Process } from '../../types/Process';

export function saveSchema(processId: number) {
    const SchemaRequest = z.array(Attribute.omit({ id: true, name: true, process: true }));
    const SchemaResponse = z.array(Attribute);

    return api<z.infer<typeof SchemaRequest>, z.infer<typeof SchemaResponse>>({
        method: HTTPMethod.POST,
        path: `/processes/${processId}/schema`,
        requestSchema: SchemaRequest,
        responseSchema: SchemaResponse,
    });
}

export function uploadEventLogData(processId: number) {
    const PostEventLogResponse = EventLog;

    return api<FormData, z.infer<typeof PostEventLogResponse>>({
        method: HTTPMethod.POST,
        path: `/processes/${processId}/event-logs`,
        responseSchema: PostEventLogResponse,
    });
}

export function createProcess() {
    const CreateProcessRequest = Process;
    const CreateProcessResponse = Process;

    return api<z.infer<typeof CreateProcessRequest>, z.infer<typeof CreateProcessResponse>>({
        method: HTTPMethod.POST,
        path: '/processes',
        requestSchema: CreateProcessRequest,
        responseSchema: CreateProcessResponse,
    });
}
