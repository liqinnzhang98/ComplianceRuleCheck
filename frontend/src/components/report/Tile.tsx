export default function Tile({
    title,
    number,
    totalCases,
    displayMode,
}: {
    title: string;
    number?: number;
    totalCases?: number;
    displayMode: string;
}) {
    let displayNumber;
    let percentageValue;

    if (totalCases != undefined && number != undefined) {
        percentageValue = ((number / totalCases) * 100).toFixed(2) + '%';

        if (displayMode === 'Show Percentage') {
            displayNumber = percentageValue;
        } else if (displayMode === 'Show Value') {
            displayNumber = number;
        } else if (displayMode === 'Show Both') {
            displayNumber = `${number} (${percentageValue})`;
        }
    }

    return (
        <div className="w-full bg-white border border-solid border-black border-t-black border-l-8 border-l-oceanic-600 mb-10 mr-2 inline-block h-28 rounded-md">
            <div className="text-xs mr-2 ml-2 overflow-hidden overflow-ellipsis whitespace-nowrap" title={title}>
                {title}
            </div>
            <div className="text-lg mt-5">{displayNumber}</div>
        </div>
    );
}
