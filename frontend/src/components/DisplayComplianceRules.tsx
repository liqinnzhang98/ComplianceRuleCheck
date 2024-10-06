import { useState } from 'react';
import { useDeleteComplianceRule, useTemplate, useTemplates } from '../hooks/complianceRuleHooks';
import { Rule, ComplianceRule, ComplianceRules } from '../types/ComplianceRule';
import { getRuleDescription } from '../utils/complianceRule';
import Button from './Button';
import { UpdateComplianceTemplatePopup } from './FillComplianceTemplatePopup';

type Props = {
    complianceRule?: ComplianceRule;
    handleDelete: (id : number) => void;
};

function DisplayTemplate({ rule }: { rule: Rule }) {
    const { data: templates } = useTemplates();
    
    return (
        <div>
            {getRuleDescription(rule, templates)}
        </div>
    );
}

function DisplayAnd({ rules }: { rules: Rule[] }) {
    return (
        <div className="p-2 min-w-[50vw] border">
            {rules.map((rule, index) => (
                <div key={index}>
                    <DisplayTemplate rule={rule} />
                    {index < rules.length - 1 && 'AND'}
                </div>
            ))}
        </div>
    );
}

function DisplayOr({ rules }: { rules: ComplianceRules }) {
    return (
        <div className="p-2 min-w-[50vw] border">
            {rules.map((ruleGroup, index) => (
                <div key={index}>
                    <DisplayAnd rules={ruleGroup} />
                    {index < rules.length - 1 && 'OR'}
                </div>
            ))}
        </div>
    );
}

export default function DisplayComplianceRules({ complianceRule, handleDelete }: Props) {
    const [viewEditComplianceRules, setViewEditComplianceRules] = useState(false);
    const deleteComplianceRuleMutation = useDeleteComplianceRule(complianceRule?.id ?? 0);

    if (complianceRule?.rules === undefined) {
        return null;
    }

    const handleEdit = () => {
        setViewEditComplianceRules(true);
    };

    const handleDeleteButton = async (id: number) => {
        await deleteComplianceRuleMutation.mutate(id);
        handleDelete(id);
    };

    return (
        <div className="min-w-[50vw]">
            <DisplayOr rules={complianceRule.rules} />
            <div className="p-2 flex w-full justify-end">
                <Button
                    key="Edit"
                    text="Edit"
                    onClick={() => {
                        handleEdit();
                    }}
                    className="mr-2"
                />
                <Button
                    key="Delete"
                    text="Delete"
                    onClick={() => {
                        handleDeleteButton(complianceRule.id);
                    }}
                />
            </div>
            <UpdateComplianceTemplatePopup
                complianceRule={complianceRule}
                show={viewEditComplianceRules}
                handleClose={() => setViewEditComplianceRules(false)}
            />
        </div>
    );
}
