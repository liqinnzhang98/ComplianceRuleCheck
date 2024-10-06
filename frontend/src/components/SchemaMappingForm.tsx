import { useEffect, useState } from 'react';
import Button from './Button';
import { CheckCircleIcon, XCircleIcon } from '@heroicons/react/24/outline';
import { Attribute } from '../types/Attribute';
import { parseCSVHeader, updateCSVHeader } from './SchemaForm';

interface SchemaFormProps {
    processId: number;
    file: File;
    onSaveSchema: (file: File) => void;
    onCancel: () => void;
    schema: Attribute[];
}

function SchemaMappingForm({ file, schema, onSaveSchema, onCancel }: SchemaFormProps) {
    const [columnNames, setColumnNames] = useState<string[]>([]);

    const [attributeMapping, setAttributeMapping] = useState<{ [key: string]: string }>({});

    function getInitialAttributeMapping() {
        const newAttributeMapping: { [key: string]: string } = {};

        for (const attribute of schema) {
            newAttributeMapping[attribute.name] = '';

            const similarName = columnNames.find(
                (label) => label.toLowerCase() === attribute.displayName.toLowerCase(),
            );

            if (similarName) {
                newAttributeMapping[attribute.name] = similarName;
            }
        }
        return newAttributeMapping;
    }

    function getSelectedAttributes() {
        return Object.values(attributeMapping).filter((value) => value !== '');
    }

    useEffect(() => {
        parseCSVHeader(file).then((columnNames) => setColumnNames(columnNames));
    }, [file]);

    useEffect(() => {
        if (!columnNames || !columnNames.length) return;

        setAttributeMapping(getInitialAttributeMapping());
    }, [columnNames]);

    const handleResetData = () => {
        setAttributeMapping(getInitialAttributeMapping());
    };

    const handleConfirm = async () => {
        const modifiedColumnNames = [...columnNames];

        for (const attribute of schema) {
            const oldHeader = attributeMapping[attribute.name];
            modifiedColumnNames[columnNames.indexOf(oldHeader)] = attribute.name;
        }

        const fileToSend = await updateCSVHeader(file, modifiedColumnNames);
        onSaveSchema(fileToSend);
    };

    // handler for the field name changes
    const handleAttributeSelectChange = (index: number, value: string) => {
        const newAttributeMapping = { ...attributeMapping };
        newAttributeMapping[schema[index].name] = value;
        setAttributeMapping(newAttributeMapping);
    };

    return (
        <div className="relative">
            {/* table of schema mapping */}
            <table className="min-w-full text-center text-sm font-light ">
                {/* header of the table */}
                <thead>
                    <tr>
                        <th className="py-5 px-4 bg-gray-300 text-aliceblue w-300 text-left border-white border-2">
                            Field Name in Schema
                        </th>
                        <th className="py-5 px-4 bg-gray-300 text-aliceblue w-300 text-left border-white border-2">
                            Field Name in CSV
                        </th>

                        <th className="py-5 px-4 bg-gray-300 text-aliceblue w-300 text-left border-white border-2">
                            Attribute Type
                        </th>
                        <th className="py-5 px-4 bg-gray-300 text-aliceblue w-300 text-left border-white border-2">
                            Data Type
                        </th>
                    </tr>
                </thead>

                <tbody>
                    {schema.map((attribute, index) => (
                        <tr key={index}>
                            {/* "Field Name in Schema" colunm*/}
                            <td className="py-4 px-4 md:px-6 text-left border-gainsboro border-2">
                                {attribute.displayName}
                            </td>
                            {/* "Field Name in CSV" colunm*/}
                            <td className="py-4 px-4 md:px-6 text-left border-gainsboro border-2">
                                <select
                                    className="px-4 py-2 border border-grey rounded text-sm w-full"
                                    value={attributeMapping[attribute.name]}
                                    onChange={(event) =>
                                        handleAttributeSelectChange(index, event.target.value as string)
                                    }
                                >
                                    {columnNames.map((label, index) => {
                                        // Remove the option if it's already selected for another attribute
                                        if (
                                            getSelectedAttributes().includes(label) &&
                                            attributeMapping[attribute.name] !== label
                                        ) {
                                            return null;
                                        }
                                        return (
                                            <option key={index} value={label}>
                                                {label}
                                            </option>
                                        );
                                    })}
                                </select>
                            </td>

                            <td className="py-4 px-4 md:px-6 text-left border-gainsboro border-2">
                                {attribute.dataType}
                            </td>
                            <td className="py-4 px-4 md:px-6 text-left border-gainsboro border-2">{attribute.type}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {/* the confirm and cancel section */}
            <div className="flex m-2">
                <Button text="Reset Schema" onClick={handleResetData} size="lg" className="mr-auto" />
                <Button text="OK" Icon={CheckCircleIcon} onClick={handleConfirm} size="lg" className="mr-2" />
                <Button text="Cancel" Icon={XCircleIcon} onClick={onCancel} size="lg" />
            </div>
        </div>
    );
}
export default SchemaMappingForm;
