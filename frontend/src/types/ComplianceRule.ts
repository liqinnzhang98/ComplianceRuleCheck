import { z } from 'zod';

export const ComplianceRuleAttributeValue = z.string().or(z.boolean()).or(z.number());
export type ComplianceRuleAttributeValue = z.infer<typeof ComplianceRuleAttributeValue>;

export const ComplianceRuleValues = z.record(z.string(), ComplianceRuleAttributeValue);
export type ComplianceRuleValues = z.infer<typeof ComplianceRuleValues>;

export const Rule = z.object({
    templateId: z.number(),
    values: ComplianceRuleValues,
});
export type Rule = z.infer<typeof Rule>;

export const ComplianceRules = z.array(z.array(Rule));
export type ComplianceRules = z.infer<typeof ComplianceRules>;

export const ComplianceRule = z.object({
    id: z.number(),
    processId: z.number(),
    controlId: z.number(),
    rules: ComplianceRules
});

export type ComplianceRule = z.infer<typeof ComplianceRule>;
