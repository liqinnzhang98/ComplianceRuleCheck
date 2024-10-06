import { useTemplate } from '../hooks/complianceRuleHooks';
import { Rule } from '../types/ComplianceRule';
import { FormField } from '../types/FormField';
import { Template } from '../types/Template';

export function getRuleDescription(rule: Rule, templates?: Template[]) {
    if (!templates) {
        console.log("Could not find templates");
        return null;
    }
    const template = templates?.find((item) => item.id === rule.templateId);

    const fieldNames = template?.formFields?.map((field) => field.name);
    if (!fieldNames) {
        return null;
    }

    const concatList = template?.formFields?.map((field) => {
        if (field.name.startsWith('rule_text')) {
            return field.text;
        } else if (field.name.startsWith('column_')) {
            return '';
        } else if (field.name == 'duration_k') {
            return (rule.values[field.name] as string).toLowerCase() + '(s)';
        }
        return rule.values[field.name];
    });

    return concatList && concatList.join(' ');
}

function getConjunctionDescription(rules: Rule[], templates: Template[]) {
    const concatList = rules?.map((rule) => {
        return getRuleDescription(rule, templates);
    });

    if (concatList && concatList?.length > 1) {
        return concatList && concatList.join(' AND ');
    }
    return concatList && concatList.join('');
}

export function getComplianceRuleDescription(rules: Rule[][] | undefined, templates?: Template[]) {
    if (!templates) {
        console.log("Could not find templates");
        return null;
    }

    const concatList = rules?.map((andRules) => {
        return getConjunctionDescription(andRules, templates);
    });

    if (concatList && concatList?.length > 1) {
        return concatList && '(' + concatList.join(') OR (') + ')';
    }
    return concatList && concatList.join('');
}
