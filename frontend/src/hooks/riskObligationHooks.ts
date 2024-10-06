import { useMutation, useQuery } from 'react-query';
import { getControl, getControls, getRiskObligations } from '../api/riskObligation/riskObligationQueries';
import {
    createControl,
    createRiskObligation,
    updateControl,
    uploadRiskObligationData,
} from '../api/riskObligation/riskObligationMutations';

export function useControl(controlId: number) {
    return useQuery(['riskObligations', controlId, 'control'], () => getControl(controlId)());
}
    
export function useControls() {
    return useQuery(['controls'], () => getControls()());
}

export function useRiskObligations() {
    return useQuery(['riskObligation'], () => getRiskObligations()());
}

export function useCreateControl() {
    return useMutation(createControl());
}

export function useCreateRiskObligation() {
    return useMutation(createRiskObligation());
}

export function useUploadRiskObligationData() {
    return useMutation(uploadRiskObligationData());
}

export function useUpdateControl(controlId: number) {
    return useMutation(updateControl(controlId));
}
