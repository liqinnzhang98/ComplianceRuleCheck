import { Combobox, Transition } from '@headlessui/react';
import { ChevronDownIcon } from '@heroicons/react/24/outline';
import { Fragment, forwardRef, useState } from 'react';
import { useEffect } from 'react';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    options: string[];
    onSelectChange?: (val: string) => void;
}

function AutoComplete(
    { label, options, onSelectChange, value, ...rest }: InputProps,
    ref: React.LegacyRef<HTMLInputElement>,
) {
    const [selectedOption, setSelectedOption] = useState('');
    const [query, setQuery] = useState('');

    const filteredOptions =
        query === ''
            ? options
            : options.filter((option) => {
                  return option.toLowerCase().includes(query.toLowerCase());
              });

    // update control type every time
    useEffect(() => {
        if (value !== undefined) {
            if (typeof value === 'string') {
                setSelectedOption(value);
            }
        }
    }, [value]);
    return (
        <Combobox
            value={selectedOption}
            onChange={(val) => {
                setSelectedOption(val || '');
                setQuery('');
                onSelectChange && onSelectChange(val || '');
            }}
            name={rest.name}
            nullable
        >
            <div className="w-full flex items-center">
                {label && <Combobox.Label className="text-right m-3 w-1/4">{label}:</Combobox.Label>}
                <div className="w-full flex-1 items-center relative">
                    <Combobox.Input
                        className="border border-solid border-grey-100 hover:border-oceanic-600 focus:border-oceanic-600 rounded-md p-1 flex-1 w-full"
                        onChange={(event) => {
                            setQuery(event.target.value);
                            rest.onChange && rest.onChange(event);
                        }}
                        placeholder={label}
                        defaultValue={rest.defaultValue}
                    />
                    <Combobox.Button className="absolute inset-y-0 right-0 flex items-center pr-2">
                        <ChevronDownIcon className="h-5 w-5 text-gray-400" aria-hidden="true" />{' '}
                    </Combobox.Button>
                    <Transition
                        as={Fragment}
                        leave="transition ease-in duration-100"
                        leaveFrom="opacity-100"
                        leaveTo="opacity-0"
                    >
                        <Combobox.Options className="absolute mt-1 max-h-60 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm z-50">
                            {[...filteredOptions, ...(query.length > 0 ? [query] : [])].map((option, i) => (
                                <Combobox.Option
                                    key={i}
                                    value={option}
                                    className={({ active }) =>
                                        `relative cursor-default select-none py-2 px-4 ${
                                            active ? 'bg-oceanic-600 text-white' : 'text-gray-900'
                                        }`
                                    }
                                >
                                    {({ selected }) => (
                                        <span className={`block truncate ${selected ? 'font-medium' : 'font-normal'}`}>
                                            {option}
                                        </span>
                                    )}
                                </Combobox.Option>
                            ))}
                        </Combobox.Options>
                    </Transition>
                </div>
            </div>
        </Combobox>
    );
}

export default forwardRef(AutoComplete);
