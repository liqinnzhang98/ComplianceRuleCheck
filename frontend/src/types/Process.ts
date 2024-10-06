import { z } from 'zod'; // zod is a checking data formate library
import { EventLog } from './EventLog';

export const Process = z.object({
    id: z.number().min(1), // id is a number and it should be greater than 1
    name: z.string(),
    eventLogs: z.array(EventLog).optional(),
});

export const ProcessMetadata = z.record(z.string(), z.array(z.string()));

// generate a TypeScript type based on the Process schema, and export it as Process
export type Process = z.infer<typeof Process>;

export type ProcessMetadata = z.infer<typeof ProcessMetadata>;
