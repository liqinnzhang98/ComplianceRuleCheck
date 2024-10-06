import { Control } from '../types/Control';
import Table from './Table';
import { CellProps } from 'react-table';
import { PlusIcon } from '@heroicons/react/24/outline';
import { useControls } from '../hooks/riskObligationHooks';

type Props = {
    addControl: (control: Control) => void;
};

function ControlPopUp(props: Props) {
    const { addControl } = props;
    const handleAddControl = (control: Control) => {
        addControl(control);
    };

    const { data } = useControls();

    if (data) {
        return (
            <div className="min-w-[28em] min-h-[14em] max-h-[80vh] overflow-auto">
                <Table
                    data={data}
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
                                    <PlusIcon
                                        className="w-5 h-5 hover:text-oceanic-600 hover:cursor-pointer"
                                        onClick={() => handleAddControl(cellProps.cell.row.original)}
                                    />
                                </div>
                            ),
                        },
                    ]}
                />
            </div>
        );
    } else {
        return <div className="w-[28em] h-[14em] grid place-items-center">No controls have been created</div>;
    }
}

export default ControlPopUp;
