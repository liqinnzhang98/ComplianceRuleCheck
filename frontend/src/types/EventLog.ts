import { z } from 'zod';
import { Process } from './Process';

export const EventLog = z.object({
    id: z.number(),
    name: z.string(),
    createdAt: z.string().optional(),
    // process: Process.optional(),
});

export type EventLog = z.infer<typeof EventLog>;

export const EventLogTableColumns = [
    { Header: 'ID', accessor: 'id' },
    { Header: 'Name', accessor: 'name' },
    { Header: 'Created At', accessor: 'createdAt' },
];
