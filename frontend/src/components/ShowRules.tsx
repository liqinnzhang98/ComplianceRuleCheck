import React from 'react';
import { getComplianceRuleDescription } from '../utils/complianceRule';
import { useTemplates } from '../hooks/complianceRuleHooks';

type ShowRulesValue = Array<Array<{ templateId: number; values: { [key: string]: string } }>>;

interface ShowRulesProps {
    value: ShowRulesValue;
}

const ShowRules: React.FC<ShowRulesProps> = ({ value }) => {
    const { data: templates } = useTemplates();
    return (
        <div>
            <div>{getComplianceRuleDescription(value, templates)}</div>
        </div>
    );
};

export default ShowRules;
