import { Rule } from '../types/Rule';
import DisplayComplianceRules from './DisplayComplianceRules';
import { Disclosure, Transition } from '@headlessui/react';
import { ChevronUpIcon } from '@heroicons/react/24/outline';
import { useProcess } from '../hooks/processHooks';
import { useControl } from '../hooks/riskObligationHooks';
import { ComplianceRule } from '../types/ComplianceRule';
import { useState } from 'react';

type Props = {
    type: string;
    id: number;
    complianceRule: ComplianceRule;
    handleDelete: (id : number) => void;
};

export default function DisclosureComplianceRule({ type, id, complianceRule, handleDelete }: Props) {
    if (type == 'Control') {
        const { data: process } = useProcess(id);
        return (
            <DisclosureHelper
                header={process?.name}
                description={''}
                complianceRule={complianceRule}
                handleDelete={handleDelete}
            />
        );
    } else {
        const { data: control } = useControl(id);
        return (
            <DisclosureHelper
                header={control?.name}
                description={control?.description}
                complianceRule={complianceRule}
                handleDelete={handleDelete}
            />
        );
    }
}

function DisclosureHelper({
    header,
    description,
    complianceRule,
    handleDelete,
}: {
    header?: string;
    description?: string;
    complianceRule?: ComplianceRule;
    handleDelete: (id : number) => void;
}) {
    const [hide, setHide] = useState(false);

    return (
        <Disclosure>
            {({ open }) => (
                <>
                    <Disclosure.Button className="flex w-full border items-center justify-between rounded-md bg-soft-blue-100 px-4 py-2 text-left text-sm font-medium text-maroon-900 hover:bg-soft-blue-200 focus:outline-none focus-visible:ring focus-visible:ring-soft-blue-500 focus-visible:ring-opacity-75">
                        <div>
                            <p>
                                <strong>{header}</strong>: {description}
                            </p>
                        </div>
                        <ChevronUpIcon className={`${open ? 'rotate-180 transform' : ''} h-5 w-5 text-maroon-500`} />
                    </Disclosure.Button>
                    <Transition
                        enter="transition duration-100 ease-out"
                        enterFrom="transform scale-95 opacity-0"
                        enterTo="transform scale-100 opacity-100"
                        leave="transition duration-75 ease-out"
                        leaveFrom="transform scale-100 opacity-100"
                        leaveTo="transform scale-95 opacity-0"
                    >
                        <Disclosure.Panel
                            className={`px-4 pb-2 text-sm grid gap-y-4 grid-cols-1 items-center border mx-3 py-4`}
                        >
                            <DisplayComplianceRules complianceRule={complianceRule} handleDelete={handleDelete} />
                        </Disclosure.Panel>
                    </Transition>
                </>
            )}
        </Disclosure>
    );
}
