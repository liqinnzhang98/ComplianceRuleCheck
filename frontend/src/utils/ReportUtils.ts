import { Report } from '../types/Report';

export function getLatestReportForProcess(reports: Report[], processName: string) {
    return reports
        ?.filter((report) => report.process.name == processName)
        .reduce((prev, current) => (prev.runAt > current.runAt ? prev : current));
}

export function getLatestReport(reports: Report[]) {
    return reports.reduce((prev, current) => (prev.runAt > current.runAt ? prev : current), {} as Report);
}

export function getReportByProcessAndTimestamp(reports: Report[], processId: number, runAt: string) {
    return reports?.filter((report) => report.process.id == processId && report.runAt.toLocaleString() == runAt);
}

export function getProcessesFromReport(reports: Report[]) {
    return reports?.map((r) => r.process.name).filter((value, index, array) => array.indexOf(value) === index);
}

export function getTimestampsForProcessReports(reports: Report[], processId: number) {
    return reports?.filter((report) => report.process.id == processId).map((report) => report.runAt);
}
