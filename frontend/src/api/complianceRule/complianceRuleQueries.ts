import { z } from 'zod';
import api, { HTTPMethod } from '../api';
import { Template } from '../../types/Template';
import { ComplianceRule } from '../../types/ComplianceRule';

export function getComplianceRuleTemplates() {
    const GetAllTemplatesRequest = z
        .object({
            control_description: z.string().optional(),
        })
        .optional();
    const GetAllTemplatesResponse = z.array(Template);

    return api<z.infer<typeof GetAllTemplatesRequest>, z.infer<typeof GetAllTemplatesResponse>>({
        method: HTTPMethod.GET,
        path: '/compliance-rule-templates',
        requestSchema: GetAllTemplatesRequest,
        responseSchema: GetAllTemplatesResponse,
    });
}

export function getTemplate(templateId: number) {
    const GetSchemaRequest = z.void();
    const GetSchemaResponse = Template;

    return api<z.infer<typeof GetSchemaRequest>, z.infer<typeof GetSchemaResponse>>({
        method: HTTPMethod.GET,
        path: `/compliance-rule-templates/${templateId}`,
        requestSchema: GetSchemaRequest,
        responseSchema: GetSchemaResponse,
    });
}

export function getTemplates() {
    const GetSchemaRequest = z.void();
    const GetSchemaResponse = z.array(Template);

    return api<z.infer<typeof GetSchemaRequest>, z.infer<typeof GetSchemaResponse>>({
        method: HTTPMethod.GET,
        path: `/compliance-rule-templates`,
        requestSchema: GetSchemaRequest,
        responseSchema: GetSchemaResponse,
    });
}

export function getComplianceRulesByControl(controlId: number) {
    const GetSchemaRequest = z.void();
    const GetSchemaResponse = z.array(ComplianceRule);

    return api<z.infer<typeof GetSchemaRequest>, z.infer<typeof GetSchemaResponse>>({
        method: HTTPMethod.GET,
        path: `/control/${controlId}/compliance-rules`,
        requestSchema: GetSchemaRequest,
        responseSchema: GetSchemaResponse,
    });
}

export function getComplianceRulesByProcess(processId: number) {
    const GetSchemaRequest = z.void();
    const GetSchemaResponse = z.array(ComplianceRule);

    return api<z.infer<typeof GetSchemaRequest>, z.infer<typeof GetSchemaResponse>>({
        method: HTTPMethod.GET,
        path: `/processes/${processId}/compliance-rules`,
        requestSchema: GetSchemaRequest,
        responseSchema: GetSchemaResponse,
    });
}
