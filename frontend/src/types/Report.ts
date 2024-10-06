import { z } from 'zod';
import { ReportStatus } from './ReportStatus';
import { Process } from './Process';
import { Control } from './Control';
import { ComplianceRule } from './ComplianceRule';

export const Breach = z.object({
    rule: ComplianceRule,
    control: Control,
    breachedCaseIds: z.array(z.string()),
});

export type Breach = z.infer<typeof Breach>;

export const Report = z.object({
    id: z.number().min(1),
    runAt: z.string().refine((value) => !isNaN(Date.parse(value)), {
        message: 'Invalid date string',
    }),
    status: z.nativeEnum(ReportStatus),
    process: Process,
    report: z.optional(
        z.object({
            totalCases: z.number(),
            totalBreachedCases: z.number(),
            stats: z.object({
                max: z.object({
                    caseId: z.string(),
                    value: z.number(),
                }),
                min: z.object({
                    caseId: z.string(),
                    value: z.number(),
                }),
                average: z.number(),
            }),
            breaches: z.array(Breach),
        }),
    ),
});

export type Report = z.infer<typeof Report>;
