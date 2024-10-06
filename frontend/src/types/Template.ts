import { FormField } from './FormField';
import { z } from 'zod';

export const Template = z.object({
    id: z.number(),
    rule: z.string(),
    description: z.string(),
    example: z.string(),
    category: z.string().optional(),
    formFields: z.array(FormField).optional(),
    recommendation: z.number().optional(),
});

export type Template = z.infer<typeof Template>;
