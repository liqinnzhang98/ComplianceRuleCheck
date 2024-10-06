/**
 * This file contains the ErrorPopup component.
 * It displays a error popup message when the schema definition fails and the eventlog upload fails.
 */

// Importing required modules and types
import { PopupPanel, PopupHeader, PopupFooter } from './Popup';
import Button from './Button';

/**
 * @function ErrorPopup
 * @description The ErrorPopup component.
 * @param {boolean} show - Whether the popup should be displayed.
 * @param {Function} handleClose - Function to close the popup.
 * @returns {ReactNode} - The rendered ErrorPopup component.
 */
export function ErrorPopup({
    show,
    handleClose,
    errorMessage,
    header,
}: {
    show: boolean;
    handleClose: () => void;
    errorMessage: string | null;
    header: string | null;
}) {
    return (
        <PopupPanel show={show} handleClose={handleClose}>
            <PopupHeader handleClose={handleClose}>{header}</PopupHeader>
            <div className="p-4">
                {/* the Error title with red color and larger font */}
                <p className="text-red-500 text-lg font-bold">Error</p>
                <p>Error Message: {errorMessage}</p>
            </div>
            <PopupFooter>
                <Button text="OK" onClick={handleClose} />
            </PopupFooter>
        </PopupPanel>
    );
}
