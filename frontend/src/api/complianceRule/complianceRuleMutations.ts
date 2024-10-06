import { z } from 'zod';
import api, { HTTPMethod } from '../api';
import { ComplianceRule, ComplianceRules } from '../../types/ComplianceRule';

export function saveComplianceRule() {
    const SaveComplianceRuleRequest = z.object({
        processId: z.number(),
        controlId: z.number(),
        rules: ComplianceRules,
    });
    const SaveComplianceRuleResponse = SaveComplianceRuleRequest.extend({ id: z.number() });

    return api<z.infer<typeof SaveComplianceRuleRequest>, z.infer<typeof SaveComplianceRuleResponse>>({
        method: HTTPMethod.POST,
        path: `/compliance-rules`,
        requestSchema: SaveComplianceRuleRequest,
        responseSchema: SaveComplianceRuleResponse,
    });
}

export function editComplianceRule( id: number ) {
    const EditComplianceRuleRequest = ComplianceRule;
    const EditComplianceRuleResponse = ComplianceRule;

    return api<z.infer<typeof EditComplianceRuleRequest>, z.infer<typeof EditComplianceRuleResponse>>({
        method: HTTPMethod.POST,
        path: `/compliance-rules/${id}`,
        requestSchema: EditComplianceRuleRequest,
        responseSchema: EditComplianceRuleResponse,
    });
}

export function deleteComplianceRule( id: number ) {
    const DeleteComplianceRuleRequest = z.number();
    const DeleteComplianceRuleResponse = z.string();

    return api<z.infer<typeof DeleteComplianceRuleRequest>, z.infer<typeof DeleteComplianceRuleResponse>>({
        method: HTTPMethod.DELETE,
        path: `/compliance-rules/${id}`,
        requestSchema: DeleteComplianceRuleRequest,
        responseSchema: DeleteComplianceRuleResponse,
    });
}
