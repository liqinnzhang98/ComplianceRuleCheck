import { z } from 'zod';

export const Rule = z.object({
    templateId: z.number(),
    values: z.any(),
});

export type Rule = z.infer<typeof Rule>;