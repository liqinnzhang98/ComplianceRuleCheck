import { Combobox, Listbox, Transition } from '@headlessui/react';
import { CheckIcon, ChevronDownIcon, ChevronUpDownIcon } from '@heroicons/react/24/outline';
import { Fragment, useState, useRef } from 'react';

type props = {
    label: string | undefined;
    options: string[] | undefined;
    selectedOption: string;
    onOptionChange: (option: string) => void;
};

export default function Select({ label, options, selectedOption, onOptionChange }: props) {
    const hasOptions = options && options.length > 0;

    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const dropdownRef = useRef(null);

    const closeDropdown = () => {
        setIsDropdownOpen(false);
    };

    const handleMouseEnter = () => {
        if (hasOptions) {
            setIsDropdownOpen(true);
        }
    };

    const handleMouseLeave = () => {
        setIsDropdownOpen(false);
    };

    return (
        <div
            className="apromore-autocomplete"
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
        >
            {label && <label className="block text-sm font-medium text-gray-700"> {label} </label>}
            {hasOptions && (
                <Listbox value={selectedOption} onChange={onOptionChange}>
                    <div className="relative mt-1">
                        <Listbox.Button
                            className="relative w-full cursor-default rounded-lg bg-white py-2 pl-3 pr-10 text-left shadow-md focus:outline-none focus-visible:border-maroon-500 focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75 focus-visible:ring-offset-2 focus-visible:ring-offset-maroon-300 sm:text-sm"
                            onMouseEnter={handleMouseEnter}
                        >
                            <span className="block truncate">{selectedOption || 'Select...'}</span>
                            <span className="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2">
                                <ChevronUpDownIcon className="h-5 w-5 text-gray-400" aria-hidden="true" />
                            </span>
                        </Listbox.Button>
                        <Transition
                            as={Fragment}
                            leave="transition ease-in duration-100"
                            leaveFrom="opacity-100"
                            leaveTo="opacity-0"
                            show={isDropdownOpen}
                        >
                            <Listbox.Options
                                ref={dropdownRef}
                                className="absolute mt-1 max-h-60 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm z-50"
                                onMouseLeave={closeDropdown}
                            >
                                {options.map((option, index) => (
                                    <Listbox.Option
                                        key={index}
                                        className={({ active }) =>
                                            `relative cursor-default select-none py-2 pl-10 pr-4 w-full ${
                                                active ? 'bg-amber-100 text-amber-900' : 'text-gray-900'
                                            }`
                                        }
                                        value={option}
                                    >
                                        {({ selected }) => (
                                            <>
                                                <span
                                                    className={`block truncate ${
                                                        selected ? 'font-medium' : 'font-normal'
                                                    }`}
                                                >
                                                    {option}
                                                </span>
                                                {selected ? (
                                                    <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-amber-600">
                                                        <CheckIcon className="h-5 w-5" aria-hidden="true" />
                                                    </span>
                                                ) : null}
                                            </>
                                        )}
                                    </Listbox.Option>
                                ))}
                            </Listbox.Options>
                        </Transition>
                    </div>
                </Listbox>
            )}
        </div>
    );
}