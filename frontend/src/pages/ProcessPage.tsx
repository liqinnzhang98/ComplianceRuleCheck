// the page for each process, where the user can upload a csv file, define the schema, and view the schema table on the right panal

import React, { useEffect, useState } from 'react';
import { unstable_batchedUpdates } from 'react-dom';
import { useNavigate, useParams } from 'react-router-dom';
import ActionBar, { Seperator } from '../components/ActionBar';
import SchemaForm from '../components/SchemaForm';
import SyncIcon from '../assets/icons/sync.svg';
import UploadIcon from '../assets/icons/upload.svg';
import Loader from '../assets/loader.svg';
import DownloadIcon from '../assets/icons/download.svg';
import TrashIcon from '../assets/icons/trash.svg';
import FileInfo from '../assets/icons/info.svg';
import LogIcon from '../assets/icons/log.svg';
import RenameIcon from '../assets/icons/rename.svg';
import Breadcrumb from '../components/Breadcrumb';
import FileUploadPopup from '../components/FileUploadPopup';
import { ErrorPopup } from '../components/ErrorPopup';
import { EventLog, EventLogTableColumns } from '../types/EventLog';
import Table from '../components/Table';
import { Column } from 'react-table';
import { ViewComplianceRulesByProcessPopup } from '../components/ViewComplianceRulesPopup';
import { ComplianceRule } from '../types/ComplianceRule';
import { useComplianceRulesByProcess, useEventLogs, useSchema, useUploadEventLogData } from '../hooks/processHooks';
import SchemaMappingForm from '../components/SchemaMappingForm';
import { useGenerateReport } from '../hooks/reportHooks';
import { PopupHeader, PopupPanel } from '../components/Popup';
import Button from '../components/Button';
import ShowRules from '../components/ShowRules';

function ProcessPage() {
    const ComplianceRuleTableColumns = [
        // { Header: 'ComplianceRuleID', accessor: 'id', width: '30%' },
        {
            Header: 'Rules',
            accessor: 'rules',
            Cell: ({ value }: { value: Array<Array<{ templateId: number; values: { [key: string]: string } }>> }) => {
                return <ShowRules value={value} />;
            },
            width: '90%',
        },
    ];
    // the processId retrived from the url
    const { processId: processIdString } = useParams<{ processId: string }>();
    const processId = processIdString ? parseInt(processIdString) : 0;
    // add useEffect that whenever the processId changes refresh the page
    useEffect(() => {
        refetchEventLogs();
        refetchSchema();
    }, [processId]);

    // subpage & pop up management States
    const [showUploadPopup, setShowUploadPopup] = useState(false); //TODO: for delete eventlogs function
    const [showSchemaErrorPopup, setShowSchemaErrorPopup] = useState(false);
    const [showEmptyRulesErrorPopup, setShowEmptyRulesErrorPopup] = useState(false);
    const [showDefineSchemaPage, setDefineSchemaPage] = useState(false);
    const [viewComplianceRulesPopup, setViewComplianceRulesPopup] = useState(false);
    const [schemaDefined, setSchemaDefined] = useState(false);

    // the error message pop up and its content
    const [showErrorPopup, setShowErrorPopup] = useState(false);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    // temporary stored data on client side States
    const [selectedRows, setSelectedRows] = useState<EventLog[]>([]);
    const [uploadedCsvFileName, setUploadedCsvFileName] = useState<string>('');
    const [savedFile, setSavedFile] = useState<File | null>(null);

    // fetched(GET) data from backend States
    const { data: eventLogs, refetch: refetchEventLogs } = useEventLogs(processId);
    const { data: schema, refetch: refetchSchema } = useSchema(processId);

    // mutations to send request to backend endpoint
    const uploadEventLogDataMutation = useUploadEventLogData(processId, {
        onError: (error: any) => {
            const errorMsg = error.response?.data?.errorDetails[0]?.description || 'An unknown error occurred';
            setErrorMessage(errorMsg);
            setShowErrorPopup(true);
        },
    });

    // report generation variables
    const { data: complianceRules } = useComplianceRulesByProcess(processId);
    useEffect(() => {
        setViewComplianceRules(complianceRules);
    }, [complianceRules]);
    const [viewComplianceRules, setViewComplianceRules] = useState<ComplianceRule[]>();
    const [showConfirmReport, setShowConfirmReport] = useState(false);
    const reportGenerator = useGenerateReport(processId);
    const [reportGenerationStatus, setReportGenerationStatus] = useState('IDLE');
    const [reportName, setReportName] = useState('');

    const handleReportNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setReportName(e.target.value);
    };
    const navigate = useNavigate();

    const [rows, setRows] = useState<ComplianceRule[]>([]);
    const [selectedRowsCompliance, setSelectedRowsCompliance] = useState<ComplianceRule[]>([]);

    useEffect(() => {
        setRows(
            complianceRules
                ? complianceRules.flatMap((cr) =>
                      cr.rules.map((c) => {
                          return {
                              ...cr,
                          } as ComplianceRule;
                      }),
                  )
                : [],
        );
    }, [complianceRules]);

    const handleFileUpload = async (file: File) => {
        if (!/text\/csv/.exec(file.type)) {
            throw new Error('File is not a CSV');
        }

        setUploadedCsvFileName(file.name);
        setSavedFile(file);

        setShowUploadPopup(false);
        setDefineSchemaPage(true);

        if (schema && schema.length) {
            setSchemaDefined(true);
        }
    };

    async function uploadEventLogData(file: File) {
        // Fetch the schema of the process
        await refetchSchema();

        const formData = new FormData();
        formData.append('file', file);

        // send the file to the backend, with processId get from the url
        const response = await uploadEventLogDataMutation.mutateAsync(formData);
        // if the returned eventlogDto.id is -1, meaning file invalid, show the error popup
        if (response.id === -1) {
            setShowSchemaErrorPopup(true);
        }

        refetchEventLogs();
    }

    useEffect(() => {
        if (schema && schema.length) {
            setSchemaDefined(true);
        }
    }, [schema]);

    useEffect(() => {
        console.log(selectedRowsCompliance);
    }, [selectedRowsCompliance]);

    async function handleSaveSchemaButton(file: File) {
        await refetchSchema().then(() => {
            unstable_batchedUpdates(() => {
                setDefineSchemaPage(false);
                setSchemaDefined(true);
            });
            uploadEventLogData(file);
        });
    }

    function cancelReportGeneration() {
        setShowConfirmReport(false);
        setReportGenerationStatus('IDLE');
    }

    // the items on the action bar above the process page
    const actionBarItems = [
        {
            icon: SyncIcon,
            tooltip: 'Sync',
            handler: () => {
                console.log('SYNC');
            },
        },
        Seperator,
        {
            icon: UploadIcon,
            tooltip: 'Upload',
            handler: () => setShowUploadPopup(true),
        },
        Seperator,
        {
            icon: DownloadIcon,
            tooltip: 'Download',
            handler: () => {
                console.log('DOWNLOAD');
            },
        },
        Seperator,
        {
            icon: TrashIcon,
            tooltip: 'Delete',
            handler: () => {
                console.log('DELETE');
            },
        },
        Seperator,
        {
            icon: RenameIcon,
            tooltip: 'Rename',
            handler: () => {
                console.log('RENAME');
            },
        },
        Seperator,
        {
            icon: LogIcon,
            tooltip: 'Check process compliance',
            handler: () => {
                setShowConfirmReport(true);
            },
        },
        Seperator,
        {
            icon: FileInfo,
            tooltip: 'View Compliance Rules',
            handler: () => {
                if (viewComplianceRules && viewComplianceRules.length > 0) {
                    setViewComplianceRulesPopup(true);
                } else {
                    setShowEmptyRulesErrorPopup(true);
                }
            },
        },
    ];

    useEffect(() => {
        if (schema && schema.length) {
            setSchemaDefined(true);
        }
    }, [schema]);

    // renders the content of the page(Action bar; Eventlogs list; Schema/Table; file upload popup)
    const renderContent = () => {
        return (
            <div>
                <ActionBar items={actionBarItems} />

                {/* the table of all eventlogs uploaded */}
                <Breadcrumb>Event logs</Breadcrumb>
                <div className="relative overflow-x-scroll flex-grow">
                    <Table
                        columns={EventLogTableColumns as Column<EventLog>[]}
                        data={eventLogs || []}
                        onRowSelectStateChange={(logs: EventLog[]) => setSelectedRows(logs)}
                    />
                </div>

                {/* Render the FileUploadPopup component only when showUploadPopup is set to true*/}
                {showUploadPopup && (
                    <FileUploadPopup
                        title="Upload Event Log"
                        onConfirm={handleFileUpload}
                        onCancel={() => setShowUploadPopup(false)}
                    />
                )}

                <ViewComplianceRulesByProcessPopup
                    data={viewComplianceRules}
                    show={viewComplianceRulesPopup}
                    handleClose={() => {
                        setViewComplianceRulesPopup(false);
                    }}
                    handleDelete={(id: number) => {
                        const updatedRules = viewComplianceRules?.filter((item) => item.id !== id);
                        setViewComplianceRules(updatedRules);
                        if (updatedRules && updatedRules.length == 0) {
                            setViewComplianceRulesPopup(false);
                            setShowEmptyRulesErrorPopup(true);
                        }
                    }}
                />
                <ErrorPopup
                    show={showEmptyRulesErrorPopup}
                    handleClose={() => setShowEmptyRulesErrorPopup(false)}
                    errorMessage={'No compliance rules associated with this process.'}
                    header={'View Compliance Rules Error'}
                />

                <PopupPanel show={showConfirmReport} handleClose={cancelReportGeneration}>
                    <PopupHeader handleClose={cancelReportGeneration}>Generate report?</PopupHeader>
                    <div className="p-5 w-[40vw]">
                        {reportGenerationStatus == 'IDLE' && (
                            <>
                                <p className="text-center mb-5">
                                    Are you sure you want to generate a compliance check for this project?
                                </p>
                                <div className="flex flex-col justify-center items-center mb-2">
                                    <input
                                        type="text"
                                        placeholder="Enter Report Name Here..."
                                        className="p-1 w-full max-w-sm border border-black"
                                        value={reportName}
                                        onChange={handleReportNameChange}
                                    ></input>
                                </div>
                                <div className="mb-4">
                                    <Table
                                        columns={ComplianceRuleTableColumns as Column<ComplianceRule>[]}
                                        data={rows}
                                        onRowSelectStateChange={(r: ComplianceRule[]) => {
                                            setSelectedRowsCompliance(r);
                                        }}
                                    />
                                </div>
                                <div className="flex justify-center">
                                    <Button
                                        className="mr-2"
                                        text="Yes"
                                        onClick={() => {
                                            if (selectedRowsCompliance.length === 0) {
                                                alert('No rows selected!');
                                                return;
                                            }
                                            setReportGenerationStatus('RUNNING');
                                            reportGenerator.mutate(selectedRowsCompliance, {
                                                onSuccess: () => {
                                                    setReportGenerationStatus('COMPLETED');
                                                },
                                            });
                                        }}
                                    />
                                    <Button className="ml-2" text="Cancel" onClick={cancelReportGeneration} />
                                    <Button text="test" className="ml-2"></Button>
                                </div>
                            </>
                        )}
                        {reportGenerationStatus == 'RUNNING' && (
                            <div className="h-full flex flex-col items-center justify-center">
                                <img className="h-20" src={Loader} alt="loading animation" />
                                <p className="mt-3">Generating Report</p>
                            </div>
                        )}
                        {reportGenerationStatus == 'COMPLETED' && (
                            <>
                                <p className="text-center mb-5">
                                    Compliance has been checked for this process. An aggregated report can be found on
                                    the dashboard page.
                                </p>
                                <Button
                                    className="text-center w-full"
                                    text="Go to dashboard"
                                    onClick={() => navigate('/')}
                                />
                            </>
                        )}
                    </div>
                </PopupPanel>
            </div>
        );
    };

    // the function to render the user input schema form, used when the schema is not defined
    const renderSchemaForm = () => {
        if (!savedFile) {
            return <p>No file is selected!</p>;
        }

        return (
            <div>
                <Breadcrumb>Define Process Schema</Breadcrumb>
                <SchemaForm
                    processId={processId}
                    file={savedFile}
                    // csv_headers={csvHeaders}
                    onSaveSchema={handleSaveSchemaButton}
                    onCancel={() => setDefineSchemaPage(false)}
                />
            </div>
        );
    };

    // the function to render the schema mapping form, used when the schema is defined
    const renderSchemaMapForm = () => {
        if (!savedFile) {
            return <p>No file is selected!</p>;
        }

        if (!schema) {
            return <p>No schema is defined!</p>;
        }

        // if the schema is not matching the csv, show the error popup

        return (
            <div>
                <Breadcrumb>{'Map Attributes: ' + uploadedCsvFileName}</Breadcrumb>
                <SchemaMappingForm
                    processId={processId}
                    schema={schema}
                    file={savedFile}
                    onSaveSchema={handleSaveSchemaButton}
                    onCancel={() => setDefineSchemaPage(false)}
                />
            </div>
        );
    };

    // render the process page
    return (
        <div>
            {/* the eventlog file upload error pop up */}
            <ErrorPopup
                show={showErrorPopup}
                handleClose={() => setShowErrorPopup(false)}
                errorMessage={errorMessage}
                header={'Incompatible Schema'}
            />

            {showDefineSchemaPage ? (!schemaDefined ? renderSchemaForm() : renderSchemaMapForm()) : renderContent()}
        </div>
    );
}

export default ProcessPage;
