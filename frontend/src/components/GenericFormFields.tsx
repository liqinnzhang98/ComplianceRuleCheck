import { useState, useEffect } from 'react';

export const TextField = ({
    label,
    value,
    onChange,
}: {
    label: string;
    value: string;
    onChange: (v: string) => void;
}) => {
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        onChange(event.target.value);
    };

    return (
        <div>
            <label htmlFor="text-input" className="block text-sm font-medium text-gray-700">
                {label}
            </label>
            <input
                id="text-input"
                name="text-input"
                type="text"
                value={value}
                onChange={handleChange}
                className="mt-1 block w-full rounded-md border border-black h-8 px-3 shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            />
        </div>
    );
};

export const TimeField = ({ label, initialValue }: { label: string; initialValue: string }) => {
    const [value, setValue] = useState(initialValue);

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setValue(event.target.value);
    };

    return (
        <div>
            <label htmlFor="time-input" className="block text-sm font-medium text-gray-700">
                {label}
            </label>
            <input
                id="time-input"
                name="time-input"
                type="time"
                value={value}
                onChange={handleChange}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                step="1"
            />
        </div>
    );
};

export const DurationField = ({
    label,
    initialValue,
    onDurationChange,
}: {
    label: string;
    initialValue: string;
    onDurationChange: (value: string) => void;
}) => {
    const [selectedOption, setSelectedOption] = useState<string>("SECOND");
    const [valueInput, setValueInput] = useState<string>(initialValue);

    useEffect(() => {
        const durationString = `${valueInput} ${selectedOption}`;
        onDurationChange(durationString);
    }, [valueInput, selectedOption]);

    const handleOptionChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedOption(event.target.value);
    };

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setValueInput(event.target.value);
    };

    return (
        <div>
            <label htmlFor="duration-input" className="block text-sm font-medium text-gray-700">
                {label}
            </label>
            <input
                id="duration-input"
                name="duration-input"
                type="number"
                onChange={handleInputChange}
                className="border pl-4 p-2 mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            />
            <select
                value={selectedOption}
                onChange={handleOptionChange}
                className="w-full p-2 border"
            >
                <option value="SECOND">SECOND(S)</option>
                <option value="MINUTE">MINUTE(S)</option>
                <option value="HOUR">HOUR(S)</option>
                <option value="DAY">DAY(S)</option>
                <option value="WEEK">WEEK(S)</option>
                <option value="MONTH">MONTH(S)</option>
                <option value="YEAR">YEAR(S)</option>
            </select>
        </div>
    );
};

interface CountdownProps {
    duration: string;
}

export const Countdown: React.FC<CountdownProps> = ({ duration }) => {
    const [timeRemaining, setTimeRemaining] = useState<string>(duration);

    useEffect(() => {
        let durationInSeconds = convertToSeconds(duration);

        const interval = setInterval(() => {
            if (durationInSeconds > 0) {
                durationInSeconds--;
                setTimeRemaining(convertToDuration(durationInSeconds));
            } else {
                clearInterval(interval);
            }
        }, 1000);

        return () => clearInterval(interval);
    }, [duration]);

    const convertToSeconds = (duration: string): number => {
        const [days, hours, minutes, seconds] = duration.split(':').map(Number);
        return days * 86400 + hours * 3600 + minutes * 60 + seconds;
    };

    const convertToDuration = (seconds: number): string => {
        const days = Math.floor(seconds / 86400);
        seconds %= 86400;
        const hours = Math.floor(seconds / 3600);
        seconds %= 3600;
        const minutes = Math.floor(seconds / 60);
        seconds %= 60;
        return `${days}:${hours}:${minutes}:${seconds}`;
    };

    return (
        <div>
            <span>Countdown: {timeRemaining}</span>
        </div>
    );
};

export const ColumnBreak = () => {
    return <div className="basis-full w-0"></div>;
};

export const DropdownField = ({
    label,
    options,
    selectedOption,
    onOptionChange,
}: {
    label: string;
    options: string[];
    selectedOption: string;
    onOptionChange: (option: string) => void;
}) => {
    const handleChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        onOptionChange(event.target.value);
    };

    return (
        <div>
            <label htmlFor="dropdown-input" className="block text-sm font-medium text-gray-700">
                {label}
            </label>
            <select
                id="dropdown-input"
                name="dropdown-input"
                value={selectedOption}
                onChange={handleChange}
                className="mt-1 block rounded-md border-gray-300 shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
            >
                {options &&
                    options.map((option) => {
                        return (
                            <option key={option} value={option}>
                                {option}
                            </option>
                        );
                    })}
            </select>
        </div>
    );
};

export const CheckboxField = ({
    label,
    checked,
    onCheckChange,
}: {
    label: string;
    checked: boolean;
    onCheckChange: (checked: boolean) => void;
}) => {
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        onCheckChange(event.target.checked);
    };

    return (
        <div className="flex items-center">
            <input
                id="checkbox-input"
                name="checkbox-input"
                type="checkbox"
                checked={checked}
                onChange={handleChange}
                className="rounded border-gray-300 text-indigo-600 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
            />
            <label htmlFor="checkbox-input" className="ml-2 block text-sm text-gray-900">
                {label}
            </label>
        </div>
    );
};
