import { z } from 'zod';
import { Attribute } from './Attribute';

export const EventLogData = z.object({
    data: z.array(z.record(z.string(), z.any())),
    attributes: z.array(Attribute),
});

export type EventLogData = z.infer<typeof EventLogData>;
