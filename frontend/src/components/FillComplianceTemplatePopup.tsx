import { PopupFooter, PopupGroup, PopupHeader, PopupPanel } from './Popup';
import { Control } from '../types/Control';
import { useEffect, useState } from 'react';
import { FormField, FormFieldType } from '../types/FormField';
import { CheckboxField, TextField, DurationField, TimeField } from './GenericFormFields';
import { Template } from '../types/Template';
import Input from './form/Input';
import { Disclosure, Transition } from '@headlessui/react';
import { ChevronUpIcon } from '@heroicons/react/24/outline';
import SelectField from './form/SelectField';
import Select from '../components/form/Select';
import { Process, ProcessMetadata } from '../types/Process';
import {
    ComplianceRuleValues,
    ComplianceRules,
    ComplianceRuleAttributeValue,
    ComplianceRule,
} from '../types/ComplianceRule';
import { useMetadata, useProcess, useProcesses } from '../hooks/processHooks';
import { z } from 'zod';
import { useEditComplianceRule, useSaveComplianceRule } from '../hooks/complianceRuleHooks';
import Button from './Button';
import DisclosureComplianceRule from './DisclosureComplianceRule';
import { Rule } from '../types/Rule';
import { useControl } from '../hooks/riskObligationHooks';
import { createLogger } from 'vite';

type Props = {
    data?: Control[];
    handleClose: () => void;
};

type UpdateProps = {
    complianceRule: ComplianceRule;
    show: boolean;
    handleClose: () => void;
};

function getSelectOptions(metadata: ProcessMetadata, field: FormField) {
    if (field.type !== FormFieldType.SELECT) return;
    if (field.options) return field.options;
    if (field.selectFrom == undefined || field.selectFrom == null) return;
    if (!Object.keys(metadata).includes(field.selectFrom)) return;
    return [...metadata[field.selectFrom]];
}

function checkFieldDependency(dependencyString: string | null, templateData: ComplianceRuleValues) {
    if (!dependencyString) return true;

    let isNot = false;
    if (dependencyString[0] == '!') {
        isNot = true;
        dependencyString = dependencyString.replace(/^./, '');
    }
    const dependencyVal = templateData[dependencyString];
    if (isNot) {
        return !dependencyVal;
    } else {
        return dependencyVal;
    }
}

function FormFieldsGroup({
    fields,
    templateData,
    updateTemplateData,
    metadata,
}: {
    fields: FormField[];
    templateData: ComplianceRuleValues;
    updateTemplateData: (key: string, value: ComplianceRuleAttributeValue) => void;
    metadata: ProcessMetadata;
}) {
    return (
        <div className="grid gap-2 my-2">
            {fields.map((field, index) => (
                <FormFieldUI
                    key={field.name + '-' + index}
                    field={field}
                    templateData={templateData}
                    updateTemplateData={updateTemplateData}
                    metadata={metadata}
                />
            ))}
        </div>
    );
}

function FormFieldUI({
    field,
    templateData,
    updateTemplateData,
    metadata,
}: {
    field: FormField;
    templateData: ComplianceRuleValues;
    updateTemplateData: (key: string, value: ComplianceRuleAttributeValue) => void;
    metadata: ProcessMetadata;
}) {
    if (field.dependsOn && !checkFieldDependency(field.dependsOn, templateData)) return <></>;

    switch (field.type) {
        case FormFieldType.CHECK:
            return (
                <CheckboxField
                    label={field.label || ''}
                    checked={!!templateData[field.name]}
                    onCheckChange={() => {
                        updateTemplateData(field.name, !templateData[field.name]);
                    }}
                />
            );
        case FormFieldType.DATA:
        case FormFieldType.NUMBER:
            return (
                <TextField
                    label={field.label || ''}
                    value={(templateData[field.name] || '').toString()}
                    onChange={(v) => {
                        updateTemplateData(field.name, v);
                    }}
                />
            );
        case FormFieldType.DURATION:
            return (
                <DurationField
                    label={field.label || ''}
                    initialValue="0"
                    onDurationChange={(v) => {
                        updateTemplateData(field.name, v);
                    }}
                />
            );
        case FormFieldType.SELECT:
            return (
                <SelectField
                    label={field.label}
                    options={getSelectOptions(metadata, field)}
                    selectedOption={(templateData[field.name] || '').toString()}
                    onOptionChange={(o) => {
                        updateTemplateData(field.name, o);
                    }}
                />
            );
        case FormFieldType.TIME:
            return <TimeField label={field.label || ''} initialValue="12:20:20" />;
        case FormFieldType.TEXT:
            return <p className="text-center font-bold text-maroon-900">{field.text}</p>;
    }
    return <> </>;
}

function prepareFormFields(formFields: FormField[]) {
    const formFieldsGroups: FormField[][] = [];
    let currentGroup: FormField[] = [];
    formFields.forEach((field) => {
        if (field.type === FormFieldType.COLUMN_BREAK) {
            formFieldsGroups.push(currentGroup);
            currentGroup = [];
        } else {
            currentGroup.push(field);
        }
    });
    if (currentGroup.length > 0) formFieldsGroups.push(currentGroup);
    return formFieldsGroups;
}

function RuleTemplate({
    template,
    metadata,
    updateFilledTemplates,
}: {
    template: Template;
    metadata: ProcessMetadata;
    updateFilledTemplates: (td: ComplianceRuleValues) => void;
}) {
    const initialTemplateData: ComplianceRuleValues = template.formFields
        ? template.formFields.reduce((prev, field) => ({ ...prev, [field.name]: null }), {})
        : [];
    const [templateData, setTemplateData] = useState(initialTemplateData);

    function updateTemplateData(key: string, value: ComplianceRuleAttributeValue) {
        const newTemplateData = {
            ...templateData,
            [key]: value,
        } as ComplianceRuleValues;
        setTemplateData(newTemplateData);
        updateFilledTemplates(newTemplateData);
    }

    const fieldsGroupToRender = prepareFormFields(template.formFields ?? []);

    return (
        <Disclosure>
            {({ open }) => (
                <>
                    <Disclosure.Button className="flex w-full border items-center justify-between rounded-md bg-soft-blue-100 px-4 py-2 text-left text-sm font-medium text-maroon-900 hover:bg-soft-blue-200 focus:outline-none focus-visible:ring focus-visible:ring-soft-blue-500 focus-visible:ring-opacity-75">
                        <div>
                            <p>
                                <strong>{template.rule}</strong>: {template.description}
                            </p>
                            <small>{template.example}</small>
                        </div>

                        <ChevronUpIcon className={`${open ? 'rotate-180 transform' : ''} h-5 w-5 text-maroon-500`} />
                    </Disclosure.Button>
                    <Transition
                        enter="transition duration-100 ease-out"
                        enterFrom="transform scale-95 opacity-0"
                        enterTo="transform scale-100 opacity-100"
                        leave="transition duration-75 ease-out"
                        leaveFrom="transform scale-100 opacity-100"
                        leaveTo="transform scale-95 opacity-0"
                    >
                        <Disclosure.Panel
                            className={`px-4 pb-2 text-sm grid gap-y-4 grid-cols-${fieldsGroupToRender.length} items-center border mx-3 py-4`}
                        >
                            {fieldsGroupToRender.map((fields: FormField[], index) => (
                                <FormFieldsGroup
                                    key={index}
                                    fields={fields}
                                    templateData={templateData}
                                    updateTemplateData={updateTemplateData}
                                    metadata={metadata}
                                />
                            ))}
                        </Disclosure.Panel>
                    </Transition>
                </>
            )}
        </Disclosure>
    );
}

function RuleTemplateGroup({
    index,
    ruleGroup,
    metadata,
    ruleGroupId,
    updateFilledTemplates,
}: {
    index: number;
    ruleGroup: Template[];
    metadata: ProcessMetadata;
    ruleGroupId: number;
    updateFilledTemplates: (td: ComplianceRuleValues, i: number, j: number) => void;
}) {
    return (
        <div>
            {index != 0 && <p className="ml-5">OR</p>}
            <div className="m-3 border border-gray-400 p-3 rounded-md">
                <ul>
                    {ruleGroup.map((template, ruleIndex) => {
                        return (
                            <li key={ruleIndex}>
                                {ruleIndex != 0 && <p className="mb-3 ml-4">AND</p>}
                                <div className="mb-3 border-gray-400 p-3 rounded-md">
                                    <RuleTemplate
                                        key={ruleIndex}
                                        template={template}
                                        metadata={metadata}
                                        updateFilledTemplates={(td) =>
                                            updateFilledTemplates(td, ruleGroupId, ruleIndex)
                                        }
                                    />
                                </div>
                            </li>
                        );
                    })}
                </ul>
            </div>
        </div>
    );
}

function FillingErrorContent({ text }: { text: string }) {
    return (
        <div className="border-gray-200 border-solid border-2 rounded-md mx-5">
            <p className="text-center py-5">{text}</p>
        </div>
    );
}

const ControlWithRule = Control.extend({ rules: z.optional(ComplianceRules) });
type ControlWithRule = z.infer<typeof ControlWithRule>;

export function FillComplianceTemplatePopup({ data, handleClose }: Props) {
    const { data: allProcesses } = useProcesses();

    const [processes, setProcesses] = useState<Process[]>();
    const [process, setProcess] = useState<Process>();
    const { data: metadata } = useMetadata(process ? process.id : 0);
    const complianceRuleMutation = useSaveComplianceRule();

    useEffect(() => {
        setProcesses(allProcesses?.filter((p) => !!p.eventLogs?.length));
    }, [allProcesses]);

    if (!processes || processes.length == 0) {
        return (
            <PopupPanel show={data != null} handleClose={handleClose}>
                <PopupHeader handleClose={handleClose}>Error: No Processes Available</PopupHeader>
                <FillingErrorContent text="There are no processes defined yet" />
            </PopupPanel>
        );
    }

    function PopupContent({ item, updateItem }: { item: ControlWithRule; updateItem: (c: ControlWithRule) => void }) {
        useEffect(() => {
            if (processes && process == null) {
                setProcess(processes[0]);
            }
        }, [processes]);

        if (item) {
            const [rules, setRules] = useState<ComplianceRules>(
                item.templates?.map((templateGroup) => {
                    return templateGroup.map((template) => {
                        return {
                            values: template.formFields?.reduce((values, v) => {
                                const newVal = {} as { [key: string]: ComplianceRuleAttributeValue };
                                newVal[v.name] = '';
                                return { ...values, ...newVal };
                            }, {}) as ComplianceRuleValues,
                            templateId: template.id,
                        };
                    });
                }) || [[]],
            );
            return (
                <div className="p-2 min-w-[50vw] max-h-[80vh] overflow-auto">
                    <div className="mx-5">
                        <Input label="Control Name" value={item.name || ''} disabled />
                        <Input label="Control Description" value={item.description || ''} disabled />
                        <Select
                            label="Process"
                            value={process?.name}
                            onChange={(e) => setProcess(processes?.filter((p) => p.name == e.target.value)[0])}
                        >
                            {processes?.map((_, i) => (
                                <option key={i}>{processes[i].name}</option>
                            ))}
                        </Select>
                    </div>
                    {!item.templates && <FillingErrorContent text="No templates mapped" />}
                    {!metadata && Object.keys(metadata || {}).length == 0 && (
                        <FillingErrorContent text="No event logs have been uploaded for this process" />
                    )}
                    {item.templates &&
                        metadata &&
                        Object.keys(metadata || {}).length != 0 &&
                        item.templates.map((templateGroup, indexT) => (
                            <RuleTemplateGroup
                                key={indexT}
                                ruleGroupId={indexT}
                                ruleGroup={templateGroup.map((_, i) => templateGroup[i])}
                                index={indexT}
                                metadata={metadata}
                                updateFilledTemplates={(td, i, j) => {
                                    const newFilledTemplates = [...rules];
                                    newFilledTemplates[i][j] = {
                                        templateId: templateGroup[j].id,
                                        values: td,
                                    };
                                    setRules(newFilledTemplates);
                                    updateItem({ ...item, rules: newFilledTemplates });
                                }}
                            />
                        ))}
                </div>
            );
        } else return <div className="p-2 min-w-[50vw]"></div>;
    }
    return (
        <PopupGroup
            data={data || []}
            PopupContent={PopupContent}
            handleClose={handleClose}
            getPopupHeaderText={(_, i) => {
                return 'Fill Compliance Template (' + (i + 1) + ' of ' + (data?.length || 0 + 1) + ')';
            }}
            onNext={(item: ControlWithRule) => {
                if (process && item && item.id && item.rules) {
                    const checkAttributes = item.templates
                        ?.map((tg) =>
                            tg
                                .map((t) =>
                                    t.formFields
                                        ?.filter((ff) => ff.type == FormFieldType.CHECK)
                                        .map((ff) => ff.name)
                                        .flat(),
                                )
                                .flat(),
                        )
                        .flat();
                    const complianceRules = {
                        processId: process.id,
                        controlId: item.id,
                        rules: item.rules.map((ruleGroup) =>
                            ruleGroup.map((rule) => {
                                const filteredValues: ComplianceRuleValues = {};
                                for (const attribute in rule.values) {
                                    const value = rule.values[attribute];
                                    if (value !== null && !checkAttributes?.includes(attribute)) {
                                        filteredValues[attribute] = value;
                                    }
                                }
                                rule.values = filteredValues;
                                return rule;
                            }),
                        ),
                    };
                    complianceRuleMutation.mutate(complianceRules);
                }
            }}
        />
    );
}

export function UpdateComplianceTemplatePopup({ complianceRule, show, handleClose }: UpdateProps) {
    const ControlRule = Control.extend({ rules: z.optional(z.array(z.array(Rule))) });
    type ControlRule = z.infer<typeof ControlRule>;

    const id = complianceRule.id;
    const [process, setProcess] = useState<Process>();
    const complianceRuleMutation = useEditComplianceRule(id);

    const { data: currentProcess } = useProcess(complianceRule.processId);
    const { data: processes } = useProcesses();
    const { data: metadata } = useMetadata(process ? process.id : 0);
    const { data: control } = useControl(complianceRule.controlId);

    const handleSave = () => {
        if (process && control && control.id && complianceRule.rules) {
            const checkAttributes = control.templates
                ?.map((tg) =>
                    tg
                        .map((t) =>
                            t.formFields
                                ?.filter((ff) => ff.type == FormFieldType.CHECK)
                                .map((ff) => ff.name)
                                .flat(),
                        )
                        .flat(),
                )
                .flat();
            const complianceRules = {
                id: id,
                processId: process.id,
                controlId: control.id,
                rules: complianceRule.rules.map((ruleGroup) =>
                    ruleGroup.map((rule) => {
                        const filteredValues: ComplianceRuleValues = {};
                        for (const attribute in rule.values) {
                            const value = rule.values[attribute];
                            if (value !== null && !checkAttributes?.includes(attribute)) {
                                filteredValues[attribute] = value;
                            }
                        }
                        rule.values = filteredValues;
                        return rule;
                    }),
                ),
            };
            complianceRuleMutation.mutate(complianceRules);
        }
    };

    function EditComplianceRulePopupContent({ complianceRule }: { complianceRule: ComplianceRule }) {
        const [rules, setRules] = useState<Rule[][]>(complianceRule.rules);

        useEffect(() => {
            if (processes && process == null) {
                setProcess(currentProcess);
            }
        }, [process]);

        return (
            <div className="p-2 min-w-[50vw] max-h-[80vh]">
                <div className="mx-5">
                    <Input label="Control Name" value={control?.name || ''} disabled />
                    <Input label="Control Description" value={control?.description || ''} disabled />
                    <Select
                        label="Process"
                        value={process?.name}
                        onChange={(e) => setProcess(processes?.filter((p) => p.name == e.target.value)[0])}
                    >
                        {processes?.map((_, i) => (
                            <option key={i}>{processes[i].name}</option>
                        ))}
                    </Select>
                </div>
                {control?.templates &&
                    metadata &&
                    Object.keys(metadata || {}).length != 0 &&
                    control.templates.map((templateGroup, indexT) => (
                        <RuleTemplateGroup
                            key={indexT}
                            ruleGroupId={indexT}
                            ruleGroup={templateGroup.map((_, i) => templateGroup[i])}
                            index={indexT}
                            metadata={metadata}
                            updateFilledTemplates={(td, i, j) => {
                                const newFilledTemplates = [...rules];
                                newFilledTemplates[i][j] = {
                                    templateId: templateGroup[j].id,
                                    values: td,
                                };
                                setRules(newFilledTemplates);
                                // updateItem({ ...control, rules: newFilledTemplates });
                            }}
                        />
                    ))}
            </div>
        );
    }
    return (
        <PopupPanel show={show} handleClose={handleClose}>
            <PopupHeader handleClose={handleClose}>Edit Compliance Rule</PopupHeader>
            <EditComplianceRulePopupContent complianceRule={complianceRule} />
            <PopupFooter>
                <Button
                    key="Close"
                    text="Close"
                    onClick={() => {
                        handleClose();
                    }}
                    className="mr-2"
                />
                <Button
                    key="Save"
                    text="Save"
                    onClick={() => {
                        handleSave();
                        handleClose();
                    }}
                    className="mr-2"
                />
            </PopupFooter>
        </PopupPanel>
    );
}
