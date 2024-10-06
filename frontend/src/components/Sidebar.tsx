import { Link, useLocation, useNavigate } from 'react-router-dom';

import * as React from 'react';
import { FolderPlusIcon, XCircleIcon, CheckCircleIcon } from '@heroicons/react/24/outline';
import Loader from '../assets/loader.svg';
import { Process } from '../types/Process';
import { useQueryClient } from 'react-query';
import Button from './Button';
import { PopupPanel, PopupHeader, PopupFooter } from './Popup';
import Input from './form/Input';
import { useCreateProcess } from '../hooks/processHooks';

export type SidebarListItem = {
    icon?: string;
    text: string;
    url: string;
};

function SidebarListItem({ listItem, isFirst }: { listItem: SidebarListItem; isFirst: boolean }) {
    const location = useLocation();

    return (
        <Link to={listItem.url}>
            <div
                className={
                    'flex py-2 px-4 items-center hover:bg-primary-lighter ' +
                    (isFirst ? 'pt-4 ' : '') +
                    (location.pathname == listItem.url ? 'bg-primary-lighter' : '')
                }
            >
                <img className="h-6 mr-1.5" src={listItem.icon} alt={'icon for ' + listItem.text} />
                {listItem.text}
            </div>
        </Link>
    );
}

function CreateProcessPopup({
    open,
    handleClose,
    listItems,
}: {
    open: boolean;
    handleClose: () => void;
    listItems: SidebarListItem[];
}) {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const [processName, setProcessName] = React.useState<string>('');

    const mutation = useCreateProcess();

    return (
        <PopupPanel show={open} handleClose={handleClose}>
            <PopupHeader handleClose={handleClose}>Create new Process</PopupHeader>
            <div className="mr-5 mt-2 w-[28em]">
                <Input
                    id="new_process"
                    name="new_process"
                    label="Process Name"
                    type="text"
                    placeholder="New process"
                    onChange={(event) => setProcessName(event.target.value)}
                />
            </div>
            <PopupFooter>
                <Button
                    key="OK"
                    text="OK"
                    Icon={CheckCircleIcon}
                    onClick={() => {
                        mutation.mutate(
                            {
                                id: 1,
                                name: processName,
                            },
                            {
                                onSuccess: (data) => {
                                    const oldItems: Process[] = listItems
                                        .filter((item) => item.url.includes('processes'))
                                        .map((item) => {
                                            return { id: parseInt(item.url.split('/').at(-1) || '0'), name: item.text };
                                        });
                                    queryClient.setQueryData(['processes'], [...oldItems, data]);
                                    navigate('/processes/' + data.id);
                                },
                                onError: (error) => {
                                    console.error('Error:', error);
                                },
                            },
                        );
                        handleClose();
                    }}
                    className="mr-2"
                />
                <Button key="Cancel" text="Cancel" Icon={XCircleIcon} onClick={handleClose} />
            </PopupFooter>
        </PopupPanel>
    );
}

function Sidebar({
    className,
    listItems,
    isLoading,
}: {
    className?: string;
    listItems: SidebarListItem[];
    isLoading: boolean;
}) {
    const [open, setOpen] = React.useState(false);

    return (
        <div className={'overflow-y-auto border-[1px] rounded-lg border-grey-50 text-sm flex flex-col ' + className}>
            <div className="overflow-y-auto">
                {listItems.map((listItem, index) => (
                    <SidebarListItem isFirst={index == 0} key={index} listItem={listItem} />
                ))}
            </div>
            {isLoading && <img className="h-10 mx-auto mt-2" src={Loader} alt="loading animation" />}
            <div className="flex-grow"></div>
            <Button
                text="Create Process Folder"
                Icon={FolderPlusIcon}
                onClick={() => setOpen(true)}
                className="rounded-none"
                size="lg"
            />
            <CreateProcessPopup open={open} handleClose={() => setOpen(false)} listItems={listItems} />
        </div>
    );
}

export default Sidebar;
