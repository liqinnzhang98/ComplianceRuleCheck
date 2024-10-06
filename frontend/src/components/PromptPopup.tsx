import { XCircleIcon, CheckCircleIcon } from '@heroicons/react/24/outline';
import { PopupPanel, PopupHeader, PopupFooter } from '../components/Popup';
import Button from '../components/Button';

export function PromptPopup({
    openPrompt,
    handleClosePrompt,
    getAllControlsWithoutTemplate,
}: {
    openPrompt: boolean;
    handleClosePrompt: () => void;
    getAllControlsWithoutTemplate: () => void;
}) {
    return (
        <PopupPanel show={openPrompt} handleClose={handleClosePrompt}>
            <PopupHeader handleClose={handleClosePrompt}>Apromore Compliance Center</PopupHeader>
            <div className="ml-5 mr-5 mt-2 w-[28em]"> Remember to create new compliance rules!</div>
            <PopupFooter>
                <Button
                    key="Do it now"
                    text="Do it now"
                    Icon={CheckCircleIcon}
                    onClick={async () => {
                        handleClosePrompt();
                        getAllControlsWithoutTemplate();
                    }}
                    className="mr-2"
                />
                <Button key="Later" text="Later" Icon={XCircleIcon} onClick={handleClosePrompt} />
            </PopupFooter>
        </PopupPanel>
    );
}
