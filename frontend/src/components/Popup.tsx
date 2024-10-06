import { Dialog, Transition } from '@headlessui/react';
import React, { ReactNode, useEffect, useState } from 'react';
import { XMarkIcon } from '@heroicons/react/24/outline';
import Button from './Button';

export function PopupPanel({
    children,
    show,
    className = '',
    showBackdrop = true,
    handleClose,
}: {
    children: ReactNode;
    show: boolean;
    className?: string;
    showBackdrop?: boolean;
    handleClose: () => void;
}) {
    return (
        <div className="fixed z-50">
            <Transition appear show={show} as={React.Fragment}>
                <Dialog as="div" className="relative z-10" onClose={handleClose}>
                    {showBackdrop && (
                        <Transition.Child
                            as={React.Fragment}
                            enter="ease-out duration-300"
                            enterFrom="opacity-0"
                            enterTo="opacity-100"
                            leave="ease-in duration-200"
                            leaveFrom="opacity-100"
                            leaveTo="opacity-0"
                        >
                            <div className={'fixed inset-0 bg-gray-300 bg-opacity-60'} />
                        </Transition.Child>
                    )}
                    <div className="flex min-h-full items-center justify-center p-4 text-center fixed inset-0 overflow-y-auto">
                        <Transition.Child
                            as={React.Fragment}
                            enter="ease-out duration-300"
                            enterFrom="opacity-0 scale-95"
                            enterTo="opacity-100 scale-100"
                            leave="ease-in duration-200"
                            leaveFrom="opacity-100 scale-100"
                            leaveTo="opacity-0 scale-95"
                        >
                            <Dialog.Panel
                                className={
                                    'transform bg-white text-left align-middle shadow-xl transition-all rounded-md ' +
                                    className
                                }
                            >
                                {children}
                            </Dialog.Panel>
                        </Transition.Child>
                    </div>
                </Dialog>
            </Transition>
        </div>
    );
}

export function PopupHeader({ children, handleClose }: { children: ReactNode; handleClose: () => void }) {
    return (
        <Dialog.Title as="h3" className="font-brand text-sm p-2 h-9 bg-popup-header flex justify-between">
            {children}
            <XMarkIcon
                className="h-4 w-4 m-1 text-gray-700 hover:text-black hover:cursor-pointer"
                onClick={handleClose}
            />
        </Dialog.Title>
    );
}

export function PopupFooter({ children }: { children: ReactNode }) {
    return <div className="bg-popup-footer p-2 flex w-full justify-end">{children}</div>;
}

type PopupGroupContentProps<T extends object> = {
    item: T;
    updateItem: (item: T) => void;
};

type PopupGroupProps<T extends object> = {
    data: T[];
    PopupContent: React.FC<PopupGroupContentProps<T>>;
    onNext?: (item: T) => void;
    onFinishedAll?: (items: T[]) => void;
    handleClose: () => void;
    getPopupHeaderText: (item: T, index: number) => string;
    disableBacktracking?: boolean;
};

/**
 * A Popup Group component that lets you create a set of consecutive popups to perform
 * operations on multiple objects
 * @param data the array of objects of type T you wish to perform actions on
 * @param PopupContent react component that accepts the item and a setter function as props
 * @param onNext callback function that runs every time user clicks next - takes in current item as input
 * @param onFinishedAll callback function that runs when user clicks done - takes in all items as input
 * @param handleClose callback function for closing the popup (set your data variable to null)
 * @param getPopupHeaderText generate the header text using the current index and item
 * @param disableBacktracking disable user ability to go backwards to previous popups
 * @returns ReactNode
 */
export function PopupGroup<T extends object>({
    data,
    PopupContent,
    onNext,
    onFinishedAll,
    getPopupHeaderText,
    handleClose,
    disableBacktracking,
}: PopupGroupProps<T>) {
    const [index, setIndex] = useState(0);
    const [items, setItems] = useState(data);
    const closeGroup = () => {
        setIndex(0);
        setItems([]);
        handleClose();
    };

    useEffect(() => {
        setItems(data);
    }, [data]);

    return (
        <PopupPanel show={items.length != 0} handleClose={closeGroup}>
            <PopupHeader handleClose={closeGroup}>{getPopupHeaderText(items[index], index)}</PopupHeader>

            <PopupContent
                item={items && items[index]}
                updateItem={(newItem: T) => {
                    setItems(
                        items
                            .slice(0, index)
                            .concat([newItem])
                            .concat(items.slice(index + 1, undefined)),
                    );
                }}
            />
            <PopupFooter>
                <Button className="mr-2" text={onNext ? 'Exit' : 'Cancel'} onClick={closeGroup} />
                {index != 0 && !disableBacktracking && (
                    <Button
                        text="Back"
                        className="mr-2"
                        onClick={() => {
                            setIndex(index - 1);
                        }}
                    />
                )}
                <Button
                    text={
                        index == items.length - 1
                            ? onNext
                                ? 'Save & Complete'
                                : 'Save'
                            : onNext
                            ? 'Save & Continue'
                            : 'Next'
                    }
                    onClick={() => {
                        if (onNext) onNext(items[index]);
                        if (index == items.length - 1) {
                            if (onFinishedAll) onFinishedAll(items);
                            closeGroup();
                        } else {
                            setIndex(index + 1);
                        }
                    }}
                />
            </PopupFooter>
        </PopupPanel>
    );
}
