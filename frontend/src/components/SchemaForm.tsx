import { useEffect, useState } from 'react';
import Button from './Button';
import { CheckCircleIcon, XCircleIcon } from '@heroicons/react/24/outline';
import { Attribute, Schema } from '../types/Attribute';
import { AttributeType } from '../types/AttributeType';
import { AttributeDataType } from '../types/AttributeDataType';
import { useSaveSchema } from '../hooks/processHooks';
import { ErrorPopup } from './ErrorPopup';

interface SchemaFormProps {
    processId: number;
    file: File;
    onSaveSchema: (file: File) => void;
    onCancel: () => void;
}

interface SelectOptions {
    [key: string]: string;
}

const attributeOptions: SelectOptions = {
    '': '',
    'Case ID': AttributeType.CASE_ID,
    Activity: AttributeType.ACTIVITY,
    'Start Timestamp': AttributeType.START_TIMESTAMP,
    'End Timestamp': AttributeType.COMPLETE_TIMESTAMP,
    Resource: AttributeType.RESOURCE,
    Role: AttributeType.ROLE,
    'Event Attribute': AttributeType.EVENT_ATTRIBUTE,
    'Case Attribute': AttributeType.CASE_ATTRIBUTE,
    'Ignore Attribute': AttributeType.IGNORE_ATTRIBUTE,
};

// the attribute types that are required to be mapped in the schema
const criticalAttributes = [
    AttributeType.CASE_ID,
    AttributeType.ACTIVITY,
    AttributeType.RESOURCE,
    AttributeType.ROLE,
    AttributeType.START_TIMESTAMP,
    AttributeType.COMPLETE_TIMESTAMP,
];

const dataTypeOptions: SelectOptions = {
    String: AttributeDataType.TEXT,
    Number: AttributeDataType.INT,
    DateTime: AttributeDataType.TIMESTAMP,
};

// use fuzzy search to guess the attribute type with the header name
function guessType(header: string) {
    header = header.toLowerCase().replace(/[^a-z0-9]/gi, '');
    if (header.includes('caseid')) return AttributeType.CASE_ID;
    if (header.includes('activity')) return AttributeType.ACTIVITY;
    if (header.includes('start')) return AttributeType.START_TIMESTAMP;
    if (header.includes('end')) return AttributeType.COMPLETE_TIMESTAMP;
    if (header.includes('completetimestamp')) return AttributeType.COMPLETE_TIMESTAMP;
    if (header.includes('resource')) return AttributeType.RESOURCE;
    if (header.includes('role')) return AttributeType.ROLE;
}

// use attribute type to guess the data type (CASE_ID is not a number: Application_123456)
function guessDataType(type: AttributeType) {
    if (type === AttributeType.START_TIMESTAMP || type === AttributeType.COMPLETE_TIMESTAMP)
        return AttributeDataType.TIMESTAMP;
    return AttributeDataType.TEXT;
}

// assuming the header is in the first 5kb of the file
const FILE_HEADER_CHUNK_SIZE = 1024 * 5;

export function parseCSVHeader(file: File) {
    return new Promise<string[]>((resolve, reject) => {
        const slice = file.slice(0, FILE_HEADER_CHUNK_SIZE);
        const reader = new FileReader();

        reader.onload = (e: ProgressEvent<FileReader>) => {
            const result = e.target?.result as string;

            const header = result.split('\n')[0];

            resolve(header.split(','));
        };

        reader.readAsText(slice);
    });
}

export function updateCSVHeader(file: File, modifiedHeaders: string[]) {
    return new Promise<File>((resolve, reject) => {
        const slice = file.slice(0, FILE_HEADER_CHUNK_SIZE);

        const reader = new FileReader();

        reader.onload = (e: ProgressEvent<FileReader>) => {
            if (e.target && e.target.result) {
                const result = e.target.result as string;

                // eslint-disable-next-line prefer-const
                let [header, ...rest] = result.split('\n'); // Split by line

                // Modify the header
                header = modifiedHeaders.join(',');

                // Construct blob using modified header and rest of the file
                const headerBlob = new Blob([header + '\n'], { type: 'text/csv' });
                const restBlob = new Blob([rest.join('\n')], { type: 'text/csv' });

                const modifiedFile = new File([headerBlob, restBlob, file.slice(FILE_HEADER_CHUNK_SIZE)], file.name, {
                    type: 'text/csv',
                    lastModified: new Date().getTime(),
                });

                resolve(modifiedFile);
            } else {
                reject(new Error('Failed to read the file.'));
            }
        };

        reader.onerror = () => {
            reject(new Error('Error reading the file, make sure it is a valid CSV (UTF-8) file.'));
        };

        reader.readAsText(slice, 'utf-8');
    });
}

function SchemaForm({ processId, file, onSaveSchema, onCancel }: SchemaFormProps) {
    const [schema, setSchema] = useState<Schema>([]);

    const [columnNames, setColumnNames] = useState<string[]>([]);

    const saveSchemaMutation = useSaveSchema(processId, {
        onError: (error: any) => {
            const errorMsg = error.response?.data?.errorDetails[0]?.description || 'An unknown error occurred';
            setErrorMessage(errorMsg);
            setShowErrorPopup(true);
        },
    });

    // the missing critical attributes
    const [missingAttributes, setMissingAttributes] = useState<string[]>(criticalAttributes);

    const [showErrorPopup, setShowErrorPopup] = useState(false);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    // if the file is updated(uploaded), fetch the header of the csv file
    useEffect(() => {
        parseCSVHeader(file).then((columnNames) => setColumnNames(columnNames));
    }, [file]);

    // once the headers is updated, initialize the schema with the header
    useEffect(() => {
        if (!columnNames.length) return;

        setSchema(getInitialSchema());
    }, [columnNames]);

    // once the schema is initialized, update the missing attributes
    useEffect(() => {
        const selectedAttributes = schema.map((attr) => attr.type);
        const newMissingAttributes = criticalAttributes.filter((attr) => !selectedAttributes.includes(attr));
        setMissingAttributes(newMissingAttributes);
    }, [schema]);

    function getInitialSchema(): Attribute[] {
        return columnNames.map<Attribute>((header) => {
            const type = guessType(header);
            return {
                displayName: header,
                dataType: type && guessDataType(type),
                type,
            } as Attribute;
        });
    }

    const handleResetData = () => {
        setSchema(getInitialSchema());
    };

    // handler for the field name changes
    const handleCsvNameChange = (index: number, newValue: string) => {
        const newSchema = [...schema];
        newSchema[index].displayName = newValue;
        setSchema(newSchema);
    };

    // handler for the type selection changes
    const handleTypeChange = (index: number, newValue: AttributeType) => {
        const newSchema = [...schema];
        newSchema[index].type = newValue;
        newSchema[index].dataType = guessDataType(newValue);
        setSchema(newSchema);

        // Update missing attributes
        const selectedAttributes = newSchema.map((attr) => attr.type);
        const newMissingAttributes = criticalAttributes.filter((attr) => !selectedAttributes.includes(attr));
        setMissingAttributes(newMissingAttributes);
    };

    // handler for the attribute type selection changes
    const handleDataTypeChange = (index: number, newValue: AttributeDataType) => {
        const newSchema = [...schema];
        newSchema[index].dataType = newValue;
        setSchema(newSchema);
    };

    const handleConfirm = async () => {
        const sanitizedSchema = sanitizeSchema(schema);
        setSchema(sanitizedSchema);

        await saveSchemaMutation.mutateAsync(sanitizedSchema);

        const modifiedColumnNames = schema.map((attribute) => attribute.displayName);
        const updatedFile = await updateCSVHeader(file, modifiedColumnNames);

        onSaveSchema(updatedFile);
    };

    function sanitizeSchema(schema: Attribute[]) {
        const sanitizedSchema = [];

        for (const attribute of schema) {
            sanitizedSchema.push({
                displayName: attribute.displayName,
                type: attribute.type || AttributeType.ATTRIBUTE,
                dataType: attribute.dataType || AttributeDataType.TEXT,
            } as Attribute);
        }

        return sanitizedSchema;
    }

    // the color of the attribute types option in the select dropdown
    // green for critical attributes, yellow for missing critical attributes, black for others
    function getOptionColor(optionValue: AttributeType): string {
        if (criticalAttributes.includes(optionValue)) {
            return missingAttributes.includes(optionValue) ? 'text-yellow-500' : 'text-green-500';
        }
        return 'black';
    }

    if (!columnNames.length) {
        return <div>Loading...</div>;
    }

    return (
        <div className="relative">
            {/* the schema save error pop up */}
            <ErrorPopup
                show={showErrorPopup}
                handleClose={() => setShowErrorPopup(false)}
                errorMessage={errorMessage}
                header={'Incompatible Schema'}
            />

            {/* table of schema mapping */}
            <table className="min-w-full text-center text-sm font-light ">
                {/* header of the table */}
                <thead>
                    <tr>
                        {/* <th className="py-5 px-4 bg-gray-300 text-aliceblue w-300 text-left border-white border-2">
                            Attribute Name in CSV
                        </th> */}
                        <th className="py-5 px-4 bg-gray-300 text-aliceblue w-300 text-left border-white border-2">
                            Attribute Name
                        </th>
                        {/* <th className="py-5 px-4 bg-gray-300 text-aliceblue w-300 text-left border-white border-2">
                            Case Attribute
                        </th> */}
                        <th className="py-5 px-4 bg-gray-300 text-aliceblue w-300 text-left border-white border-2">
                            Attribute Type
                        </th>
                        <th className="py-5 px-4 bg-gray-300 text-aliceblue w-300 text-left border-white border-2">
                            Data Type
                        </th>
                    </tr>
                    {/* the row that shows The missing critical attributes*/}
                    {missingAttributes.length > 0 && (
                        <tr>
                            <td colSpan={3} className="text-yellow-500">
                                Missing Critical Attributes:{' '}
                                {missingAttributes
                                    .map((attr) => {
                                        return (
                                            Object.keys(attributeOptions).find(
                                                (key) => attributeOptions[key] === attr,
                                            ) || attr
                                        );
                                    })
                                    .join(', ')}
                            </td>
                        </tr>
                    )}
                </thead>

                {/* table body, each row is the csv_header; the new name in schema input; a type to be selected  */}
                <tbody>
                    {schema.map((attribute, index) => (
                        <tr key={index}>
                            {/* "Attribute Name" colunm*/}
                            <td className="py-4 px-4 md:px-6 text-left border-gainsboro border-2">
                                <input
                                    className="px-4 py-2 border border-grey rounded text-sm w-full"
                                    type="text"
                                    value={attribute.displayName}
                                    onChange={(event) => handleCsvNameChange(index, event.target.value)}
                                />
                            </td>

                            {/* "Attribute Type" column */}
                            <td className="py-4 px-4 md:px-6 text-left border-gainsboro border-2">
                                <select
                                    className="px-4 py-2 border border-grey rounded text-sm w-full"
                                    value={attribute.type}
                                    onChange={(event) => handleTypeChange(index, event.target.value as AttributeType)}
                                >
                                    {Object.keys(attributeOptions).map((label, index) => {
                                        const optionValue = attributeOptions[label] as AttributeType;
                                        if (
                                            criticalAttributes.includes(optionValue) &&
                                            !missingAttributes.includes(optionValue) &&
                                            attribute.type !== optionValue
                                        ) {
                                            return null; // Hide the option
                                        }
                                        return (
                                            <option
                                                key={label}
                                                value={optionValue}
                                                className={getOptionColor(optionValue)}
                                            >
                                                {label}
                                            </option>
                                        );
                                    })}
                                </select>
                            </td>

                            {/* "Data Type" colunm*/}
                            <td className="py-4 px-4 md:px-6 text-left border-gainsboro border-2">
                                <select
                                    className="px-4 py-2 border border-grey rounded text-sm w-full"
                                    value={attribute.dataType}
                                    onChange={(event) =>
                                        handleDataTypeChange(index, event.target.value as AttributeDataType)
                                    }
                                >
                                    {Object.keys(dataTypeOptions).map((label, index) => (
                                        <option key={index} value={dataTypeOptions[label]}>
                                            {label}
                                        </option>
                                    ))}
                                </select>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {/* the confirm and cancel section */}
            <div className="flex m-2">
                <Button text="Reset Schema" onClick={handleResetData} size="lg" className="mr-auto" />
                {missingAttributes.length === 0 && (
                    <Button text="OK" Icon={CheckCircleIcon} onClick={handleConfirm} size="lg" className="mr-2" />
                )}
                <Button text="Cancel" Icon={XCircleIcon} onClick={onCancel} size="lg" />
            </div>
        </div>
    );
}
export default SchemaForm;
