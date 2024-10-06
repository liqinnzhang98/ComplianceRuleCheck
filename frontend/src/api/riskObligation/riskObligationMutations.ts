import { z } from 'zod';
import api, { HTTPMethod } from '../api';
import { Control } from '../../types/Control';
import { RiskObligation } from '../../types/RiskObligation';

export function uploadRiskObligationData() {
    return api<FormData, string>({
        method: HTTPMethod.POST,
        path: '/riskObligation/upload',
        responseSchema: z.string(),
    });
}

export function createRiskObligation() {
    const PostRiskObligationRequest = RiskObligation.omit({ id: true });
    const PostRiskObligationResponse = RiskObligation;

    return api<z.infer<typeof PostRiskObligationRequest>, z.infer<typeof PostRiskObligationResponse>>({
        method: HTTPMethod.POST,
        path: '/riskObligation',
        requestSchema: PostRiskObligationRequest,
        responseSchema: PostRiskObligationResponse,
    });
}

export function createControl() {
    const PostControlRequest = Control.omit({ id: true });
    const PostControlResponse = Control;

    return api<z.infer<typeof PostControlRequest>, z.infer<typeof PostControlResponse>>({
        method: HTTPMethod.POST,
        path: '/riskObligation/control',
        requestSchema: PostControlRequest,
        responseSchema: PostControlResponse,
    });
}

export function updateControl(controlId: number) {
    const PostControlRequest = Control;
    const PostControlResponse = Control;

    return api<z.infer<typeof PostControlRequest>, z.infer<typeof PostControlResponse>>({
        method: HTTPMethod.PUT,
        path: '/riskObligation/control/' + controlId,
        requestSchema: PostControlRequest,
        responseSchema: PostControlResponse,
    });
}
