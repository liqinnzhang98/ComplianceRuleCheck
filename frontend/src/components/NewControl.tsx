import { useForm } from 'react-hook-form';
import { Control, ControlDefaultTypes } from '../types/Control';
import Input from './form/Input';
import Button from './Button';
import { useEffect, useState } from 'react';
import Select from './form/Select';
import { TrashIcon } from '@heroicons/react/24/outline';
import { Template } from '../types/Template';
import debounce from '../utils/debounce';
import AutoComplete from './form/AutoComplete';
import { useComplianceRuleTemplates } from '../hooks/complianceRuleHooks';

const debounceControlDescription = debounce(
    async (cd: string, setControlDescription: (cd: string | undefined) => void) => {
        setControlDescription(cd || undefined);
    },
    1000,
);

// use existing controls for continuous popup
function NewControl({ item, updateItem }: { item: Control; updateItem: (item: Control) => void }) {
    // const { show, addControl, handleClose, item } = props;
    const [templates, setTemplates] = useState<Template[]>([]);
    const [controlDescription, setControlDescription] = useState<string | undefined>(item.description || undefined);
    const [templateType, setTemplateType] = useState<string>('');
    const [mappedTemplates, setMappedTemplates] = useState<Template[][]>(item.templates || []);
    const [inputValue, setInputValue] = useState(item.description || '');
    const { data: newTemplates } = useComplianceRuleTemplates(controlDescription);

    const { register, setValue, watch } = useForm<{
        type?: string;
        name?: string;
        description?: string;
        templates?: Template[][];
    }>({ defaultValues: item });

    useEffect(() => {
        setTemplateType(item.type);
        setControlDescription(item.description);
    }, [item]);

    useEffect(() => {
        if (newTemplates !== undefined) {
            newTemplates.sort((a, b) =>
                a.recommendation && b.recommendation ? b.recommendation - a.recommendation : 0,
            );
            setTemplates(newTemplates as Template[]);
            setMappedTemplates([
                [newTemplates.filter((t) => !(item.type in ControlDefaultTypes) || t.category == item.type)[0]],
            ]);
        }
    }, [newTemplates]);

    useEffect(() => {
        updateItem({ ...item, templates: mappedTemplates } as Control);
        watch((data) => {
            const fullData = { ...data, templates: mappedTemplates };
            updateItem(fullData as Control);
        });
    }, [watch, mappedTemplates]);

    useEffect(() => {
        setInputValue(item.description || '');
    }, [item.description]);

    const handleDeleteTemplate = (groupIndex: number, templateIndex: number) => {
        let _mappedTemplates = [...mappedTemplates];
        _mappedTemplates[groupIndex] = _mappedTemplates[groupIndex].filter((_, i) => i !== templateIndex);
        _mappedTemplates = _mappedTemplates.filter((group) => group.length != 0);
        setMappedTemplates(_mappedTemplates);
    };

    const handleDeleteTemplateGroup = (index: number) => {
        setMappedTemplates(mappedTemplates.filter((_, i) => i !== index));
    };

    const handleAddTemplate = (index: number) => {
        const _mappedTemplates = [...mappedTemplates];

        _mappedTemplates[index] = [..._mappedTemplates[index]];
        _mappedTemplates[index].push(templates[0]);
        setMappedTemplates(_mappedTemplates);
    };

    const addTemplateGroup = () => {
        setMappedTemplates([...mappedTemplates, [templates[0]]]);
    };

    const handleSelectTemplate = (
        groupIndex: number,
        templateIndex: number,
        e: React.ChangeEvent<HTMLSelectElement>,
    ) => {
        const _mappedTemplates = [...mappedTemplates];
        _mappedTemplates[groupIndex] = [..._mappedTemplates[groupIndex]];

        const optionIndex = templates.map((t) => t.rule).indexOf(e.target.value);
        _mappedTemplates[groupIndex][templateIndex] = templates[optionIndex];
        setMappedTemplates(_mappedTemplates);
    };

    return (
        <form className="max-h-[80vh] overflow-auto">
            <div className="p-2 w-[28em]">
                <Input
                    label="Control Name"
                    type="text"
                    placeholder="Control name"
                    {...register('name', {
                        required: true,
                    })}
                    value={item.name}
                />
                <Input
                    label="Control Description"
                    type="text"
                    placeholder="Control description"
                    {...register('description', {
                        required: true,
                    })}
                    onChange={(e) => {
                        const newValue = e.target.value;
                        setInputValue(newValue);
                        debounceControlDescription(newValue, setControlDescription);
                    }}
                    value={inputValue}
                />
                <AutoComplete
                    label="Control Type"
                    options={templates
                        .map((template) => template.category || '')
                        .filter((value, index, array) => array.indexOf(value) === index)}
                    {...register('type', {
                        required: true,
                    })}
                    onSelectChange={(val) => {
                        setValue('type', val);
                        setTemplateType(val);
                    }}
                    onChange={(event) => {
                        setValue('type', event.target.value);
                        setTemplateType(event.target.value);
                    }}
                    value={templateType}
                />
            </div>

            {templates && templates.length > 0 && (
                <>
                    <Button text="Add Rule Template Group" onClick={() => addTemplateGroup()} className="m-3" />

                    {mappedTemplates.map((templateGroup, index) => (
                        <RuleTemplateGroup
                            key={index}
                            index={index}
                            templates={templates}
                            templateGroup={templateGroup}
                            addTemplate={handleAddTemplate}
                            updateTemplate={handleSelectTemplate}
                            deleteTemplateGroup={() => handleDeleteTemplateGroup(index)}
                            deleteTemplate={handleDeleteTemplate}
                            templateCategoryFilter={
                                templates
                                    .map((template) => template.category || '')
                                    .filter((value, index, array) => array.indexOf(value) === index)
                                    .includes(templateType)
                                    ? templateType
                                    : undefined
                            }
                        />
                    ))}
                </>
            )}
        </form>
    );
}

export function RuleTemplate({
    isFirst,
    selectedTemplate,
    templates,
    updateRuleGroup,
    deleteRule,
    templateCategoryFilter,
}: {
    isFirst: boolean;
    selectedTemplate: Template;
    templates: Template[];
    updateRuleGroup: (e: React.ChangeEvent<HTMLSelectElement>) => void;
    deleteRule: () => void;
    templateCategoryFilter?: string;
}) {
    return (
        <div>
            {!isFirst && <p className="mb-3">AND</p>}
            <li className="mb-3 border border-gray-400 p-3 rounded-md flex items-center">
                <Select value={selectedTemplate.rule} onChange={updateRuleGroup}>
                    {templates
                        .filter((template) => {
                            if (templateCategoryFilter) return template.category == templateCategoryFilter;
                            return true;
                        })
                        .map(({ id, rule }) => (
                            <option key={id} value={rule}>
                                {rule}
                            </option>
                        ))}
                </Select>
                <TrashIcon className="h-6 w-6 ml-2 hover:text-red-500 hover:cursor-pointer" onClick={deleteRule} />
            </li>
        </div>
    );
}

export function RuleTemplateGroup({
    index,
    templateGroup,
    templates,
    addTemplate,
    updateTemplate,
    deleteTemplate,
    deleteTemplateGroup,
    templateCategoryFilter,
}: {
    index: number;
    templateGroup: Template[];
    templates: Template[];
    deleteTemplateGroup: () => void;
    addTemplate: (groupIndex: number) => void;
    deleteTemplate: (groupIndex: number, templateIndex: number) => void;
    updateTemplate: (groupIndex: number, templateIndex: number, e: React.ChangeEvent<HTMLSelectElement>) => void;
    templateCategoryFilter?: string;
}) {
    return (
        <div>
            {index != 0 && <p className="ml-5">OR</p>}
            <div className="m-3 border border-gray-400 p-3 rounded-md">
                <ul>
                    {templateGroup.map((template, ruleIndex) => (
                        <RuleTemplate
                            key={ruleIndex}
                            isFirst={ruleIndex == 0}
                            templates={templates}
                            selectedTemplate={template}
                            deleteRule={() => deleteTemplate(index, ruleIndex)}
                            updateRuleGroup={(e) => updateTemplate(index, ruleIndex, e)}
                            templateCategoryFilter={templateCategoryFilter}
                        />
                    ))}
                </ul>
                <div className="flex justify-end">
                    <Button text="Add Template" onClick={() => addTemplate(index)} />
                    <Button text="Delete Group" onClick={deleteTemplateGroup} className="ml-2" />
                </div>
            </div>
        </div>
    );
}

export default NewControl;
