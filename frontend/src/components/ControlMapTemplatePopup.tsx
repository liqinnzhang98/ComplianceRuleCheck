import { PopupGroup } from './Popup';
import { Control } from '../types/Control';
import NewControl from './NewControl';

// used to show continuous control popups
type Props = {
    data?: Control[];
    onNext: (control: Control) => void;
    handleClose: () => void;
    disableBacktracking: boolean;
};

export default function ControlMapTemplatePopup({ data, onNext, handleClose, disableBacktracking }: Props) {
    if (data == null || data.length === 0) {
        return null;
    }
    return (
        <PopupGroup
            data={data || []}
            PopupContent={NewControl}
            handleClose={handleClose}
            getPopupHeaderText={(_, i) => {
                return 'Update controls (' + (i + 1) + ' of ' + (data?.length || 0 + 1) + ')';
            }}
            onNext={onNext}
            disableBacktracking={disableBacktracking}
        />
    );
}
