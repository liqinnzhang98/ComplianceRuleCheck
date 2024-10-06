import { useState } from 'react';
import downloadFile from '../assets/icons/download-file.svg';
import { PopupPanel, PopupHeader, PopupFooter } from './Popup';
import Button from './Button';

// Define the props that the component expects
type FileUploadPopupProps = {
    title: string;
    onConfirm: (file: File) => void;
    onCancel: () => void;
};

// Define the component as a function that takes in the props
function FileUploadPopup({ title, onConfirm, onCancel }: FileUploadPopupProps) {
    const [isOpen, setIsOpen] = useState(true);
    const [selectedFile, setSelectedFile] = useState<File | null>(null); // init null type, can hold File  null
    const [dragging, setDragging] = useState(false);
    const [notCSV, setNotCSV] = useState(false);

    function closeModal() {
        setIsOpen(false);

        setTimeout(() => {
            onCancel();
        }, 300);
    }

    function removeFile() {
        setSelectedFile(null);
        setNotCSV(false);
    }

    function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
        const file = e.target.files && e.target.files[0];
        setSelectedFile(file || null);
    }

    function handleConfirm() {
        if (selectedFile) {
            setIsOpen(false);
            onConfirm(selectedFile);
        }
    }

    const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setDragging(true);
    };

    const handleDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setDragging(false);
    };

    const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setDragging(false);

        const droppedFile = e.dataTransfer.files[0];
        if (droppedFile.type.match('text/csv')) {
            setNotCSV(false);
            setSelectedFile(droppedFile);
        } else {
            setNotCSV(true);
        }
    };

    return (
        <PopupPanel show={isOpen} handleClose={onCancel}>
            <PopupHeader handleClose={onCancel}>{title}</PopupHeader>
            <div className="w-[30em]">
                {/* File selection section */}
                {selectedFile ? (
                    <div className="p-4 h-40">
                        <div>
                            <p className="mt-2 text-center">Selected CSV file:</p>
                            <p className="mt-4 text-center">{selectedFile.name}</p>
                        </div>
                        <div className="text-center">
                            <button className="cursor-pointer text-blue-500 hover:text-blue-600" onClick={removeFile}>
                                <p className="mt-6 text-center">Remove file</p>
                            </button>
                        </div>
                    </div>
                ) : (
                    <div>
                        <div
                            className={`m-3 h-40 bg-background-lighter border-4 border-dashed border-gray-400 rounded-lg flex items-center justify-center ${
                                dragging ? 'border-oceanic-400 bg-blue-100' : ''
                            }`}
                            onDragOver={handleDragOver}
                            onDragLeave={handleDragLeave}
                            onDrop={handleDrop}
                        >
                            <div className="text-center">
                                <div>
                                    <img src={downloadFile} alt="My SVG" className="w-14 h-14 mx-auto" />
                                </div>
                                <div className="mt-4">
                                    <span>
                                        <label
                                            htmlFor="file-input"
                                            className="cursor-pointer text-oceanic-600 hover:text-oceanic-700"
                                        >
                                            Click to select a file
                                        </label>
                                        <input
                                            type="file"
                                            id="file-input"
                                            accept=".csv"
                                            className="hidden"
                                            onChange={handleFileChange}
                                        />
                                    </span>
                                    <span> or drag it here.</span>
                                </div>
                            </div>
                        </div>
                        <div>
                            {notCSV && (
                                <div className="p-1 text-center text-red-500">File uploaded must be type .csv</div>
                            )}
                        </div>
                    </div>
                )}
            </div>
            <PopupFooter>
                {selectedFile && <Button text="OK" onClick={handleConfirm} className="mr-2" />}
                <Button text="Cancel" onClick={closeModal} />
            </PopupFooter>
        </PopupPanel>
    );
}

// Export the component as the default export
export default FileUploadPopup;
