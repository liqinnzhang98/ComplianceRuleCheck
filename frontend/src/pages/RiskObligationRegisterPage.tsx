import ActionBar, { Seperator } from '../components/ActionBar';
import Breadcrumb from '../components/Breadcrumb';
import { useForm } from 'react-hook-form';
import SyncIcon from '../assets/icons/sync.svg';
import UploadIcon from '../assets/icons/upload.svg';
import FileInfo from '@heroicons/react/24/outline/EyeIcon';
import LogIcon from '@heroicons/react/24/outline/WrenchScrewdriverIcon';
import EditIcon from '@heroicons/react/24/outline/AdjustmentsHorizontalIcon';
import { Control } from '../types/Control';
import * as React from 'react';
import ControlPopUp from '../components/ControlPopUp';
import FileUploadPopup from '../components/FileUploadPopup';
import { RiskObligation, RiskObligationTableColumns } from '../types/RiskObligation';
import { useEffect, useState } from 'react';
import { XCircleIcon, CheckCircleIcon, TrashIcon, PlusIcon } from '@heroicons/react/24/outline';
import Table from '../components/Table';
import { PopupPanel, PopupHeader, PopupFooter } from '../components/Popup';
import Button from '../components/Button';
import Input from '../components/form/Input';
import { useQueryClient } from 'react-query';
import { CellProps, Column } from 'react-table';
import Select from '../components/form/Select';
import { FillComplianceTemplatePopup } from '../components/FillComplianceTemplatePopup';
import { ViewComplianceRulesByControlPopup } from '../components/ViewComplianceRulesPopup';
import ControlMapTemplatePopup from '../components/ControlMapTemplatePopup';
import { PromptPopup } from '../components/PromptPopup';
import {
    useCreateControl,
    useCreateRiskObligation,
    useRiskObligations,
    useUploadRiskObligationData,
} from '../hooks/riskObligationHooks';
import { getControls } from '../api/riskObligation/riskObligationQueries';
import { updateControl } from '../api/riskObligation/riskObligationMutations';
import { ErrorPopup } from '../components/ErrorPopup';

interface RiskObligationTableItem extends Omit<RiskObligation, 'controls'> {
    // manually redefine control here
    controlId: number;
    controlName: string;
    controlDescription: string;
}

function capitalise(text?: string) {
    if (text == undefined) return text;
    return text.charAt(0).toUpperCase() + text.slice(1);
}

function RiskObligationRegisterPage() {
    const queryClient = useQueryClient();
    const [open, setOpen] = React.useState(false);
    const [showUploadPopup, setShowUploadPopup] = React.useState(false);
    const [openControl, setOpenControl] = React.useState(false);
    const [showNotSelectedErrorPopup, setShowNotSelectedErrorPopup] = useState(false);
    const [openCreateControl, setOpenCreateControl] = React.useState<Control[]>([]);
    const [controls, setControl] = React.useState<Control[]>([]);
    const [rows, setRows] = useState<RiskObligationTableItem[]>([]);
    const [selectedRows, setSelectedRows] = useState<RiskObligationTableItem[]>([]);
    const [controlsToFill, setControlsToFill] = useState<Control[]>();
    const [viewControls, setViewControls] = useState<Control[]>();
    const [controlsToChange, setControlsToChange] = useState<Control[]>();
    const [openPrompt, setOpenPrompt] = React.useState(false);
    const { data, refetch } = useRiskObligations();
    const mutation = useCreateRiskObligation();
    const createControlMutation = useCreateControl();
    const uploadRiskObligationDataMutation = useUploadRiskObligationData();

    useEffect(() => {
        setRows(
            data
                ? data.flatMap((ro) =>
                      ro.controls.map((c) => {
                          return {
                              ...ro,
                              controlId: c.id,
                              controlName: c.name,
                              controlDescription: c.description,
                          } as RiskObligationTableItem;
                      }),
                  )
                : [],
        );
    }, [data]);

    // define form manager
    const { register, handleSubmit, setValue, reset, watch } = useForm<RiskObligation>({
        defaultValues: { type: 'risk' },
    });

    const onSubmit = (formData: RiskObligation) => {
        mutation.mutate(formData, {
            onSuccess: (newData) => {
                queryClient.setQueryData(['riskObligation'], [...(data as []), newData]);
                handleClose();
            },
            onError: (error) => {
                console.error('Error:', error);
            },
        });
        // pop up to notice the user to create new compliance rules
        setOpenPrompt(true);
    };

    // close create riskObligation pop-up and clear the control list
    const handleClose = () => {
        setControl([]);
        reset();
        setOpen(false);
    };

    // add control to the control list
    const addControl = (control: Control) => {
        if (
            controls.some(
                (c) =>
                    c.id === control.id &&
                    c.type === control.type &&
                    c.description === control.description &&
                    c.name === control.name,
            )
        ) {
            alert('Control is already selected!');
        } else {
            createControlMutation.mutate(control, {
                onSuccess(response) {
                    setControl([...controls, response]);
                    setValue('controls', [...controls, response]);
                    setOpenCreateControl([]);
                    setOpenControl(false);
                },
            });
        }
    };

    // remove control from the control list
    const removeControl = (controlRemove: Control) => {
        const updatedControls = controls.filter((control) => control.id !== controlRemove.id);
        setControl(updatedControls);
        setValue('controls', updatedControls);
    };

    const handleFileUpload = (file: File) => {
        setShowUploadPopup(false);
        postValidCSV(file);
    };

    const postValidCSV = async (file: File) => {
        const formData = new FormData();
        formData.append('file', file);

        await uploadRiskObligationDataMutation.mutateAsync(formData);
        refetch();
        setOpenPrompt(true);
    };

    const getAllControlsWithoutTemplate = async () => {
        try {
            const allControls = await getControls()();
            const filterControls = allControls.filter(
                (control: Control) => !control.templates || control.templates.length === 0,
            );
            setControlsToChange(filterControls);
        } catch (error) {
            console.error('Error fetching controls', error);
        }
    };

    return (
        <div className="bg-white flex flex-col h-full">
            <PromptPopup
                openPrompt={openPrompt}
                handleClosePrompt={() => setOpenPrompt(false)}
                getAllControlsWithoutTemplate={() => getAllControlsWithoutTemplate()}
            />
            <ActionBar
                items={[
                    {
                        icon: SyncIcon,
                        tooltip: 'Sync',
                        handler: () => {
                            console.log('SYNC');
                        },
                    },
                    Seperator,
                    {
                        icon: PlusIcon,
                        tooltip: 'Create New Risk or Obligation',
                        handler: () => {
                            setOpen(true);
                        },
                    },
                    Seperator,
                    {
                        icon: UploadIcon,
                        tooltip: 'Upload',
                        handler: () => {
                            setShowUploadPopup(true);
                        },
                    },
                    Seperator,
                    {
                        icon: LogIcon,
                        tooltip: 'Create Compliance Rules',
                        handler: () => {
                            if (data && selectedRows.length > 0) {
                                setControlsToFill(
                                    data.reduce((result, current) => {
                                        const selectedControls = selectedRows.map((row) => row.controlId);
                                        return result.concat(
                                            current.controls.filter(
                                                (control) => control.id && selectedControls.includes(control.id),
                                            ),
                                        );
                                    }, [] as Control[]),
                                );
                            }
                        },
                    },
                    Seperator,
                    {
                        icon: EditIcon,
                        tooltip: 'Edit selected Controls',
                        handler: () => {
                            if (data && selectedRows.length > 0) {
                                const selectedControls = selectedRows.map((row) => row.controlId);
                                const controlsToChange = [] as Control[];
                                data.forEach((riskObligation) => {
                                    riskObligation.controls.forEach((control) => {
                                        if (
                                            selectedControls.includes(control.id || -1) &&
                                            !controlsToChange.map((c) => c.id).includes(control.id)
                                        ) {
                                            controlsToChange.push(control);
                                        }
                                    });
                                });
                                setControlsToChange(controlsToChange);
                            }
                        },
                    },
                    Seperator,
                    {
                        icon: FileInfo,
                        tooltip: 'View Compliance Rules',
                        handler: () => {
                            if (data && selectedRows.length > 0) {
                                setViewControls(
                                    data.reduce((result, current) => {
                                        const selectedControls = selectedRows.map((row) => row.controlId);
                                        return result.concat(
                                            current.controls.filter(
                                                (control) => control.id && selectedControls.includes(control.id),
                                            ),
                                        );
                                    }, [] as Control[]),
                                );
                            }
                            if (data && selectedRows.length == 0) {
                                setShowNotSelectedErrorPopup(true);
                            }
                        },
                    },
                ]}
            />
            <Breadcrumb>Risk & Obligation Register</Breadcrumb>
            <div className="relative overflow-x-auto flex-grow">
                <Table
                    columns={RiskObligationTableColumns as Column<RiskObligationTableItem>[]}
                    data={rows}
                    onRowSelectStateChange={(r: RiskObligationTableItem[]) => setSelectedRows(r)}
                    filtering
                />
            </div>

            <FillComplianceTemplatePopup data={controlsToFill} handleClose={() => setControlsToFill(undefined)} />

            <ControlMapTemplatePopup
                data={controlsToChange}
                onNext={(control) => {
                    if (control.id) {
                        updateControl(control.id)(control);
                        if (data) {
                            const iRO = data.findIndex((ro) => ro.controls.map((c) => c.id).includes(control.id));
                            const iC = data[iRO].controls.findIndex((c) => c.id == control.id);
                            data[iRO].controls[iC] = control;
                            queryClient.setQueryData(['riskObligation'], [...(data as [])]);
                        }
                    }
                }}
                handleClose={() => {
                    setControlsToChange(undefined);
                }}
                disableBacktracking={true}
            />

            <ViewComplianceRulesByControlPopup
                data={viewControls}
                onNext={(control) => console.log(control)}
                handleClose={() => setViewControls(undefined)}
            />
            <ErrorPopup
                show={showNotSelectedErrorPopup}
                handleClose={() => setShowNotSelectedErrorPopup(false)}
                errorMessage={'No controls selected.'}
                header={'View Compliance Rules Error'}
            />

            {/* Render the FileUploadPopup component only when showUploadPopup is set to true*/}
            {showUploadPopup && (
                <FileUploadPopup
                    title={'Upload Risk & Obligation Register'}
                    onConfirm={handleFileUpload}
                    onCancel={() => setShowUploadPopup(false)}
                />
            )}

            <PopupPanel show={open} handleClose={handleClose}>
                <PopupHeader handleClose={handleClose}>Create Risk or Obligation</PopupHeader>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <div className="p-3 w-[32em] max-h-[70vh] overflow-auto">
                        <Select label="Type" {...register('type')}>
                            <option value="risk">Risk</option>
                            <option value="obligation">Obligation</option>
                        </Select>
                        <Input
                            label={capitalise(watch('type')) + ' Name'}
                            type="text"
                            placeholder={capitalise(watch('type')) + ' name'}
                            required
                            {...register('name', { required: true })}
                        />
                        <Input
                            label={capitalise(watch('type')) + ' Description'}
                            type="text"
                            placeholder={capitalise(watch('type')) + ' description'}
                            required
                            {...register('description', { required: true })}
                        />
                        <Input
                            label={capitalise(watch('type')) + ' Category'}
                            type="text"
                            placeholder={capitalise(watch('type')) + ' category'}
                            required
                            {...register('category', { required: true })}
                        />
                        <Input
                            label={capitalise(watch('type')) + ' Sub-Category'}
                            type="text"
                            placeholder={capitalise(watch('type')) + ' sub-category'}
                            {...register('subCategory')}
                        />

                        {/* input riskObligation Controls list */}
                        <Table
                            columns={[
                                { Header: 'ID', accessor: 'id' },
                                { Header: 'Type', accessor: 'type' },
                                { Header: 'Name', accessor: 'name' },
                                { Header: 'Description', accessor: 'description' },
                                {
                                    Header: '',
                                    id: 'delete',
                                    accessor: 'id',
                                    Cell: (cellProps: CellProps<Control>) => (
                                        <div className="pr-1">
                                            <TrashIcon
                                                className="w-5 h-5 hover:text-red-600 hover:cursor-pointer"
                                                onClick={() => removeControl(cellProps.cell.row.original)}
                                            />
                                        </div>
                                    ),
                                },
                            ]}
                            data={controls}
                        />
                    </div>

                    <PopupPanel show={openControl} handleClose={() => setOpenControl(false)}>
                        <PopupHeader handleClose={() => setOpenControl(false)}>Select Controls</PopupHeader>
                        <ControlPopUp addControl={addControl} />
                        <PopupFooter>
                            <Button
                                text="New Controls"
                                onClick={() =>
                                    setOpenCreateControl([
                                        {
                                            type: '',
                                            name: '',
                                            description: '',
                                            templates: [],
                                        },
                                    ])
                                }
                                className="mr-auto"
                            />
                            <Button text="Cancel" onClick={() => setOpenControl(false)} />
                        </PopupFooter>

                        <ControlMapTemplatePopup
                            data={openCreateControl}
                            onNext={addControl}
                            handleClose={() => setOpenCreateControl([])}
                            disableBacktracking={true}
                        />
                    </PopupPanel>
                    <PopupFooter>
                        <Button text="Add Controls" onClick={() => setOpenControl(true)} className="mr-auto" />
                        <Button text="OK" Icon={CheckCircleIcon} type="submit" className="mr-2" />
                        <Button text="Cancel" Icon={XCircleIcon} onClick={handleClose} />
                    </PopupFooter>
                </form>
            </PopupPanel>
        </div>
    );
}

export default RiskObligationRegisterPage;
