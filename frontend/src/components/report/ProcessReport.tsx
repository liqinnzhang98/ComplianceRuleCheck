import { useEffect, useState } from 'react';
import { useFullReport, useReports } from '../../hooks/reportHooks';
import { Report } from '../../types/Report';
import Select from '../form/Select';
import Loader from '../../assets/loader.svg';
import Tile from './Tile';
import ReportTable from './ReportTable';
import {
    getLatestReport,
    getLatestReportForProcess,
    getProcessesFromReport,
    getReportByProcessAndTimestamp,
    getTimestampsForProcessReports,
} from '../../utils/ReportUtils';
import ParetoCharts from './ParetoCharts';

export default function ProcessReport() {
    const { data: reports, isLoading } = useReports();
    const [selectedReport, setSelectedReport] = useState<Report>();
    const { data: report } = useFullReport(selectedReport?.process?.id, selectedReport?.id);
    const [displayMode, setDisplayMode] = useState('Show Value');

    useEffect(() => {
        if (reports && !selectedReport) setSelectedReport(getLatestReport(reports || []));
    }, [reports]);

    if (isLoading || !reports) {
        return (
            <div className="h-full flex items-center justify-center">
                <img className="h-20" src={Loader} alt="loading animation" />
            </div>
        );
    }

    if (!selectedReport || Object.keys(selectedReport).length === 0) {
        return <p>No report available</p>;
    }

    return (
        <>
            <div className="flex">
                <Select
                    value={selectedReport?.process.name}
                    onChange={(e) => setSelectedReport(getLatestReportForProcess(reports || [], e.target.value))}
                >
                    {getProcessesFromReport(reports || []).map((process, index) => (
                        <option key={index}>{process}</option>
                    ))}
                </Select>
                <Select
                    label="Report Name"
                    value={selectedReport?.runAt.toLocaleString()}
                    onChange={(e) =>
                        setSelectedReport(
                            getReportByProcessAndTimestamp(
                                reports,
                                selectedReport ? selectedReport?.process.id : 0,
                                e.target.value,
                            )[0],
                        )
                    }
                >
                    {getTimestampsForProcessReports(reports || [], selectedReport?.process.id || 0).map(
                        (timestamp, index) => (
                            <option key={index}>{timestamp.toLocaleString()}</option>
                        ),
                    )}
                </Select>
                <Select label="Report Format" value={displayMode} onChange={(e) => setDisplayMode(e.target.value)}>
                    <option>Show Value</option>
                    <option>Show Percentage</option>
                    <option>Show Both</option>
                </Select>
            </div>
            {/* <p>{JSON.stringify(report)}</p> */}
            <div className="flex">
                <Tile
                    title="No. Never Breached"
                    number={
                        report?.report?.totalCases !== undefined && report?.report?.totalBreachedCases !== undefined
                            ? report.report.totalCases - report.report.totalBreachedCases
                            : undefined
                    }
                    totalCases={report?.report?.totalCases}
                    displayMode={displayMode}
                />

                <Tile
                    title="Max Breaches per Case"
                    number={report?.report?.stats.max.value}
                    totalCases={report?.report?.totalCases}
                    displayMode={displayMode}
                />
                <Tile
                    title="Min Breaches per Case"
                    number={report?.report?.stats.min.value}
                    totalCases={report?.report?.totalCases}
                    displayMode={displayMode}
                />
                <Tile
                    title="Average Breaches per Case"
                    number={parseFloat(report?.report?.stats.average.toFixed(2) as any)}
                    totalCases={report?.report?.totalCases}
                    displayMode={displayMode}
                />
                <Tile
                    title="No. Breached Cases"
                    number={report?.report?.totalBreachedCases}
                    totalCases={report?.report?.totalCases}
                    displayMode={displayMode}
                />
                <Tile
                    title="Total No. of Cases"
                    number={report?.report?.totalCases}
                    totalCases={report?.report?.totalCases}
                    displayMode={displayMode}
                />
            </div>
            <div className="flex items-start">
                <div className="flex-1 mr-1">
                    <ReportTable selectedReport={report} />
                </div>
                <div className="flex-1 ml-1">
                    <ParetoCharts selectedReport={report} />
                </div>
            </div>
        </>
    );
}
