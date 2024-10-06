import { useMutation, useQuery } from 'react-query';
import { deleteComplianceRule, editComplianceRule, saveComplianceRule } from '../api/complianceRule/complianceRuleMutations';
import { ComplianceRule } from '../types/ComplianceRule';
import { getComplianceRulesByControl, getComplianceRuleTemplates, getTemplate, getTemplates } from '../api/complianceRule/complianceRuleQueries';

export function useSaveComplianceRule() {
    return useMutation(saveComplianceRule());
}

export function useEditComplianceRule( id : number ) {
    return useMutation(editComplianceRule(id));
}

export function useDeleteComplianceRule(id : number) {
    return useMutation(deleteComplianceRule(id));
}

export function useComplianceRuleTemplates(controlDescription?: string) {
    return useQuery({
        queryKey: ['templates', controlDescription],
        queryFn: () => getComplianceRuleTemplates()({ control_description: controlDescription }),
        refetchOnWindowFocus: false,
    });
}

export function useTemplate(templateId: number) {
    return useQuery(['templates', templateId, 'template'], () => getTemplate(templateId)());
}

export function useTemplates() {
    return useQuery(['templates', 'template'], () => getTemplates()());
}

export function useComplianceRulesByControl(controlId: number) {
    return useQuery(['control', controlId, 'compliance-rules'], () => getComplianceRulesByControl(controlId)());
}
