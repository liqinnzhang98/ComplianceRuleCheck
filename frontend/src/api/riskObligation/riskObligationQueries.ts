import { z } from 'zod';
import api, { HTTPMethod } from '../api';
import { Control } from '../../types/Control';
import { RiskObligation } from '../../types/RiskObligation';
import { ComplianceRule } from '../../types/ComplianceRule';

export function getRiskObligations() {
    const GetRiskObligationRequest = z.void();
    const GetRiskObligationResponse = z.array(RiskObligation);

    return api<z.infer<typeof GetRiskObligationRequest>, z.infer<typeof GetRiskObligationResponse>>({
        method: HTTPMethod.GET,
        path: '/riskObligation',
        requestSchema: GetRiskObligationRequest,
        responseSchema: GetRiskObligationResponse,
    });
}

export function getControls() {
    const GetControlRequest = z.void();
    const GetControlResponse = z.array(Control);

    return api<z.infer<typeof GetControlRequest>, z.infer<typeof GetControlResponse>>({
        method: HTTPMethod.GET,
        path: '/riskObligation/control',
        requestSchema: GetControlRequest,
        responseSchema: GetControlResponse,
    });
}

export function getControl(controlId: number) {
    const GetSchemaRequest = z.void();
    const GetSchemaResponse = Control;

    return api<z.infer<typeof GetSchemaRequest>, z.infer<typeof GetSchemaResponse>>({
        method: HTTPMethod.GET,
        path: `/riskObligation/control/${controlId}`,
        requestSchema: GetSchemaRequest,
        responseSchema: GetSchemaResponse,
    });
}
