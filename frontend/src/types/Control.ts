import { z } from 'zod';
import { Template } from './Template';

export const Control = z.object({
    id: z.number().min(1).optional(),
    type: z.string(),
    name: z.string(),
    description: z.string(),
    templates: z.array(z.array(Template)).optional(),
});

export enum ControlDefaultTypes {
    'Control Flow',
    'Data',
    'Resource',
    'Time',
}

export type Control = z.infer<typeof Control>;
