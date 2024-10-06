import React from 'react';
import { ApexOptions } from 'apexcharts';
import ReactApexChart from 'react-apexcharts';
import { Report } from '../../types/Report';
import { useTemplates } from '../../hooks/complianceRuleHooks';
import { getComplianceRuleDescription } from '../../utils/complianceRule';

type FormatterParams = {
    seriesIndex: number;
    dataPointIndex: number;
};

type OptsType = {
    dataPointIndex: number;
};

export default function ParetoCharts({ selectedReport }: { selectedReport?: Report | null }) {
    return (
        <div>
            <div className="border border-gray-300 mb-4">
                <ParetoChartByControl selectedReport={selectedReport} />
            </div>
            <div className="border border-gray-300">
                <ParetoChartByRule selectedReport={selectedReport} />
            </div>
        </div>
    );
}

function ParetoChartByControl({ selectedReport }: { selectedReport?: Report | null }) {
    if (selectedReport?.report == null) {
        return <p>No data available for the report</p>;
    }

    const { controlIds, controlNames, numberOfApplication, cumulativeData } = getRequiredDataForControl({
        selectedReport,
    });

    const options: ApexOptions = {
        chart: {
            type: 'line',
            height: 350,
            toolbar: {
                show: false,
            },
            zoom: {
                enabled: false,
            },
        },
        title: { text: 'Pareto Chart - Controls' },
        series: [
            {
                name: 'Number of Applications',
                type: 'column',
                data: numberOfApplication,
                color: '#78b7d1',
            },
            {
                name: 'Cumulative Percentage',
                type: 'line',
                data: cumulativeData,
                color: '#fa8133',
            },
        ],
        xaxis: {
            categories: controlIds,
            tooltip: {
                enabled: false,
            },
            labels: {
                style: {
                    fontWeight: 600,
                },
            },
        },
        yaxis: [
            {
                title: {
                    text: 'Number of Applications',
                },
                max: selectedReport?.report?.totalCases,
                tickAmount: 5,
                labels: {
                    formatter: (val: number) => `${val.toFixed(0)}`,
                    style: {
                        fontWeight: 600,
                    },
                },
            },
            {
                opposite: true,
                title: {
                    text: 'Cumulative Percentage',
                },
                max: 100,
                tickAmount: 5,
                labels: {
                    formatter: (val: number) => `${val.toFixed(0)}%`,
                    style: {
                        fontWeight: 600,
                    },
                },
            },
        ],
        legend: {
            show: true,
        },
        plotOptions: {
            bar: {
                columnWidth: '40%',
            },
        },
        tooltip: {
            enabled: true,
            y: {
                formatter: function (val: number, { seriesIndex }: FormatterParams) {
                    if (seriesIndex === 0) {
                        return val.toFixed(0);
                    } else {
                        return `${val.toFixed(0)}%`;
                    }
                },
            },
            x: {
                show: true,
                formatter: function (val: number, opts: OptsType) {
                    return controlNames[opts.dataPointIndex];
                },
            },
        },
    };

    return <ReactApexChart options={options} series={options.series} type="line" height={350} />;
}

function ParetoChartByRule({ selectedReport }: { selectedReport?: Report | null }) {
    if (selectedReport?.report == null) {
        return <p>No data available for the report</p>;
    }

    const {
        ruleIds = [],
        descriptions = [],
        numberOfApplication = [],
        cumulativeData = [],
    } = getRequiredDataForRule({ selectedReport });

    const options: ApexOptions = {
        chart: {
            type: 'line',
            height: 350,
            toolbar: {
                show: false,
            },
            zoom: {
                enabled: false,
            },
        },
        title: { text: 'Pareto Chart - Rules' },
        series: [
            {
                name: 'Number of Applications',
                type: 'column',
                data: numberOfApplication,
                color: '#78b7d1',
            },
            {
                name: 'Cumulative Percentage',
                type: 'line',
                data: cumulativeData,
                color: '#fa8133',
            },
        ],
        xaxis: {
            categories: ruleIds,
            tooltip: {
                enabled: false,
            },
            labels: {
                style: {
                    fontWeight: 600,
                },
            },
        },
        yaxis: [
            {
                title: {
                    text: 'Number of Applications',
                },
                max: selectedReport?.report?.totalCases,
                tickAmount: 5,
                labels: {
                    formatter: (val: number) => `${val.toFixed(0)}`,
                    style: {
                        fontWeight: 600,
                    },
                },
            },
            {
                opposite: true,
                title: {
                    text: 'Cumulative Percentage',
                },
                max: 100,
                tickAmount: 5,
                labels: {
                    formatter: (val: number) => `${val.toFixed(0)}%`,
                    style: {
                        fontWeight: 600,
                    },
                },
            },
        ],
        legend: {
            show: true,
        },
        plotOptions: {
            bar: {
                columnWidth: '40%',
            },
        },
        tooltip: {
            enabled: true,
            y: {
                formatter: function (val: number, { seriesIndex }: FormatterParams) {
                    if (seriesIndex === 0) {
                        return val.toFixed(0);
                    } else {
                        return `${val.toFixed(0)}%`;
                    }
                },
            },
            x: {
                show: true,
                formatter: function (val: number, opts: OptsType) {
                    return descriptions[opts.dataPointIndex];
                },
            },
        },
    };

    return <ReactApexChart options={options} series={options.series} type="line" height={350} />;
}

// get data required for pareto chart based on control
function getRequiredDataForControl({ selectedReport }: { selectedReport?: Report | null }) {
    if (!selectedReport?.report?.breaches) {
        return { controlIds: [], controlNames: [], numberOfApplication: [], cumulativeData: [] };
    }
    const controlIds = selectedReport.report.breaches.map((breach) => `C${breach.control.id}`);
    const unsortedBreaches: { [key: number]: string[] } = {};
    const ruleToControlName: { [key: number]: string } = {};

    selectedReport.report.breaches.forEach((breach) => {
        const ruleId = breach.rule.id;
        if (!unsortedBreaches[ruleId]) {
            unsortedBreaches[ruleId] = [];
        }
        unsortedBreaches[ruleId].push(...breach.breachedCaseIds);

        ruleToControlName[ruleId] = breach.control.name;
    });

    const entries = Object.entries(unsortedBreaches);
    const sortedEntries = entries.sort((a, b) => b[1].length - a[1].length);
    const controlNames = sortedEntries.map(
        ([key]) => ruleToControlName[parseInt(key)] || `Unknown Control with Rule ${key}`,
    );
    const numberOfApplication = sortedEntries.map(([, arr]) => arr.length);

    const total = numberOfApplication.reduce((acc, val) => acc + val, 0);
    let cumulative = 0;
    const cumulativeData = numberOfApplication.map((val) => {
        cumulative += (val / total) * 100;
        return parseFloat(cumulative.toFixed(2));
    });

    return { controlIds, controlNames, numberOfApplication, cumulativeData };
}

// get data required for pareto chart based on rule
function getRequiredDataForRule({ selectedReport }: { selectedReport?: Report | null }) {
    const { data: templates } = useTemplates();

    if (!selectedReport?.report?.breaches) {
        return { ruleIds: [], description: [], numberOfApplication: [], cumulativeData: [] };
    }

    const descriptions = (
        selectedReport.report.breaches.map((breach) => getComplianceRuleDescription(breach.rule.rules, templates)) || []
    ).filter((desc): desc is string => !!desc);
    const ruleIds = selectedReport.report.breaches.map((breach) => `Rule ${breach.rule.id}`);
    const unsortedBreaches: { [key: number]: string[] } = {};

    selectedReport.report.breaches.forEach((breach) => {
        const ruleId = breach.rule.id;
        if (!unsortedBreaches[ruleId]) {
            unsortedBreaches[ruleId] = [];
        }
        unsortedBreaches[ruleId].push(...breach.breachedCaseIds);
    });

    const entries = Object.entries(unsortedBreaches);
    const sortedEntries = entries.sort((a, b) => b[1].length - a[1].length);
    const numberOfApplication = sortedEntries.map(([, arr]) => arr.length);
    const total = numberOfApplication.reduce((acc, val) => acc + val, 0);
    let cumulative = 0;
    const cumulativeData = numberOfApplication.map((val) => {
        cumulative += (val / total) * 100;
        return parseFloat(cumulative.toFixed(2));
    });

    return { ruleIds, descriptions, numberOfApplication, cumulativeData };
}
