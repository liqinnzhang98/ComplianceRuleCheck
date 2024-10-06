import React, { useState } from 'react';
import { Column } from 'react-table';
import Table from '../Table';
import { Report, Breach } from '../../types/Report';
import Button from '../Button';
import { PopupPanel, PopupHeader } from '../Popup';
import { useEventLogData } from '../../hooks/eventLogHooks';
import { InformationCircleIcon } from '@heroicons/react/24/outline';

export default function ReportTable({ selectedReport }: { selectedReport?: Report | null }) {
    const breaches = selectedReport?.report?.breaches || [];
    const sortedEntries = breaches.sort((a, b) => b.breachedCaseIds.length - a.breachedCaseIds.length);

    if (!selectedReport) {
        return <p>No report selected</p>;
    }

    // Explicitly define column types here
    const columns: Column<Breach>[] = React.useMemo(
        () => [
            {
                Header: () => <div className="text-center">Rule ID</div>,
                accessor: 'rule',
                width: '10%',
                Cell: ({ value }) => {
                    return <div className="text-center">{value.id}</div>;
                },
            },
            {
                Header: () => <div className="text-center">Control</div>,
                accessor: 'control',
                Cell: ({ value }) => {
                    return (
                        <div>
                            <p className="font-bold">{value.name}</p>
                            <p>{value.description}</p>
                        </div>
                    );
                },
                width: '60%',
            },

            {
                Header: () => <div className="text-center">Breached Cases</div>,
                accessor: 'breachedCaseIds',
                Cell: ({ value, row }) => {
                    const [showPopup, setShowPopup] = useState(false);

                    const cellValue = value.length.toString();
                    return (
                        <div className="flex justify-center">
                            <Button
                                text={cellValue}
                                className="text-center text-primary hover:bg-gray-100"
                                size="lg"
                                onClick={() => setShowPopup(true)}
                            />
                            <BreachPopup
                                breach={row.values as Breach}
                                showPopup={showPopup}
                                setShowPopup={setShowPopup}
                            />
                        </div>
                    );
                },
                width: '10%',
            },
        ],
        [],
    );

    return (
        <>
            <div className="w-full overflow-hidden rounded-lg shadow-xs">
                <div className="w-full overflow-x-auto border border-gray-300">
                    <Table columns={columns} data={sortedEntries} />
                </div>
            </div>
        </>
    );
}

function BreachPopup({
    breach,
    showPopup,
    setShowPopup,
}: {
    breach: Breach;
    showPopup: boolean;
    setShowPopup: (show: boolean) => void;
}) {
    const [showViewCaseData, setShowViewCaseData] = React.useState(false);

    return (
        <PopupPanel show={showPopup} handleClose={() => setShowPopup(false)} className="w-1/4">
            <PopupHeader handleClose={() => setShowPopup(false)}>Breached Cases</PopupHeader>
            <div className="flex flex-col overflow-y-auto">
                {breach.breachedCaseIds.map((caseId) => {
                    return (
                        <div key={caseId} className="border-b-2 p-2 flex justify-between">
                            <p>{caseId}</p>
                            <button
                                className="p-0 w-5 h-5 bg-transparent border-none text-grey-500"
                                onClick={() => setShowViewCaseData(true)}
                            >
                                <InformationCircleIcon className="w-full h-full solid" />
                            </button>
                            <ViewCaseDataPopup
                                processId={breach.rule.processId}
                                caseId={caseId}
                                showPopup={showViewCaseData}
                                setShowPopup={setShowViewCaseData}
                            />
                        </div>
                    );
                })}
            </div>
        </PopupPanel>
    );
}

function ViewCaseDataPopup({
    processId,
    caseId,
    showPopup,
    setShowPopup,
}: {
    processId: number;
    caseId: string;
    showPopup: boolean;
    setShowPopup: (show: boolean) => void;
}) {
    return (
        <PopupPanel
            show={showPopup}
            showBackdrop={false}
            handleClose={() => setShowPopup(false)}
            className="w-full h-full overflow-y-auto"
        >
            <PopupHeader handleClose={() => setShowPopup(false)}>Case: {caseId}</PopupHeader>
            <EventLogCaseDataTable processId={processId} caseId={caseId} />
        </PopupPanel>
    );
}

function EventLogCaseDataTable({ processId, caseId }: { processId: number; caseId: string }) {
    const eventLogData = useEventLogData(processId, caseId);

    if (!eventLogData.data) {
        return <p>Loading...</p>;
    }

    const columns = eventLogData.data.attributes.map((attr) => ({ Header: attr.name, accessor: attr.name }));

    return (
        <div className="flex flex-col border-b p-2">
            <p className="font-bold">Case ID: {caseId}</p>
            <br />
            <Table columns={columns} data={eventLogData.data.data} filtering />
        </div>
    );
}
