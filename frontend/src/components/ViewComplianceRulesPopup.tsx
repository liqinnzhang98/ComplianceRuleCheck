import { PopupFooter, PopupGroup, PopupHeader, PopupPanel } from './Popup';
import { ComplianceRule } from '../types/ComplianceRule';
import { Control } from '../types/Control';
import { useComplianceRulesByControl } from '../hooks/complianceRuleHooks';
import DisclosureComplianceRule from './DisclosureComplianceRule';
import Button from './Button';
import { useEffect, useState } from 'react';

type ControlProps = {
    data?: Control[];
    onNext: (control: Control) => void;
    handleClose: () => void;
};

type ProcessProps = {
    data?: ComplianceRule[];
    show: boolean;
    handleClose: () => void;
    handleDelete: (id: number) => void;
};

function DisplayControl({ control }: { control: Control }) {
    return (
        <div className="border p-5 bg-soft-blue-400">
            <h1 className="text-xl">Control: {control.name}</h1>
            <p>Description: {control.description}</p>
        </div>
    );
}

function ByControlPopupContent({ item }: { item: Control }) {
    if (item == undefined) {
        return null;
    }
    if (item.id == undefined) {
        return null;
    }
    const { data: complianceRules } = useComplianceRulesByControl(item.id);
    const [noRules, setNoRules] = useState(false);
    const [viewComplianceRules, setViewComplianceRules] = useState<ComplianceRule[]>();
    useEffect(() => {
        if (complianceRules && complianceRules.length > 0) {
            setViewComplianceRules(complianceRules);
        }
        if (complianceRules?.length == 0 || viewComplianceRules?.length == 0) {
            setNoRules(true);
        }
    }, [complianceRules, viewComplianceRules]);

    const deleteComplianceRule = (id: number) => {
        const updatedRules = complianceRules?.filter((item) => item.id !== id);
        setViewComplianceRules(updatedRules);
    };

    return (
        <div>
            <DisplayControl control={item} />
            <div className="p-4 min-w-[50vw] border max-h-[80vh] overflow-auto">
                {noRules ? (
                    <p>There are no compliance rules associated with this control.</p>
                ) : (
                    viewComplianceRules?.map((complianceRule, index) => (
                        <div key={index} className="p-2 min-w-[50vw]">
                            <DisclosureComplianceRule
                                type={'Control'}
                                id={complianceRule.processId}
                                complianceRule={complianceRule}
                                handleDelete={(id: number) => deleteComplianceRule(id)}
                            />
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export function ViewComplianceRulesByControlPopup({ data, onNext, handleClose }: ControlProps) {
    return (
        <PopupGroup
            data={data || []}
            PopupContent={ByControlPopupContent}
            handleClose={handleClose}
            getPopupHeaderText={(_, i) => {
                return 'View Compliance Rules (' + (i + 1) + ' of ' + (data?.length || 0 + 1) + ')';
            }}
            onNext={onNext}
        />
    );
}

export function ViewComplianceRulesByProcessPopup({ data, show, handleClose, handleDelete }: ProcessProps) {
    return (
        <PopupPanel show={show} handleClose={handleClose}>
            <PopupHeader handleClose={handleClose}>View Compliance Rules</PopupHeader>
            <div className="p-5 max-h-[80vh] overflow-auto">
                {data?.map((complianceRule, index) => (
                    <div key={index} className="p-2 min-w-[50vw]">
                        <DisclosureComplianceRule
                            type={'Process'}
                            id={complianceRule.controlId}
                            complianceRule={complianceRule}
                            handleDelete={handleDelete}
                        />
                    </div>
                ))}
            </div>
            <PopupFooter>
                <Button
                    key="Close"
                    text="Close"
                    onClick={() => {
                        handleClose();
                    }}
                    className="mr-2"
                />
            </PopupFooter>
        </PopupPanel>
    );
}
