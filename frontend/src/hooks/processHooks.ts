import { useMutation, useQuery, UseMutationOptions } from 'react-query';
import { getEventLogs, getMetadata, getProcess, getProcesses, getSchema } from '../api/process/processQueries';
import { createProcess, saveSchema, uploadEventLogData } from '../api/process/processMutations';
import { getComplianceRulesByProcess } from '../api/complianceRule/complianceRuleQueries';

export function useEventLogs(processId: number) {
    return useQuery(['processes', processId, 'event-logs'], () => getEventLogs(processId)());
}

export function useSchema(processId: number) {
    return useQuery(['processes', processId, 'schema'], () => getSchema(processId)());
}

export function useSaveSchema(processId: number, options?: UseMutationOptions<any, any, any>) {
    return useMutation(saveSchema(processId), {
        onSuccess: (data) => {
            console.log('Schema saved successfully: \n');
        },
        onError: (error: any) => {
            console.log('Error message on saving schema: \n');
            console.log(error.response.data.errorDetails[0].description);
        },
        ...options,
    });
}

export function useUploadEventLogData(processId: number, options?: UseMutationOptions<any, any, any>) {
    return useMutation(uploadEventLogData(processId), {
        onSuccess: (data) => {
            console.log('File uploaded successfully: \n');
        },
        onError: (error: any) => {
            console.log('Error on uploading file: \n');
            console.log(error.response.data.errorDetails[0].description);
        },
    });
}

export function useComplianceRulesByProcess(processId: number) {
    return useQuery(['processes', processId, 'compliance-rules'], () => getComplianceRulesByProcess(processId)());
}

export function useProcess(processId: number) {
    return useQuery(['processes', processId, 'process'], () => getProcess(processId)());
}

export function useCreateProcess() {
    return useMutation(createProcess());
}

export function useProcesses() {
    return useQuery(['processes'], () => getProcesses()());
}

export function useMetadata(processId: number) {
    return useQuery(['processes', processId, 'metadata'], () => getMetadata(processId)());
}
